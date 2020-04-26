package filehandler.project.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class Conversion {

    public static File multiPartToFile(MultipartFile multipartFile) throws IOException {
        File file = new java.io.File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multipartFile.getBytes());
        fos.close();
        fos.flush();
        return file;
    }
}
