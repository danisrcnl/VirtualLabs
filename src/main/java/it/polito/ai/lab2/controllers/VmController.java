package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.services.VmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/API/vms")
public class VmController {

    @Autowired
    VmService vmService;
/*
    @GetMapping("/{courseName}")
    public List<VmDTO> getVmsForCourse(@PathVariable String courseName) throws ResponseStatusException {
        try {
            return vmService.getVmsByCourse(courseName);
        } catch (Exception e) {

        }
    }
*/
}
