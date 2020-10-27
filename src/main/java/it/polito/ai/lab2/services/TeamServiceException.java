package it.polito.ai.lab2.services;

public class TeamServiceException extends RuntimeException {

    public TeamServiceException() {}

    public TeamServiceException(String msg) {
        System.out.println(msg);
    };
}
