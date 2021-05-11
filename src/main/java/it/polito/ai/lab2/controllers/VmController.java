/*
 * VmController.java:
 * Classe deputata a offrire APIs accessibili tramite richieste HTTP per la gestione delle VMs istanziate dagli
 * studenti e dei VmModels creati dal docente per i diversi corsi.
 * */

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

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/API/vms")
public class VmController {

    @Autowired
    VmService vmService;

    @Autowired
    TeamService teamService;

    /*
    * Il metodo permette di istanziare una nuova VM per il team desiderato nell'ambito di un determinato corso.
    * Il team deve essere attivo (tutti gli studenti hanno accettato l'invito) e nel corpo della richiesta deve essere
    * presente un DTO di VM e la matricola di colui che ne sarà il creatore.
    * */
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

    /*
    * Restituisce tutte le vms istanziate nell'ambito di un determinato corso, da qualsiasi team.
    * */
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

    /*
    * Restituisce il vmModel con id specificato.
    * */
    @GetMapping("/{id}/getVmModel")
    public VmModelDTO getVmModel(@PathVariable Long id) throws ResponseStatusException {
        Optional<VmModelDTO> outcome = vmService.getVmModel(id);
        if(outcome.isPresent())
            return ModelHelper.enrich(outcome.get());
        else
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Couldn't find any vmModel with id: " + id.toString());
    }

    /*
     * Restituisce, qualora fosse presente, il vmModel legato ad un determinato corso.
     * */
    @GetMapping("/courses/{courseName}/getVmModelOfCourse")
    public VmModelDTO getVmModelOfCourse(@PathVariable String courseName) throws ResponseStatusException {
        Optional<VmModelDTO> outcome = vmService.getVmModelForCourse(courseName);
        if(outcome.isPresent())
            return ModelHelper.enrich(outcome.get());
        else
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Couldn't find any VmModel for course: " + courseName);
    }

    /*
    * Restituisce tutte le vms istanziate da un certo team, a partire dal suo identificatore.
    * */
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

    /*
    * Metodo che restituisce la vm con id specificato.
    * */
    @GetMapping("/{vmId}")
    public VmDTO getOne (@PathVariable Long vmId) throws ResponseStatusException {
        Optional<VmDTO> vmDTO = vmService.getVm(vmId);
        if(!vmDTO.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.valueOf(vmId));
        return ModelHelper.enrich(vmDTO.get());
    }

    /*
    * Metodo che aggiunge un vmModel per il corso specificato. Qualora fosse già presente, questo viene sostituito.
    * */
    @PostMapping("/courses/{courseName}/setVmModel")
    public VmModelDTO setVmModel (@PathVariable String courseName, @RequestBody VmModelDTO vmModelDTO) {
        Long id = null;
        try {
            vmModelDTO.setId(null);
            id = vmService.addVmModelForCourse(vmModelDTO, courseName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }
        vmModelDTO.setId(id);
        return ModelHelper.enrich(vmModelDTO);
    }

    /*
    * Metodo che setta i parametri delle risorse per una certa vm. Questi devono soddisfare i limiti imposti dal
    * corrispondente vmModel.
    * */
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

    /*
    * Metodo che modifica lo stato di una vm a quello specificato da "command", qualora possibile.
    * */
    @GetMapping("/{vmId}/changeState/{command}")
    public VmDTO changeState (@PathVariable Long vmId, @PathVariable String command) throws ResponseStatusException {
        if(!vmService.getVm(vmId).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La vm specificata non esiste");

        switch (command) {
            case "start" :
                try {
                    vmService.startVm(vmId);
                } catch (AiException e) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
                }
                return ModelHelper.enrich(vmService.getVm(vmId).get());
            case "shutDown" :
                try {
                    vmService.shutDownVm(vmId);
                } catch (AiException e) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
                }
                return ModelHelper.enrich(vmService.getVm(vmId).get());
            case "freeze" :
                try {
                    vmService.freezeVm(vmId);
                } catch (AiException e) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
                }
                return ModelHelper.enrich(vmService.getVm(vmId).get());
            case "delete" :
                try {
                    vmService.deleteVm(vmId);
                } catch (AiException e) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
                }
                return VmDTO.builder().build();
            default:
                return VmDTO.builder().build();
        }
    }

}
