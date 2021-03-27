package it.polito.ai.lab2.services.team;

import it.polito.ai.lab2.dtos.*;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.repositories.CourseRepository;
import it.polito.ai.lab2.repositories.StudentRepository;
import it.polito.ai.lab2.repositories.TeacherRepository;
import it.polito.ai.lab2.repositories.TeamRepository;
import it.polito.ai.lab2.services.course.CourseService;
import it.polito.ai.lab2.services.student.StudentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
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
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    ModelMapper modelMapper;


    @Override
    public void activateTeamById(int id) throws TeamNotFoundException {
        if(!teamRepository.existsById(id))
            throw new TeamNotFoundException(id);

        teamRepository.getOne(id).setStatus(1);
    }

    @Override
    public void activateTeam(String courseName, String teamName) throws TeamNotFoundException {
        Team t = teamRepository.getTeamByCourseAndName(courseName, teamName);
        if(t == null)
            throw new TeamNotFoundException(teamName);

        t.setStatus(1);
    }

    @Override
    public void evictTeamById(int id) throws TeamNotFoundException {
        if(!teamRepository.existsById(id))
            throw new TeamNotFoundException(id);

        teamRepository.delete(teamRepository.getOne(id));
        teamRepository.flush();
    }

    @Override
    public void evictTeam(String courseName, String teamName) throws TeamNotFoundException {
        Team t = teamRepository.getTeamByCourseAndName(courseName, teamName);
        if(t == null)
            throw new TeamNotFoundException(teamName);

        teamRepository.delete(t);
        teamRepository.flush();
    }

    @Override
    public TeamDTO getTeam(String courseName, String teamName) throws CourseNotFoundException, TeamNotFoundException {
        Team t = teamRepository.getTeamByCourseAndName(courseName, teamName);
        if(t == null)
            throw new TeamNotFoundException(teamName);
        return modelMapper.map(t, TeamDTO.class);
    }

    @Override
    public TeamDTO getTeamById(int id) throws TeamNotFoundException {
        if(!teamRepository.existsById(id))
            throw new TeamNotFoundException(id);
        return modelMapper.map(
                teamRepository.getOne(id),
                TeamDTO.class
        );
    }

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

    @Override
    public TeamDTO proposeTeam(String courseName, String teamName, List<String> memberIds)
        throws TeamServiceException {

        Team team;
        List<Student> members = new ArrayList<>();

        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        if(teamRepository.getTeamByCourseAndName(courseName, teamName) != null)
            throw new TeamServiceException("Name " + teamName + " has been already chosen for course " + courseName);

        Course c = courseRepository.getOne(courseName);
        if(memberIds.size() > c.getMax() || memberIds.size() < c.getMin())
            throw new TeamServiceException("Be sure team is into the chosen min/max range");

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
            if(!availableIds.contains(memberId))
                throw new TeamServiceException("Student " + memberId + " has already a team for course " + courseName);
        }

        for(String memberId : memberIds) {
            if(courseService.hasAlreadyATeamFor(memberId, courseName))
                throw new TeamServiceException("Student " + " has already a team for course " + courseName);
            team.addMember(studentRepository.getOne(memberId));
        }

        Team t = teamRepository.save(team);


        return modelMapper.map(t, TeamDTO.class);
    }

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

    @Override
    public int getTeamId(String courseName, String teamName) throws TeamNotFoundException {
        if(teamRepository.getTeamByCourseAndName(courseName, teamName) == null)
            throw new TeamNotFoundException(teamName);
        return teamRepository
                .getTeamByCourseAndName(courseName, teamName)
                .getId();
    }
}
