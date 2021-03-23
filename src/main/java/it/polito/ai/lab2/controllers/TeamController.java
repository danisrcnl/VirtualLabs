package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dataStructures.MemberStatus;
import it.polito.ai.lab2.dataStructures.TeamRequest;
import it.polito.ai.lab2.dataStructures.UsedResources;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.services.NotificationService;
import it.polito.ai.lab2.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/teams")
public class TeamController {

    @Autowired
    TeamService teamService;

    @Autowired
    NotificationService notificationService;

    @GetMapping("/course/{courseName}")
    public List<TeamDTO> all(@PathVariable String courseName) {
        return teamService
                .getTeamForCourse(courseName)
                .stream()
                .map(ModelHelper :: enrich)
                .collect(Collectors.toList());
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

    @GetMapping("/{courseName}/{teamName}/members")
    public List<StudentDTO> getMembers(@PathVariable String courseName, @PathVariable String teamName) {
        return teamService
                .getMembers(courseName, teamName)
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{courseName}/{teamName}/membersStatus")
    public List<MemberStatus> getMembersStatus(@PathVariable String courseName, @PathVariable String teamName) {
        int id = teamService.getTeamId(courseName, teamName);
        return notificationService.getMembersStatus(id);
    }

    @PostMapping("/{courseName}/add")
    public TeamDTO addTeam(@PathVariable String courseName, @RequestBody TeamRequest teamRequest) throws ResponseStatusException {
        String teamName = teamRequest.getTeamName();
        List<String> memberIds = teamRequest.getMemberIds();
        int hours = teamRequest.getHours();

        try {
            teamService.proposeTeam(courseName, teamName, memberIds);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, teamName);
        }

        notificationService.notifyTeam(courseName, teamName, memberIds, hours);
        return ModelHelper.enrich(
                        teamService
                        .getTeam(courseName, teamName)
                );
    }

    @GetMapping("/{courseName}/{teamName}/getUsedResources")
    public UsedResources getUsedResources (@PathVariable String courseName, @PathVariable String teamName) throws ResponseStatusException {
        try {
            teamService.getTeam(courseName, teamName);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, teamName);
        }
        UsedResources usedResources = new UsedResources(
                teamService.getUsedNVCpuForTeam(courseName, teamName),
                teamService.getUsedRamForTeam(courseName, teamName),
                teamService.getUsedDiskForTeam(courseName, teamName)
        );
        return usedResources;
    }

}
