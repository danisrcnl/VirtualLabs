package it.polito.ai.lab2.services.student;


import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.repositories.StudentRepository;
import it.polito.ai.lab2.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    UserRepository userRepository;

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
    public List<StudentDTO> getAllStudents() {
        return studentRepository
                .findAll()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
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
    public List<Boolean> addAll(List<StudentDTO> students) {
        List<Boolean> successes = new ArrayList<Boolean>();
        for (StudentDTO student : students) successes.add(addStudent(student));
        return successes;
    }

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
    public void linkToUser(String studentId, String userId) throws StudentNotFoundException, UserNotFoundException {
        if(!studentRepository.existsById(studentId))
            throw new StudentNotFoundException(studentId);
        if(!userRepository.findByUsername(userId).isPresent())
            throw new UserNotFoundException(userId);
        User u = userRepository.findByUsername(userId).get();
        Student s = studentRepository.getOne(studentId);
        s.setUser(u);
        u.setTeacher(null);
        u.setStudent(s);
        userRepository.flush();
        studentRepository.flush();
    }

}
