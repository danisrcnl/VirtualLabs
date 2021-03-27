package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.team.TeamServiceException;

public class TeamAlreadyExistingException extends TeamServiceException {

    public TeamAlreadyExistingException(String msg) {
        this.setErrorMessage(msg);
        System.out.println(msg);
    }

}
