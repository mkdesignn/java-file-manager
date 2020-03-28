package filehandler.project.controller;

import filehandler.project.service.MailService;
import filehandler.project.transformer.BaseResponseDTO;
import filehandler.project.transformer.EmailDTO;
import filehandler.project.transformer.MessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping(path = "/mail")
    public BaseResponseDTO sendEmail(@ModelAttribute EmailDTO email) throws Exception {
        MessageDTO message = mailService.send(email);
        return new BaseResponseDTO<>(message, HttpStatus.OK.value());
    }

}
