package filehandler.project.transformer;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DownloadDTO {

    private HttpHeaders headers;

    private long contentLength;

    private MediaType contentType;

    private Resource resource;

}
