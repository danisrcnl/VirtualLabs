package it.polito.ai.lab2.services.auth;

public interface AuthenticationService {

    public void addUser (String username, String password);

    public void deleteUser (String username);
}
