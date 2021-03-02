package it.polito.ai.lab2.controllers;


import it.polito.ai.lab2.dataStructures.SignUpRequest;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.repositories.UserRepository;
import it.polito.ai.lab2.security.AuthenticationRequest;
import it.polito.ai.lab2.security.JwtTokenProvider;
import it.polito.ai.lab2.services.AuthenticationService;
import it.polito.ai.lab2.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserRepository users;

    @Autowired
    TeamService teamService;

    @PostMapping("/signin")
    public ResponseEntity signin(@RequestBody AuthenticationRequest data) {
        try {
            System.out.println(data.getUsername());
            System.out.println(data.getPassword());
            String username = data.getUsername();
            System.out.println(username);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
            String token = jwtTokenProvider.createToken(username, this.users.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found")).getRoles());
            Map<Object, Object> model = new HashMap<>();
            model.put("username", username);
            model.put("token", token);
            return ok(model);
        } catch(AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }


    }

    @PostMapping("/signup")
    public void signup(@RequestBody SignUpRequest signUpRequest) {
        authenticationService.addUser(signUpRequest.getEmail(), signUpRequest.getPassword());
        StudentDTO studentDTO = StudentDTO
                .builder()
                .name(signUpRequest.getLastName())
                .firstName(signUpRequest.getFirstName())
                .email(signUpRequest.getEmail())
                .photoPath("")
                .id(signUpRequest.getId())
                .build();

        teamService.addStudent(studentDTO);
    }
}
