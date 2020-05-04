package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.TeamServiceException;

public class TeamNotFoundException extends TeamServiceException {

    public TeamNotFoundException(String msg) {
        System.out.println(msg);
    }

}
