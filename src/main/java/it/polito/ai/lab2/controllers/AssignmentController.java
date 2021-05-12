/*
* AssignmentController.java:
* Classe deputata a offrire APIs accessibili tramite richieste HTTP per la gestione di Assignments (consegne
* create dal docente) e Papers (elaborati presentati dagli studenti)
* */

package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.AssignmentDTO;
import it.polito.ai.lab2.dtos.PaperDTO;
import it.polito.ai.lab2.dtos.PaperStatusTimeDTO;
import it.polito.ai.lab2.services.image.ImageService;
import it.polito.ai.lab2.services.AiException;
import it.polito.ai.lab2.services.assignment.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/API/assignments")
public class AssignmentController {

    @Autowired
    AssignmentService assignmentService;
    @Autowired
    ImageService imageService;

    /*
    * Metodo che ritorna tutti gli Assignments presenti nel database
    * */
    @GetMapping({"", "/"})
    public List<AssignmentDTO> all () {
        return assignmentService
                .getAllAssignments()
                .stream()
                .map(ModelHelper :: enrich)
                .collect(Collectors.toList());
    }

    /*
    * Il metodo crea un nuovo Assignment, secondo i parametri ricevuti e lo assegna al corso desiderato. Internamente, il
    * metodo di servizio chiamato (addAssignmentToCourse) si incaricherà di creare un paper relativo ad ogni studente iscritto
    * al corso, inizializzato a NULL.
    * */
    @PostMapping("/{courseName}")
    public Long addAssignmentToCourse (@PathVariable String courseName, @RequestBody AssignmentDTO assignmentDTO) throws ResponseStatusException {

        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Paris"));
        Timestamp t = Timestamp.valueOf(localDateTime);
        assignmentDTO.setCreationDate(t);

        if (t.compareTo(assignmentDTO.getExpiryDate()) > 0)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La data di scadenza non può essere inferiore a quella odierna");


        Long id = assignmentService.addAssignment(assignmentDTO);

        try {
            assignmentService.addAssignmentToCourse(id, courseName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }
        return id;
    }

    /*
    * Metodo che si interfaccia con l'ImageService per caricare nel filesystem del server l'immagine ricevuta in input
    * ed assegnarla all'assignment desiderato per farne da contenuto.
    * */
    @PostMapping("/{assignmentId}/setContent")
    public String setAssignmentContent (@PathVariable Long assignmentId , @RequestParam("file") MultipartFile multipartFile) throws ResponseStatusException {
        if(!multipartFile.getContentType().equals("image/jpeg") && !multipartFile.getContentType().equals("image/png"))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "L'immagine deve essere jpg, jpeg o png");
        String subfolder = "assignments";
        String name = assignmentId.toString();
        String value = "";
        try {
            value = imageService.uploadImage(multipartFile, subfolder, name);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Problems with image");
        }
        try {
            assignmentService.setAssignmentContent(assignmentId, value);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }
        return value;
    }

