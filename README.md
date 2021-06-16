# OpenID Connect Via Enclave
A simple attempt at running a Spring Boot server 
(with a resource protected by OpenID Connect authentication) inside a secure enclave.

### (Eventual) Use Case
A user has an AWS Cognito ID, can sign in with AWS Cognito, and wants to
(eventually, through intermediate APIs) make e.g. Corda transactions.
The objective here is to achieve the following:
* The user should only be able to perform the action when authenticated
* The Oauth2 Client (that receives and checks the id token) is *NOT*
  the same party that runs the e.g. Corda node.
* The party running the e.g. Corda node should *NOT* receive the user's identity info,
apart from what is necessary for the transaction 
  (eventually, the plan is to use corda accounts, 
  but a user may have multiple different accounts despite having one Cognito ID/login.
  In this case, the party running the Corda node would only receive the account name).
* The host running the Oauth2 Client is not necessarily trusted 
  (e.g. it could run in the cloud)

### (Eventual) Architecture
The user interacts with the untrusted host via an HTTP/HTTPS API.
The flow used is Code Authentication, with the caveat that the nonce 
sent with the id token request (along with the code) should be generated and checked
from the enclave, to prevent a malicious host from replaying old ID tokens.

When received from the authorization server, the ID token should be passed to the enclave,
where the nonce and signature are checked from trusted code.
From there, the enclave may do some extra work 
(e.g. query a database for the user's accounts, or process the user's request in some way),
and then communicate with the trusted party 
(that would be running the Corda node in our example)
using encrypted Mail.

The party running the Corda node wouldn't receive information about the user,
but could act with certainty (insofar as the authorization server is trusted)
that the user is valid.

A possible further advantage for this (beyond hiding user info) is to allow
for the user's request to be routed to the correct (e.g. Corda node-running) party,
where the user's account could be on one of many nodes. In general, it seems this separation
of authenticating and acting parties would be very useful for larger networks
with a lot of different acting parties.

## Current State of Affairs
Despite the grandiose description above, this project is just a small experiment so far.
Currently, the host and client are bare-bones, and the enclave program is (almost)
a copy-pasted Spring Boot project made with [Spring initializer](https://start.spring.io/#!type=gradle-project&language=java&platformVersion=2.5.1.RELEASE&packaging=jar&jvmVersion=11&groupId=com.example&artifactId=graal-oidc&name=graal-oidc&description=OpenID%20Connect%20flow%20on%20GraalVM&packageName=com.example.graal-oidc&dependencies=native,web,oauth2-client)
containing the Web, Oauth2, and Spring Native dependencies 
(I think Spring Native's 
GraalVM support is necessary to run the Spring Boot server in an Enclave
because if it isn't included, the compiler throws errors similar to what happens
when you run a Spring application without the GraalVM plugin in GraalVM).

