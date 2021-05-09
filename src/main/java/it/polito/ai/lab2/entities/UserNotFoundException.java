package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.team.TeamServiceException;

public class UserNotFoundException extends TeamServiceException {

    public UserNotFoundException(String id) {
        this.setErrorMessage("Non esiste un utente con id = " + id);
        System.out.println(this.getErrorMessage());
    }
}
