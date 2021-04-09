package it.polito.ai.lab2.controllers;


import it.polito.ai.lab2.dataStructures.SignUpRequest;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeacherDTO;
import it.polito.ai.lab2.repositories.UserRepository;
import it.polito.ai.lab2.security.AuthenticationRequest;
import it.polito.ai.lab2.security.JwtTokenProvider;
import it.polito.ai.lab2.services.AiException;
import it.polito.ai.lab2.services.auth.AuthenticationService;
import it.polito.ai.lab2.services.notification.NotificationService;
import it.polito.ai.lab2.services.student.StudentService;
import it.polito.ai.lab2.services.teacher.TeacherService;
import it.polito.ai.lab2.services.team.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
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

    @Autowired
    StudentService studentService;

    @Autowired
    TeacherService teacherService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    NotificationService notificationService;

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
    public void signup(@RequestBody SignUpRequest signUpRequest) throws ResponseStatusException {
        if (!regEx(signUpRequest.getEmail(), signUpRequest.getId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Provided email has not the desired format");
        try {
            authenticationService.addUser(signUpRequest.getEmail(), passwordEncoder.encode(signUpRequest.getPassword()));
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }
        if (signUpRequest.getEmail().charAt(0) == 's') {
            if (!studentService.getStudent(signUpRequest.getId()).isPresent()) {
                StudentDTO studentDTO = StudentDTO
                        .builder()
                        .name(signUpRequest.getLastName())
                        .firstName(signUpRequest.getFirstName())
                        .email(signUpRequest.getEmail())
                        .photoPath("")
                        .id(signUpRequest.getId())
                        .build();
                try {
                    studentService.addStudent(studentDTO);
                } catch (AiException e) {
                    authenticationService.deleteUser(signUpRequest.getEmail());
                    throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
                }
            }
            studentService.linkToUser(signUpRequest.getId(), signUpRequest.getEmail());
            authenticationService.setPrivileges(signUpRequest.getEmail(), Arrays.asList("ROLE_STUDENT"));
        } else if (signUpRequest.getEmail().charAt(0) == 'd') {
            TeacherDTO teacherDTO = TeacherDTO
                    .builder()
                    .name(signUpRequest.getLastName())
                    .firstName(signUpRequest.getFirstName())
                    .email(signUpRequest.getEmail())
                    .photoPath("")
                    .id(signUpRequest.getId())
                    .build();

            try {
                teacherService.addTeacher(teacherDTO);
            } catch (AiException e) {
                authenticationService.deleteUser(signUpRequest.getEmail());
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
            }
            teacherService.linkToUser(signUpRequest.getId(), signUpRequest.getEmail());
            authenticationService.setPrivileges(signUpRequest.getEmail(), Arrays.asList("ROLE_TEACHER"));
        }
        notificationService.notifyUser(signUpRequest.getEmail());
    }

    private Boolean regEx (String email, String id) {
        Boolean isTeacher = false;
        if (email.charAt(0) != 's' && email.charAt(0) != 'd')
            return false;
        if (email.charAt(0) == 'd')
            isTeacher = true;
        String[] strings = email.split("@");
        if (strings.length != 2)
            return false;
        String encodedId = "";
        for (int i = 1; i < strings[0].length(); i++)
            encodedId += strings[0].charAt(i);
        if(encodedId.length() != 6)
            return false;
        if (!id.equals(encodedId))
            return false;
        if (!(strings[1].equals("polito.it") || strings[1].equals("studenti.polito.it")))
            return false;
        if(isTeacher && strings[1].equals("studenti.polito.it"))
            return false;
        if(!isTeacher && strings[1].equals("polito.it"))
            return false;
        return true;
    }
}
