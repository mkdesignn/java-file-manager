package filehandler.project.service;

import filehandler.project.entity.EmailMessage;
import filehandler.project.entity.EmailReceiver;
import filehandler.project.repository.EmailMessageRepository;
import filehandler.project.repository.EmailReceiverRepository;
import filehandler.project.transformer.EmailDTO;
import filehandler.project.transformer.MessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailServiceImp implements MailService {

    @Value("${file.attachmentDir}")
    public String attachmentDir;

    private final JavaMailSender mailSender;
    private final EmailMessageRepository emailMessageRepository;
    private final EmailReceiverRepository emailReceiverRepository;


    private File attachmentFile;
    private String attachmentFileName;
    private String attachmentFilePath;

    @Override
    public MessageDTO send(EmailDTO email) throws Exception {

        uploadAttachment(email.getAttachment());

        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);

        String[] receiversEmails = email.getReceivers().split(",");
        helper.setTo(receiversEmails);
        helper.setSubject(email.getSubject());
        helper.setText(email.getText(), true);

        if (attachmentFile != null) {
            helper.addAttachment(attachmentFileName, attachmentFile);
        }

        try {
            System.out.println("Sending Email...");
            mailSender.send(msg);
            System.out.println("Email successfully sent");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }

        List<EmailReceiver> receivers = new ArrayList<>();
        for (String receiverEmail : receiversEmails) {
            Optional<EmailReceiver> optionalEmailReceiver = emailReceiverRepository.findByEmail(receiverEmail);
            if (optionalEmailReceiver.isPresent()) {
                receivers.add(optionalEmailReceiver.get());
            } else {
                EmailReceiver emailReceiver = emailReceiverRepository.save(EmailReceiver.builder().email(receiverEmail).build());
                receivers.add(emailReceiver);
            }
        }

        emailMessageRepository.save(
                EmailMessage.builder()
                        .subject(email.getSubject())
                        .text(email.getText())
                        .attachmentFileName(attachmentFileName)
                        .attachmentFilePath(attachmentFilePath)
                        .receivers(receivers)
                        .build()
        );

        return MessageDTO.builder().message("Email successfully sent").build();
    }

    private void uploadAttachment(MultipartFile attachment) throws Exception {

        if (attachment == null)
            return;

        UUID uuid = UUID.randomUUID();
        String fullName = StringUtils.cleanPath(attachment.getOriginalFilename());

        int lastIndex = fullName.lastIndexOf('.');
        String fileName = fullName.substring(0, lastIndex);
        String fileExtension = fullName.substring(lastIndex + 1);

        String filePath = attachmentDir + File.separator + uuid.toString() + "." + fileExtension;

        Path copyLocation = Paths.get(filePath);
        Files.copy(attachment.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

        attachmentFile = new File(filePath);
        attachmentFileName = fullName;
        attachmentFilePath = uuid.toString() + "." + fileExtension;

    }
}
