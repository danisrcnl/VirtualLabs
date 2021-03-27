package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.assignment.AssignmentServiceException;

public class AssignmentNotFoundException extends AssignmentServiceException {

    public AssignmentNotFoundException(String id) {
        this.setErrorMessage(id + " not found!");
        System.out.println(this.getErrorMessage());
    }

}
