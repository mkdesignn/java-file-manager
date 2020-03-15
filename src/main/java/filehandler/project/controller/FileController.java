package filehandler.project.controller;

import filehandler.project.entity.File;
import filehandler.project.service.FileServiceImp;
import filehandler.project.transformer.BaseResponseDTO;
import filehandler.project.transformer.UploadFileDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class FileController {

    @Value("${file.dir}")
    public String uploadDir;

    private final FileServiceImp fileServiceImp;

    @PostMapping(path = "/upload")
    public BaseResponseDTO upload(@RequestParam(name = "file") MultipartFile file) throws IOException {

        ModelMapper modelMapper = new ModelMapper();
        UploadFileDTO uploadFileDTO = modelMapper.map(fileServiceImp.upload(file), UploadFileDTO.class);

        return new BaseResponseDTO<>(uploadFileDTO, HttpStatus.OK.value());
    }

}
