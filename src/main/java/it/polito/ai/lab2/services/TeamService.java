package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.*;

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

    void activateTeamById(int id);

    void activateTeam(String courseName, String teamName);

    void evictTeamById(int id);
    void evictTeam(String courseName, String teamName);

    List<CourseDTO> getTeacherCourses(String teacherId);

    void setMinForCourse(int value, String courseName);

    void setMaxForCourse(int value, String courseName);

    /* studente */

    TeamDTO getTeam(String courseName, String teamName);

    List<CourseDTO> getCourses(String studentId);

    List<TeamDTO> getTeamsForStudent(String studentId);

    List<StudentDTO> getMembersById(int id);

    List<StudentDTO> getMembers(String courseName, String teamName);

    Boolean hasAlreadyATeamFor(String studentId, String courseName);

    TeamDTO proposeTeam(String courseName, String teamName, List<String> memberIds);

    int getTeamId(String courseName, String teamName);

    List<TeamDTO> getTeamForCourse(String courseName);

    List<StudentDTO> getStudentsInTeams(String courseName);

    List<StudentDTO> getAvailableStudents(String courseName);

    int getUsedNVCpuForTeam(String courseName, String teamName);

    int getUsedDiskForTeam(String courseName, String teamName);

    int getUsedRamForTeam(String courseName, String teamName);

    List<VmDTO> getVmsForStudent(String studentId);
}
