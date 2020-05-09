package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @GetMapping("/confirm/{tokenId}")
    public String confirm(@PathVariable String tokenId) {
        if(notificationService.confirm(tokenId))
            return "redirect:/confirm/success";
        else return "redirect:/confirm/failure";
    }

    @GetMapping("/reject/{tokenId}")
    public String reject(@PathVariable String tokenId) {
        if(notificationService.reject(tokenId))
            return "redirect:/reject/success";
        else return "redirect:/reject/failure";
    }

    @GetMapping("/confirm/{outcome}")
    public String showConfirm(@PathVariable String outcome, Model model) {
        if(outcome.equals("success"))
            model.addAttribute("msg", "Confirmation avvenuta con successo!");
        else model.addAttribute("msg", "Confirmation fallita");
        return "confirm";
    }

    @GetMapping("/reject/{outcome}")
    public String showReject(@PathVariable String outcome, Model model) {
        if(outcome.equals("success"))
            model.addAttribute("msg", "Rejection avvenuta con successo!");
        else model.addAttribute("msg", "Rejection fallita");
        return "reject";
    }

}
