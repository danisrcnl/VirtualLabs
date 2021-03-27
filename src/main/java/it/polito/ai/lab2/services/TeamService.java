package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.*;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface TeamService {

    /* corso */

    boolean addCourse (CourseDTO course);

    Optional<CourseDTO> getCourse (String name);

    List<CourseDTO> getAllCourses ();

    boolean addStudentToCourse (String studentId, String courseName);

    boolean addTeacherToCourse (String teacherId, String courseName);

    void enableCourse (String courseName);

    void disableCourse (String courseName);

    void deleteCourse (String courseName);

    void editCourseName (String courseName, String newName);

    List<Boolean> enrollAll (List<String> studentIds, String courseName);

    List<Boolean> addAndEnroll (Reader r, String courseName);

    List<StudentDTO> getEnrolledStudents (String courseName);

    List<TeacherDTO> getTeachersForCourse (String courseName);

    void setMinForCourse (int value, String courseName);

    void setMaxForCourse (int value, String courseName);

    List<TeamDTO> getStudentTeamInCourse (String id, String courseName);

    List<StudentDTO> getStudentsInTeams (String courseName);

    List<StudentDTO> getAvailableStudents (String courseName);

    List<TeamDTO> getTeamForCourse (String courseName);




    /* studente */

    boolean addStudent (StudentDTO student);

    Optional<StudentDTO> getStudent (String studentId);

    List<StudentDTO> getAllStudents ();

    List<CourseDTO> getCoursesForStudent (String studentId);

    List<Boolean> addAll (List<StudentDTO> students);

    List<TeamDTO> getTeamsForStudent (String studentId);

    Boolean hasAlreadyATeamFor (String studentId, String courseName);

    List<VmDTO> getVmsForStudent (String studentId);




    boolean addTeacher (TeacherDTO teacher);

    Optional<TeacherDTO> getTeacher (String teacherId);

    List<TeacherDTO> getAllTeachers ();

    List<CourseDTO> getCoursesForTeacher (String teacherId);






    void activateTeamById (int id);

    void activateTeam (String courseName, String teamName);

    void evictTeamById (int id);

    void evictTeam (String courseName, String teamName);

    TeamDTO getTeam (String courseName, String teamName);

    TeamDTO getTeamById (int id);

    List<StudentDTO> getMembersById (int id);

    List<StudentDTO> getMembers (String courseName, String teamName);

    TeamDTO proposeTeam (String courseName, String teamName, List<String> memberIds);

    int getTeamId (String courseName, String teamName);

    int getUsedNVCpuForTeam (String courseName, String teamName);

    int getUsedDiskForTeam (String courseName, String teamName);

    int getUsedRamForTeam (String courseName, String teamName);

    List<VmDTO> getVmsForTeam (String courseName, String teamName);

    List<VmDTO> getVmsForTeamById (int id);



    UnknownUserDTO getDetailsFromUsername (String username);
}
