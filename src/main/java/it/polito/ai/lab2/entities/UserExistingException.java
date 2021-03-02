package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.TeamServiceException;

public class UserExistingException extends TeamServiceException {

    public UserExistingException (String username) {
        System.out.println("User " + username + " already existing");
    }
}
