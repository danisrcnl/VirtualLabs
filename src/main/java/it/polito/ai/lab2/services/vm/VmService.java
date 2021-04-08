package it.polito.ai.lab2.services.vm;

import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.dtos.VmModelDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface VmService {

    // @PreAuthorize("hasAnyRole('ROLE_COURSE_' + #courseName + '_TEACHER', 'ROLE_TEAM_' + #courseName + '_' + #teamName + '_MEMBER')")
    Long addVmToTeam(VmDTO vm, String courseName, String teamName, String creator);

    // @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_VM_' + #id + '_OWNER')")
    Optional<VmDTO> getVm(Long id);

    // @PreAuthorize("hasRole('ROLE_TEACHER')")
    List<VmDTO> getAllVms();

    // @PreAuthorize("hasRole('ROLE_COURSE_' + #courseName + '_TEACHER')")
    List<VmDTO> getVmsByCourse(String courseName);

    // @PreAuthorize("hasRole('ROLE_VM_' + #id + '_OWNER')")
    void startVm(Long id);

    // @PreAuthorize("hasRole('ROLE_VM_' + #id + '_OWNER')")
    void shutDownVm(Long id);

    // @PreAuthorize("hasRole('ROLE_VM_' + #id + '_OWNER')")
    void freezeVm(Long id);

    // @PreAuthorize("hasRole('ROLE_VM_' + #id + '_OWNER')")
    void deleteVm(Long id);

    // @PreAuthorize("hasRole('ROLE_VM_' + #id + '_CREATOR')")
    boolean addOwner(Long vmId, String studentId);

    // @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_VM_' + #id + '_OWNER')")
    List<StudentDTO> getOwnersForVm(Long vmId);

    // @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_VM_' + #vmDTO.id + '_OWNER')")
    Optional<VmDTO> setVmResources(VmDTO vmDTO);

    // @PreAuthorize("hasRole('ROLE_COURSE_' + #courseName + '_TEACHER')")
    Long addVmModelForCourse(VmModelDTO vmModel, String courseName);

    // @PreAuthorize("hasAnyRole('ROLE_COURSE_' + #courseName + '_TEACHER', 'ROLE_COURSE_' + #courseName + '_STUDENT')")
    Optional<VmModelDTO> getVmModelForCourse(String courseName);

    // @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    Optional<VmModelDTO> getVmModel(Long id);

    // @PreAuthorize("hasRole('ROLE_TEACHER')")
    List<VmModelDTO> getAllVmModels();

}
