package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.services.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @GetMapping("/confirm/{tokenId}")
    public String confirm (@PathVariable String tokenId) {
        if(notificationService.confirm(tokenId))
            return "redirect:/notification/confirmation/success";
        else return "redirect:/notification/confirmation/failure";
    }

    @GetMapping("/confirm/{teamId}/{studentId}")
    public String confirmTeamMember (@PathVariable int teamId, @PathVariable String studentId) {
        Optional<String> token = notificationService.getMemberToken(teamId, studentId);
        if (!token.isPresent())
            return "redirect:/notification/confirmation/failure";
        if (notificationService.confirm(token.get()))
            return "redirect:/notification/confirmation/success";
        else return "redirect:/notification/confirmation/failure";
    }

    @GetMapping("/register/confirm/{tokenId}")
    public String confirmSignUp (@PathVariable String tokenId) {
        if(notificationService.confirmUser(tokenId))
            return "redirect:/notification/confirmation/success";
        else return "redirect:/notification/confirmation/success";
    }

    @GetMapping("/reject/{tokenId}")
    public String reject (@PathVariable String tokenId) {
        if(notificationService.reject(tokenId))
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
