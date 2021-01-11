package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dataStructures.MemberStatus;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.services.NotificationService;
import it.polito.ai.lab2.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/API/teams")
public class TeamController {

    @Autowired
    TeamService teamService;

    @Autowired
    NotificationService notificationService;

    @GetMapping("/{courseName}")
    public List<TeamDTO> all(@PathVariable String courseName) {
        return teamService
                .getTeamForCourse(courseName);
    }

    @GetMapping("/{id}")
    public TeamDTO getOne(@PathVariable int id) {
        return ModelHelper.enrich(
                teamService
                .getTeamById(id));
    }

    @GetMapping("/{courseName}/{teamName}")
    public TeamDTO getOneByName(@PathVariable String courseName, @PathVariable String teamName) {
        teamService.getTeamId(courseName, teamName);
        return ModelHelper.enrich(
                teamService
                .getTeam(courseName, teamName));
    }

    @GetMapping("{courseName}/{teamName}/membersStatus")
    public List<MemberStatus> getMembersStatus(@PathVariable String courseName, @PathVariable String teamName) {
        int id = teamService.getTeamId(courseName, teamName);
        return notificationService.getMembersStatus(id);
    }

}
