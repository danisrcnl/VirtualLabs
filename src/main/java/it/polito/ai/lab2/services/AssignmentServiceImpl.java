package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dataStructures.PaperStatus;
import it.polito.ai.lab2.dtos.AssignmentDTO;
import it.polito.ai.lab2.dtos.PaperDTO;
import it.polito.ai.lab2.dtos.PaperStatusTimeDTO;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.repositories.AssignmentRepository;
import it.polito.ai.lab2.repositories.PaperRepository;
import it.polito.ai.lab2.repositories.PaperStatusTimeRepository;
import it.polito.ai.lab2.repositories.TeamRepository;
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
    ModelMapper modelMapper;

    @Override
    public boolean addAssignment(AssignmentDTO assignment) {
        Assignment a = modelMapper.map(assignment, Assignment.class);
        if(assignmentRepository.existsById(assignment.getId()))
            return false;
        assignmentRepository.save(a);
        assignmentRepository.flush();
        return true;
    }

    @Override
    public Optional<AssignmentDTO> getAssignment(String id) {
        Assignment a = assignmentRepository.getOne(id);
        return Optional.ofNullable(modelMapper.map(a, AssignmentDTO.class));
    }

    @Override
    public List<AssignmentDTO> getAllAssignments() {
        return assignmentRepository
                .findAll()
                .stream()
                .map(a -> modelMapper.map(a, AssignmentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean addPaper(PaperDTO paper) {
        Paper p = modelMapper.map(paper, Paper.class);
        if(paperRepository.existsById(paper.getId()))
            return false;
        paperRepository.save(p);
        paperRepository.flush();
        return true;
    }

    @Override
    public boolean linkPaperToAssignment(String paperId, String assignmentId) throws PaperNotFoundException,
            AssignmentNotFoundException {
        if(!paperRepository.existsById(paperId))
            throw new PaperNotFoundException(paperId);
        if(!assignmentRepository.existsById(assignmentId))
            throw new AssignmentNotFoundException(assignmentId);

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
    public Optional<PaperDTO> getPaper(String id) {
        Paper paper = paperRepository.getOne(id);
        return Optional.ofNullable(modelMapper.map(paper, PaperDTO.class));
    }

    @Override
    public List<PaperDTO> getPapersForTeam(Long teamId) throws TeamNotFoundException {
        if(!teamRepository.existsById(teamId))
            throw new TeamNotFoundException(teamId.toString());
        return teamRepository
                .getOne(teamId)
                .getPapers()
                .stream()
                .map(p -> modelMapper.map(p, PaperDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<PaperDTO> getPapersForAssignment(String id) throws AssignmentNotFoundException {
        if(!assignmentRepository.existsById(id))
            throw new AssignmentNotFoundException(id);
        return assignmentRepository
                .getOne(id)
                .getPapers()
                .stream()
                .map(p -> modelMapper.map(p, PaperDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<PaperDTO> getAllPapers() {
        return paperRepository
                .findAll()
                .stream()
                .map(p -> modelMapper.map(p, PaperDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void ratePaper(String paperId, int mark) throws PaperNotFoundException {
        if(!paperRepository.existsById(paperId))
            throw new PaperNotFoundException(paperId);
        paperRepository
                .getOne(paperId)
                .setMark(mark);
    }

    @Override
    public String readPaper(String paperId) throws PaperNotFoundException {
        if(!paperRepository.existsById(paperId))
            throw new PaperNotFoundException(paperId);
        Paper p = paperRepository.getOne(paperId);
        p.setCurrentStatus(PaperStatus.LETTO);

        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Paris"));
        Timestamp t = Timestamp.valueOf(localDateTime);
        PaperStatusTimeDTO paperStatusTimeDTO = PaperStatusTimeDTO.builder()
                .paperStatus(PaperStatus.LETTO)
                .timestamp(t)
                .content(p.getContent())
                .build();
        addPaperStatusTime(paperStatusTimeDTO, paperId);

        return p.getContent();
    }

    @Override
    public void reviewPaper(String paperId) throws PaperNotFoundException {
        if(!paperRepository.existsById(paperId))
            throw new PaperNotFoundException(paperId);
        Paper p = paperRepository.getOne(paperId);
        p.setCurrentStatus(PaperStatus.RIVISTO);

        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Paris"));
        Timestamp t = Timestamp.valueOf(localDateTime);
        PaperStatusTimeDTO paperStatusTimeDTO = PaperStatusTimeDTO.builder()
                .paperStatus(PaperStatus.RIVISTO)
                .timestamp(t)
                .content(p.getContent())
                .build();
        addPaperStatusTime(paperStatusTimeDTO, paperId);

    }

    @Override
    public void deliverPaper(String paperId) throws PaperNotFoundException {
        if(!paperRepository.existsById(paperId))
            throw new PaperNotFoundException(paperId);
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

    }

    @Override
    public void setPaperContent(String paperId, String content) throws PaperNotFoundException {
        if(!paperRepository.existsById(paperId))
            throw new PaperNotFoundException(paperId);
        paperRepository
                .getOne(paperId)
                .setContent(content);
    }

    @Override
    public boolean addPaperStatusTime(PaperStatusTimeDTO paperStatusTimeDTO, String paperId) throws
            PaperNotFoundException {
        if(!paperRepository.existsById(paperId))
            throw new PaperNotFoundException(paperId);
        if(paperStatusTimeRepository.existsById(paperStatusTimeDTO.getId()))
            return false;
        PaperStatusTime p = modelMapper.map(paperStatusTimeDTO, PaperStatusTime.class);
        paperStatusTimeRepository.save(p);
        paperStatusTimeRepository.flush();
        paperRepository.getOne(paperId).addStatusHistory(p);
        return true;
    }

    @Override
    public List<PaperStatusTimeDTO> getPaperHistory(String paperId) throws PaperNotFoundException {
        if(!paperRepository.existsById(paperId))
            throw new PaperNotFoundException(paperId);
        return paperRepository
                .getOne(paperId)
                .getStatusHistory()
                .stream()
                .map(h -> modelMapper.map(h, PaperStatusTimeDTO.class))
                .collect(Collectors.toList());
    }
}
