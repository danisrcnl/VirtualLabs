package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.team.TeamServiceException;

public class UserNotFoundException extends TeamServiceException {

    public UserNotFoundException(String id) {
        this.setErrorMessage("User with id " + id + " not found");
        System.out.println(this.getErrorMessage());
    }
}
