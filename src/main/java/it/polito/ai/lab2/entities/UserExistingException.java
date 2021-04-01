package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.team.TeamServiceException;

public class UserExistingException extends TeamServiceException {

    public UserExistingException (String username) {
        this.setErrorMessage("User " + username + " already exists");
        System.out.println(this.getErrorMessage());
    }
}
