package it.polito.ai.lab2.services.notification;
import it.polito.ai.lab2.controllers.NotificationController;
import it.polito.ai.lab2.dataStructures.MemberStatus;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.repositories.TokenRepository;
import it.polito.ai.lab2.repositories.UserRepository;
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

        tokenRepository.delete(t);
        tokenRepository.flush();

        if (tokenRepository.findAllByTeamId(teamId).isEmpty())
            teamService.activateTeamById(teamId);

        List<String> memberIds = teamService
                .getMembersById(t.getTeamId())
                .stream()
                .map(s -> s.getId())
                .collect(Collectors.toList());
        List<Token> deleted;
        for (String id : memberIds) {
            List<Token> studentTokens = tokenRepository.findAllByStudentId(id);
            for (Token tk : studentTokens) {
                if (tk.getTeamId() != t.getTeamId()) {
                    deleted = tokenRepository.findAllByTeamId(tk.getTeamId());
                    teamService.evictTeamById(tk.getTeamId());
                    for (Token tkDel : deleted)
                        tokenRepository.delete(tkDel);
                }
            }
        }

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
    public void notifyUser (String email) throws UserNotFoundException {

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
        String name = "";
        if (teacher != null) name = teacher.getFirstName();
        if (student != null) name = student.getFirstName();

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
            memberStatus.setHasAccepted(!pending.contains(member));
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
}
