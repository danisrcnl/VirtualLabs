package it.polito.ai.lab2.services.image;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

    String uploadImage(MultipartFile file, String subfolder, String name) throws IOException;
}
