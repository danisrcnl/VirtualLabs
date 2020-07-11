package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.VmServiceException;

public class VmModelNotFoundException extends VmServiceException {

    public VmModelNotFoundException(String id) {
        System.out.println(id + " not found!");
    }

}
