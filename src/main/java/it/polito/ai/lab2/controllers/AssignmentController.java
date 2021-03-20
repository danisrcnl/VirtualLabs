package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.AssignmentDTO;
import it.polito.ai.lab2.dtos.PaperDTO;
import it.polito.ai.lab2.dtos.PaperStatusTimeDTO;
import it.polito.ai.lab2.services.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/assignments")
public class AssignmentController {

    @Autowired
    AssignmentService assignmentService;

    @GetMapping({"", "/"})
    public List<AssignmentDTO> all () {
        return assignmentService
                .getAllAssignments()
                .stream()
                .map(ModelHelper :: enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AssignmentDTO getOne (@PathVariable Long id) throws ResponseStatusException {
        Optional<AssignmentDTO> outcome = assignmentService.getAssignment(id);
        if(outcome.isPresent())
            return ModelHelper.enrich(outcome.get());
        throw new ResponseStatusException(HttpStatus.CONFLICT, id.toString());
    }

    @GetMapping("/paper/{id}")
    public PaperDTO getPaper (@PathVariable Long id) {
        Optional<PaperDTO> outcome = assignmentService.getPaper(id);
        if(outcome.isPresent())
            return ModelHelper.enrich(outcome.get());
        throw new ResponseStatusException(HttpStatus.CONFLICT, id.toString());
    }

    @GetMapping("/{courseName}/getAssignments")
    public List<AssignmentDTO> getCourseAssignments (@PathVariable String courseName) throws ResponseStatusException {
        List<AssignmentDTO> outcome;
        try {
            outcome = assignmentService.getCourseAssignments(courseName);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, courseName);
        }

        return outcome
                .stream()
                .map(ModelHelper :: enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{assignmentId}/getPapers")
    public List<PaperDTO> getAssignmentPapers (@PathVariable Long assignmentId) throws ResponseStatusException {
        List<PaperDTO> outcome;
        try {
            outcome = assignmentService.getPapersForAssignment(assignmentId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, assignmentId.toString());
        }

        return outcome
                .stream()
                .map(ModelHelper :: enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{paperId}/getHistory")
    public List<PaperStatusTimeDTO> getPaperHistory (@PathVariable Long paperId) {
        List<PaperStatusTimeDTO> outcome;
        try {
            outcome = assignmentService.getPaperHistory(paperId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, paperId.toString());
        }

        return outcome;
    }
}
