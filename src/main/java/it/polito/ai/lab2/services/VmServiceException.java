package it.polito.ai.lab2.services;

public class VmServiceException extends RuntimeException {

    public VmServiceException() {}

    public VmServiceException(String msg) {
        System.out.println(msg);
    }
}
