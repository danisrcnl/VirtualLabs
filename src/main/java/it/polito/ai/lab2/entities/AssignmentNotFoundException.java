package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.AssignmentServiceException;

public class AssignmentNotFoundException extends AssignmentServiceException {

    public AssignmentNotFoundException(String id) {
        System.out.println(id + " not found!");
    }

}
