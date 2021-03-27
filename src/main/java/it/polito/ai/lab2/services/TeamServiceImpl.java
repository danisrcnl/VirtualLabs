package it.polito.ai.lab2.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.lab2.dtos.*;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.repositories.CourseRepository;
import it.polito.ai.lab2.repositories.StudentRepository;
import it.polito.ai.lab2.repositories.TeacherRepository;
import it.polito.ai.lab2.repositories.TeamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    ModelMapper modelMapper;


    @Override
    public boolean addCourse(CourseDTO course) {
        Course c = modelMapper.map(course, Course.class);
        if(courseRepository.existsById(course.getName()))
            return false;
        courseRepository.save(c);
        courseRepository.flush();
        return true;
    }

    @Override
    public Optional<CourseDTO> getCourse(String name) {
        Course c = courseRepository.getOne(name);
        return Optional.ofNullable(modelMapper.map(c, CourseDTO.class));
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepository
                .findAll()
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean addStudent(StudentDTO student) {
        Student s = modelMapper.map(student, Student.class);
        if(studentRepository.existsById(student.getId()))
            return false;
        studentRepository.save(s);
        studentRepository.flush();
        return true;
    }

    @Override
    public Optional<StudentDTO> getStudent(String studentId) {
        Student s = studentRepository.getOne(studentId);
        return Optional.ofNullable(modelMapper.map(s, StudentDTO.class));
    }

    @Override
    public Optional<TeacherDTO> getTeacher(String teacherId) {
        if(!teacherRepository.existsById(teacherId))
            return Optional.empty();
        Teacher t = teacherRepository.getOne(teacherId);
        return Optional.ofNullable(modelMapper.map(t, TeacherDTO.class));
    }

    @Override
    public List<StudentDTO> getAllStudents() {
        return studentRepository
                .findAll()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getEnrolledStudents(String courseName) throws CourseNotFoundException {
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);
        return courseRepository
                .getOne(courseName)
                .getStudents()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean addTeacher(TeacherDTO teacher) {
        Teacher t = modelMapper.map(teacher, Teacher.class);
        if(teacherRepository.existsById(teacher.getId()))
            return false;
        teacherRepository.save(t);
        teacherRepository.flush();
        return true;
    }

    @Override
    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository
                .findAll()
                .stream()
                .map(t -> modelMapper.map(t, TeacherDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TeacherDTO> getTeachersForCourse(String courseName) throws CourseNotFoundException {
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);
        return courseRepository
                .getOne(courseName)
                .getTeachers()
                .stream()
                .map(t -> modelMapper.map(t, TeacherDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseDTO> getCoursesForTeacher(String teacherId) throws TeacherNotFoundException {
        if(!teacherRepository.existsById(teacherId))
            throw new TeacherNotFoundException(teacherId);

        return teacherRepository
                .getOne(teacherId)
                .getCourses()
                .stream()
                .map(t -> modelMapper.map(t, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseDTO> getCoursesForStudent(String studentId) throws StudentNotFoundException {
        if(!studentRepository.existsById(studentId))
            throw new StudentNotFoundException(studentId);

        return studentRepository
                .getOne(studentId)
                .getCourses()
                .stream()
                .map(t -> modelMapper.map(t, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean addStudentToCourse(String studentId, String courseName) throws CourseNotFoundException, StudentNotFoundException {
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);
        if(!studentRepository.existsById(studentId))
            throw new StudentNotFoundException(studentId);

        List<Student> students = courseRepository
                .getOne(courseName)
                .getStudents();

        for (Student student : students) {
            if (student.getId().equals(studentId))
                return false;
        }

        courseRepository
                .getOne(courseName)
                .addStudent(studentRepository.getOne(studentId));

        return true;
    }

    @Override
    public boolean addTeacherToCourse(String teacherId, String courseName) throws CourseNotFoundException, TeacherNotFoundException {

        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);
        if(!teacherRepository.existsById(teacherId))
            throw new TeacherNotFoundException(teacherId);

        List<Teacher> teachers = courseRepository
                .getOne(courseName)
                .getTeachers();

        for(Teacher teacher : teachers) {
            if(teacher.getId().equals(teacherId))
                return false;
        }

        courseRepository
                .getOne(courseName)
                .addTeacher(teacherRepository.getOne(teacherId));

        return true;

    }

    @Override
    public void enableCourse(String courseName) throws CourseNotFoundException {
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        courseRepository
                .getOne(courseName)
                .setEnabled(true);
    }

    @Override
    public void disableCourse(String courseName) throws CourseNotFoundException {
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        courseRepository
                .getOne(courseName)
                .setEnabled(false);
    }

    @Override
    public void deleteCourse(String courseName) throws CourseNotFoundException {
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        Course c = courseRepository.getOne(courseName);
        c.removeRelations();
        courseRepository.delete(c);
        courseRepository.flush();
    }

    @Override
    public void editCourseName(String courseName, String newName) throws CourseNotFoundException {

        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        courseRepository
                .getOne(courseName)
                .setName(newName);
    }

    @Override
    public List<Boolean> addAll(List<StudentDTO> students) {
        List<Boolean> successes = new ArrayList<Boolean>();
        for (StudentDTO student : students) successes.add(addStudent(student));
        return successes;
    }

    @Override
    public List<Boolean> enrollAll(List<String> studentIds, String courseName) throws CourseNotFoundException, StudentNotFoundException {
        List<Boolean> successes = new ArrayList<Boolean>();
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);
        for (String studentId : studentIds) {
            try {
                successes.add(addStudentToCourse(studentId, courseName));
            } catch (StudentNotFoundException e) {
                successes.add(false);
            }
        }
        return successes;
    }

    public List<Boolean> addAndEnroll(Reader r, String courseName) {
        CsvToBean<StudentDTO> csvToBean = new CsvToBeanBuilder(r)
                .withType(StudentDTO.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        List<StudentDTO> students = csvToBean.parse();
        addAll(students);

        List<String> Ids = new ArrayList<String>();

        for (StudentDTO student : students) Ids.add(student.getId());

        return enrollAll(Ids, courseName);
    }

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
/*
    @Override
    public List<CourseDTO> getTeacherCourses(String teacherId) throws TeacherNotFoundException {
        if(!teacherRepository.existsById(teacherId))
            throw new TeacherNotFoundException(teacherId);

        return teacherRepository
                .getOne(teacherId)
                .getCourses()
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }
*/
    @Override
    public void setMinForCourse(int value, String courseName) throws CourseNotFoundException {
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        courseRepository
                .getOne(courseName)
                .setMin(value);
    }

    @Override
    public void setMaxForCourse(int value, String courseName) throws CourseNotFoundException {
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        courseRepository
                .getOne(courseName)
                .setMax(value);
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
/*
    @Override
    public List<CourseDTO> getCourses(String studentId) throws StudentNotFoundException {
        if(!studentRepository.existsById(studentId))
            throw new StudentNotFoundException(studentId);

        return studentRepository
                .getOne(studentId)
                .getCourses()
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }
*/
    @Override
    public List<TeamDTO> getTeamsForStudent(String studentId) throws StudentNotFoundException {
        if(!studentRepository.existsById(studentId))
            throw new StudentNotFoundException(studentId);
        return studentRepository
                .getOne(studentId)
                .getTeams()
                .stream()
                .map(t -> modelMapper.map(t, TeamDTO.class))
                .collect(Collectors.toList());
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
    public Boolean hasAlreadyATeamFor(String studentId, String courseName) throws StudentNotFoundException, CourseNotFoundException {
        if(!studentRepository.existsById(studentId))
            throw new StudentNotFoundException(studentId);
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);
        List<String> ids = this
                .getStudentsInTeams(courseName)
                .stream()
                .map(StudentDTO :: getId)
                .collect(Collectors.toList());
        if(ids.contains(studentId))
            return true;
        return false;
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

        List<String> availableIds = getAvailableStudents(courseName)
                .stream()
                .map(StudentDTO::getId)
                .collect(Collectors.toList());

        for (String memberId : memberIds) {
            if(!availableIds.contains(memberId))
                throw new TeamServiceException("Student " + memberId + " has already a team for course " + courseName);
        }

        for(String memberId : memberIds) {
            if(hasAlreadyATeamFor(memberId, courseName))
                throw new TeamServiceException("Student " + " has already a team for course " + courseName);
            team.addMember(studentRepository.getOne(memberId));
        }

        Team t = teamRepository.save(team);


        return modelMapper.map(t, TeamDTO.class);
    }

    @Override
    public List<TeamDTO> getTeamForCourse(String courseName) throws CourseNotFoundException {
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        return courseRepository
                .getOne(courseName)
                .getTeams()
                .stream()
                .map(t -> modelMapper.map(t, TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamDTO> getStudentTeamInCourse(String id, String courseName) throws CourseNotFoundException, StudentNotFoundException {

        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        if(!studentRepository.existsById(id))
            throw new StudentNotFoundException(id);

        return studentRepository
                .getOne(id)
                .getTeams()
                .stream()
                .filter(t -> t.getCourse().getName().equals(courseName))
                .map(t -> modelMapper.map(t, TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getStudentsInTeams(String courseName) throws CourseNotFoundException {
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        return courseRepository
                .getStudentsInTeams(courseName)
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getAvailableStudents(String courseName) throws CourseNotFoundException {
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        return courseRepository
                .getStudentsNotInTeams(courseName)
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
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
    public List<VmDTO> getVmsForStudent(String studentId) throws StudentNotFoundException {
        if(!studentRepository.existsById(studentId))
            throw new StudentNotFoundException(studentId);

        return studentRepository
                .getOne(studentId)
                .getVms()
                .stream()
                .map(vm -> modelMapper.map(vm, VmDTO.class))
                .collect(Collectors.toList());
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
    public UnknownUserDTO getDetailsFromUsername(String username) {
        return null;
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
