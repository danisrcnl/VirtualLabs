package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.AssignmentDTO;
import it.polito.ai.lab2.dtos.PaperDTO;
import it.polito.ai.lab2.dtos.PaperStatusTimeDTO;
import it.polito.ai.lab2.services.AiException;
import it.polito.ai.lab2.services.assignment.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/{courseName}")
    public void addAssignmentToCourse (@PathVariable String courseName, @RequestBody AssignmentDTO assignmentDTO) throws ResponseStatusException {

        Long id = assignmentService.addAssignment(assignmentDTO);

        try {
            assignmentService.addAssignmentToCourse(id, courseName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

    }

    @GetMapping("/{id}")
    public AssignmentDTO getOne (@PathVariable Long id) throws ResponseStatusException {
        Optional<AssignmentDTO> outcome = assignmentService.getAssignment(id);
        if(outcome.isPresent())
            return ModelHelper.enrich(outcome.get());
        throw new ResponseStatusException(HttpStatus.CONFLICT, "No assignments with id " + id.toString());
    }

    @GetMapping("/paper/{id}")
    public PaperDTO getPaper (@PathVariable Long id) {
        Optional<PaperDTO> outcome = assignmentService.getPaper(id);
        if(outcome.isPresent())
            return ModelHelper.enrich(outcome.get());
        throw new ResponseStatusException(HttpStatus.CONFLICT, "No papers with id " + id.toString());
    }

    @GetMapping("/{courseName}/getAssignments")
    public List<AssignmentDTO> getCourseAssignments (@PathVariable String courseName) throws ResponseStatusException {
        List<AssignmentDTO> outcome;
        try {
            outcome = assignmentService.getCourseAssignments(courseName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
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
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return outcome
                .stream()
                .map(ModelHelper :: enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{courseName}/{studentId}/getPapers")
    public List<PaperDTO> getStudentPapers (@PathVariable String courseName, @PathVariable String studentId) throws ResponseStatusException {
        List<PaperDTO> outcome;
        try {
            outcome = assignmentService.getPapersForStudentCourse(courseName, studentId);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
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
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return outcome;
    }


}
