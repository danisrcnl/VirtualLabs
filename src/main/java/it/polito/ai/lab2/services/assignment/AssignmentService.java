package it.polito.ai.lab2.services.assignment;

import it.polito.ai.lab2.dtos.AssignmentDTO;
import it.polito.ai.lab2.dtos.PaperDTO;
import it.polito.ai.lab2.dtos.PaperStatusTimeDTO;
import it.polito.ai.lab2.entities.PaperStatusTime;

import java.util.List;
import java.util.Optional;

public interface AssignmentService {


    Long addAssignment (AssignmentDTO assignment);

    Boolean addAssignmentToCourse (Long assignmentId, String courseName);

    Optional<AssignmentDTO> getAssignment (Long id);

    List<AssignmentDTO> getAllAssignments ();

    AssignmentDTO setAssignmentContent (Long assignmentId, String content);


    List<AssignmentDTO> getCourseAssignments (String courseName);

    Long addPaper (PaperDTO paper, String courseName, String studentId);

    boolean linkPaperToAssignment (Long paperId, Long assignmentId);

    Optional<PaperDTO> getStudentPaper (Long assignmentId, String studentId);

    Optional<PaperDTO> getPaper (Long id);

    String getPaperCreator (Long id);

    List<PaperDTO> getPapersForStudentCourse (String courseName, String studentId);

    List<PaperDTO> getPapersForAssignment (Long id);

    List<PaperDTO> getAllPapers ();

    Boolean initializePaperStatus (Long paperId);

    void lockPaper (Long paperId);

    Boolean readPaper (Long paperId);

    Boolean reviewPaper (Long paperId, String content);

    Boolean deliverPaper (Long paperId);

    Boolean ratePaper (Long paperId, int mark);

    Boolean setPaperContent (Long paperId, String content);


    Long addPaperStatusTime (PaperStatusTimeDTO paperStatusTimeDTO, Long paperId);

    List<PaperStatusTimeDTO> getPaperHistory (Long paperId);

}
