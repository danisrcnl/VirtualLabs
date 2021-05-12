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

    /*
    * Aggiunge il DTO ricevuto, mappato su corrispondente entità nel db dei corsi.
    * */
    @Override
    public boolean addCourse(CourseDTO course) {
        Course c = modelMapper.map(course, Course.class);
        if(courseRepository.existsById(course.getName()))
            return false;
        courseRepository.save(c);
        courseRepository.flush();
        return true;
    }

    /*
    * Mappa su un DTO il corso reperito dal db con nome uguale a quello richiesto. Se presente lo torna come Optional,
    * altrimenti tornerà un Empty.
    * */
    @Override
    public Optional<CourseDTO> getCourse(String name) {
        if(!courseRepository.existsById(name))
            return Optional.empty();
        Course c = courseRepository.getOne(name);
        return Optional.ofNullable(modelMapper.map(c, CourseDTO.class));
    }

    /*
    * Torna tutti i corsi presenti nel db, mappati su DTOs corrispondenti.
    * */
    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepository
                .findAll()
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }


    /*
    * Dato un corso, vengono restituiti tutti gli studenti in relazione con esso.
    * */
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

    /*
    * Esatto contrario del metodo precedente. Restituisce tutti gli studenti attivi che non sono ancora presenti nel
    * corso.
    * */
    @Override
    public List<StudentDTO> getNotEnrolled(String courseName) throws CourseNotFoundException {
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        Course c = courseRepository.getOne(courseName);

        return studentRepository
                .findAll()
                .stream()
                .filter(s -> !s.getCourses().contains(c) && s.getUser() != null)
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }


    /*
    * Dato un corso, vengono ritornati tutti i docenti che sono in relazione con esso.
    * */
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

    /*
    * Per prima cosa verifica che lo studente da aggiungere non sia già presente nel corso. Dopo di ciò lo studente viene
    * effettivamente aggiunto alla lista degli iscritti. A cascata, allo studente vengono aggiunti tanti papers quanti
    * sono gli assignments del corso. Se lo studente è agganciato a uno user, allora a esso vengono aggiunti i privilegi
    * di studente del corso indicato.
    * */
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

    /*
    * Rende possibile l'eliminazione di uno studente dal corso selezionato, se e solo se questo non appartenga a un
    * team, in quel caso viene lanciata una corrispondente eccezione. Se va a buon fine, elimina anche il ruolo dello
    * user corrispondente.
    * */
    @Override
    public boolean evictOne (String studentId, String courseName) throws StudentNotFoundException, CourseNotFoundException {
        if (!studentRepository.existsById(studentId))
            throw new StudentNotFoundException(studentId);
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);
        if (hasAlreadyATeamFor(studentId, courseName))
            throw new TeamServiceException("Non puoi eliminare uno studente che fa parte di un team");

        Student s = studentRepository.getOne(studentId);
        Course c = courseRepository.getOne(courseName);
        s.getCourses().remove(c);
        c.getStudents().remove(s);

        User u = s.getUser();
        if(u != null)
            u.getRoles().remove("ROLE_COURSE_" + courseName + "_STUDENT");

        return true;
    }

    /*
    * Se non già presente, aggiunge il teacher selezionato alla lista dei docenti del corso. Al termine aggiunge il
    * privilegio di teacher per quel corso allo user corrispondente.
    * */
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

    /*
    * Abilita un corso, settando a true la proprietà enabled.
    * */
    @Override
    public void enableCourse(String courseName) throws CourseNotFoundException {
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        courseRepository
                .getOne(courseName)
                .setEnabled(true);
    }

    /*
    * Disabilita un corso, settando a false la proprietà enabled.
    * */
    @Override
    public void disableCourse(String courseName) throws CourseNotFoundException {
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        courseRepository
                .getOne(courseName)
                .setEnabled(false);
    }

    /*
    * Elimina il corso selezionato. Prima rimuove tutte le relazioni con le corrispondenti entità.
    * */
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

    /*
    * Data una lista di matricole, viene tentata la loro aggiunta al corso selezionato. Il risultato dell'aggiunta
    * (un booleano come da specifiche) viene memorizzato in una lista di booleani che servirà a rappresentare l'esito
    * di ogni singola aggiunta. La lista viene dunque restituita come return value.
    * */
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

    /*
    * Effettua il parse del csv che viene trasformato in Reader dal metodo di controllore. Dopo di ciò chiama la
    * addAll e la enrollAll precedentemente definite.
    * */
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

    /*
    * Setta il valore minimo di studenti per team per il corso courseName.
    * */
    @Override
    public void setMinForCourse(int value, String courseName) throws CourseNotFoundException {
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        courseRepository
                .getOne(courseName)
                .setMin(value);
    }

    /*
     * Setta il valore massimo di studenti per team per il corso courseName.
     * */
    @Override
    public void setMaxForCourse(int value, String courseName) throws CourseNotFoundException {
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        courseRepository
                .getOne(courseName)
                .setMax(value);
    }

    /*
    * Restituisce i teams presenti nel db, corrispondenti al corso indicato da courseName.
    * */
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

    /*
    * Se lo studente con matricola pari a id ha già un team per il corso indicato da courseName, questo viene inserito
    * in una lista di TeamDTO che verrà tornata come risultato. La sua dimensione sarà 0 se lo studente non ha ancora
    * un team per quel corso, 1 in caso contrario.
    * */
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

    /*
    * Ritorna gli studenti che per il corso hanno già un team, avvalendosi di un metodo del repository mappato su
    * una query HQL.
    * */
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

    /*
    * Duale del precedente, restituisce gli studenti che non hanno ancora un team nell'ambito del corso courseName.
    * Per un presunto errore nell'implementazione della query HQL, un ulteriore filtraggio viene effettuato sullo
    * stream.
    * */
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

    /*
    * Se getStudentsInTeam contiene la matricola dello studente passato come parametro alla funzione, torna true. False
    * altrimenti.
    * */
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

    /*
    * Dato un team, restituisce il corso cui esso appartiene.
    * */
    @Override
    public CourseDTO getTeamCourse(int teamId) throws TeamNotFoundException {
        if(!teamRepository.existsById(teamId))
            throw new TeamNotFoundException(teamId);
        return modelMapper.map(teamRepository
                .getOne(teamId)
                .getCourse(), CourseDTO.class);
    }
}
