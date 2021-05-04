package it.polito.ai.lab2.services.assignment;

import it.polito.ai.lab2.dataStructures.PaperStatus;
import it.polito.ai.lab2.dtos.AssignmentDTO;
import it.polito.ai.lab2.dtos.PaperDTO;
import it.polito.ai.lab2.dtos.PaperStatusTimeDTO;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.repositories.*;
import it.polito.ai.lab2.services.AiException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssignmentServiceImpl implements AssignmentService {

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    PaperRepository paperRepository;

    @Autowired
    PaperStatusTimeRepository paperStatusTimeRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Long addAssignment (AssignmentDTO assignment) {
        Assignment a = modelMapper.map(assignment, Assignment.class);
        assignmentRepository.save(a);
        assignmentRepository.flush();
        return a.getId();
    }

    @Override
    public Boolean addAssignmentToCourse (Long assignmentId, String courseName) {

        if (!courseRepository.existsById(courseName))
            return false;
        if (!assignmentRepository.existsById(assignmentId))
            return false;

        courseRepository
                .getOne(courseName)
                .addAssignment(
                        assignmentRepository
                        .getOne(assignmentId)
                );

        courseRepository.flush();

        return true;

    }

    @Override
    public Optional<AssignmentDTO> getAssignment (Long id) {
        Assignment a = assignmentRepository.getOne(id);
        return Optional.ofNullable(modelMapper.map(a, AssignmentDTO.class));
    }

    @Override
    public List<AssignmentDTO> getAllAssignments () {
        return assignmentRepository
                .findAll()
                .stream()
                .map(a -> modelMapper.map(a, AssignmentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentDTO> getCourseAssignments (String courseName) throws CourseNotFoundException {

        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        return courseRepository
                .getOne(courseName)
                .getAssignments()
                .stream()
                .map(a -> modelMapper.map(a, AssignmentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Long addPaper (PaperDTO paper, String courseName, String studentId) throws StudentNotFoundException {

        if(!studentRepository.existsById(studentId))
            throw new StudentNotFoundException(studentId);

        Paper p = modelMapper.map(paper, Paper.class);
        p.setEditable(true);
        p.setCreator(studentId);
        p.setStudent(studentRepository.getOne(studentId));

        paperRepository.save(p);
        paperRepository.flush();

        return p.getId();
    }

    @Override
    public boolean linkPaperToAssignment (Long paperId, Long assignmentId) throws PaperNotFoundException,
            AssignmentNotFoundException {
        if(!paperRepository.existsById(paperId))
            throw new PaperNotFoundException(paperId.toString());
        if(!assignmentRepository.existsById(assignmentId))
            throw new AssignmentNotFoundException(assignmentId.toString());

        List<Paper> papers = assignmentRepository
                .getOne(assignmentId)
                .getPapers();

        for(Paper paper : papers) {
            if(paper.getId().equals(paperId))
                return false;
        }

        assignmentRepository
                .getOne(assignmentId)
                .addPaper(paperRepository.getOne(paperId));

        return true;
    }

    @Override
    public Optional<PaperDTO> getStudentPaper(Long assignmentId, String studentId) {
        if (!assignmentRepository.existsById(assignmentId))
            return Optional.empty();
        List<PaperDTO> papers = assignmentRepository
                .getOne(assignmentId)
                .getPapers()
                .stream()
                .filter(p -> p.getCreator().equals(studentId))
                .map(p -> modelMapper.map(p, PaperDTO.class))
                .collect(Collectors.toList());

        if (papers.size() == 0)
            return Optional.empty();

        return Optional.of(papers.get(0));
    }

    @Override
    public Optional<PaperDTO> getPaper (Long id) {
        Paper paper = paperRepository.getOne(id);
        return Optional.ofNullable(modelMapper.map(paper, PaperDTO.class));
    }

    @Override
    public String getPaperCreator(Long id) throws PaperNotFoundException {
        if(!paperRepository.existsById(id))
            throw new PaperNotFoundException(id.toString());

        return paperRepository
                .getOne(id)
                .getStudent()
                .getId();
    }

    @Override
    public List<PaperDTO> getPapersForStudentCourse (String courseName, String studentId) throws StudentNotFoundException {
        if(!studentRepository.existsById(studentId))
            throw new StudentNotFoundException(studentId);
        return studentRepository
                .getOne(studentId)
                .getPapers()
                .stream()
                .map(p -> modelMapper.map(p, PaperDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<PaperDTO> getPapersForAssignment (Long id) throws AssignmentNotFoundException {
        if(!assignmentRepository.existsById(id))
            throw new AssignmentNotFoundException(id.toString());
        return assignmentRepository
                .getOne(id)
                .getPapers()
                .stream()
                .map(p -> modelMapper.map(p, PaperDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<PaperDTO> getAllPapers () {
        return paperRepository
                .findAll()
                .stream()
                .map(p -> modelMapper.map(p, PaperDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Boolean ratePaper (Long paperId, int mark) throws PaperNotFoundException {
        if(!paperRepository.existsById(paperId))
            throw new PaperNotFoundException(paperId.toString());
        if(!paperRepository.getOne(paperId).getCurrentStatus().equals(PaperStatus.RIVISTO) &&
        !paperRepository.getOne(paperId).getCurrentStatus().equals(PaperStatus.CONSEGNATO))
            return false;
        if(paperRepository.getOne(paperId).getEditable())
            return false;
        paperRepository
                .getOne(paperId)
                .setMark(mark);

        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Paris"));
        Timestamp t = Timestamp.valueOf(localDateTime);
        PaperStatusTimeDTO paperStatusTimeDTO = PaperStatusTimeDTO.builder()
                .paperStatus(PaperStatus.VALUTATO)
                .timestamp(t)
                .content(paperRepository.getOne(paperId).getContent())
                .build();

        addPaperStatusTime(paperStatusTimeDTO, paperId);
        return true;
    }

    @Override
    public Boolean initializePaperStatus (Long paperId) throws PaperNotFoundException {
        if(!paperRepository.existsById(paperId))
            throw new PaperNotFoundException(paperId.toString());
        if(!paperRepository.getOne(paperId).getEditable())
            return false;
        Paper p = paperRepository.getOne(paperId);
        p.setCurrentStatus(PaperStatus.NULL);

        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Paris"));
        Timestamp t = Timestamp.valueOf(localDateTime);
        PaperStatusTimeDTO paperStatusTimeDTO = PaperStatusTimeDTO.builder()
                .paperStatus(PaperStatus.NULL)
                .timestamp(t)
                .content(p.getContent())
                .build();
        addPaperStatusTime(paperStatusTimeDTO, paperId);

        return true;
    }

    @Override
    public void lockPaper (Long paperId) throws PaperNotFoundException {
        if(!paperRepository.existsById(paperId))
            throw new PaperNotFoundException(paperId.toString());

        paperRepository
                .getOne(paperId)
                .setEditable(false);
    }

    @Override
    public Boolean readPaper (Long paperId) throws AiException {
        if(!paperRepository.existsById(paperId))
            throw new PaperNotFoundException(paperId.toString());
        Paper p = paperRepository.getOne(paperId);
        if(!paperRepository.getOne(paperId).getEditable())
            return false;
        if(!p.getCurrentStatus().equals(PaperStatus.NULL))
            throw new AiException();
        p.setCurrentStatus(PaperStatus.LETTO);

        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Paris"));
        Timestamp t = Timestamp.valueOf(localDateTime);
        PaperStatusTimeDTO paperStatusTimeDTO = PaperStatusTimeDTO.builder()
                .paperStatus(PaperStatus.LETTO)
                .timestamp(t)
                .content(p.getContent())
                .build();
        addPaperStatusTime(paperStatusTimeDTO, paperId);

        return true;
    }

    @Override
    public Boolean reviewPaper (Long paperId, String content) throws PaperNotFoundException {
        if(!paperRepository.existsById(paperId))
            throw new PaperNotFoundException(paperId.toString());
        if(!paperRepository.getOne(paperId).getEditable())
            return false;
        Paper p = paperRepository.getOne(paperId);
        p.setCurrentStatus(PaperStatus.RIVISTO);
        p.setContent(content);

        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Paris"));
        Timestamp t = Timestamp.valueOf(localDateTime);
        PaperStatusTimeDTO paperStatusTimeDTO = PaperStatusTimeDTO.builder()
                .paperStatus(PaperStatus.RIVISTO)
                .timestamp(t)
                .content(p.getContent())
                .build();
        addPaperStatusTime(paperStatusTimeDTO, paperId);

        return true;
    }

    @Override
    public Boolean deliverPaper (Long paperId) throws PaperNotFoundException {
        if(!paperRepository.existsById(paperId))
            throw new PaperNotFoundException(paperId.toString());
        if(!paperRepository.getOne(paperId).getEditable())
            return false;
        Paper p = paperRepository.getOne(paperId);
        p.setCurrentStatus(PaperStatus.CONSEGNATO);

        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Paris"));
        Timestamp t = Timestamp.valueOf(localDateTime);
        PaperStatusTimeDTO paperStatusTimeDTO = PaperStatusTimeDTO.builder()
                .paperStatus(PaperStatus.CONSEGNATO)
                .timestamp(t)
                .content(p.getContent())
                .build();
        addPaperStatusTime(paperStatusTimeDTO, paperId);

        return true;
    }

    @Override
    public Boolean setPaperContent (Long paperId, String content) throws PaperNotFoundException {
        if(!paperRepository.existsById(paperId))
            throw new PaperNotFoundException(paperId.toString());
        if(!paperRepository.getOne(paperId).getEditable())
            return false;
        paperRepository
                .getOne(paperId)
                .setContent(content);
        return true;
    }

    @Override
    public Long addPaperStatusTime (PaperStatusTimeDTO paperStatusTimeDTO, Long paperId) throws PaperNotFoundException {
        if(!paperRepository.existsById(paperId))
            throw new PaperNotFoundException(paperId.toString());
        PaperStatusTime p = modelMapper.map(paperStatusTimeDTO, PaperStatusTime.class);
        p.setPaper(paperRepository.getOne(paperId));
        paperStatusTimeRepository.save(p);
        paperStatusTimeRepository.flush();
        paperRepository.getOne(paperId).addStatusHistory(p);
        return p.getId();
    }

    @Override
    public List<PaperStatusTimeDTO> getPaperHistory (Long paperId) throws PaperNotFoundException {
        if(!paperRepository.existsById(paperId))
            throw new PaperNotFoundException(paperId.toString());
        return paperRepository
                .getOne(paperId)
                .getStatusHistory()
                .stream()
                .map(h -> modelMapper.map(h, PaperStatusTimeDTO.class))
                .collect(Collectors.toList());
    }
}
