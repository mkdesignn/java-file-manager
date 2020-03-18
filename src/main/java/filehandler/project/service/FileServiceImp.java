package filehandler.project.service;

import filehandler.project.entity.File;
import filehandler.project.repository.FileRepository;
import filehandler.project.transformer.DownloadDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImp implements FileService {

    @Value("${file.dir}")
    public String uploadDir;

    private final FileRepository fileRepository;

    @Override
    public File upload(MultipartFile file) throws IOException {

        UUID uuid = UUID.randomUUID();
        String fullName = StringUtils.cleanPath(file.getOriginalFilename());

        int lastIndex = fullName.lastIndexOf('.');
        String fileName = fullName.substring(0, lastIndex);
        String fileExtension = fullName.substring(lastIndex + 1);

        Path copyLocation = Paths.get(uploadDir + java.io.File.separator + uuid.toString() + "." + fileExtension);
        Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

        return fileRepository.save(
                File.builder()
                        .name(fileName)
                        .size(file.getSize())
                        .type(fileExtension)
                        .uuid(uuid.toString())
                        .build()
        );

    }

    @Override
    public DownloadDTO download(String uuid) throws IOException {

        File file = fileRepository.findFileByUuid(uuid);
        String fileBasename = file.getName() + "." + file.getType();
        java.io.File javaFile = new java.io.File(uploadDir + java.io.File.separator + uuid + "." + file.getType());
        Path path = Paths.get(javaFile.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileBasename);
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return DownloadDTO.builder()
                .headers(headers)
                .contentLength(javaFile.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .resource(resource)
                .build();
    }
}
