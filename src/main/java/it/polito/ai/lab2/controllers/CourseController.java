package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dataStructures.CourseWithTeacher;
import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeacherDTO;
import it.polito.ai.lab2.services.AiException;
import it.polito.ai.lab2.services.course.CourseService;
import org.modelmapper.ModelMapper;
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

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/API/courses")
public class CourseController {

    @Autowired
    CourseService courseService;

    @GetMapping({"", "/"})
    public List<CourseDTO> all() {
        return courseService
                .getAllCourses()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{name}")
    public CourseDTO getOne (@PathVariable String name) throws ResponseStatusException {
        Optional<CourseDTO> course = courseService.getCourse(name);
        if(course.isPresent())
            return ModelHelper.enrich(course.get());
        else
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course " + name + " doesn't exist");
    }

    @GetMapping("/{name}/enrolled")
    public List<StudentDTO> enrolledStudents (@PathVariable String name) throws ResponseStatusException {
        List<StudentDTO> enrolled;
        try {
            enrolled = courseService.getEnrolledStudents(name);
        } catch(AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }
        return enrolled
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @PostMapping({"", "/"})
    public CourseDTO addCourse (@RequestBody CourseWithTeacher courseWithTeacher)
            throws ResponseStatusException {
        if(courseService.addCourse(courseWithTeacher.getCourseDTO())) {
            try {
                courseService.addTeacherToCourse(courseWithTeacher.getTeacherId(), courseWithTeacher.getCourseDTO().getName());
            } catch (AiException e) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
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
            courseService.addTeacherToCourse(teacherId, courseName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }
        return courseService
                .getTeachersForCourse(courseName)
                .stream()
                .map(ModelHelper :: enrich)
                .collect(Collectors.toList());
    }

    @PostMapping({"/{name}/enrollOne"})
    public List<StudentDTO> enrollOne (@PathVariable String name, @RequestBody StudentDTO studentDTO)
            throws ResponseStatusException {
        Optional<CourseDTO> course = courseService.getCourse(name);
        if(!course.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course " + name + " doesn't exist");
        if(!courseService.addStudentToCourse(studentDTO.getId(), name))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Couldn't enroll student");
        return enrolledStudents(name);
    }

    @PostMapping("{name}/enrollMany")
    public List<StudentDTO> enrollMany (@PathVariable String name, @RequestParam("file") MultipartFile multipartFile)
                                                            throws UnsupportedMediaTypeStatusException {
        if(!multipartFile.getContentType().equals("text/csv"))
            throw new UnsupportedMediaTypeStatusException(multipartFile.getContentType());
        try {
            Reader reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()));
            courseService.addAndEnroll(reader, name);
        } catch(Exception e) {
            System.out.println("Exception generated in reader block");
        }
        return enrolledStudents(name);
    }
/*
    @PostMapping("/{name}/editName")
    public CourseDTO editName (@PathVariable String name, @RequestBody String newName) throws ResponseStatusException {
        try {
            courseService.editCourseName(name, newName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }
        return ModelHelper.enrich(courseService.getCourse(newName).get());
    }
*/
    @GetMapping("/{courseName}/delete")
    public List<CourseDTO> delete (@PathVariable String courseName) throws ResponseStatusException {
        try {
            courseService.deleteCourse(courseName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return courseService.getAllCourses();
    }

    @GetMapping("/{courseName}/getAvailableStudents")
    public List<StudentDTO> getAvailableStudents (@PathVariable String courseName) {
        return courseService
                .getAvailableStudents(courseName)
                .stream()
                .map(ModelHelper :: enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{courseName}/setMin/{value}")
    public CourseDTO setMin (@PathVariable String courseName, @PathVariable int value) throws ResponseStatusException {

        try {
            courseService.setMinForCourse(value, courseName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return ModelHelper.enrich(
            courseService
            .getCourse(courseName)
            .get()
        );
    }

    @GetMapping("/{courseName}/setMax/{value}")
    public CourseDTO setMax (@PathVariable String courseName, @PathVariable int value) throws ResponseStatusException {

        try {
            courseService.setMaxForCourse(value, courseName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return ModelHelper.enrich(
                courseService
                        .getCourse(courseName)
                        .get()
        );
    }

    @GetMapping("/{courseName}/setEnabled/{value}")
    public CourseDTO setEnabled (@PathVariable String courseName, @PathVariable Boolean value) throws ResponseStatusException {

        if(!(value.equals(true) || value.equals(false)))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bad input");

        try {
            if(value)
                courseService.enableCourse(courseName);
            else
                courseService.disableCourse(courseName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return ModelHelper.enrich(
                courseService
                        .getCourse(courseName)
                        .get()
        );
    }

    @GetMapping("/{courseName}/{studentId}/evict")
    public List<StudentDTO> evictOne (@PathVariable String studentId, @PathVariable String courseName) throws ResponseStatusException {

        try {
            courseService.evictOne(studentId, courseName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return courseService
                .getEnrolledStudents(courseName)
                .stream()
                .map(ModelHelper :: enrich)
                .collect(Collectors.toList());

    }

}
