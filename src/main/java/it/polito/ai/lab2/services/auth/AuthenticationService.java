package it.polito.ai.lab2.services.auth;

import java.util.List;

public interface AuthenticationService {

    void addUser (String username, String password);

    void deleteUser (String username);

    void setPrivileges (String username, List<String> roles);

    String getUsername (Long userId);
}
