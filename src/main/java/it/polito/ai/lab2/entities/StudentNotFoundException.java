package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.TeamServiceException;

public class StudentNotFoundException extends TeamServiceException {

    public StudentNotFoundException(String msg) {
        System.out.println(msg);
    }

}
