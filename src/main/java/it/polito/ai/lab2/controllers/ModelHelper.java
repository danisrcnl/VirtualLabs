package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeacherDTO;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

public class ModelHelper {

    public static CourseDTO enrich(CourseDTO courseDTO) {
        Link self = linkTo(methodOn(CourseController.class).getOne(courseDTO.getName())).withSelfRel();
        Link enrolled = linkTo(methodOn(CourseController.class).enrolledStudents(courseDTO.getName())).withRel("enrolled");
        courseDTO.add(self);
        courseDTO.add(enrolled);
        return courseDTO;
    }

    public static StudentDTO enrich(StudentDTO studentDTO) {
        Link link = linkTo(methodOn(StudentController.class).getOne(studentDTO.getId())).withSelfRel();
        studentDTO.add(link);
        return studentDTO;
    }

    public static TeacherDTO enrich(TeacherDTO teacherDTO) {
        Link link = linkTo(methodOn(TeacherController.class).getOne(teacherDTO.getId())).withSelfRel();
        teacherDTO.add(link);
        return teacherDTO;
    }
}
