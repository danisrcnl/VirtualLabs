package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeacherDTO;
import it.polito.ai.lab2.dtos.TeamDTO;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface TeamService {

    /* corso */

    boolean addCourse(CourseDTO course);

    Optional<CourseDTO> getCourse(String name);

    List<CourseDTO> getAllCourses();

    boolean addStudentToCourse(String studentId, String courseName);

    boolean addTeacherToCourse(String teacherId, String courseName);

    void enableCourse(String courseName);

    void disableCourse(String courseName);

    List<Boolean> enrollAll(List<String> studentIds, String courseName);

    List<Boolean> addAndEnroll(Reader r, String courseName);

    /* studente */

    boolean addStudent(StudentDTO student);

    Optional<StudentDTO> getStudent(String studentId);

    List<StudentDTO> getAllStudents();

    List<StudentDTO> getEnrolledStudents(String courseName);

    /* docente */

    boolean addTeacher(TeacherDTO teacher);

    Optional<TeacherDTO> getTeacher(String teacherId);

    List<TeacherDTO> getAllTeachers();

    List<TeacherDTO> getTeachersForCourse(String courseName);


    List<Boolean> addAll(List<StudentDTO> students);

    void activateTeam(String teamId);

    void evictTeam(String teamId);

    List<CourseDTO> getTeacherCourses(String teacherId);

    void setMinForCourse(int value, String courseName);

    void setMaxForCourse(int value, String courseName);

    /* studente */

    List<CourseDTO> getCourses(String studentId);

    TeamDTO getTeamForStudent(String studentId);

    List<StudentDTO> getMembers(String teamId);

    TeamDTO proposeTeam(String courseId, String name, List<String> memberIds);

    List<TeamDTO> getTeamForCourse(String courseName);

    List<StudentDTO> getStudentsInTeams(String courseName);

    List<StudentDTO> getAvailableStudents(String courseName);

    int getUsedNVCpuForTeam(String teamName); // ultimi tre metodi da testare

    int getUsedDiskForTeam(String teamName);

    int getUsedRamForTeam(String teamName);
}
