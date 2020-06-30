package it.polito.ai.lab2.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeacherDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
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
    public List<Boolean> addAll(List<StudentDTO> students) {
        List<Boolean> successes = new ArrayList<Boolean>();
        for(int i = 0; i<students.size(); i++)
            successes.add(addStudent(students.get(i)));
        return successes;
    }

    @Override
    public List<Boolean> enrollAll(List<String> studentIds, String courseName) throws CourseNotFoundException, StudentNotFoundException {
        List<Boolean> successes = new ArrayList<Boolean>();
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);
        for(int i = 0; i<studentIds.size(); i++) {
            try {
                successes.add(addStudentToCourse(studentIds.get(i), courseName));
            } catch(StudentNotFoundException e) {
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

        for(int i = 0; i<students.size(); i++)
            Ids.add(students.get(i).getId());

        return enrollAll(Ids, courseName);
    }

    @Override
    public void activateTeam(Long teamId) throws TeamNotFoundException {
        if(!teamRepository.existsById(teamId))
            throw new TeamNotFoundException(teamId.toString());

        teamRepository.getOne(teamId).setStatus(1);
    }

    @Override
    public void evictTeam(Long teamId) throws TeamNotFoundException {
        if(!teamRepository.existsById(teamId))
            throw new TeamNotFoundException(teamId.toString());

        Team t = teamRepository.getOne(teamId);
        teamRepository.delete(t);
        teamRepository.flush();
    }

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

    @Override
    public TeamDTO getTeamForStudent(String studentId) throws StudentNotFoundException {
        if(!studentRepository.existsById(studentId))
            throw new StudentNotFoundException(studentId);
        return modelMapper
                .map(studentRepository
                    .getOne(studentId)
                    .getTeam(), TeamDTO.class);
    }

    @Override
    public List<StudentDTO> getMembers(Long teamId) throws TeamNotFoundException {
        if(!teamRepository.existsById(teamId))
            throw new TeamNotFoundException(teamId.toString());
        return teamRepository
                .getOne(teamId)
                .getMembers()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public TeamDTO proposeTeam(String courseName, String name, List<String> memberIds)
        throws CourseNotFoundException, StudentNotFoundException {

        Team team;
        List<Student> members = new ArrayList<>();

        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        team = Team.builder()
                .name(name)
                .members(members)
                .build();
        team.setCourse(courseRepository.getOne(courseName));

        List<String> availableIds = getAvailableStudents(courseName)
                .stream()
                .map(StudentDTO::getId)
                .collect(Collectors.toList());
        for (String memberId : memberIds) {
            if(!availableIds.contains(memberId))
                throw new TeamServiceException();
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
}
