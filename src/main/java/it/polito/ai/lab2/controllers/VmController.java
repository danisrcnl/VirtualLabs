package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.dtos.VmModelDTO;
import it.polito.ai.lab2.services.TeamService;
import it.polito.ai.lab2.services.VmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Array;
import java.util.ArrayList;
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

    @GetMapping("/courses/{courseName}")
    public List<VmDTO> getVmsForCourse (@PathVariable String courseName) throws ResponseStatusException {
        try {
            return vmService
                    .getVmsByCourse(courseName)
                    .stream()
                    .map(ModelHelper :: enrich)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, courseName);
        }
    }

    @GetMapping("/{id}/getVmModel")
    public VmModelDTO getVmModel(@PathVariable Long id) throws ResponseStatusException {
        Optional<VmModelDTO> outcome = vmService.getVmModel(id);
        if(outcome.isPresent())
            return ModelHelper.enrich(outcome.get());
        else
            throw new ResponseStatusException(HttpStatus.CONFLICT, id.toString());
    }

    @GetMapping("/courses/{courseName}/getVmModelOfCourse")
    public VmModelDTO getVmModelOfCourse(@PathVariable String courseName) throws ResponseStatusException {
        Optional<VmModelDTO> outcome = vmService.getVmModelForCourse(courseName);
        if(outcome.isPresent())
            return ModelHelper.enrich(outcome.get());
        else
            throw new ResponseStatusException(HttpStatus.CONFLICT, courseName);
    }

    @GetMapping("/teams/{teamId}")
    public List<VmDTO> getVmsForTeam (@PathVariable int teamId) throws ResponseStatusException {
        try {
            return teamService
                    .getVmsForTeamById(teamId)
                    .stream()
                    .map(ModelHelper :: enrich)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.valueOf(teamId));
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
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, courseName);
        }
        return ModelHelper.enrich(vmModelDTO);
    }

    @PostMapping("/setResources")
    public VmDTO setResources (@RequestBody VmDTO vmDTO) throws ResponseStatusException {
        Optional<VmDTO> outcome;
        try {
            outcome = vmService.setVmResources(vmDTO);
            if(outcome.isPresent())
                return ModelHelper.enrich(outcome.get());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, vmDTO.getId().toString());
        }
            throw new ResponseStatusException(HttpStatus.CONFLICT, vmDTO.getId().toString());
    }

    @GetMapping("/{vmId}/changeState/{command}")
    public VmDTO changeState (@PathVariable Long vmId, @PathVariable String command) {
        try {
            vmService.getVm(vmId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, vmId.toString());
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
