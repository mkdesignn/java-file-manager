package filehandler.project.transformer;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadFileDTO {

    private String name;

    private String size;

    private String type;

    private String uuid;
}
