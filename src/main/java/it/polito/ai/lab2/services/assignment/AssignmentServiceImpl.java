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

    /*
    * Mappa il DTO su una entità corrispondente e salva nel database.
    * */
    @Override
    public Long addAssignment (AssignmentDTO assignment) {
        Assignment a = modelMapper.map(assignment, Assignment.class);
        assignmentRepository.save(a);
        assignmentRepository.flush();
        return a.getId();
    }

    /*
    * Dopo aver verificato l'esistenza di assignment e corso passati come parametri, crea la relazione nelle entità
    * reperite dal db. Dopo di ciò crea un corrispondente paper inizializzato a null per ogni studente iscritto al
    * corso.
    * */
    @Override
    public Boolean addAssignmentToCourse (Long assignmentId, String courseName) {

        if (!courseRepository.existsById(courseName))
            return false;
        if (!assignmentRepository.existsById(assignmentId))
            return false;

        Course c = courseRepository.getOne(courseName);

        c.addAssignment(assignmentRepository.getOne(assignmentId));

        courseRepository.flush();

        List<String> ids = c
                .getStudents()
                .stream()
                .map(Student :: getId)
                .collect(Collectors.toList());

        Long paperId;
        for (String id : ids) {
            paperId = addPaper(PaperDTO.builder().build(), courseName, id);
            linkPaperToAssignment(paperId, assignmentId);
        }

        return true;

    }

    /*
    * Reperisce dal db l'assignment con id pari a quello fornito e lo mappa su un DTO prima di restituirlo.
    * */
    @Override
    public Optional<AssignmentDTO> getAssignment (Long id) {
        Assignment a = assignmentRepository.getOne(id);
        return Optional.ofNullable(modelMapper.map(a, AssignmentDTO.class));
    }

    /*
    * Reperisce dal db la lista di tutti gli assignments e la mappa su una lista di DTO prima di restituirla.
    * */
    @Override
    public List<AssignmentDTO> getAllAssignments () {
        return assignmentRepository
                .findAll()
                .stream()
                .map(a -> modelMapper.map(a, AssignmentDTO.class))
                .collect(Collectors.toList());
    }

    /*
    * Ricevuto il contenuto di un assignment (path dell'immagine) lo assegna all'entità trovata nel db.
    * */
    @Override
    public AssignmentDTO setAssignmentContent(Long assignmentId, String content) throws AssignmentNotFoundException {
        if(!assignmentRepository.existsById(assignmentId))
            throw new AssignmentNotFoundException(assignmentId.toString());
        assignmentRepository
                .getOne(assignmentId)
                .setContent(content);

        return modelMapper.map(assignmentRepository.getOne(assignmentId), AssignmentDTO.class);
    }

    /*
    * Ritorna i soli assignments che appartengono al corso indicato da courseName.
    * */
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

    /*
    * Crea un nuovo paper a partire dal DTO in ingresso e ne inizializza lo stato a NULL, l'editabilità a true e i campi
    * creator e studente corrispondente.
    * */
    @Override
    public Long addPaper (PaperDTO paper, String courseName, String studentId) throws StudentNotFoundException {

        if(!studentRepository.existsById(studentId))
            throw new StudentNotFoundException(studentId);

        Paper p = modelMapper.map(paper, Paper.class);
        p.setCurrentStatus(PaperStatus.NULL);
        p.setEditable(true);
        p.setCreator(studentId);
        p.setStudent(studentRepository.getOne(studentId));

        paperRepository.save(p);
        paperRepository.flush();

        return p.getId();
    }

    /*
    * Dati un assignment e un paper, crea la relazione tra essi, qualora non esistesse già. Torna false se il paper era
    * già stato assegnato, true in caso contrario.
    * */
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

    /*
    * Reperisce tutti i papers associati a un certo studente per un assignment. La lista avrà una dimensione di 0 o 1,
    * a seconda che lo studente abbia o meno un paper per quell'assignment (a scanzo di sorprese lo avrà in quanto viene
    * creato alla creazione dell'assignment).
    * */
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

    /*
    * Reperisce il paper identificato da id nel db e lo mappa su un DTO.
    * */
    @Override
    public Optional<PaperDTO> getPaper (Long id) {
        Paper paper = paperRepository.getOne(id);
        return Optional.ofNullable(modelMapper.map(paper, PaperDTO.class));
    }

    /*
    * Metodo implementato per avere la matricola del creatore, presa dallo student in relazione col paper. Creato prima
    * di introdurre la proprietà creator in Paper.
    * */
    @Override
    public String getPaperCreator(Long id) throws PaperNotFoundException {
        if(!paperRepository.existsById(id))
            throw new PaperNotFoundException(id.toString());

        return paperRepository
                .getOne(id)
                .getStudent()
                .getId();
    }

    /*
    * Ritorna tutti i papers associati a uno studente per un determinato corso. (manca un filter sul corso)
    * */
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

    /*
    * Dato un assignment, restituisce tutti i papers a esso associati, mappati su DTOs.
    * */
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

    /*
    * Restituisce tutti i papers presenti nel db, mappati su DTOs.
    * */
    @Override
    public List<PaperDTO> getAllPapers () {
        return paperRepository
                .findAll()
                .stream()
                .map(p -> modelMapper.map(p, PaperDTO.class))
                .collect(Collectors.toList());
    }

    /*
    * Dato un paperId e il voto, si accerta che al paper possa essere assegnato un voto e che dunque esista almeno
    * una consegna da parte dello studente. Si accerta anche che esso non sia più editabile. Dopo di ciò viene settato
    * il voto e cambiato lo stato a VALUTATO. Una nuova entrata nel db delle storie dei papers viene aggiunta con lo
    * stato aggiornato, la data corrente e uno snapshot del contenuto del paper.
    * */
    @Override
    public Boolean ratePaper (Long paperId, int mark) throws PaperNotFoundException {
        if(!paperRepository.existsById(paperId))
            throw new PaperNotFoundException(paperId.toString());
        if(!paperRepository.getOne(paperId).getCurrentStatus().equals(PaperStatus.RIVISTO) &&
        !paperRepository.getOne(paperId).getCurrentStatus().equals(PaperStatus.CONSEGNATO))
            return false;
        if(paperRepository.getOne(paperId).getEditable())
            return false;
        Paper p = paperRepository
                .getOne(paperId);

        p.setMark(mark);
        p.setCurrentStatus(PaperStatus.VALUTATO);

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

    /*
    * Qualora si volesse avere anche l'iniziale stato di NULL del paper alla creazione, occorre utilizzare questo
    * metodo per l'inizializzazione.
    * */
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

    /*
    * Rende il paper non più editabile.
    * */
    @Override
    public void lockPaper (Long paperId) throws PaperNotFoundException {
        if(!paperRepository.existsById(paperId))
            throw new PaperNotFoundException(paperId.toString());

        paperRepository
                .getOne(paperId)
                .setEditable(false);
    }

    /*
    * Se e solo se lo stato attuale del paper è NULL, effettua la lettura. Pertanto si può effettuare una sola volta.
    * L'azione viene registrata nella storia dei papers come di consueto (vedi ratePaper).
    * */
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

    /*
    * Il docente può utilizzare questo metodo per effettuare le revisioni. Come sempre l'azione viene memorizzata nel
    * db. A differenza delle altre azioni, la REVIEW porta con sè un'ulteriore informazione, ossia il campo "review" che
    * conterrà eventuali commenti del docente.
    * */
    @Override
    public Boolean reviewPaper (Long paperId, String content) throws PaperNotFoundException {
        if(!paperRepository.existsById(paperId))
            throw new PaperNotFoundException(paperId.toString());
        if(!paperRepository.getOne(paperId).getEditable())
            return false;
        Paper p = paperRepository.getOne(paperId);
        p.setCurrentStatus(PaperStatus.RIVISTO);

        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Paris"));
        Timestamp t = Timestamp.valueOf(localDateTime);
        PaperStatusTimeDTO paperStatusTimeDTO = PaperStatusTimeDTO.builder()
                .paperStatus(PaperStatus.RIVISTO)
                .review(content)
                .timestamp(t)
                .content(p.getContent())
                .build();
        addPaperStatusTime(paperStatusTimeDTO, paperId);

        return true;
    }

    /*
    * La deliver paper è pensata per essere chiamata a cascata dopo la setPaperContent. Una volta che un contenuto
    * per il paper è stato caricato, la deliver ne salva lo snapshot nello storico come di consueto.
    * */
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

    /*
    * Dato un paper e una stringa che definisca il path nel server del suo contenuto, la setPaperContent assegna
    * quest'ultima al campo content del paper.
    * */
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

    /*
    * E' la funzione chiamata ricorrentemente da tutti i metodi che vogliono aggiungere un'entrata allo storico dei
    * papers. Accetta un DTO della nuova entrata e il paperId del paper cui associarla. Crea la relazione e salva il
    * paperStatusTime nel db.
    * */
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

    /*
    * Restituisce la lista dei paperStatusTime associati a un certo paper e dunque la sua storia.
    * */
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
