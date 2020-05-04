package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.TeamServiceException;

public class TeamAlreadyExistingException extends TeamServiceException {

    public TeamAlreadyExistingException(String msg) {
        System.out.println(msg);
    }

}
