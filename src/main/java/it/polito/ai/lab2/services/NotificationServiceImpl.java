package it.polito.ai.lab2.services;
import it.polito.ai.lab2.controllers.NotificationController;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.entities.Token;
import it.polito.ai.lab2.repositories.TokenRepository;
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
import java.util.List;
import java.util.UUID;


@Transactional
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    TeamService teamService;
    @Autowired
    TokenRepository tokenRepository;

    @Override
    public void sendMessage(String address, String subject, String body) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo("kribos3@hotmail.it");
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        javaMailSender.send(simpleMailMessage);
    }

    @Override
    public boolean confirm(String token) {

        Long teamId;
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Paris"));

        if(!tokenRepository.existsById(token))
            return false;

        Token t = tokenRepository.getOne(token);
        teamId = t.getTeamId();
        List<Token> liveTokens = tokenRepository.findAllByExpiryBefore(Timestamp.valueOf(localDateTime));
        System.out.println(liveTokens);
        System.out.println(Timestamp.valueOf(localDateTime));

        if(!liveTokens.contains(t))
            return false;

        tokenRepository.delete(t);
        tokenRepository.flush();

        if(!tokenRepository.findAllByTeamId(teamId).isEmpty())
            return false;

        teamService.activateTeam(teamId);
        return true;
    }

    @Override
    public boolean reject(String token) {

        Long teamId;

        if(!tokenRepository.existsById(token))
            return false;

        Token t = tokenRepository.getOne(token);
        teamId = t.getTeamId();
        List<Token> teamTokens = tokenRepository.findAllByTeamId(teamId);
        for(Token teamToken : teamTokens)
            tokenRepository.delete(teamToken);
        tokenRepository.flush();
        teamService.evictTeam(teamId);
        return true;
    }

    @Override
    public void notifyTeam(TeamDTO dto, List<String> memberIds) {

        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Paris")).plusHours(1);

        Token t = Token.builder()
                .teamId(dto.getId())
                .expiryDate(Timestamp.valueOf(localDateTime))
                .build();

        for(String memberId : memberIds) {
            String id = UUID.randomUUID().toString();
            t.setId(id);
            tokenRepository.save(t);
            tokenRepository.flush();
            Link rootLink = linkTo(NotificationController.class).withSelfRel();
            String confirmLink = rootLink.getHref() + "/confirm/" + id;
            String rejectLink = rootLink.getHref() + "/reject/" + id;
            String message = "Ciao s" + memberId + "@studenti.polito.it, questo è un messaggio generato per gli " +
                    "utenti del team " + dto.getName() + "!" + System.lineSeparator() +
                    "Clicca qui per confermare l'invito:" + System.lineSeparator()
                    + confirmLink + System.lineSeparator() + "Clicca qui per rifiutare l'invito:" +
                    System.lineSeparator() + rejectLink;
            String receiver = "s" + memberId + "@studenti.polito.it";
            sendMessage(receiver, "Sei stato invitato a far parte di un team!", message);
        }
    }
}
