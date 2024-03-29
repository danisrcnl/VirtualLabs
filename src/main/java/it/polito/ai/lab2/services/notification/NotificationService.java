package it.polito.ai.lab2.services.notification;

import it.polito.ai.lab2.dataStructures.MemberStatus;
import it.polito.ai.lab2.entities.Token;

import java.util.List;
import java.util.Optional;

public interface NotificationService {

    void sendMessage (String address, String subject, String body);
    boolean confirm (String token);
    boolean reject (String token);
    void notifyTeam (String courseName, String teamName, List<String> memberIds, int hours);
    void notifyUser (String email, String firstName, String lastName);
    boolean confirmUser (String token);
    List<MemberStatus> getMembersStatus (int teamId);
    Optional<String> getMemberToken (int teamId, String studentId);
    void deleteOtherTeams (int teamId, String studentId);
    Optional<Token> getToken (String token);
}
