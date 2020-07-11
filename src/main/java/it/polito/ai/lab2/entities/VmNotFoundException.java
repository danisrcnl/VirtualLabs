package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.VmServiceException;

public class VmNotFoundException extends VmServiceException {

    public VmNotFoundException(String id) {
        System.out.println(id + " not found!");
    }

}
