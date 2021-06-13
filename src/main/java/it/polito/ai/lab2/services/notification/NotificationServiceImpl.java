package it.polito.ai.lab2.services.notification;
import it.polito.ai.lab2.controllers.NotificationController;
import it.polito.ai.lab2.dataStructures.MemberStatus;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.repositories.TeamRepository;
import it.polito.ai.lab2.repositories.TokenRepository;
import it.polito.ai.lab2.repositories.UserRepository;
import it.polito.ai.lab2.services.AiException;
import it.polito.ai.lab2.services.course.CourseService;
import it.polito.ai.lab2.services.student.StudentService;
import it.polito.ai.lab2.services.team.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Transactional
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    TeamService teamService;
    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    StudentService studentService;

    @Autowired
    CourseService courseService;

    @Override
    public void sendMessage (String address, String subject, String body) {
        System.out.println(address);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo("ideagraphicdesign.lecce@gmail.com");
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        javaMailSender.send(simpleMailMessage);
    }

    /*
    * Metodo che conferma l'invito a un gruppo, partendo da un token. Si utilizza il metodo findAllByExpiry before,
    * mappato su una query HQL in tokenRepository, per accertarsi che il token non sia ancora scaduto. Se così fosse,
    * tutti i token relativi a quel team ancora non eliminati verranno eliminati e il team sarà eliminato in quanto non
    * più finalizzabile. Diversamente il token viene eliminato e se era l'ultimo rimasto, allora il team viene anche
    * attivato.
    * */
    @Override
    public boolean confirm (String token) {

        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Paris"));

        if (!tokenRepository.existsById(token))
            return false;

        Token t = tokenRepository.getOne(token);

        int teamId = t.getTeamId();
        List<Token> liveTokens = tokenRepository.findAllByExpiryBefore(Timestamp.valueOf(localDateTime));

        if (!liveTokens.contains(t) || courseService.hasAlreadyATeamFor(t.getStudentId(), courseService.getTeamCourse(t.getTeamId()).getName())) {
            List<Token> teamTokens = tokenRepository.findAllByTeamId(teamId);
            for(Token teamToken : teamTokens)
                tokenRepository.delete(teamToken);
            tokenRepository.flush();
            teamService.evictTeamById(teamId);
            return false;
        }

        String studentId = t.getStudentId();

        tokenRepository.delete(t);
        tokenRepository.flush();

        if (tokenRepository.findAllByTeamId(teamId).isEmpty())
            teamService.activateTeamById(teamId);

        deleteOtherTeams(teamId, studentId);

        return true;
    }

    /*
    * Serve per rifiutare l'invito a far parte di un gruppo. Insieme ad esso vengono eliminati anche tutti gli altri
    * inviti e il team viene cancellato, in quanto non più finalizzabile.
    * */
    @Override
    public boolean reject (String token) {

        if (!tokenRepository.existsById(token))
            return false;

        Token t = tokenRepository.getOne(token);
        int teamId = t.getTeamId();
        List<Token> teamTokens = tokenRepository.findAllByTeamId(teamId);
        for(Token teamToken : teamTokens)
            tokenRepository.delete(teamToken);
        tokenRepository.flush();
        teamService.evictTeamById(teamId);
        return true;
    }

    /*
    * Serve a informare i membri della proposta di gruppo della proposta stessa. Vengono generati i token con scadenza
    * fissata a data e ora odierna più un fattore determinato da hours. Dopo aver effettuato il binding tra il token e
    * lo studente, viene generata la email da mandare a ogni utente.
    * */
    @Override
    public void notifyTeam (String courseName, String teamName, List<String> memberIds, int hours) {

        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Paris")).plusHours(hours);

        Token t = Token.builder()
                .teamId(teamService.getTeamId(courseName, teamName))
                .expiryDate(Timestamp.valueOf(localDateTime))
                .isTeam(true)
                .build();

        for(String memberId : memberIds) {
            String id = UUID.randomUUID().toString();
            t.setId(id);
            t.setStudentId(memberId);
            tokenRepository.save(t);
            tokenRepository.flush();

            String name = studentService.getStudent(memberId).get().getFirstName();

            Link rootLink = linkTo(NotificationController.class).withSelfRel();
            String confirmLink = rootLink.getHref() + "/confirm/" + id;
            String rejectLink = rootLink.getHref() + "/reject/" + id;
            String message = "Ciao " + name + ", questo è un messaggio generato per gli " +
                    "utenti del team " + teamName + "!" + System.lineSeparator() +
                    "Clicca qui per confermare l'invito:" + System.lineSeparator()
                    + confirmLink + System.lineSeparator() + "Clicca qui per rifiutare l'invito:" +
                    System.lineSeparator() + rejectLink;
            String receiver = "s" + memberId + "@studenti.polito.it";
            String subject = "[VirtualLabs] Sei stato invitato a far parte di un team!";
            sendMessage(receiver, subject, message);
        }
    }

    /*
    * Serve a rendere accessibile a colui che ha appena effettuato la registrazione un link di attivazione. Questo
    * è costruito sulla base di un token generato appositamente per l'utente. La mail di destinazione viene costruita
    * in modi differenti, a seconda che si tratti di un docente o di uno studente.
    * */
    @Override
    public void notifyUser (String email, String firstName, String lastName) throws UserNotFoundException {

        if (!userRepository.findByUsername(email).isPresent())
            throw new UserNotFoundException(email);
        User u = userRepository.findByUsername(email).get();
        Long userId = u.getId();

        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Paris")).plusHours(24);

        String id = UUID.randomUUID().toString();
        Token t = Token.builder()
                .id(id)
                .userId(userId)
                .expiryDate(Timestamp.valueOf(localDateTime))
                .isTeam(false)
                .build();
        tokenRepository.save(t);
        tokenRepository.flush();

        Teacher teacher = u.getTeacher();
        Student student = u.getStudent();
        String name = firstName;

        Link rootLink = linkTo(NotificationController.class).withSelfRel();
        String confirmLink = rootLink.getHref() + "/register/confirm/" + id;
        String message = "Ciao " + name + "! Grazie per esserti iscritto, trovi qui sotto il link " +
                " per confermare la tua iscrizione:" + System.lineSeparator() + confirmLink + System.lineSeparator() +
                System.lineSeparator() + "Se non sei stato tu a effettuare questa operazione, ti invitiamo ad ignorare la mail.";
        String receiver = "";
        if(email.charAt(0) == 's')
            receiver = u.getUsername();
        else if(email.charAt(0) == 'd')
            receiver = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@polito.it";
        String subject = "[VirtualLabs] Conferma registrazione";
        sendMessage(receiver, subject, message);
    }

    /*
    * Serve a confermare l'iscrizione presso l'applicazione. Se il token è ancora valido, allora l'utente viene
    * reperito tramite lo userId e il suo campo active viene settato a true. L'utente si potrà loggare.
    * */
    @Override
    public boolean confirmUser (String token) throws UserNotFoundException {
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Paris"));

        if (!tokenRepository.existsById(token))
            return false;

        Token t = tokenRepository.getOne(token);
        List<Token> liveTokens = tokenRepository.findAllByExpiryBefore(Timestamp.valueOf(localDateTime));
        if (!liveTokens.contains(t))
            return false;
        Long userId = t.getUserId();
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException(userId.toString());
        userRepository.getOne(userId).setActive(true);
        return true;
    }

    /*
    * Il metodo salva la lista delle matricole dei membri del gruppo e la lista dei token non ancora riscattati.
    * A questo punto viene costruita una lista di MemberStatus, ossia una struttura dati che conterrà informazioni
    * utili circa lo stato di creazione del team.
    * */
    @Override
    public List<MemberStatus> getMembersStatus(int teamId) throws TeamNotFoundException {
        List<String> membersIds =
                teamService
                        .getMembersById(teamId)
                        .stream()
                        .map(StudentDTO::getId)
                        .collect(Collectors.toList());
        List<String> pending =
                tokenRepository
                        .findAllByTeamId(teamId)
                        .stream()
                        .map(Token::getStudentId)
                        .collect(Collectors.toList());

        List<MemberStatus> memberStatuses = new ArrayList<>();

        for (String member : membersIds) {
            MemberStatus memberStatus = MemberStatus.builder().studentId(member).build();
            StudentDTO studentDTO = studentService.getStudent(member).get();
            memberStatus.setHasAccepted(!pending.contains(member));
            memberStatus.setIsCreator(member.equals(teamService.getCreator(teamId)));
            memberStatus.setFirstName(studentDTO.getFirstName());
            memberStatus.setLastName(studentDTO.getName());
            memberStatuses.add(memberStatus);
        }
        return memberStatuses;
    }

    /*
    * Data la matricola di uno studente e l'id di un team di cui fa parte, il metodo ritorna il token ad esso
    * associato, se presente. Altrimenti tornerà un optional vuoto.
    * */
    @Override
    public Optional<String> getMemberToken(int teamId, String studentId) {
        List<Token> teamTokens = new ArrayList<>(tokenRepository.findAllByTeamId(teamId));
        for (Token tk : teamTokens) {
            if (tk.getStudentId().equals(studentId))
                return Optional.ofNullable(tk.getId());
        }
        return Optional.empty();
    }

    /*
    * Per prima cosa vengono trovati tutti i token di team relativi a un certo studente (il campo studentId
    * è valido solo per quelli di team). Per ognuno di essi, se il token appartiene a un team diverso da
    * quello in cui si vuole entrare, vengono salvati in una lista tutti i token relativi allo stesso team.
    * Quel team viene dunque cancellato e il suo id salvato in una lista. Tutti i tokens precedentemente
    * salvati vengono eliminati. Gli utenti del gruppo appena eliminato vengono informati del fatto che la
    * proposta non è più valida.
    * */
    @Override
    public void deleteOtherTeams(int teamId, String studentId) throws TeamNotFoundException {

        if (!teamRepository.existsById(teamId))
            throw new TeamNotFoundException(teamId);


        List<Token> toBeDeleted;
        List<Integer> deletedTeams = new ArrayList<>();
        List<Token> studentTokens = tokenRepository.findAllByStudentId(studentId);
        for (Token tk : studentTokens) {
            int id = tk.getTeamId();
            if (id != teamId && !deletedTeams.contains(id)) {
                toBeDeleted = tokenRepository.findAllByTeamId(id);

                List<String> memberIds = teamRepository
                        .getOne(id)
                        .getMembers()
                        .stream()
                        .map(s -> s.getId())
                        .collect(Collectors.toList());

                String teamName = teamRepository
                        .getOne(id)
                        .getName();

                teamService.evictTeamById(id);
                deletedTeams.add(id);
                for (Token tkDel : toBeDeleted)
                    tokenRepository.delete(tkDel);

                for (String memberId : memberIds) {
                    String message = "Ciao, questo è un messaggio generato per gli " +
                            "utenti del team " + teamName + "!" + System.lineSeparator() +
                            "Il gruppo è stato eliminato, in quanto uno dei membri ha accettato l'invito" +
                            " per entrare a far parte di un altro gruppo.";
                    String receiver = "s" + memberId + "@studenti.polito.it";
                    String subject = "[VirtualLabs] Il gruppo " + teamName + " è stato eliminato";
                    sendMessage(receiver, subject, message);
                }
            }
        }

    }

    /*
    * Data la stringa del token torna eventualmente l'entità corrispondente.
    * */
    @Override
    public Optional<Token> getToken(String token) {
        if(!tokenRepository.existsById(token))
            return Optional.empty();
        return Optional
                .of(tokenRepository
                .getOne(token));
    }
}
