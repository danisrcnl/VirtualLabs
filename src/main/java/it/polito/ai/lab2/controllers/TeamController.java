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

@RestController
@RequestMapping("/API/teams")
public class TeamController {

    @Autowired
    TeamService teamService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    CourseService courseService;

    @GetMapping("/course/{courseName}")
    public List<TeamDTO> all (@PathVariable String courseName) {
        return courseService
                .getTeamForCourse(courseName)
                .stream()
                .map(ModelHelper :: enrich)
                .collect(Collectors.toList());
    }

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

    @PostMapping("/{courseName}/add")
    public TeamDTO addTeam(@PathVariable String courseName, @RequestBody TeamRequest teamRequest) throws ResponseStatusException {
        String teamName = teamRequest.getTeamName();
        String creator = teamRequest.getCreator();
        List<String> memberIds = teamRequest.getMemberIds();
        int hours = teamRequest.getHours();

        try {
            teamService.proposeTeam(courseName, teamName, memberIds, creator);
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
