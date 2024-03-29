/*
 * TeacherController.java:
 * Classe deputata a offrire APIs accessibili tramite richieste HTTP per la gestione dei docenti e per
 * l'accesso a loro proprietà e relazioni.
 * */

package it.polito.ai.lab2.controllers;


import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.TeacherDTO;
import it.polito.ai.lab2.services.AiException;
import it.polito.ai.lab2.services.teacher.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/API/teachers")
public class TeacherController {

    @Autowired
    TeacherService teacherService;

    /*
    * Metodo che torna tutti i docenti presenti nel database.
    * */
    @GetMapping({"", "/"})
    public List<TeacherDTO> all () {
        return teacherService
                .getAllTeachers()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    /*
    * Metodo che torna il docente con id pari a quello indicato.
    * */
    @GetMapping("/{id}")
    public TeacherDTO getOne (@PathVariable String id) throws ResponseStatusException {
        Optional<TeacherDTO> teacher = teacherService.getTeacher(id);
        if(teacher.isPresent())
            return ModelHelper.enrich(teacher.get());
        else
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Couldn't find teacher with id: " + id);
    }

    /*
    * Metodo che torna tutti i corsi tenuti dal docente con id pari a quello indicato.
    * */
    @GetMapping("/{id}/getCourses")
    public List<CourseDTO> getTeacherCourses (@PathVariable String id) throws ResponseStatusException {
        List<CourseDTO> teacherCourses;
        try {
            teacherCourses = teacherService.getCoursesForTeacher(id);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }
        return teacherCourses
                .stream()
                .map(ModelHelper :: enrich)
                .collect(Collectors.toList());
    }

}
