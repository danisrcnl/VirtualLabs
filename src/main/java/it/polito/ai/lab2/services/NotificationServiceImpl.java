package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.entities.Token;
import it.polito.ai.lab2.repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

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
        simpleMailMessage.setTo(address);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        javaMailSender.send(simpleMailMessage);
    }

    @Override
    public boolean confirm(String token) {

        Long teamId;

        if(!tokenRepository.existsById(token))
            return false;

        Token t = tokenRepository.getOne(token);
        teamId = t.getTeamId();
        List<Token> liveTokens = tokenRepository.findAllByExpiryBefore(new Timestamp(System.currentTimeMillis()));

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

    }
}
