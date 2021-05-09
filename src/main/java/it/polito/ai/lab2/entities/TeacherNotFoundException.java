package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.team.TeamServiceException;

public class TeacherNotFoundException extends TeamServiceException {

    public TeacherNotFoundException(String id) {
        this.setErrorMessage(id + " not trovato!");
        System.out.println(this.getErrorMessage());
    }

}

