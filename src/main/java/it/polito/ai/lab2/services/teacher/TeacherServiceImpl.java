package it.polito.ai.lab2.services.teacher;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.TeacherDTO;
import it.polito.ai.lab2.entities.Teacher;
import it.polito.ai.lab2.entities.TeacherNotFoundException;
import it.polito.ai.lab2.entities.User;
import it.polito.ai.lab2.entities.UserNotFoundException;
import it.polito.ai.lab2.repositories.TeacherRepository;
import it.polito.ai.lab2.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    /*
    * Dato un teacherId viene restituito il docente corrispondente, se presente.
    * */
    @Override
    public Optional<TeacherDTO> getTeacher(String teacherId) {
        if(!teacherRepository.existsById(teacherId))
            return Optional.empty();
        Teacher t = teacherRepository.getOne(teacherId);
        return Optional.ofNullable(modelMapper.map(t, TeacherDTO.class));
    }

    /*
    * Aggiunge al database un docente sulla base del DTO ricevuto in input.
    * */
    @Override
    public boolean addTeacher(TeacherDTO teacher) {
        Teacher t = modelMapper.map(teacher, Teacher.class);
        if(teacherRepository.existsById(teacher.getId()))
            return false;
        teacherRepository.save(t);
        teacherRepository.flush();
        return true;
    }

    /*
    * Ritorna tutti i docenti nel database.
    * */
    @Override
    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository
                .findAll()
                .stream()
                .map(t -> modelMapper.map(t, TeacherDTO.class))
                .collect(Collectors.toList());
    }

    /*
    * Dato un docente, restituisce tutti i corsi da egli tenuti.
    * */
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

    /*
    * Dati un teacherId e uno username, il docente e l'utente corrispondenti vengono associati in una relazione
    * one-to-one.
    * */
    @Override
    public void linkToUser(String teacherId, String userId) throws TeacherNotFoundException, UserNotFoundException {
        if(!teacherRepository.existsById(teacherId))
            throw new TeacherNotFoundException(teacherId);
        if(!userRepository.findByUsername(userId).isPresent())
            throw new UserNotFoundException(userId);
        User u = userRepository.findByUsername(userId).get();
        Teacher t = teacherRepository.getOne(teacherId);
        t.setUser(u);
        u.setTeacher(t);
        u.setStudent(null);
        teacherRepository.flush();
        userRepository.flush();
    }
}
