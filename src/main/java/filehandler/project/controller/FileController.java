package filehandler.project.controller;

import filehandler.project.service.FileService;
import filehandler.project.transformer.BaseResponseDTO;
import filehandler.project.transformer.DownloadDTO;
import filehandler.project.transformer.UploadFileDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(path = "/upload")
    public BaseResponseDTO upload(@RequestParam(name = "file") MultipartFile file) throws IOException {

        ModelMapper modelMapper = new ModelMapper();
        UploadFileDTO uploadFileDTO = modelMapper.map(fileService.upload(file), UploadFileDTO.class);

        return new BaseResponseDTO<>(uploadFileDTO, HttpStatus.OK.value());
    }

    @GetMapping(path = "/download/{uuid}")
    public ResponseEntity<Resource> download(@PathVariable(name = "uuid") String uuid) throws Exception {
        DownloadDTO dto = fileService.download(uuid);
        return ResponseEntity.ok()
                .headers(dto.getHeaders())
                .contentLength(dto.getContentLength())
                .contentType(dto.getContentType())
                .body(dto.getResource());
    }

}
