package it.polito.ai.lab2.services.student;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.dtos.VmDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    boolean addStudent (StudentDTO student);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    Optional<StudentDTO> getStudent (String studentId);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    List<StudentDTO> getAllStudents ();

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    List<CourseDTO> getCoursesForStudent (String studentId);

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    List<Boolean> addAll (List<StudentDTO> students);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    List<TeamDTO> getTeamsForStudent (String studentId);

    @PreAuthorize("authentication.name == 's' + #studentId + '@studenti.polito.it' or hasRole('ROLE_TEACHER')")
    List<VmDTO> getVmsForStudent (String studentId);

    void linkToUser (String studentId, String userId);
}
