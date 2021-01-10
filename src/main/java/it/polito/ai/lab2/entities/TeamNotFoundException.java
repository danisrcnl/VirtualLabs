package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.repositories.TeamRepository;
import it.polito.ai.lab2.services.TeamServiceException;
import org.springframework.beans.factory.annotation.Autowired;

public class TeamNotFoundException extends TeamServiceException {

    @Autowired
    TeamRepository teamRepository;

    public TeamNotFoundException(String id) {
        System.out.println(id + " not found!");
    }

    public TeamNotFoundException(int id) {
        System.out.println(teamRepository.getOne(id).getName() + " not found!");
    }

}
