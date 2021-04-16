package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.vm.VmServiceException;

public class VmModelNotFoundException extends VmServiceException {

    public VmModelNotFoundException(String id) {
        this.setErrorMessage(id + " not found!");
        System.out.println(this.getErrorMessage());
    }

}
