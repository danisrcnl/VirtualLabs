/*
 * NotificationController.java:
 * Classe deputata a offrire APIs accessibili tramite richieste HTTP funzionali ad accettare o rifiutare un invito per un
 * team o per confermare la registrazione al sito, tramite l'utilizzo dei Token.
 * */

package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.services.auth.AuthenticationService;
import it.polito.ai.lab2.services.notification.NotificationService;
import it.polito.ai.lab2.services.student.StudentService;
import it.polito.ai.lab2.services.teacher.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    NotificationService notificationService;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    StudentService studentService;
    @Autowired
    TeacherService teacherService;

    /*
    * Il metodo accetta un token ed eventualmente lo utilizza per accettare l'invito a far parte di un team.
    * Internamente ciò comporterà l'eliminazione di tutte le altre richieste ricevute e dunque dei corrispondenti
    * teams in costruzione.
    * */
    @GetMapping("/confirm/{tokenId}")
    public String confirm (@PathVariable String tokenId) {
        if(notificationService.confirm(tokenId))
            return "redirect:/notification/confirmation/success";
        else return "redirect:/notification/confirmation/failure";
    }

    /*
    * Fa lo stesso del metodo sopra, ma a partire dalla coppia teamId-studentId, per poterne garantire la chiamata
    * dall'applicazione client.
    * */
    @GetMapping("/confirm/{teamId}/{studentId}")
    public String confirmTeamMember (@PathVariable int teamId, @PathVariable String studentId) {
        Optional<String> token = notificationService.getMemberToken(teamId, studentId);
        if (!token.isPresent())
            return "redirect:/notification/confirmation/failure";
        if (notificationService.confirm(token.get()))
            return "redirect:/notification/confirmation/success";
        else return "redirect:/notification/confirmation/failure";
    }

    /*
    * Per prima cosa viene chiamata la confirmUser che, internamente, si occupa di settare lo stato di un utente ad
    * attivo se il token fornito è ancora valido. A quel punto dal token viene preso lo username corrispondente (ossia
    * la email) e ne viene estratta la matricola. Questa sarà usata per andare a prendere lo studente/docente
    * corrispondente dal database e agganciarlo allo user precedentemente creato.
    * */
    @GetMapping("/register/confirm/{tokenId}")
    public String confirmSignUp (@PathVariable String tokenId) {
        if(notificationService.confirmUser(tokenId)) {

            Long uid;
            if(!notificationService.getToken(tokenId).isPresent())
                return "redirect:/notification/confirmation/success";
            uid = notificationService.getToken(tokenId).get().getUserId();
            String username = authenticationService.getUsername(uid);
            String tmpid = username.split("@")[0];
            String id = "";
            for (int i = 1; i < tmpid.length(); i++)
                id += tmpid.charAt(i);
            if (username.charAt(0) == 's') {
                studentService.linkToUser(id, username);
                authenticationService.setPrivileges(username, Arrays.asList("ROLE_STUDENT"));
            } else if (username.charAt(0) == 'd') {
                teacherService.linkToUser(id, username);
                authenticationService.setPrivileges(username, Arrays.asList("ROLE_TEACHER"));

            } else return "redirect:/notification/confirmation/failure";

            return "redirect:/notification/confirmation/success";
        }
        else return "redirect:/notification/confirmation/success";
    }

    /*
    * Metodo esposto per rifiutare l'invito a far parte di un team.
    * */
    @GetMapping("/reject/{tokenId}")
    public String reject (@PathVariable String tokenId) {
        if(notificationService.reject(tokenId))
            return "redirect:/notification/rejection/success";
        else return "redirect:/notification/rejection/failure";
    }

    /*
    * Come il precedente, ma mediante l'utilizzo della coppia teamId-studentId.
    * */
    @GetMapping("/reject/{teamId}/{studentId}")
    public String rejectTeamMember (@PathVariable int teamId, @PathVariable String studentId) {
        Optional<String> token = notificationService.getMemberToken(teamId, studentId);
        if (!token.isPresent())
            return "redirect:/notification/rejection/failure";
        if (notificationService.reject(token.get()))
            return "redirect:/notification/rejection/success";
        else return "redirect:/notification/rejection/failure";
    }

    @GetMapping("/confirmation/{outcome}")
    public String showConfirm (@PathVariable String outcome, Model model) {
        if(outcome.equals("success"))
            model.addAttribute("msg", "Confirmation avvenuta con successo!");
        else model.addAttribute("msg", "Confirmation fallita o gruppo non ancora attivo");
        return "confirm";
    }

    @GetMapping("/rejection/{outcome}")
    public String showReject (@PathVariable String outcome, Model model) {
        if(outcome.equals("success"))
            model.addAttribute("msg", "Rejection avvenuta con successo!");
        else model.addAttribute("msg", "Rejection fallita");
        return "reject";
    }

}
