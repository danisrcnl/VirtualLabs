package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.services.AiException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;

@RequestMapping("/images")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class ImageController {
    @GetMapping(value = "/assignments/{name}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public @ResponseBody byte[] getAssignmentImg(@PathVariable String name) throws IOException {
        String fullname = "/images/assignments/" + name;
        System.out.println();
        InputStream in = getClass()
                .getResourceAsStream(fullname);
        if(in != null)
            return in.readAllBytes();
        else throw new AiException();
    }

    @GetMapping(value = "/papers/{name}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public @ResponseBody byte[] getPaperImg(@PathVariable String name) throws IOException {
        String fullname = "/images/papers/" + name;
        System.out.println();
        InputStream in = getClass()
                .getResourceAsStream(fullname);
        if(in != null)
            return in.readAllBytes();
        else throw new AiException();
    }
}
