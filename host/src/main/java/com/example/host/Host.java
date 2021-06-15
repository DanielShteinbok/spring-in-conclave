package com.example.host;

import com.r3.conclave.host.EnclaveHost;
import com.r3.conclave.host.EnclaveLoadException;

public class Host {
    public static void main(String[] args) {
        try {
            EnclaveHost.checkPlatformSupportsEnclaves(true);
            System.out.println("This platform supports all three modes");
        } catch (EnclaveLoadException e) {
            e.printStackTrace();
            System.out.println("This platform does not support hardware enclaves");
        }
        String className = "com.example.enclave.DemoApplication";
        try (EnclaveHost enclave = EnclaveHost.load(className)){
            enclave.start(null, null);
        } catch (EnclaveLoadException e) {
            e.printStackTrace();
        }
    }
}
