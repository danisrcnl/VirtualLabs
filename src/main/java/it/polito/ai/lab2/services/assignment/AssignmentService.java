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


    List<AssignmentDTO> getCourseAssignments (String courseName);

    Long addPaper (PaperDTO paper, String courseName, String studentId, Long assignmentId);

    boolean linkPaperToAssignment (Long paperId, Long assignmentId);

    Optional<PaperDTO> getPaper (Long id);

    List<PaperDTO> getPapersForStudent (String courseName, String studentId);

    List<PaperDTO> getPapersForAssignment (Long id);

    List<PaperDTO> getAllPapers ();

    boolean ratePaper (Long paperId, int mark);

    String initializePaperStatus (Long paperId);

    String readPaper (Long paperId);

    void reviewPaper (Long paperId);

    void deliverPaper (Long paperId);

    void setPaperContent (Long paperId, String content);


    Long addPaperStatusTime (PaperStatusTimeDTO paperStatusTimeDTO, Long paperId);

    List<PaperStatusTimeDTO> getPaperHistory (Long paperId);

}
