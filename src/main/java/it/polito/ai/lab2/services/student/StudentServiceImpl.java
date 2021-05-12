package it.polito.ai.lab2.services.student;


import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.repositories.StudentRepository;
import it.polito.ai.lab2.repositories.UserRepository;
import it.polito.ai.lab2.services.auth.AuthenticationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Autowired
    AuthenticationService authenticationService;

    /*
    * Costruisce un'entità student mappata sul DTO ricevuto e la salva nel database, qualora non già presente.
    * */
    @Override
    public boolean addStudent(StudentDTO student) {
        Student s = modelMapper.map(student, Student.class);
        if(studentRepository.existsById(student.getId()))
            return false;
        studentRepository.save(s);
        studentRepository.flush();
        return true;
    }

    /*
    * Ritorna un DTO dello studente associato alla matricola ricevuta come parametro.
    * */
    @Override
    public Optional<StudentDTO> getStudent(String studentId) {
        if(studentRepository.existsById(studentId))
            return Optional.ofNullable(modelMapper.map(studentRepository.getOne(studentId), StudentDTO.class));
        else
            return Optional.empty();
    }

    /*
    * Restituisce tutti gli studenti presenti nel db.
    * */
    @Override
    public List<StudentDTO> getAllStudents() {
        return studentRepository
                .findAll()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    /*
    * Dato uno studente, restituisce tutti i corsi a cui è iscritto.
    * */
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

    /*
    * Data una lista di DTOs, chiama iterativamente la addStudent e ne salva i risultati in un vettore di Boolean,
    * come nel caso della enrollAll.
    * */
    @Override
    public List<Boolean> addAll(List<StudentDTO> students) {
        List<Boolean> successes = new ArrayList<Boolean>();
        for (StudentDTO student : students) successes.add(addStudent(student));
        return successes;
    }

    /*
    * Data la matricola di uno studente, vengono trovati e restituiti tutti i team di cui fa parte.
    * */
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

    /*
     * Data la matricola di uno studente, vengono trovate e restituite tutte le vms ad esso associate.
     * */
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

    /*
    * Dati uno username e uno studentId, il metodo associa le due entità reperite dal db. Nel caso in cui lo studente
    * già esistesse perche aggiunto tramite csv dal docente, per tutti i corsi a cui lo studente risulta iscritto, viene
    * aggiunto il corrispondente ruolo di autorizzazione nella lista dei ruoli dello user appena agganciato.
    * */
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
        List<String> roles = u.getRoles();
        List<String> courses = s.getCourses().stream().map(Course::getName).collect(Collectors.toList());
        for(String courseName : courses) {
            if(!roles.contains("ROLE_COURSE_" + courseName + "_STUDENT"))
                authenticationService.setPrivileges(u.getUsername(), Arrays.asList("ROLE_COURSE_" + courseName + "_STUDENT"));
        }
    }

}
