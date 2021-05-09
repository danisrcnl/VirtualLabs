package it.polito.ai.lab2.services.auth;

import it.polito.ai.lab2.entities.User;
import it.polito.ai.lab2.entities.UserExistingException;
import it.polito.ai.lab2.entities.UserNotFoundException;
import it.polito.ai.lab2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class AuthenticationServiceImpl implements AuthenticationService{

    @Autowired
    UserRepository userRepository;

    @Override
    public void addUser(String username, String password) throws UserExistingException {
        if(userRepository.findByUsername(username).isPresent())
            throw new UserExistingException(username);
        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        u.setActive(false);
        userRepository.save(u);
    }

    @Override
    public void deleteUser(String username) throws UserNotFoundException {
        Optional<User> u = userRepository.findByUsername(username);
        if(!u.isPresent())
            throw new UserNotFoundException(username);
        userRepository.delete(u.get());
    }

    @Override
    public void setPrivileges(String username, List<String> roles) throws UserNotFoundException {
        Optional<User> u = userRepository.findByUsername(username);
        if(!u.isPresent())
            throw new UserNotFoundException(username);
        List<String> userRoles = u.get().getRoles();
        for (String role : roles) {
            if (!userRoles.contains(role))
                userRoles.add(role);
        }
        u.get().setRoles(userRoles);
    }

    @Override
    public String getUsername(Long userId) throws UserNotFoundException {
        if(!userRepository.existsById(userId))
            throw new UserNotFoundException(userId.toString());
        return userRepository
                .getOne(userId)
                .getUsername();
    }

    @Override
    public Boolean isValid(String username) throws UserNotFoundException {
        Optional<User> outcome = userRepository.findByUsername(username);
        if (!outcome.isPresent())
            throw new UserNotFoundException(username);
        List<String> roles = outcome
                .get()
                .getRoles();
        if(!roles.contains("ROLE_STUDENT") && !roles.contains("ROLE_TEACHER"))
            return false;
        return true;
    }
}
