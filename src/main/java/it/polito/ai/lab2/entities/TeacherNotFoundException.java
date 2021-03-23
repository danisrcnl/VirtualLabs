package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.TeamServiceException;

public class TeacherNotFoundException extends TeamServiceException {

    public TeacherNotFoundException(String id) {
        this.setErrorMessage(id + " not found!");
        System.out.println(this.getErrorMessage());
    }

}

