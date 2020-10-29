package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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


    @PostMapping({"", "/"})
    public StudentDTO addStudent(@RequestBody StudentDTO studentDTO) throws ResponseStatusException {
        if(teamService.addStudent(studentDTO))
            return ModelHelper.enrich(studentDTO);
        else
            throw new ResponseStatusException(HttpStatus.CONFLICT, studentDTO.getId());
    }
}
