package it.polito.ai.lab2.services.auth;

import it.polito.ai.lab2.entities.User;
import it.polito.ai.lab2.entities.UserExistingException;
import it.polito.ai.lab2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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
        userRepository.save(u);
    }
}
