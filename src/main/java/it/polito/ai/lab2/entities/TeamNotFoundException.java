package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.TeamServiceException;

public class TeamNotFoundException extends TeamServiceException {

    public TeamNotFoundException(String id) {
        System.out.println(id + " not found!");
    }

}
