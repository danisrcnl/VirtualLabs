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
/*
    @PostMapping("/{courseName}/setVmModel")
    public VmModelDTO setVmModel (@PathVariable String courseName, @RequestBody VmModelDTO vmModelDTO) {
        try {

        } catch (Exception e) {

        }
    }
*/
    @PostMapping("/{vmId}/setResources")
    public VmDTO setResources (@PathVariable Long vmId, @RequestBody VmDTO vmDTO) throws ResponseStatusException {
        Optional<VmDTO> outcome = Optional.empty();
        try {
            outcome = vmService.setVmResources(vmId, vmDTO);
            if(outcome.isPresent())
                return outcome.get();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, vmId.toString());
        }
            throw new ResponseStatusException(HttpStatus.CONFLICT, vmId.toString());
    }

}
