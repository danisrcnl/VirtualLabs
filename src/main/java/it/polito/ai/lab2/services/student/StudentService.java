package it.polito.ai.lab2.services.student;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.dtos.VmDTO;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    boolean addStudent (StudentDTO student);

    Optional<StudentDTO> getStudent (String studentId);

    List<StudentDTO> getAllStudents ();

    List<CourseDTO> getCoursesForStudent (String studentId);

    List<Boolean> addAll (List<StudentDTO> students);

    List<TeamDTO> getTeamsForStudent (String studentId);

    List<VmDTO> getVmsForStudent (String studentId);
}
