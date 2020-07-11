package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.AssignmentDTO;
import it.polito.ai.lab2.dtos.PaperDTO;
import it.polito.ai.lab2.dtos.PaperStatusTimeDTO;

import java.util.List;
import java.util.Optional;

public interface AssignmentService {

    boolean addAssignment(AssignmentDTO assignment);

    Optional<AssignmentDTO> getAssignment(String id);

    List<AssignmentDTO> getAllAssignments();



    boolean addPaper(PaperDTO paper);

    boolean linkPaperToAssignment(String paperId, String assignmentId);

    Optional<PaperDTO> getPaper(String id);

    List<PaperDTO> getPapersForTeam(Long teamId);

    List<PaperDTO> getPapersForAssignment(String id);

    List<PaperDTO> getAllPapers();

    void ratePaper(String paperId, int mark);

    String readPaper(String paperId);

    void reviewPaper(String paperId);

    void deliverPaper(String paperId);


    boolean addPaperStatusTime(PaperStatusTimeDTO paperStatusTimeDTO, String paperId);

}
