package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.repositories.TeamRepository;
import it.polito.ai.lab2.services.TeamServiceException;
import org.springframework.beans.factory.annotation.Autowired;

public class TeamNotFoundException extends TeamServiceException {

    @Autowired
    TeamRepository teamRepository;

    public TeamNotFoundException(String id) {
        this.setErrorMessage(id + " not found!");
        System.out.println(this.getErrorMessage());
    }

    public TeamNotFoundException(int id) {
        this.setErrorMessage("Team with id: " + id + " doesn't exist!");
        System.out.println(this.getErrorMessage());
    }

}
