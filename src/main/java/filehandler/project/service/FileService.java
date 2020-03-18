package filehandler.project.service;

import filehandler.project.entity.File;
import filehandler.project.transformer.DownloadDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    File upload(MultipartFile file) throws IOException;

    DownloadDTO download(String uuid) throws IOException;

}