    /*
     * Metodo che si interfaccia con l'ImageService per caricare nel filesystem del server l'immagine ricevuta in input
     * ed assegnarla al paper desiderato per farne da contenuto. Perchè funzioni, è necessario che il Paper non sia
     * nella sua versione finale (la proprietà "editable" deve essere settata a true). A cascata chiama la deliverPaper,
     * metodo dell'assignmentService che aggiorna lo stato del paper come CONSEGNATO e ne salva il log nella sua storia.
     * */
    @PostMapping("/paper/{paperId}/setContent")
    public String setPaperContent (@PathVariable Long paperId , @RequestParam("file") MultipartFile multipartFile) throws ResponseStatusException {
        if(!multipartFile.getContentType().equals("image/jpeg") && !multipartFile.getContentType().equals("image/png"))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "L'immagine deve essere jpg, jpeg o png");
        String subfolder = "papers";
        String name = paperId.toString();
        String value = "";
        try {
            value = imageService.uploadImage(multipartFile, subfolder, name);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Problems with image");
        }
        try {
            if(!assignmentService.setPaperContent(paperId, value))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The paper can't be edited anymore");
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        try {
            if(!assignmentService.deliverPaper(paperId))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The paper can't be edited anymore");
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return value;
    }


    /*
    * Metodo che ritorna l'assignment con id specificato nella richiesta.
    * */
    @GetMapping("/{id}")
    public AssignmentDTO getOne (@PathVariable Long id) throws ResponseStatusException {
        Optional<AssignmentDTO> outcome = assignmentService.getAssignment(id);
        if(outcome.isPresent())
            return ModelHelper.enrich(outcome.get());
        throw new ResponseStatusException(HttpStatus.CONFLICT, "No assignments with id " + id.toString());
    }

    /*
     * Metodo che ritorna il paper con id specificato nella richiesta.
     * */
    @GetMapping("/paper/{id}")
    public PaperDTO getPaper (@PathVariable Long id) {
        Optional<PaperDTO> outcome = assignmentService.getPaper(id);
        if(outcome.isPresent())
            return ModelHelper.enrich(outcome.get());
        throw new ResponseStatusException(HttpStatus.CONFLICT, "No papers with id " + id.toString());
    }

    /*
    * Il metodo ritorna lo studente creatore del paper con id pari a quello inserito come parametro nella richiesta.
    * */
    @GetMapping("/paper/{id}/creator")
    public String getPaperCreator (@PathVariable Long id) throws ResponseStatusException {
        Optional<PaperDTO> outcome = assignmentService.getPaper(id);
        String creator;
        if(outcome.isPresent()) {
            try {
                creator = assignmentService.getPaperCreator(id);
            } catch (AiException e) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
            }
        } else
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No papers with id " + id.toString());

        return creator;
    }

    /*
    * Metodo che ritorna tutti gli assignments relativi al corso desiderato.
    * */
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

    /*
    * Metodo che crea un nuovo paper, lo assegna all'assignment cui si riferisce e ne inizializza lo stato.
    * */
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

    /*
    * Metodo che ritorna tutti i papers relativi a un determinato assignment.
    * */
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

    /*
    * Metodo che ritorna tutti i papers relativi a un determinato studente per un determinato corso.
    * */
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

    /*
    * Metodo che "blocca" un determinato paper, andando a renderlo non più editabile.
    * */
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

    /*
    * Il metodo, se chiamato, aggiorna lo stato di un paper a LETTO.
    * */
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

    /*
     * Il metodo, se chiamato, aggiorna lo stato di un paper a RIVISTO. Accetta eventualmente un contenuto che contiene
     * le direttive del docente circa la soluzione rivista, ai fini di informare lo studente circa le modifiche da
     * apportare nella successiva consegna.
     * */
    @PostMapping("/paper/{paperId}/review")
    public PaperDTO reviewPaper (@PathVariable Long paperId, @RequestBody String content) throws ResponseStatusException {
        try {
            if(!assignmentService.reviewPaper(paperId, content))
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

    /*
     * Il metodo, se chiamato, aggiorna lo stato di un paper a CONSEGNATO.
     * */
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

    /*
    * Il metodo permette al docente di assegnare un voto a un paper non più editabile.
    * */
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

    /*
    * Metodo che ritorna la storia del paper desiderato, con data e ora di lettura, consegne, revisioni e valutazione.
    * */
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

    /*
    * Metodo che torna il paper di uno studente relativo a un determinato assignment.
    * */
    @GetMapping("/{assignmentId}/{studentId}/getPapersStudent")
    public PaperDTO getPaperStudent (@PathVariable Long assignmentId, @PathVariable String studentId) throws ResponseStatusException {
        Optional<PaperDTO> outcome = assignmentService.getStudentPaper(assignmentId, studentId);
        if (!outcome.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No papers for student " + studentId);
        return outcome.get();
    }


}
