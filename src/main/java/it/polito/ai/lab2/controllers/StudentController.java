package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
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

    @GetMapping("/{id}/getCourses")
    public List<CourseDTO> getStudentCourses (@PathVariable String id) throws ResponseStatusException {
        List<CourseDTO> studentCourses;
        try {
            studentCourses = teamService.getCoursesForStudent(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, id);
        }
        return studentCourses
                .stream()
                .map(ModelHelper :: enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/getTeams")
    public List<TeamDTO> getStudentTeams (@PathVariable String id) throws ResponseStatusException {
        List<TeamDTO> studentTeams;
        try {
            studentTeams = teamService.getTeamsForStudent(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, id);
        }
        return studentTeams
                .stream()
                .map(ModelHelper :: enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/{courseName}/getTeam")
    public List<TeamDTO> getStudentTeamForCourse (@PathVariable String id, @PathVariable String courseName) throws ResponseStatusException {
        List<TeamDTO> studentTeams;
        try {
            studentTeams = teamService.getStudentTeamInCourse(id, courseName);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, id);
        }

        return studentTeams
                .stream()
                .map(ModelHelper :: enrich)
                .collect(Collectors.toList());
    }
}
