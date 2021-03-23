package it.polito.ai.lab2.services;


public class AiException extends RuntimeException {
    private String errorMessage;

    public void setErrorMessage (String message) {
        this.errorMessage = message;
    }

    public String getErrorMessage () {
        return this.errorMessage;
    }
}
