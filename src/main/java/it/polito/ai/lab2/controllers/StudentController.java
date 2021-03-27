package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.services.AiException;
import it.polito.ai.lab2.services.course.CourseService;
import it.polito.ai.lab2.services.student.StudentService;
import it.polito.ai.lab2.services.team.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    CourseService courseService;

    @Autowired
    StudentService studentService;

    @GetMapping({"/", ""})
    public List<StudentDTO> all () {
        return studentService
                .getAllStudents()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public StudentDTO getOne (@PathVariable String id) throws ResponseStatusException {
        Optional<StudentDTO> student = studentService.getStudent(id);
        if(student.isPresent())
            return ModelHelper.enrich(student.get());
        else
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student " + id + " not found!");
    }


    @PostMapping({"", "/"})
    public StudentDTO addStudent (@RequestBody StudentDTO studentDTO) throws ResponseStatusException {
        if(studentService.addStudent(studentDTO))
            return ModelHelper.enrich(studentDTO);
        else
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Couldn't add student " + studentDTO.getId());
    }

    @GetMapping("/{id}/getCourses")
    public List<CourseDTO> getStudentCourses (@PathVariable String id) throws ResponseStatusException {
        List<CourseDTO> studentCourses;
        try {
            studentCourses = studentService.getCoursesForStudent(id);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
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
            studentTeams = studentService.getTeamsForStudent(id);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
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
            studentTeams = courseService.getStudentTeamInCourse(id, courseName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return studentTeams
                .stream()
                .map(ModelHelper :: enrich)
                .collect(Collectors.toList());
    }
}
