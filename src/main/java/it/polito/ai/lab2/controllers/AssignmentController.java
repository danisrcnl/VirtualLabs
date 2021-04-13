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

    @PostMapping("{courseName}/{assignmentId}/{studentId}/addPaper")
    public PaperDTO addPaper (@RequestBody PaperDTO paperDTO, @PathVariable Long assignmentId, @PathVariable String courseName, @PathVariable String studentId)
                throws ResponseStatusException {
        Long paperId;
        try {
            paperId = assignmentService.addPaper(paperDTO, courseName, studentId);
            assignmentService.linkPaperToAssignment(paperId, assignmentId);
            assignmentService.initializePaperStatus(paperId);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return ModelHelper.enrich(
                assignmentService
                .getPaper(paperId)
                .get()
        );
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

    @GetMapping("/paper/{paperId}/lock")
    public PaperDTO lockPaper (@PathVariable Long paperId) throws ResponseStatusException {
        try {
            assignmentService.lockPaper(paperId);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return ModelHelper.enrich(
                assignmentService
                .getPaper(paperId)
                .get()
        );
    }

    @GetMapping("/paper/{paperId}/read")
    public PaperDTO readPaper (@PathVariable Long paperId) throws ResponseStatusException {
        try {
            if(!assignmentService.readPaper(paperId))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The paper can't be edited anymore");
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return ModelHelper.enrich(
                assignmentService
                        .getPaper(paperId)
                        .get()
        );
    }

    @GetMapping("/paper/{paperId}/review")
    public PaperDTO reviewPaper (@PathVariable Long paperId) throws ResponseStatusException {
        try {
            if(!assignmentService.reviewPaper(paperId))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The paper can't be edited anymore");
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return ModelHelper.enrich(
                assignmentService
                        .getPaper(paperId)
                        .get()
        );
    }

    @GetMapping("/paper/{paperId}/deliver")
    public PaperDTO deliverPaper (@PathVariable Long paperId) throws ResponseStatusException {
        try {
            if(!assignmentService.deliverPaper(paperId))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The paper can't be edited anymore");
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return ModelHelper.enrich(
                assignmentService
                        .getPaper(paperId)
                        .get()
        );
    }

    @GetMapping("/paper/{paperId}/rate/{mark}")
    public PaperDTO ratePaper (@PathVariable Long paperId, @PathVariable int mark) throws ResponseStatusException {
        if (mark < 0 || mark > 30)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mark must be at least 0, no more than 30");
        try {
            if(!assignmentService.ratePaper(paperId, mark))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The paper can't be edited anymore");
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return ModelHelper.enrich(
                assignmentService
                        .getPaper(paperId)
                        .get()
        );
    }

    @PostMapping("/paper/{paperId}/setContent")
    public PaperDTO deliverPaper (@PathVariable Long paperId, @RequestBody String content) throws ResponseStatusException {
        try {
            if(!assignmentService.setPaperContent(paperId, content))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The paper can't be edited anymore");
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return ModelHelper.enrich(
                assignmentService
                        .getPaper(paperId)
                        .get()
        );
    }

    @GetMapping("/paper/{paperId}/getHistory")
    public List<PaperStatusTimeDTO> getPaperHistory (@PathVariable Long paperId) throws ResponseStatusException {
        List<PaperStatusTimeDTO> outcome;
        try {
            outcome = assignmentService.getPaperHistory(paperId);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return outcome;
    }


}
