package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface TeamService {

    /* docente */

    boolean addCourse(CourseDTO course);

    Optional<CourseDTO> getCourse(String name);

    List<CourseDTO> getAllCourses();

    boolean addStudent(StudentDTO student);

    Optional<StudentDTO> getStudent(String studentId);

    List<StudentDTO> getAllStudents();

    List<StudentDTO> getEnrolledStudents(String courseName);

    boolean addStudentToCourse(String studentId, String courseName);

    void enableCourse(String courseName);

    void disableCourse(String courseName);

    List<Boolean> addAll(List<StudentDTO> students);

    List<Boolean> enrollAll(List<String> studentIds, String courseName);

    List<Boolean> addAndEnroll(Reader r, String courseName);

    void activateTeam(Long teamId);

    void evictTeam(Long teamId);

    /* studente */

    List<CourseDTO> getCourses(String studentId);

    List<TeamDTO> getTeamsForStudent(String studentId);

    List<StudentDTO> getMembers(Long teamId);

    TeamDTO proposeTeam(String courseId, String name, List<String> memberIds);

    List<TeamDTO> getTeamForCourse(String courseName);

    List<StudentDTO> getStudentsInTeams(String courseName);

    List<StudentDTO> getAvailableStudents(String courseName);
}
