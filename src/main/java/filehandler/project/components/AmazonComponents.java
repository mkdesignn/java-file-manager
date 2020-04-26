package filehandler.project.components;

import com.amazonaws.services.s3.AmazonS3;
import filehandler.project.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AmazonComponents {

    private final S3Service s3Service;

    @Bean
    AmazonS3 amazonS3() {
        return s3Service.amazonClient();
    }
}
