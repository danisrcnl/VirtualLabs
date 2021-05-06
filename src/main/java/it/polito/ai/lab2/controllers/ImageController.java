package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.services.AiException;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RequestMapping("/images")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class ImageController {
    @GetMapping(value = "/assignments/{name}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public @ResponseBody byte[] getAssignmentImg(@PathVariable String name) throws IOException {
        String fullname = "/src/main/images/assignments/" + name;
        InputStream in = new FileInputStream(System.getProperty("user.dir") + fullname);
        if(in != null)
            return in.readAllBytes();
        else throw new AiException();
    }

    @GetMapping(value = "/papers/{name}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public @ResponseBody byte[] getPaperImg(@PathVariable String name) throws IOException {
        String fullname = "/src/main/images/papers/" + name;
        InputStream in = new FileInputStream(System.getProperty("user.dir") + fullname);
        if(in != null)
            return in.readAllBytes();
        else throw new AiException();
    }
}
