package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dataStructures.VmStatus;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.dtos.VmModelDTO;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class VmServiceImpl implements VmService {

    @Autowired
    VmRepository vmRepository;

    @Autowired
    VmModelRepository vmModelRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    TeamService teamService;

    @Override
    public Long addVmToTeam(VmDTO vm, String teamName) throws TeamNotFoundException, VmServiceException {

        if(!teamRepository.existsById(teamName))
            throw new TeamNotFoundException(teamName);

        if(teamService.getUsedNVCpuForTeam(teamName) + vm.getNVCpu() >
                teamRepository.getOne(teamName).getCourse().getVmModel().getMaxNVCpu())
            throw new VmServiceException("You exceeded Virtual CPU limit");

        if(teamService.getUsedDiskForTeam(teamName) + vm.getDisk() >
                teamRepository.getOne(teamName).getCourse().getVmModel().getMaxDisk())
            throw new VmServiceException("You exceeded disk space limit");

        if(teamService.getUsedRamForTeam(teamName) + vm.getRam() >
                teamRepository.getOne(teamName).getCourse().getVmModel().getMaxRam())
            throw new VmServiceException("You exceeded ram space limit");

        Vm v = modelMapper.map(vm, Vm.class);
        vmRepository.save(v);
        vmRepository.flush();
        return v.getId();
    }

    @Override
    public Optional<VmDTO> getVm(Long id) {
        Vm v = vmRepository.getOne(id);
        return Optional.ofNullable(modelMapper.map(v, VmDTO.class));
    }

    @Override
    public List<VmDTO> getAllVms() {
        return vmRepository
                .findAll()
                .stream()
                .map(vm -> modelMapper.map(vm, VmDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void startVm(Long id) throws VmNotFoundException {
        if(!vmRepository.existsById(id))
            throw new VmNotFoundException(id.toString());
        vmRepository
                .getOne(id)
                .setCurrentStatus(VmStatus.ACTIVE);
    }

    @Override
    public void shutDownVm(Long id) throws VmNotFoundException {
        if(!vmRepository.existsById(id))
            throw new VmNotFoundException(id.toString());
        vmRepository
                .getOne(id)
                .setCurrentStatus(VmStatus.OFF);
    }

    @Override
    public void freezeVm(Long id) throws VmNotFoundException {
        if(!vmRepository.existsById(id))
            throw new VmNotFoundException(id.toString());
        vmRepository
                .getOne(id)
                .setCurrentStatus(VmStatus.FREEZED);
    }

    @Override
    public void deleteVm(Long id) throws VmNotFoundException {
        if(!vmRepository.existsById(id))
            throw new VmNotFoundException(id.toString());
        vmRepository.deleteById(id);
        vmRepository.flush();
    }

    @Override
    public boolean addOwner(Long vmId, String studentId) throws VmNotFoundException, StudentNotFoundException {
        if (!vmRepository.existsById(vmId))
            throw new VmNotFoundException(vmId.toString());
        if (!studentRepository.existsById(studentId))
            throw new StudentNotFoundException(studentId);

        List<Student> owners = vmRepository
                .getOne(vmId)
                .getOwners();

        for(Student owner : owners) {
            if(owner.getId().equals(studentId))
                return false;
        }

        vmRepository
                .getOne(vmId)
                .addOwner(studentRepository.getOne(studentId));

        return true;
    }

    @Override
    public Long addVmModelForCourse(VmModelDTO vmModel, String courseName) throws CourseNotFoundException {
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        VmModel existing = courseRepository.getOne(courseName).getVmModel();
        if(existing != null)
            vmModelRepository.delete(existing);
        vmModelRepository.flush();

        VmModel m = modelMapper.map(vmModel, VmModel.class);
        vmModelRepository.save(m);
        vmModelRepository.flush();
        return m.getId();
    }

    @Override
    public Optional<VmModelDTO> getVmModel(Long id) {
        VmModel v = vmModelRepository.getOne(id);
        return Optional.ofNullable(modelMapper.map(v, VmModelDTO.class));
    }

    @Override
    public List<VmModelDTO> getAllVmModels() {
        return vmModelRepository
                .findAll()
                .stream()
                .map(v -> modelMapper.map(v, VmModelDTO.class))
                .collect(Collectors.toList());
    }
}
