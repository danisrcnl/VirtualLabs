package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.dtos.VmModelDTO;

import java.util.List;
import java.util.Optional;

public interface VmService {

    Long addVmToTeam(VmDTO vm, String teamName);

    Optional<VmDTO> getVm(Long id);

    List<VmDTO> getAllVms();

    void startVm(Long id);

    void shutDownVm(Long id);

    void freezeVm(Long id);

    void deleteVm(Long id);

    boolean addOwner(Long vmId, String studentId);



    Long addVmModelForCourse(VmModelDTO vmModel, String courseName);

    Optional<VmModelDTO> getVmModel(Long id);

    List<VmModelDTO> getAllVmModels();

}
