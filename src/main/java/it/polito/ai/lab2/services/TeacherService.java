package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.TeacherDTO;

import java.util.List;
import java.util.Optional;

public interface TeacherService {

    boolean addTeacher (TeacherDTO teacher);

    Optional<TeacherDTO> getTeacher (String teacherId);

    List<TeacherDTO> getAllTeachers ();

    List<CourseDTO> getCoursesForTeacher (String teacherId);

}
