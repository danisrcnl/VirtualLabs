package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.AssignmentDTO;
import it.polito.ai.lab2.dtos.PaperDTO;

import java.util.List;
import java.util.Optional;

public interface AssignmentService {

    void addAssignment(AssignmentDTO assignment);

    Optional<AssignmentDTO> getAssignment(String id);

    List<AssignmentDTO> getAllAssignments();



    void addPaper(PaperDTO paper);

    Optional<PaperDTO> getPaper(String id);

    Optional<PaperDTO> getPaperForTeam(String paperId, String teamId);

    List<PaperDTO> getAllPapers();

    void ratePaper(String paperId, int mark);

    String readPaper(String paperId);

    void reviewPaper(String paperId);

    void deliverPaper(String paperId);

}
