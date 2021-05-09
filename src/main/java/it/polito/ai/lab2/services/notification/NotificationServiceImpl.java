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
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo("ideagraphicdesign.lecce@gmail.com");
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        javaMailSender.send(simpleMailMessage);
    }

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

    @Override
    public void notifyUser (String email, String firstName) throws UserNotFoundException {

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
        String receiver = u.getUsername();
        String subject = "[VirtualLabs] Conferma registrazione";
        sendMessage(receiver, subject, message);
    }

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

    @Override
    public Optional<String> getMemberToken(int teamId, String studentId) {
        List<Token> teamTokens = new ArrayList<>(tokenRepository.findAllByTeamId(teamId));
        for (Token tk : teamTokens) {
            if (tk.getStudentId().equals(studentId))
                return Optional.ofNullable(tk.getId());
        }
        return Optional.empty();
    }

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
                teamService.evictTeamById(id);
                deletedTeams.add(id);
                for (Token tkDel : toBeDeleted)
                    tokenRepository.delete(tkDel);

                List<String> memberIds = teamRepository
                        .getOne(teamId)
                        .getMembers()
                        .stream()
                        .map(s -> s.getId())
                        .collect(Collectors.toList());

                String teamName = teamRepository
                        .getOne(teamId)
                        .getName();

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

    @Override
    public Optional<Token> getToken(String token) {
        if(!tokenRepository.existsById(token))
            return Optional.empty();
        return Optional
                .of(tokenRepository
                .getOne(token));
    }
}
