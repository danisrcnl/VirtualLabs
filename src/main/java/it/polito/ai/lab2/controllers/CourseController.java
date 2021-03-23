package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dataStructures.CourseWithTeacher;
import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeacherDTO;
import it.polito.ai.lab2.entities.CourseNotFoundException;
import it.polito.ai.lab2.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
    public CourseDTO addCourse(@RequestBody CourseWithTeacher courseWithTeacher)
            throws ResponseStatusException {
        if(teamService.addCourse(courseWithTeacher.getCourseDTO())) {
            try {
                teamService.addTeacherToCourse(courseWithTeacher.getTeacherId(), courseWithTeacher.getCourseDTO().getName());
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, courseWithTeacher.getTeacherId());
            }
            return ModelHelper.enrich(courseWithTeacher.getCourseDTO());
        }
        else
            throw new ResponseStatusException(HttpStatus.CONFLICT, courseWithTeacher.getCourseDTO().getName());
    }

    @GetMapping("/{courseName}/addTeacher/{teacherId}")
    public List<TeacherDTO> addTeacherToCourse (@PathVariable String courseName, @PathVariable String teacherId)
            throws ResponseStatusException {
        try {
            teamService.addTeacherToCourse(teacherId, courseName);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, teacherId);
        }
        return teamService
                .getTeachersForCourse(courseName)
                .stream()
                .map(ModelHelper :: enrich)
                .collect(Collectors.toList());
    }

    @PostMapping({"/{name}/enrollOne"})
    public List<StudentDTO> enrollOne(@PathVariable String name, @RequestBody StudentDTO studentDTO)
            throws ResponseStatusException {
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

    @PostMapping("/{name}/editName")
    public CourseDTO editName(@PathVariable String name, @RequestBody String newName) throws ResponseStatusException {
        try {
            teamService.editCourseName(name, newName);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, name);
        }
        return ModelHelper.enrich(teamService.getCourse(newName).get());
    }

    @GetMapping("/{courseName}/delete")
    public List<CourseDTO> delete (@PathVariable String courseName) throws ResponseStatusException {
        try {
            teamService.deleteCourse(courseName);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, courseName);
        }

        return teamService.getAllCourses();
    }

    @GetMapping("/{courseName}/getAvailableStudents")
    public List<StudentDTO> getAvailableStudents (@PathVariable String courseName) {
        return teamService
                .getAvailableStudents(courseName)
                .stream()
                .map(ModelHelper :: enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{courseName}/setMin/{value}")
    public CourseDTO setMin (@PathVariable String courseName, @PathVariable int value) throws ResponseStatusException {

        try {
            teamService.setMinForCourse(value, courseName);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

        return ModelHelper.enrich(
            teamService
            .getCourse(courseName)
            .get()
        );
    }

    @GetMapping("/{courseName}/setMax/{value}")
    public CourseDTO setMax (@PathVariable String courseName, @PathVariable int value) throws ResponseStatusException {

        try {
            teamService.setMaxForCourse(value, courseName);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

        return ModelHelper.enrich(
                teamService
                        .getCourse(courseName)
                        .get()
        );
    }

    @GetMapping("/{courseName}/setMin/{value}")
    public CourseDTO setEnabled (@PathVariable String courseName, @PathVariable Boolean value) throws ResponseStatusException {

        if(!(value.equals(true) || value.equals(false)))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bad input");

        try {
            if(value)
                teamService.enableCourse(courseName);
            else
                teamService.disableCourse(courseName);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

        return ModelHelper.enrich(
                teamService
                        .getCourse(courseName)
                        .get()
        );
    }

}
