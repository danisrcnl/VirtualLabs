/*
 * CourseController.java:
 * Classe deputata a offrire APIs accessibili tramite richieste HTTP per la gestione dei corsi e l'accesso alle loro
 * proprietà e relazioni
 * */

package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dataStructures.CourseWithTeacher;
import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeacherDTO;
import it.polito.ai.lab2.services.AiException;
import it.polito.ai.lab2.services.course.CourseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/API/courses")
public class CourseController {

    @Autowired
    CourseService courseService;

    /*
    * Metodo che torna tutti i corsi presenti nel database.
    * */
    @GetMapping({"", "/"})
    public List<CourseDTO> all() {
        return courseService
                .getAllCourses()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    /*
    * Metodo che torna il corso con nome pari a quello contenuto nella richiesta. Il nome è univoco.
    * */
    @GetMapping("/{name}")
    public CourseDTO getOne (@PathVariable String name) throws ResponseStatusException {
        Optional<CourseDTO> course = courseService.getCourse(name);
        if(course.isPresent())
            return ModelHelper.enrich(course.get());
        else
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course " + name + " doesn't exist");
    }

    /*
    * Metodo che torna tutti gli studenti iscritti al corso specificato nell'url della richiesta.
    * */
    @GetMapping("/{name}/enrolled")
    public List<StudentDTO> enrolledStudents (@PathVariable String name) throws ResponseStatusException {
        List<StudentDTO> enrolled;
        try {
            enrolled = courseService.getEnrolledStudents(name);
        } catch(AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }
        return enrolled
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    /*
    * Metodo che torna tutti gli studenti presenti nel database (confermati) che ancora non sono iscritti al corso
    * specificato e che dunque possono essere iscritti dal docente.
    * */
    @GetMapping("/{name}/notEnrolled")
    public List<StudentDTO> notEnrolledStudents (@PathVariable String name) throws ResponseStatusException {
        List<StudentDTO> notEnrolled;
        try {
            notEnrolled = courseService.getNotEnrolled(name);
        } catch(AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }
        return notEnrolled
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    /*
    * Metodo che permette l'aggiunta di un nuovo corso al database. In quanto creato da un docente, ne richiede la
    * matricola. In tal modo il corso verrà aggiunto al database e successivamente il docente aggiunto al corso appena
    * creato.
    * */
    @PostMapping({"", "/"})
    public CourseDTO addCourse (@RequestBody CourseWithTeacher courseWithTeacher)
            throws ResponseStatusException {
        if(courseService.addCourse(courseWithTeacher.getCourseDTO())) {
            try {
                courseService.addTeacherToCourse(courseWithTeacher.getTeacherId(), courseWithTeacher.getCourseDTO().getName());
            } catch (AiException e) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
            }
            return ModelHelper.enrich(courseWithTeacher.getCourseDTO());
        }
        else
            throw new ResponseStatusException(HttpStatus.CONFLICT, courseWithTeacher.getCourseDTO().getName());
    }

    /*
    * Il metodo rende possibile l'aggiunta del docente con id pari a teacherId al corso indicato nell'url. Al termine
    * ritorna la lista dei docenti che tengono il corso.
    * */
    @GetMapping("/{courseName}/addTeacher/{teacherId}")
    public List<TeacherDTO> addTeacherToCourse (@PathVariable String courseName, @PathVariable String teacherId)
            throws ResponseStatusException {
        try {
            courseService.addTeacherToCourse(teacherId, courseName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }
        return courseService
                .getTeachersForCourse(courseName)
                .stream()
                .map(ModelHelper :: enrich)
                .collect(Collectors.toList());
    }

    /*
    * Metodo che permette l'aggiunta al corso desiderato dello studente il cui DTO è stato passato come corpo
    * della richiesta.
    * */
    @PostMapping({"/{name}/enrollOne"})
    public List<StudentDTO> enrollOne (@PathVariable String name, @RequestBody StudentDTO studentDTO)
            throws ResponseStatusException {
        Optional<CourseDTO> course = courseService.getCourse(name);
        if(!course.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course " + name + " doesn't exist");
        if(!courseService.addStudentToCourse(studentDTO.getId(), name))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Couldn't enroll student");
        return enrolledStudents(name);
    }

    /*
    * In questo caso il docente può caricare un csv contenete le informazioni degli studenti. Queste saranno usate
    * per crearne le entrate corrispondenti nel database e successivamente iscrivere i suddetti studenti al corso
    * selezionato. E' questo un caso in cui lo studente può esistere anche prima che l'utente corrispondente sia
    * registrato.
    * */
    @PostMapping("{name}/enrollMany")
    public List<StudentDTO> enrollMany (@PathVariable String name, @RequestParam("file") MultipartFile multipartFile)
                                                            throws UnsupportedMediaTypeStatusException {
        if(!multipartFile.getContentType().equals("text/csv"))
            throw new UnsupportedMediaTypeStatusException(multipartFile.getContentType());
        try {
            Reader reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()));
            courseService.addAndEnroll(reader, name);
        } catch(Exception e) {
            System.out.println("Exception generated in reader block");
        }
        return enrolledStudents(name);
    }


    /*
    * Permette l'eliminazione del corso con nome pari a quello indicato.
    * */
    @GetMapping("/{courseName}/delete")
    public List<CourseDTO> delete (@PathVariable String courseName) throws ResponseStatusException {
        try {
            courseService.deleteCourse(courseName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return courseService.getAllCourses();
    }

    /*
    * Metodo che torna la lista di studenti iscritti ancora non uniti ad alcun team per il corso indicato con courseName.
    * */
    @GetMapping("/{courseName}/getAvailableStudents")
    public List<StudentDTO> getAvailableStudents (@PathVariable String courseName) {
        return courseService
                .getAvailableStudents(courseName)
                .stream()
                .map(ModelHelper :: enrich)
                .collect(Collectors.toList());
    }

    /*
    * Metodo che permette di settare il valore minimo di studenti per team in un determinato corso.
    * */
    @GetMapping("/{courseName}/setMin/{value}")
    public CourseDTO setMin (@PathVariable String courseName, @PathVariable int value) throws ResponseStatusException {

        try {
            courseService.setMinForCourse(value, courseName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return ModelHelper.enrich(
            courseService
            .getCourse(courseName)
            .get()
        );
    }

    /*
     * Metodo che permette di settare il valore massimo di studenti per team in un determinato corso.
     * */
    @GetMapping("/{courseName}/setMax/{value}")
    public CourseDTO setMax (@PathVariable String courseName, @PathVariable int value) throws ResponseStatusException {

        try {
            courseService.setMaxForCourse(value, courseName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return ModelHelper.enrich(
                courseService
                        .getCourse(courseName)
                        .get()
        );
    }

    /*
     * Metodo che permette di abilitare o disabilitare un determinato corso.
     * */
    @GetMapping("/{courseName}/setEnabled/{value}")
    public CourseDTO setEnabled (@PathVariable String courseName, @PathVariable Boolean value) throws ResponseStatusException {

        if(!(value.equals(true) || value.equals(false)))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bad input");

        try {
            if(value)
                courseService.enableCourse(courseName);
            else
                courseService.disableCourse(courseName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return ModelHelper.enrich(
                courseService
                        .getCourse(courseName)
                        .get()
        );
    }

    /*
    * Metodo che permette di eliminare un dato studente dalla lista degli iscritti al corso indicato.
    * */
    @GetMapping("/{courseName}/{studentId}/evict")
    public List<StudentDTO> evictOne (@PathVariable String studentId, @PathVariable String courseName) throws ResponseStatusException {

        try {
            courseService.evictOne(studentId, courseName);
        } catch (AiException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getErrorMessage());
        }

        return courseService
                .getEnrolledStudents(courseName)
                .stream()
                .map(ModelHelper :: enrich)
                .collect(Collectors.toList());

    }

}
