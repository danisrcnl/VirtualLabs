package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.services.TeamServiceException;

public class CourseNotFoundException extends TeamServiceException {

    public CourseNotFoundException(String courseName) {
        System.out.println(courseName + " not found!");
    }

}
