package it.polito.ai.lab2.services.vm;

import it.polito.ai.lab2.services.AiException;

public class VmServiceException extends AiException {

    public VmServiceException() {}

    public VmServiceException(String msg) {
        this.setErrorMessage(msg);
        System.out.println(msg);
    }
}
