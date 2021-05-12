package it.polito.ai.lab2.services.vm;

import it.polito.ai.lab2.dataStructures.VmStatus;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.dtos.VmModelDTO;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.repositories.*;
import it.polito.ai.lab2.services.AiException;
import it.polito.ai.lab2.services.auth.AuthenticationService;
import it.polito.ai.lab2.services.student.StudentService;
import it.polito.ai.lab2.services.team.TeamService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Array;
import java.util.Arrays;
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

    @Autowired
    StudentService studentService;

    @Autowired
    AuthenticationService authenticationService;

    /*
    * A valle di una serie di controlli dettati dal vmModel impostato dal docente, la vm parametrizzata dal dto ricevuto
    * viene istanziata e associata al team desiderato. I vari membri del team vengono dunque arricchiti dai relativi
    * ruoli.
    * */
    @Override
    public Long addVmToTeam(VmDTO vm, String courseName, String teamName, String creator) throws TeamNotFoundException, VmServiceException {

        if(teamRepository.getTeamByCourseAndName(courseName, teamName) == null)
            throw new TeamNotFoundException(teamName);

        if(vmModelRepository.getVmModelByCourse(courseName) == null)
            throw new VmModelNotFoundException("VmModel for course " + courseName + " ");

        if(teamService.getUsedNVCpuForTeam(courseName, teamName) + vm.getNVCpu() >
                teamRepository.getTeamByCourseAndName(courseName, teamName).getCourse().getVmModel().getMaxNVCpu())
            throw new VmServiceException("Hai superato il limite di virtual CPUs");

        if(teamService.getUsedDiskForTeam(courseName, teamName) + vm.getDisk() >
                teamRepository.getTeamByCourseAndName(courseName, teamName).getCourse().getVmModel().getMaxDisk())
            throw new VmServiceException("Hai superato il limite di memoria secondaria");

        if(teamService.getUsedRamForTeam(courseName, teamName) + vm.getRam() >
                teamRepository.getTeamByCourseAndName(courseName, teamName).getCourse().getVmModel().getMaxRam())
            throw new VmServiceException("Hai superato il limite di RAM");

        if(vmRepository.getVmsForCourse(courseName).size() + 1 >
                vmModelRepository.getVmModelByCourse(courseName).getMaxVmsForCourse())
            throw new VmServiceException("Hai ecceduto il massimo numero di VMs allocate per il corso di " + courseName);

        if(!studentRepository.existsById(creator))
            throw new StudentNotFoundException("Creator has an invalid identifier (" + creator + ")");

        StudentDTO creatorStudent = studentService.getStudent(creator).get();

        if(!teamService.getMembers(courseName, teamName).contains(creatorStudent))
            throw new StudentNotFoundException("Creator doesn't belong to team " + teamName);


        Student creator_entity = studentRepository.getOne(creator);

        Vm v = modelMapper.map(vm, Vm.class);
        Team vmTeam = teamRepository.getTeamByCourseAndName(courseName, teamName);

        if (!vmTeam.getMembers().contains(creator_entity))
            throw new VmServiceException("Creator must belong to the team");

        v.setTeam(vmTeam);
        v.setCreator(creator_entity);
        v.setCurrentStatus(VmStatus.OFF);
        vmRepository.save(v);
        vmRepository.flush();

        List<User> users = vmTeam
                .getMembers()
                .stream()
                .map(Student :: getUser)
                .collect(Collectors.toList());

        for (User u : users) {
            if (u.getStudent().getId().equals(creator))
                authenticationService.setPrivileges(u.getUsername(), Arrays.asList("ROLE_VM_" + v.getId() + "_CREATOR"));
            authenticationService.setPrivileges(u.getUsername(), Arrays.asList("ROLE_VM_" + v.getId() + "_OWNER"));
        }

        return v.getId();
    }

    /*
    * Ritorna la vm con id pari a quello indicato.
    * */
    @Override
    public Optional<VmDTO> getVm(Long id) {
        Vm v = vmRepository.getOne(id);
        return Optional.ofNullable(modelMapper.map(v, VmDTO.class));
    }

    /*
    * Ritorna tutte le vms salvate nel database.
    * */
    @Override
    public List<VmDTO> getAllVms() {
        return vmRepository
                .findAll()
                .stream()
                .map(vm -> modelMapper.map(vm, VmDTO.class))
                .collect(Collectors.toList());
    }

    /*
    * Ritorna tutte le vms istanziate nell'ambito di un certo corso.
    * */
    @Override
    public List<VmDTO> getVmsByCourse(String courseName) throws CourseNotFoundException{
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);
        return vmRepository
                .getVmsForCourse(courseName)
                .stream()
                .map(v -> modelMapper.map(v, VmDTO.class))
                .collect(Collectors.toList());
    }

    /*
    * Se possibile (per i limiti dati dal max numero di vms attive nel corso) viene attivata la vm con id pari a quello
    * indicato.
    * */
    @Override
    public void startVm(Long id) throws VmNotFoundException {
        if(!vmRepository.existsById(id))
            throw new VmNotFoundException(id.toString());

        Course course = vmRepository
                .getOne(id)
                .getTeam()
                .getCourse();

        List<VmDTO> activeVms = vmRepository
                .getVmsForCourse(course.getName())
                .stream()
                .filter(vm -> vm.getCurrentStatus().equals(VmStatus.ACTIVE))
                .map(vm -> modelMapper.map(vm, VmDTO.class))
                .collect(Collectors.toList());

        if (activeVms.size() + 1 > course.getVmModel().getMaxActiveVms())
            throw new VmServiceException("Ci sono troppe VMs attive per il corso di " + course.getName());

        vmRepository
                .getOne(id)
                .setCurrentStatus(VmStatus.ACTIVE);
    }

    /*
    * La vm indicata viene spenta.
    * */
    @Override
    public void shutDownVm(Long id) throws VmNotFoundException {
        if(!vmRepository.existsById(id))
            throw new VmNotFoundException(id.toString());
        vmRepository
                .getOne(id)
                .setCurrentStatus(VmStatus.OFF);
    }

    /*
    * Se la vm indicata è in esecuzione, allora viene messa in pausa.
    * */
    @Override
    public void freezeVm(Long id) throws AiException {
        if(!vmRepository.existsById(id))
            throw new VmNotFoundException(id.toString());
        Vm vm = vmRepository.getOne(id);
        if (vm.getCurrentStatus() != VmStatus.ACTIVE)
            throw new AiException();
        vm.setCurrentStatus(VmStatus.FREEZED);
    }

    /*
    * La vm indicata viene eliminata e per ognuno dei membri del team che la ha istanziata, vengono rimossi i relativi
    * permessi.
    * */
    @Override
    public void deleteVm(Long id) throws VmNotFoundException {
        if(!vmRepository.existsById(id))
            throw new VmNotFoundException(id.toString());

        Vm vm = vmRepository.getOne(id);

        List<User> users = vm
                .getTeam()
                .getMembers()
                .stream()
                .map(Student :: getUser)
                .collect(Collectors.toList());

        for (User u : users) {
            u
                .getRoles()
                .removeIf(role -> role.contains(vm.getId().toString()) && role.contains("VM"));
        }

        vm.removeRelations();
        vmRepository.deleteById(id);
        vmRepository.flush();
    }

    /*
    * Permette di aggiungere un owner indicato da studentId per la vm desiderata, qualora non già presente.
    * */
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

    /*
    * Restituisce i dto di tutti gli studenti owners della vm indicata.
    * */
    @Override
    public List<StudentDTO> getOwnersForVm(Long vmId) throws VmNotFoundException {
        if(!vmRepository.existsById(vmId))
            throw new VmNotFoundException(vmId.toString());

        return vmRepository
                .getOne(vmId)
                .getOwners()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    /*
    * Nei limiti imposti dal vmModel (contando le risorse già allocate come disponibili), permette di modificare
    * i parametri impostati alla vm.
    * */
    @Override
    public Optional<VmDTO> setVmResources(VmDTO vmDTO) {
        if(!vmRepository.existsById(vmDTO.getId()))
            return Optional.empty();

        Vm vm = vmRepository.getOne(vmDTO.getId());
        String courseName = vm.getTeam().getCourse().getName();
        String teamName = vm.getTeam().getName();

        if(teamService.getUsedNVCpuForTeam(courseName, teamName) - vm.getNVCpu() + vmDTO.getNVCpu() >
                teamRepository.getTeamByCourseAndName(courseName, teamName).getCourse().getVmModel().getMaxNVCpu())
            throw new VmServiceException("Hai superato il limite di virtual CPUs");

        if(teamService.getUsedDiskForTeam(courseName, teamName) - vm.getDisk() + vmDTO.getDisk() >
                teamRepository.getTeamByCourseAndName(courseName, teamName).getCourse().getVmModel().getMaxDisk())
            throw new VmServiceException("Hai superato il limite di memoria secondaria");

        if(teamService.getUsedRamForTeam(courseName, teamName) - vm.getRam() + vmDTO.getRam() >
                teamRepository.getTeamByCourseAndName(courseName, teamName).getCourse().getVmModel().getMaxRam())
            throw new VmServiceException("Hai superato il limite di RAM");

        vm.setNVCpu(vmDTO.getNVCpu());
        vm.setDisk(vmDTO.getDisk());
        vm.setRam(vmDTO.getRam());
        return Optional.ofNullable(modelMapper.map(vm, VmDTO.class));
    }

    /*
    * Vale sia per la creazione che per la modifica di un vmModel. Questo non deve influenzare le vms precedentemente
    * allocate, pertanto non vengono effettuati controlli di questo genere.
    * */
    @Override
    public Long addVmModelForCourse(VmModelDTO vmModel, String courseName) throws CourseNotFoundException {
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        Course c = courseRepository.getOne(courseName);
        VmModel existing = c.getVmModel();
        if(existing != null) {
            c.setVmModel(null);
            vmModelRepository.delete(existing);
            vmModelRepository.flush();
        }

        VmModel m = modelMapper.map(vmModel, VmModel.class);
        m.setCourse(c);
        c.setVmModel(m);
        vmModelRepository.save(m);
        vmModelRepository.flush();
        return m.getId();
    }

    /*
    * Torna il vmModel associato a un determinato corso.
    * */
    @Override
    public Optional<VmModelDTO> getVmModelForCourse(String courseName) {
        if(!courseRepository.existsById(courseName))
            return Optional.empty();
        VmModel v = vmModelRepository.getVmModelByCourse(courseName);
        return Optional.ofNullable(modelMapper.map(v, VmModelDTO.class));
    }

    /*
    * Torna il vmModel con id pari a quello indicato.
    * */
    @Override
    public Optional<VmModelDTO> getVmModel(Long id) {
        VmModel v = vmModelRepository.getOne(id);
        return Optional.ofNullable(modelMapper.map(v, VmModelDTO.class));
    }

    /*
    * Torna tutti i vmModels salvati nel database.
    * */
    @Override
    public List<VmModelDTO> getAllVmModels() {
        return vmModelRepository
                .findAll()
                .stream()
                .map(v -> modelMapper.map(v, VmModelDTO.class))
                .collect(Collectors.toList());
    }
}
