package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dataStructures.VmStatus;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.dtos.VmModelDTO;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.repositories.StudentRepository;
import it.polito.ai.lab2.repositories.TeamRepository;
import it.polito.ai.lab2.repositories.VmModelRepository;
import it.polito.ai.lab2.repositories.VmRepository;
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
    ModelMapper modelMapper;

    @Override
    public boolean addVm(VmDTO vm) {
        Vm v = modelMapper.map(vm, Vm.class);
        if(vmRepository.existsById(vm.getId()))
            return false;
        vmRepository.save(v);
        vmRepository.flush();
        return true;
    }

    @Override
    public Optional<VmDTO> getVm(String id) {
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
    public void startVm(String id) throws VmNotFoundException {
        if(!vmRepository.existsById(id))
            throw new VmNotFoundException(id);
        vmRepository
                .getOne(id)
                .setCurrentStatus(VmStatus.ACTIVE);
    }

    @Override
    public void shutDownVm(String id) throws VmNotFoundException {
        if(!vmRepository.existsById(id))
            throw new VmNotFoundException(id);
        vmRepository
                .getOne(id)
                .setCurrentStatus(VmStatus.OFF);
    }

    @Override
    public void freezeVm(String id) throws VmNotFoundException {
        if(!vmRepository.existsById(id))
            throw new VmNotFoundException(id);
        vmRepository
                .getOne(id)
                .setCurrentStatus(VmStatus.FREEZED);
    }

    @Override
    public boolean addOwner(String vmId, String studentId) throws VmNotFoundException, StudentNotFoundException {
        if (!vmRepository.existsById(vmId))
            throw new VmNotFoundException(vmId);
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
    public void configVm(String vmId, String vmModelId) throws VmNotFoundException, VmModelNotFoundException {
        if(!vmRepository.existsById(vmId))
            throw new VmNotFoundException(vmId);
        if(!vmModelRepository.existsById(vmModelId))
            throw new VmModelNotFoundException(vmModelId);
        vmRepository.getOne(vmId).setVmModel(
                vmModelRepository
                .getOne(vmModelId)
        );
    }

    @Override
    public String addVmModel(VmModelDTO vmModel) {
        VmModel v = modelMapper.map(vmModel, VmModel.class);
        if(vmModelRepository.existsById(vmModel.getId()))
            return "err";
        vmModelRepository.save(v);
        vmModelRepository.flush();
        return v.getId();
    }

    @Override
    public Optional<VmModelDTO> getVmModel(String id) {
        VmModel v = vmModelRepository.getOne(id);
        return Optional.ofNullable(modelMapper.map(v, VmModelDTO.class));
    }

    @Override
    public Optional<VmModelDTO> getConfigForVm(String vmId) throws VmNotFoundException {
        if(!vmRepository.existsById(vmId))
            throw new VmNotFoundException(vmId);
        return Optional.ofNullable(
                modelMapper.map(vmRepository
                        .getOne(vmId)
                        .getVmModel(), VmModelDTO.class)
        );
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
