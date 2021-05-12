package it.polito.ai.lab2.services.team;

import it.polito.ai.lab2.dtos.*;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.repositories.*;
import it.polito.ai.lab2.services.auth.AuthenticationService;
import it.polito.ai.lab2.services.course.CourseService;
import it.polito.ai.lab2.services.student.StudentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentService studentService;

    @Autowired
    CourseService courseService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    ModelMapper modelMapper;

    /*
    * Il metodo attiva il team selezionato mettendo a 1 il suo status. Dopo di ciò i membri del team vengono arricchiti
    * con i relativi privilegi (tutti con MEMBER, il creatore anche con CREATOR).
    * */
    @Override
    public void activateTeamById(int id) throws TeamNotFoundException {
        if(!teamRepository.existsById(id))
            throw new TeamNotFoundException(id);

        Team t = teamRepository.getOne(id);

        t.setStatus(1);

        List<User> users = t
                .getMembers()
                .stream()
                .map(Student :: getUser)
                .collect(Collectors.toList());

        String creator = t.getCreator().getId();
        String courseName = t.getCourse().getName();
        String teamName = t.getName();

        for (User user : users) {
            if(user.getStudent().getId().equals(creator)) {
                authenticationService.setPrivileges(user.getUsername(), Arrays.asList("ROLE_TEAM_" + courseName + "_" + teamName + "_CREATOR"));
                authenticationService.setPrivileges(user.getUsername(), Arrays.asList("ROLE_TEAM_" + t.getId() + "_CREATOR"));
            }
            authenticationService.setPrivileges(user.getUsername(), Arrays.asList("ROLE_TEAM_" + courseName + "_" + teamName + "_MEMBER"));
            authenticationService.setPrivileges(user.getUsername(), Arrays.asList("ROLE_TEAM_" + t.getId() + "_MEMBER"));
        }
    }

    /*
    * Stesso metodo di sopra, ma chiamabile a partire da courseName e teamName.
    * */
    @Override
    public void activateTeam(String courseName, String teamName) throws TeamNotFoundException {
        Team t = teamRepository.getTeamByCourseAndName(courseName, teamName);
        if(t == null)
            throw new TeamNotFoundException(teamName);

        t.setStatus(1);

        List<User> users = t
                .getMembers()
                .stream()
                .map(Student :: getUser)
                .collect(Collectors.toList());

        String creator = t.getCreator().getId();

        for (User user : users) {
            if(user.getStudent().getId().equals(creator)) {
                authenticationService.setPrivileges(user.getUsername(), Arrays.asList("ROLE_TEAM_" + courseName + "_" + teamName + "_CREATOR"));
                authenticationService.setPrivileges(user.getUsername(), Arrays.asList("ROLE_TEAM_" + t.getId() + "_CREATOR"));
            }
            authenticationService.setPrivileges(user.getUsername(), Arrays.asList("ROLE_TEAM_" + courseName + "_" + teamName + "_MEMBER"));
            authenticationService.setPrivileges(user.getUsername(), Arrays.asList("ROLE_TEAM_" + t.getId() + "_MEMBER"));
        }
    }

    /*
    * Dato l'id di un team, questo viene eliminato e tutti i ruoli corrispondenti ai suoi membri nei confronti del gruppo
    * vengono cancellati.
    * */
    @Override
    public void evictTeamById(int id) throws TeamNotFoundException {
        if(!teamRepository.existsById(id))
            throw new TeamNotFoundException(id);

        Team t = teamRepository.getOne(id);
        String teamName = t.getName();
        String courseName = t.getCourse().getName();

        List<User> users = t
                .getMembers()
                .stream()
                .map(Student :: getUser)
                .collect(Collectors.toList());

        for (User user : users) {
            user.getRoles().removeIf(r -> (r.contains("TEAM_" + id) || (r.contains("TEAM_" + courseName + "_" + teamName))));
        }

        teamRepository.delete(teamRepository.getOne(id));
        teamRepository.flush();
    }

    /*
    * Come sopra, ma a partire da courseName e teamName.
    * */
    @Override
    public void evictTeam(String courseName, String teamName) throws TeamNotFoundException {
        Team t = teamRepository.getTeamByCourseAndName(courseName, teamName);
        if(t == null)
            throw new TeamNotFoundException(teamName);

        int id = t.getId();

        List<User> users = teamRepository
                .getTeamByCourseAndName(courseName, teamName)
                .getMembers()
                .stream()
                .map(Student :: getUser)
                .collect(Collectors.toList());

        for (User user : users) {
            user.getRoles().removeIf(r -> (r.contains("TEAM_" + id) || (r.contains("TEAM_" + courseName + "_" + teamName))));
        }

        teamRepository.delete(t);
        teamRepository.flush();
    }

    private void deleteRolesOfTeam (int id) throws TeamNotFoundException {
        if(!teamRepository.existsById(id))
            throw new TeamNotFoundException(id);
        Team t = teamRepository.getOne(id);
        List<User> users = teamRepository
                .getOne(id)
                .getMembers()
                .stream()
                .map(s -> s.getUser())
                .collect(Collectors.toList());
        Integer idO = id;
        for (User user : users) {
            user
                    .getRoles()
                    .removeIf(role -> (role.contains(idO.toString()) && role.contains("TEAM")) || (role.contains(t.getName()) && role.contains(t.getCourse().getName())));
        }
    }

    /*
    * A partire dall'id di un gruppo, ritorna il gruppo corrispondente.
    * */
    @Override
    public TeamDTO getTeamById(int id) throws TeamNotFoundException {
        if(!teamRepository.existsById(id))
            throw new TeamNotFoundException(id);
        return modelMapper.map(
                teamRepository.getOne(id),
                TeamDTO.class
        );
    }

    /*
    * Come sopra, ma a partire da courseName e teamName.
    * */
    @Override
    public TeamDTO getTeam(String courseName, String teamName) throws CourseNotFoundException, TeamNotFoundException {
        Team t = teamRepository.getTeamByCourseAndName(courseName, teamName);
        if(t == null)
            throw new TeamNotFoundException(teamName);
        return modelMapper.map(t, TeamDTO.class);
    }

    /*
    * Dato l'id di un team, vengono tornati i suoi membri come studentDTO.
    * */
    @Override
    public List<StudentDTO> getMembersById(int id) throws TeamNotFoundException {
        if(!teamRepository.existsById(id))
            throw new TeamNotFoundException(id);
        return teamRepository
                .getOne(id)
                .getMembers()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    /*
    * Come sopra, ma a partire da courseName e teamName.
    * */
    @Override
    public List<StudentDTO> getMembers(String courseName, String teamName) throws TeamNotFoundException {
        Team t = teamRepository.getTeamByCourseAndName(courseName, teamName);
        if(t == null)
            throw new TeamNotFoundException(teamName);
        return t
                .getMembers()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    /*
    * A valle di tutti i check relativi a esistenza delle entità e rispetto delle specifiche imposte dal docente,
    * per ogni membro vengono salvati in teamsForCourseStudent tutti i gruppi a cui è stato invitato e in
    * tokensForCourseStudent tutti i tokens di team relativi a quel corso. In questo modo se la dimensione delle due
    * liste è differente, allora vuol dire che l'utente ha già accettato un invito e pertanto non è più invitabile,
    * benchè il suo id figuri tra gli availableIds. Se uno dei membri ricade in questo caso, la proposta viene abortita.
    * Dopo di ciò si provvede a collegare ogni entità student con l'entità team creata a monte e la proposta viene
    * salvata in db.
    * */
    @Override
    public TeamDTO proposeTeam(String courseName, String teamName, List<String> memberIds, String creator)
        throws TeamServiceException {

        Team team;
        List<Student> members = new ArrayList<>();

        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        if(teamRepository.getTeamByCourseAndName(courseName, teamName) != null)
            throw new TeamServiceException("Name " + teamName + " has been already chosen for course " + courseName);

        if(!memberIds.contains(creator))
            throw new TeamServiceException("Creator " + creator + " is not in the list of members");

        Course c = courseRepository.getOne(courseName);
        if(memberIds.size() > c.getMax() || memberIds.size() < c.getMin())
            throw new TeamServiceException("Assicurati di aver selezionato il corretto numero di studenti");

        team = Team.builder()
                .name(teamName)
                .members(members)
                .build();
        team.setCourse(c);

        List<String> availableIds = courseService.getAvailableStudents(courseName)
                .stream()
                .map(StudentDTO::getId)
                .collect(Collectors.toList());

        for (String memberId : memberIds) {
            List<Team> teamsForCourseStudent = studentRepository
                    .getOne(memberId)
                    .getTeams()
                    .stream()
                    .filter(t -> t.getCourse().getName().equals(courseName))
                    .collect(Collectors.toList());

            List<Token> tokensForCourseStudent = tokenRepository
                    .findAllByStudentId(memberId)
                    .stream()
                    .filter(tk ->
                            tk.getIsTeam().equals(true)
                                    && (
                            teamRepository
                                    .getOne(tk.getTeamId())
                                    .getCourse()
                                    .getName()
                                    .equals(courseName)))
                    .collect(Collectors.toList());

            if(!availableIds.contains(memberId) || (teamsForCourseStudent.size() != tokensForCourseStudent.size()))
                throw new TeamServiceException("Lo studente con matricola " + memberId + " ha già accettato una proposta per " + courseName);
        }

        for(String memberId : memberIds) {
            if(courseService.hasAlreadyATeamFor(memberId, courseName))
                throw new TeamServiceException("Student " + memberId + " has already a team for course " + courseName);
            team.addMember(studentRepository.getOne(memberId));
        }

        Student creator_entity = studentRepository.getOne(creator);
        team.setCreator(creator_entity);

        Team t = teamRepository.save(team);

        return modelMapper.map(t, TeamDTO.class);
    }

    /*
    * Dato un team (identificato da courseName e teamName) viene tornato l'attuale utilizzo di VCPU nell'ambito della
    * allocazione di VMs.
    * */
    @Override
    public int getUsedNVCpuForTeam(String courseName, String teamName) throws TeamNotFoundException {
        Team t = teamRepository.getTeamByCourseAndName(courseName, teamName);
        if(t == null)
            throw new TeamNotFoundException(teamName);

        return t
                .getVms()
                .stream()
                .map(vm -> vm.getNVCpu())
                .reduce(0, Integer :: sum);
    }

    /*
     * Dato un team (identificato da courseName e teamName) viene tornato l'attuale utilizzo di disco nell'ambito della
     * allocazione di VMs.
     * */
    @Override
    public int getUsedDiskForTeam(String courseName, String teamName) throws TeamNotFoundException {
        Team t = teamRepository.getTeamByCourseAndName(courseName, teamName);
        if(t == null)
            throw new TeamNotFoundException(teamName);

        return t
                .getVms()
                .stream()
                .map(vm -> vm.getDisk())
                .reduce(0, Integer :: sum);
    }

    /*
     * Dato un team (identificato da courseName e teamName) viene tornato l'attuale utilizzo di RAM nell'ambito della
     * allocazione di VMs.
     * */
    @Override
    public int getUsedRamForTeam(String courseName, String teamName) throws TeamNotFoundException {
        Team t = teamRepository.getTeamByCourseAndName(courseName, teamName);
        if(t == null)
            throw new TeamNotFoundException(teamName);

        return t
                .getVms()
                .stream()
                .map(vm -> vm.getRam())
                .reduce(0, Integer :: sum);
    }


    /*
     * Torna tutte le VMs istanziate dai membri di un team.
     * */
    @Override
    public List<VmDTO> getVmsForTeam(String courseName, String teamName) throws TeamNotFoundException {
        Team t = teamRepository.getTeamByCourseAndName(courseName, teamName);
        if(t == null)
            throw new TeamNotFoundException(teamName);

        return t
                .getVms()
                .stream()
                .map(vm -> modelMapper.map(vm, VmDTO.class))
                .collect(Collectors.toList());
    }

    /*
    * Come sopra, ma a partire dall'id del team.
    * */
    @Override
    public List<VmDTO> getVmsForTeamById(int id) throws TeamNotFoundException {
        if(!teamRepository.existsById(id))
            throw new TeamNotFoundException(id);

        return teamRepository
                .getOne(id)
                .getVms()
                .stream()
                .map(vm -> modelMapper.map(vm, VmDTO.class))
                .collect(Collectors.toList());
    }

    /*
    * Ritorna la matricola dello studente creatore del gruppo.
    * */
    @Override
    public String getCreator(int id) throws TeamNotFoundException {
        if (!teamRepository.existsById(id))
            throw new TeamNotFoundException(id);

        return teamRepository
                .getOne(id)
                .getCreator()
                .getId();
    }

    /*
    * Dato il nome di un gruppo nell'ambito di un corso, viene tornato l'id del gruppo stesso.
    * */
    @Override
    public int getTeamId(String courseName, String teamName) throws TeamNotFoundException {
        if(teamRepository.getTeamByCourseAndName(courseName, teamName) == null)
            throw new TeamNotFoundException(teamName);
        return teamRepository
                .getTeamByCourseAndName(courseName, teamName)
                .getId();
    }
}
