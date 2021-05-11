/*
 * TeamController.java:
 * Classe deputata a offrire APIs accessibili tramite richieste HTTP per la gestione dei Team, entità formate
 * da più studenti, nell'ambito di un corso.
 * */

package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dataStructures.MemberStatus;
import it.polito.ai.lab2.dataStructures.TeamRequest;
import it.polito.ai.lab2.dataStructures.UsedResources;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.services.AiException;
import it.polito.ai.lab2.services.course.CourseService;
import it.polito.ai.lab2.services.notification.NotificationService;
import it.polito.ai.lab2.services.team.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/API/teams")
public class TeamController {

    @Autowired
    TeamService teamService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    CourseService courseService;

    /*
    * Ritorna tutti i teams presenti nel database.
    * */
    @GetMapping("/course/{courseName}")
    public List<TeamDTO> all (@PathVariable String courseName) {
        return courseService
                .getTeamForCourse(courseName)
                .stream()
                .map(ModelHelper :: enrich)
                .collect(Collectors.toList());
    }

    /*
    * Ritorna il team con id pari a quello indicato.
    * */
    @GetMapping("/{id}")
    public TeamDTO getOne (@PathVariable int id) throws ResponseStatusException {
        TeamDTO teamDTO;
        try {
            teamDTO = teamService.getTeamById(id);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }
        return ModelHelper.enrich(teamDTO);
    }

    /*
    * Ritorna il team col nome indicato nell'ambito di un determinato corso.
    * */
    @GetMapping("/{courseName}/{teamName}")
    public TeamDTO getOneByName(@PathVariable String courseName, @PathVariable String teamName) throws ResponseStatusException {
        TeamDTO teamDTO;
        try {
            teamDTO = teamService.getTeam(courseName, teamName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }
        return ModelHelper.enrich(teamDTO);
    }

    /*
    * Restituisce una lista di studenti, membri del gruppo identificato dalla coppia courseName-teamName.
    * */
    @GetMapping("/{courseName}/{teamName}/members")
    public List<StudentDTO> getMembers(@PathVariable String courseName, @PathVariable String teamName) throws ResponseStatusException {
        List<StudentDTO> students;
        try {
            students = teamService.getMembers(courseName, teamName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }
        return students
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    /*
    * Metodo che torna una lista di MemberStatus, ossia informazioni relative ai membri di un team proposto, insieme
    * allo stato di accettazione o meno dell'invito ricevuto.
    * */
    @GetMapping("/{courseName}/{teamName}/membersStatus")
    public List<MemberStatus> getMembersStatus(@PathVariable String courseName, @PathVariable String teamName) throws ResponseStatusException {
        int id;
        try {
            id = teamService.getTeamId(courseName, teamName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }
        return notificationService.getMembersStatus(id);
    }

    /*
    * Il metodo offre la possibilità di aggiungere un nuovo team. Si articola in una prima fase per cui viene creata
    * la proposta di team. Dopo di ciò, tutti i team in cui il creatore è stato invitato vengono "uccisi" in quanto
    * colui che crea il team ne ha automaticamente accettato l'invito. Dopo di ciò tutti i restanti membri della
    * proposta di team vengono informati con una email della proposta ricevuta.
    * */
    @PostMapping("/{courseName}/add")
    public TeamDTO addTeam(@PathVariable String courseName, @RequestBody TeamRequest teamRequest) throws ResponseStatusException {
        String teamName = teamRequest.getTeamName();
        String creator = teamRequest.getCreator();
        List<String> memberIds = teamRequest.getMemberIds();
        int hours = teamRequest.getHours();

        try {
            TeamDTO teamDTO = teamService.proposeTeam(courseName, teamName, memberIds, creator);
            notificationService.deleteOtherTeams(teamDTO.getId(), creator);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        memberIds.remove(creator);
        notificationService.notifyTeam(courseName, teamName, memberIds, hours);
        return ModelHelper.enrich(
                        teamService
                        .getTeam(courseName, teamName)
                );
    }

    /*
    * Il metodo offre la possibilità di avere informazioni circa l'attuale consumo di risorse legate alle virtual
    * machines per un certo corso, di un certo team.
    * */
    @GetMapping("/{courseName}/{teamName}/getUsedResources")
    public UsedResources getUsedResources (@PathVariable String courseName, @PathVariable String teamName) throws ResponseStatusException {
        try {
            teamService.getTeam(courseName, teamName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }
        UsedResources usedResources = new UsedResources(
                teamService.getUsedNVCpuForTeam(courseName, teamName),
                teamService.getUsedRamForTeam(courseName, teamName),
                teamService.getUsedDiskForTeam(courseName, teamName)
        );
        return usedResources;
    }

}
