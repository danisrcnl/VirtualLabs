package it.polito.ai.lab2.controllers;


import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeacherDTO;
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
@RequestMapping("/API/teachers")
public class TeacherController {

    @Autowired
    TeamService teamService;

    @GetMapping({"", "/"})
    public List<TeacherDTO> all() {
        return teamService
                .getAllTeachers()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TeacherDTO getOne(@PathVariable String id) throws ResponseStatusException {
        Optional<TeacherDTO> teacher = teamService.getTeacher(id);
        if(teacher.isPresent())
            return ModelHelper.enrich(teacher.get());
        else
            throw new ResponseStatusException(HttpStatus.CONFLICT, id);
    }

}
