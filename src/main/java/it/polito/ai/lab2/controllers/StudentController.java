package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/students")
public class StudentController {

    @Autowired
    TeamService teamService;

    @GetMapping({"/", ""})
    public List<StudentDTO> all() {
        return teamService
                .getAllStudents()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public StudentDTO getOne(@PathVariable String id) throws ResponseStatusException {
        Optional<StudentDTO> student = teamService.getStudent(id);
        if(student.isPresent())
            return ModelHelper.enrich(student.get());
        else
            throw new ResponseStatusException(HttpStatus.CONFLICT, id);
    }
}
