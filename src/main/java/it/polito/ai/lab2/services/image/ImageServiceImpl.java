package it.polito.ai.lab2.services.image;

import it.polito.ai.lab2.services.AiException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {
    @Override
    public String uploadImage(MultipartFile file, String subfolder, String name) throws IOException {
        String folder = "images/" + subfolder + "/";
        String newFileName = name;
        byte[] imageBytes = file.getBytes();
        String fileName = file.getOriginalFilename();
        int i = 0;
        Optional<String> outcome = getExtensionByStringHandling(fileName);
        if(!outcome.isPresent())
            throw new AiException();
        newFileName += ("." + outcome.get());
        File prova = new File("src\\main\\resources\\images\\" + subfolder + "\\" + newFileName);
        while (prova.exists()) {
            i++;
            newFileName = name + "_" + i + "." + outcome.get();
            prova = new File("src\\main\\resources\\images\\" + subfolder + "\\" + newFileName);
        }
        String fileLocation = new File("src\\main\\resources\\images\\" + subfolder + "\\").getAbsolutePath() + "\\" + newFileName;
        FileOutputStream fos = new FileOutputStream(fileLocation);
        fos.write(imageBytes);
        fos.close();
        return folder + newFileName;
    }

    public Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }
}
