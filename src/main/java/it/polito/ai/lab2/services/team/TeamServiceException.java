package it.polito.ai.lab2.services.team;

import it.polito.ai.lab2.services.AiException;

public class TeamServiceException extends AiException {

    public TeamServiceException() {}

    public TeamServiceException(String msg) {
        this.setErrorMessage(msg);
        System.out.println(msg);
    };
}
