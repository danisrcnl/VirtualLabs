package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dataStructures.VmStatus;
import it.polito.ai.lab2.dataStructures.VmSubmission;
import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.dtos.VmModelDTO;
import it.polito.ai.lab2.services.AiException;
import it.polito.ai.lab2.services.team.TeamService;
import it.polito.ai.lab2.services.vm.VmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/vms")
public class VmController {

    @Autowired
    VmService vmService;

    @Autowired
    TeamService teamService;

    @PostMapping("/{courseName}/{teamName}")
    public Long add (@PathVariable String courseName, @PathVariable String teamName, @RequestBody VmSubmission vmSubmission) throws ResponseStatusException {
        if (teamService.getTeam(courseName, teamName).getStatus() == 0)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Team " + teamName + " is not active");
        try {
            return vmService
                    .addVmToTeam(vmSubmission.getVmDTO(), courseName, teamName, vmSubmission.getCreator());
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }
    }

    @GetMapping("/courses/{courseName}")
    public List<VmDTO> getVmsForCourse (@PathVariable String courseName) throws ResponseStatusException {
        try {
            return vmService
                    .getVmsByCourse(courseName)
                    .stream()
                    .map(ModelHelper :: enrich)
                    .collect(Collectors.toList());
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }
    }

    @GetMapping("/{id}/getVmModel")
    public VmModelDTO getVmModel(@PathVariable Long id) throws ResponseStatusException {
        Optional<VmModelDTO> outcome = vmService.getVmModel(id);
        if(outcome.isPresent())
            return ModelHelper.enrich(outcome.get());
        else
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Couldn't find any vmModel with id: " + id.toString());
    }

    @GetMapping("/courses/{courseName}/getVmModelOfCourse")
    public VmModelDTO getVmModelOfCourse(@PathVariable String courseName) throws ResponseStatusException {
        Optional<VmModelDTO> outcome = vmService.getVmModelForCourse(courseName);
        if(outcome.isPresent())
            return ModelHelper.enrich(outcome.get());
        else
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Couldn't find any VmModel for course: " + courseName);
    }

    @GetMapping("/teams/{teamId}")
    public List<VmDTO> getVmsForTeam (@PathVariable int teamId) throws ResponseStatusException {
        if (teamService.getTeamById(teamId).getStatus() == 0)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Team " + teamId + " is not active");
        try {
            return teamService
                    .getVmsForTeamById(teamId)
                    .stream()
                    .map(ModelHelper :: enrich)
                    .collect(Collectors.toList());
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }
    }

    @GetMapping("/{vmId}")
    public VmDTO getOne (@PathVariable Long vmId) throws ResponseStatusException {
        Optional<VmDTO> vmDTO = vmService.getVm(vmId);
        if(!vmDTO.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.valueOf(vmId));
        return ModelHelper.enrich(vmDTO.get());
    }

    @PostMapping("/courses/{courseName}/setVmModel")
    public VmModelDTO setVmModel (@PathVariable String courseName, @RequestBody VmModelDTO vmModelDTO) {
        try {
            vmService.addVmModelForCourse(vmModelDTO, courseName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }
        return ModelHelper.enrich(vmModelDTO);
    }

    @PostMapping("/setResources")
    public VmDTO setResources (@RequestBody VmDTO vmDTO) throws ResponseStatusException {
        Optional<VmDTO> outcome;
        try {
            outcome = vmService.getVm(vmDTO.getId());
            if(outcome.isPresent()) {
                if(outcome.get().getVmStatus().equals(VmStatus.OFF))
                    outcome = vmService.setVmResources(vmDTO);
                else
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Couldn't find VM with id: " + vmDTO.getId());
            }
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }
        return ModelHelper.enrich(outcome.get());
    }

    @GetMapping("/{vmId}/changeState/{command}")
    public VmDTO changeState (@PathVariable Long vmId, @PathVariable String command) throws ResponseStatusException {
        try {
            vmService.getVm(vmId);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        switch (command) {
            case "start" :
                vmService.startVm(vmId);
                return ModelHelper.enrich(vmService.getVm(vmId).get());
            case "shutDown" :
                vmService.shutDownVm(vmId);
                return ModelHelper.enrich(vmService.getVm(vmId).get());
            case "freeze" :
                vmService.freezeVm(vmId);
                return ModelHelper.enrich(vmService.getVm(vmId).get());
            case "delete" :
                vmService.deleteVm(vmId);
                return VmDTO.builder().build();
            default:
                return VmDTO.builder().build();
        }
    }

}
