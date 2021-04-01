package it.polito.ai.lab2.services.auth;

import java.util.List;

public interface AuthenticationService {

    public void addUser (String username, String password);

    public void deleteUser (String username);

    public void setPrivileges (String username, List<String> roles);
}
