package filehandler.project.service;

import filehandler.project.entity.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    public File upload(MultipartFile file) throws IOException;
}
