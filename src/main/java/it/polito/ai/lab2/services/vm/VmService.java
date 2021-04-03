package it.polito.ai.lab2.services.vm;

import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.dtos.VmModelDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface VmService {

    Long addVmToTeam(VmDTO vm, String courseName, String teamName);

    Optional<VmDTO> getVm(Long id);

    List<VmDTO> getAllVms();

    List<VmDTO> getVmsByCourse(String courseName);

    // @PreAuthorize("hasRole('ROLE_VM_' + #id + '_OWNER')")
    void startVm(Long id);

    void shutDownVm(Long id);

    void freezeVm(Long id);

    void deleteVm(Long id);

    boolean addOwner(Long vmId, String studentId);

    List<StudentDTO> getOwnersForVm(Long vmId);

    Optional<VmDTO> setVmResources(VmDTO vmDTO);



    Long addVmModelForCourse(VmModelDTO vmModel, String courseName);

    Optional<VmModelDTO> getVmModelForCourse(String courseName);

    Optional<VmModelDTO> getVmModel(Long id);

    List<VmModelDTO> getAllVmModels();

}
