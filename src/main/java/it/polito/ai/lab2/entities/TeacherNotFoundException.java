package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.TeamServiceException;

public class TeacherNotFoundException extends TeamServiceException {

    public TeacherNotFoundException(String id) {
        System.out.println(id + " not found!");
    }

}

