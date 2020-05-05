package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.TeamServiceException;

public class StudentNotFoundException extends TeamServiceException {

    public StudentNotFoundException(String id) {
        System.out.println(id + " not found!");
    }

}
