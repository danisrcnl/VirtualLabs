package it.polito.ai.lab2.services.course;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeacherDTO;
import it.polito.ai.lab2.dtos.TeamDTO;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface CourseService {

    boolean addCourse (CourseDTO course);

    Optional<CourseDTO> getCourse (String name);

    List<CourseDTO> getAllCourses ();

    boolean addStudentToCourse (String studentId, String courseName);

    boolean evictOne (String studentId, String courseName);

    boolean addTeacherToCourse (String teacherId, String courseName);

    void enableCourse (String courseName);

    void disableCourse (String courseName);

    void deleteCourse (String courseName);

    void editCourseName (String courseName, String newName);

    List<Boolean> enrollAll (List<String> studentIds, String courseName);

    List<Boolean> addAndEnroll (Reader r, String courseName);

    List<StudentDTO> getEnrolledStudents (String courseName);

    List<StudentDTO> getNotEnrolled (String courseName);

    List<TeacherDTO> getTeachersForCourse (String courseName);

    void setMinForCourse (int value, String courseName);

    void setMaxForCourse (int value, String courseName);

    List<TeamDTO> getStudentTeamInCourse (String id, String courseName);

    List<StudentDTO> getStudentsInTeams (String courseName);

    List<StudentDTO> getAvailableStudents (String courseName);

    List<TeamDTO> getTeamForCourse (String courseName);

    Boolean hasAlreadyATeamFor(String studentId, String courseName);

    CourseDTO getTeamCourse (int teamId);
}
