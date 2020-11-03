package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.entities.CourseNotFoundException;
import it.polito.ai.lab2.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/courses")
public class CourseController {

    @Autowired
    TeamService teamService;

    @GetMapping({"", "/"})
    public List<CourseDTO> all() {
        return teamService
                .getAllCourses()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{name}")
    public CourseDTO getOne(@PathVariable String name) throws ResponseStatusException {
        Optional<CourseDTO> course = teamService.getCourse(name);
        if(course.isPresent())
            return ModelHelper.enrich(course.get());
        else
            throw new ResponseStatusException(HttpStatus.CONFLICT, name);
    }

    @GetMapping("/{name}/enrolled")
    public List<StudentDTO> enrolledStudents(@PathVariable String name) throws ResponseStatusException {
        List<StudentDTO> enrolled;
        try {
            enrolled = teamService.getEnrolledStudents(name);
        } catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, name);
        }
        return enrolled
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @PostMapping({"", "/"})
    public CourseDTO addCourse(@RequestBody CourseDTO courseDTO) throws ResponseStatusException {
        if(teamService.addCourse(courseDTO))
            return ModelHelper.enrich(courseDTO);
        else
            throw new ResponseStatusException(HttpStatus.CONFLICT, courseDTO.getName());
    }

    @PostMapping({"/{name}/enrollOne"})
    public List<StudentDTO> enrollOne(@PathVariable String name, @RequestBody StudentDTO studentDTO) throws ResponseStatusException {
        Optional<CourseDTO> course = teamService.getCourse(name);
        if(!course.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, name);
        if(!teamService.addStudentToCourse(studentDTO.getId(), name))
            throw new ResponseStatusException(HttpStatus.CONFLICT, studentDTO.getId());
        return enrolledStudents(name);
    }

    @PostMapping("{name}/enrollMany")
    public List<StudentDTO> enrollMany(@PathVariable String name, @RequestParam("file") MultipartFile multipartFile)
                                                            throws UnsupportedMediaTypeStatusException {
        if(!multipartFile.getContentType().equals("text/csv"))
            throw new UnsupportedMediaTypeStatusException(multipartFile.getContentType());
        try {
            Reader reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()));
            teamService.addAndEnroll(reader, name);
        } catch(Exception e) {
            System.out.println("Exception generated in reader block");
        }
        return enrolledStudents(name);
    }

}
