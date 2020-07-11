package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.dtos.VmModelDTO;

import java.util.List;
import java.util.Optional;

public interface VmService {

    boolean addVm(VmDTO vm);

    Optional<VmDTO> getVm(String id);

    List<VmDTO> getAllVms();

    void startVm(String id);

    void shutDownVm(String id);

    void freezeVm(String id);

    boolean addOwner(String vmId, String studentId);

    void configVm(String vmId, String vmModelId);



    String addVmModel(VmModelDTO vmModel);

    Optional<VmModelDTO> getVmModel(String id);

    Optional<VmModelDTO> getConfigForVm(String vmId);

    List<VmModelDTO> getAllVmModels();

}
