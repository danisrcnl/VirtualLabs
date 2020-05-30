package it.polito.ai.lab2.security;

public class InvalidJwtAuthenticationException extends RuntimeException {
    public InvalidJwtAuthenticationException(String msg) {
        System.out.println(msg);
    }
}
