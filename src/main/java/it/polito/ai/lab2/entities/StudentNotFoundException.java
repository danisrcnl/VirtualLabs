package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.TeamServiceException;

public class StudentNotFoundException extends TeamServiceException {

    public StudentNotFoundException(String id) {
        this.setErrorMessage(id + " not found!");
        System.out.println(this.getErrorMessage());
    }

}
