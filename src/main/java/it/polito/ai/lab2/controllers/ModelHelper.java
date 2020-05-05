package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.CourseDTO;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

public class ModelHelper {

    public static CourseDTO enrich(CourseDTO courseDTO) {
        Link link = linkTo(methodOn(CourseController.class).getOne(courseDTO.getName())).withSelfRel();
        courseDTO.add(link);
        return courseDTO;
    }

}
