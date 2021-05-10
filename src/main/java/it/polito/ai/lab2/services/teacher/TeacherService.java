package it.polito.ai.lab2.services.teacher;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.TeacherDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface TeacherService {

    boolean addTeacher (TeacherDTO teacher);

    Optional<TeacherDTO> getTeacher (String teacherId);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    List<TeacherDTO> getAllTeachers ();

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    List<CourseDTO> getCoursesForTeacher (String teacherId);

    void linkToUser (String teacherId, String userId);

}
