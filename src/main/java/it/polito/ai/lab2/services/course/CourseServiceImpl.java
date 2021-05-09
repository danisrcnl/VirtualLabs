package it.polito.ai.lab2.services.course;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.lab2.dtos.*;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.repositories.CourseRepository;
import it.polito.ai.lab2.repositories.StudentRepository;
import it.polito.ai.lab2.repositories.TeacherRepository;
import it.polito.ai.lab2.repositories.TeamRepository;
import it.polito.ai.lab2.services.AiException;
import it.polito.ai.lab2.services.assignment.AssignmentService;
import it.polito.ai.lab2.services.auth.AuthenticationService;
import it.polito.ai.lab2.services.student.StudentService;
import it.polito.ai.lab2.services.team.TeamService;
import it.polito.ai.lab2.services.team.TeamServiceException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    StudentService studentService;

    @Autowired
    TeamService teamService;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    AssignmentService assignmentService;

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
        if(!courseRepository.existsById(name))
            return Optional.empty();
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
    public List<StudentDTO> getNotEnrolled(String courseName) throws CourseNotFoundException {
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        Course c = courseRepository.getOne(courseName);

        return studentRepository
                .findAll()
                .stream()
                .filter(s -> !s.getCourses().contains(c))
                .map(s -> modelMapper.map(s, StudentDTO.class))
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

        List<Long> assignmentIds = courseRepository
                .getOne(courseName)
                .getAssignments()
                .stream()
                .map(a -> a.getId())
                .collect(Collectors.toList());

        Long paperId;
        for (Long id : assignmentIds) {
            paperId = assignmentService.addPaper(PaperDTO.builder().build(), courseName, studentId);
            assignmentService.linkPaperToAssignment(paperId, id);
        }

        User u = studentRepository.getOne(studentId).getUser();
        if(u != null)
            authenticationService.setPrivileges(u.getUsername(), Arrays.asList("ROLE_COURSE_" + courseName + "_STUDENT"));

        return true;
    }

    @Override
    public boolean evictOne (String studentId, String courseName) throws StudentNotFoundException, CourseNotFoundException {
        if (!studentRepository.existsById(studentId))
            throw new StudentNotFoundException(studentId);
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);
        if (hasAlreadyATeamFor(studentId, courseName))
            throw new TeamServiceException("Cannot delete student belonging to a team");

        Student s = studentRepository.getOne(studentId);
        Course c = courseRepository.getOne(courseName);
        s.getCourses().remove(c);
        c.getStudents().remove(s);
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

        User u = teacherRepository.getOne(teacherId).getUser();
        if (u != null)
            authenticationService.setPrivileges(u.getUsername(), Arrays.asList("ROLE_COURSE_" + courseName + "_TEACHER"));

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
        List<Integer> teams = courseRepository
                .getOne(courseName)
                .getTeams()
                .stream()
                .map(Team :: getId)
                .collect(Collectors.toList());
        for(Integer i : teams) {
            teamService.evictTeamById(i.intValue());
        }
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

    @Override
    public List<Boolean> addAndEnroll(Reader r, String courseName) {
        CsvToBean<StudentDTO> csvToBean = new CsvToBeanBuilder(r)
                .withType(StudentDTO.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        List<StudentDTO> students = csvToBean.parse();
        studentService.addAll(students);

        List<String> Ids = new ArrayList<String>();

        for (StudentDTO student : students) Ids.add(student.getId());

        return enrollAll(Ids, courseName);
    }

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

        Course c = courseRepository.getOne(courseName);

        return courseRepository
                .getStudentsNotInTeams(courseName)
                .stream()
                .filter(s -> s.getCourses().contains(c))
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
    public CourseDTO getTeamCourse(int teamId) throws TeamNotFoundException {
        if(!teamRepository.existsById(teamId))
            throw new TeamNotFoundException(teamId);
        return modelMapper.map(teamRepository
                .getOne(teamId)
                .getCourse(), CourseDTO.class);
    }
}
