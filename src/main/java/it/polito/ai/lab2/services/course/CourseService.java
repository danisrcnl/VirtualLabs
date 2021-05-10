package it.polito.ai.lab2.services.course;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeacherDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface CourseService {

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    boolean addCourse (CourseDTO course);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    Optional<CourseDTO> getCourse (String name);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    List<CourseDTO> getAllCourses ();

    @PreAuthorize("hasRole('ROLE_COURSE_' + #courseName + '_TEACHER')")
    boolean addStudentToCourse (String studentId, String courseName);

    @PreAuthorize("hasRole('ROLE_COURSE_' + #courseName + '_TEACHER')")
    boolean evictOne (String studentId, String courseName);

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    boolean addTeacherToCourse (String teacherId, String courseName);

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    void enableCourse (String courseName);

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    void disableCourse (String courseName);

    @PreAuthorize("hasRole('ROLE_COURSE_' + #courseName + '_TEACHER')")
    void deleteCourse (String courseName);

    @PreAuthorize("hasRole('ROLE_COURSE_' + #courseName + '_TEACHER')")
    List<Boolean> enrollAll (List<String> studentIds, String courseName);

    @PreAuthorize("hasRole('ROLE_COURSE_' + #courseName + '_TEACHER')")
    List<Boolean> addAndEnroll (Reader r, String courseName);

    @PreAuthorize("hasAnyRole('ROLE_COURSE_' + #courseName + '_TEACHER', 'ROLE_COURSE_' + #courseName + '_STUDENT')")
    List<StudentDTO> getEnrolledStudents (String courseName);

    @PreAuthorize("hasAnyRole('ROLE_COURSE_' + #courseName + '_TEACHER', 'ROLE_COURSE_' + #courseName + '_STUDENT')")
    List<StudentDTO> getNotEnrolled (String courseName);

    @PreAuthorize("hasAnyRole('ROLE_COURSE_' + #courseName + '_TEACHER', 'ROLE_COURSE_' + #courseName + '_STUDENT')")
    List<TeacherDTO> getTeachersForCourse (String courseName);

    @PreAuthorize("hasRole('ROLE_COURSE_' + #courseName + '_TEACHER')")
    void setMinForCourse (int value, String courseName);

    @PreAuthorize("hasRole('ROLE_COURSE_' + #courseName + '_TEACHER')")
    void setMaxForCourse (int value, String courseName);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    List<TeamDTO> getStudentTeamInCourse (String id, String courseName);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    List<StudentDTO> getStudentsInTeams (String courseName);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    List<StudentDTO> getAvailableStudents (String courseName);

    @PreAuthorize("hasAnyRole('ROLE_COURSE_' + #courseName + '_TEACHER', 'ROLE_COURSE_' + #courseName + '_STUDENT')")
    List<TeamDTO> getTeamForCourse (String courseName);

    @PreAuthorize("hasAnyRole('ROLE_COURSE_' + #courseName + '_TEACHER', 'ROLE_COURSE_' + #courseName + '_STUDENT')")
    Boolean hasAlreadyATeamFor(String studentId, String courseName);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    CourseDTO getTeamCourse (int teamId);
}
