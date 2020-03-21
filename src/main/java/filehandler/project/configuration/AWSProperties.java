package filehandler.project.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "aws")
public class AWSProperties {

    private String key;

    private String secret;

    private String bucketName;

    private String endpointUrl;
}