package filehandler.project.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import filehandler.project.configuration.AWSProperties;
import filehandler.project.entity.File;
import filehandler.project.exceptions.UuidNotFoundException;
import filehandler.project.repository.FileRepository;
import filehandler.project.transformer.DownloadDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImp implements FileService {

    @Value("${file.dir}")
    public String uploadDir;

    @Value("${aws.key}")
    private String key;

    @Value("${aws.secret}")
    private String secret;

    @Value("${aws.bucket-name}")
    private String bucketName;

    private final FileRepository fileRepository;

    @Bean
    private AmazonS3 amazonClient() {
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(key, secret)))
//                .withRegion(Regions.fromName(awsProperties.getS3().getRegion()))
                .withPathStyleAccessEnabled(true)
                .build();

    }


    private java.io.File convertMultiPartToFile(MultipartFile file) throws IOException {
        java.io.File convFile = new java.io.File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        fos.flush();
        return convFile;
    }


    @Override
    public File upload(MultipartFile file) throws IOException {

        AmazonS3 s3Client = amazonClient();

        UUID uuid = UUID.randomUUID();
        String fullName = StringUtils.cleanPath(file.getOriginalFilename());

        int lastIndex = fullName.lastIndexOf('.');
        String fileName = fullName.substring(0, lastIndex);
        String fileExtension = fullName.substring(lastIndex + 1);

//        Path copyLocation = Paths.get(uploadDir + java.io.File.separator + uuid.toString() + "." + fileExtension);
//        Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
        java.io.File convertMultiPartToFile = convertMultiPartToFile(file);
        s3Client.putObject(new PutObjectRequest(bucketName, uploadDir + fullName, convertMultiPartToFile));

        convertMultiPartToFile.delete();
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
    public DownloadDTO download(String uuid) throws Exception {

        File file = fileRepository.findFileByUuid(uuid);
        if (file == null) {
            throw new UuidNotFoundException(uuid);
        }
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
