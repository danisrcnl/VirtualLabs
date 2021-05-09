package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.repositories.TeamRepository;
import it.polito.ai.lab2.services.team.TeamServiceException;
import org.springframework.beans.factory.annotation.Autowired;

public class TeamNotFoundException extends TeamServiceException {

    @Autowired
    TeamRepository teamRepository;

    public TeamNotFoundException(String id) {
        this.setErrorMessage(id + " non trovato!");
        System.out.println(this.getErrorMessage());
    }

    public TeamNotFoundException(int id) {
        this.setErrorMessage("Il team con id: " + id + " non esiste!");
        System.out.println(this.getErrorMessage());
    }

}
