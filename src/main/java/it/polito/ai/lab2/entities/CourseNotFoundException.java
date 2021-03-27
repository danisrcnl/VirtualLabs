package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.team.TeamServiceException;

public class CourseNotFoundException extends TeamServiceException {

    public CourseNotFoundException(String courseName) {
        this.setErrorMessage(courseName + " not found!");
        System.out.println(this.getErrorMessage());
    }

}
