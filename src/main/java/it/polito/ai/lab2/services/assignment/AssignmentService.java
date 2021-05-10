package it.polito.ai.lab2.services.assignment;

import it.polito.ai.lab2.dtos.AssignmentDTO;
import it.polito.ai.lab2.dtos.PaperDTO;
import it.polito.ai.lab2.dtos.PaperStatusTimeDTO;
import it.polito.ai.lab2.entities.PaperStatusTime;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface AssignmentService {

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    Long addAssignment (AssignmentDTO assignment);

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    Boolean addAssignmentToCourse (Long assignmentId, String courseName);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    Optional<AssignmentDTO> getAssignment (Long id);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    List<AssignmentDTO> getAllAssignments ();

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    AssignmentDTO setAssignmentContent (Long assignmentId, String content);

    @PreAuthorize("hasAnyRole('ROLE_COURSE_' + #courseName + '_TEACHER', 'ROLE_COURSE_' + #courseName + '_STUDENT')")
    List<AssignmentDTO> getCourseAssignments (String courseName);

    @PreAuthorize("authentication.name == 's' + #studentId + '@studenti.polito.it'")
    Long addPaper (PaperDTO paper, String courseName, String studentId);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    boolean linkPaperToAssignment (Long paperId, Long assignmentId);

    @PreAuthorize("authentication.name == 's' + #studentId + '@studenti.polito.it' or hasRole('ROLE_TEACHER')")
    Optional<PaperDTO> getStudentPaper (Long assignmentId, String studentId);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    Optional<PaperDTO> getPaper (Long id);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    String getPaperCreator (Long id);

    @PreAuthorize("hasAnyRole('ROLE_COURSE_' + #courseName + '_TEACHER', 'ROLE_COURSE_' + #courseName + '_STUDENT')")
    List<PaperDTO> getPapersForStudentCourse (String courseName, String studentId);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    List<PaperDTO> getPapersForAssignment (Long id);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    List<PaperDTO> getAllPapers ();

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    Boolean initializePaperStatus (Long paperId);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    void lockPaper (Long paperId);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    Boolean readPaper (Long paperId);

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    Boolean reviewPaper (Long paperId, String content);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    Boolean deliverPaper (Long paperId);

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    Boolean ratePaper (Long paperId, int mark);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT')")
    Boolean setPaperContent (Long paperId, String content);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    Long addPaperStatusTime (PaperStatusTimeDTO paperStatusTimeDTO, Long paperId);

    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    List<PaperStatusTimeDTO> getPaperHistory (Long paperId);

}
