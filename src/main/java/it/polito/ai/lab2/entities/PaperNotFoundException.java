package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.assignment.AssignmentServiceException;

public class PaperNotFoundException extends AssignmentServiceException {

    public PaperNotFoundException(String id) {
        this.setErrorMessage(id + " non trovato!");
        System.out.println(this.getErrorMessage());
    }

}
