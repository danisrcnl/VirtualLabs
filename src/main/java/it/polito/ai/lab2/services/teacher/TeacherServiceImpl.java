package it.polito.ai.lab2.services.teacher;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.TeacherDTO;
import it.polito.ai.lab2.entities.Teacher;
import it.polito.ai.lab2.entities.TeacherNotFoundException;
import it.polito.ai.lab2.repositories.TeacherRepository;
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
    ModelMapper modelMapper;

    @Override
    public Optional<TeacherDTO> getTeacher(String teacherId) {
        if(!teacherRepository.existsById(teacherId))
            return Optional.empty();
        Teacher t = teacherRepository.getOne(teacherId);
        return Optional.ofNullable(modelMapper.map(t, TeacherDTO.class));
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
}
