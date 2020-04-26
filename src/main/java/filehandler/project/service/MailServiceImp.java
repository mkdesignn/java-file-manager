package filehandler.project.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import filehandler.project.entity.EmailMessage;
import filehandler.project.entity.EmailReceiver;
import filehandler.project.exceptions.EmailValidationException;
import filehandler.project.repository.EmailMessageRepository;
import filehandler.project.repository.EmailReceiverRepository;
import filehandler.project.transformer.EmailDTO;
import filehandler.project.transformer.MessageDTO;
import filehandler.project.utils.Conversion;
import filehandler.project.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailServiceImp implements MailService {

    @Value("${file.attachmentDir}")
    public String attachmentDir;

    @Value("${aws.bucket-name}")
    private String bucketName;

    private final JavaMailSender mailSender;
    private final EmailMessageRepository emailMessageRepository;
    private final EmailReceiverRepository emailReceiverRepository;

    private final AmazonS3 amazonS3;

    private File attachmentFile;
    private String attachmentFileName;
    private String attachmentFilePath;

    @Override
    public MessageDTO send(EmailDTO emailDTO) throws Exception {

        String[] emailAddresses = emailDTO.getReceivers().split(",");
        for (String email : emailAddresses) {
            if (!Validation.isValidEmail(email)) {
                throw new EmailValidationException(email);
            }
        }


        uploadAttachment(emailDTO.getAttachment());

        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);


        helper.setTo(emailAddresses);
        helper.setSubject(emailDTO.getSubject());
        helper.setText(emailDTO.getText(), true);

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
        for (String email : emailAddresses) {
            Optional<EmailReceiver> optionalEmailReceiver = emailReceiverRepository.findByEmail(email);
            if (optionalEmailReceiver.isPresent()) {
                receivers.add(optionalEmailReceiver.get());
            } else {
                EmailReceiver emailReceiver = emailReceiverRepository.save(EmailReceiver.builder().email(email).build());
                receivers.add(emailReceiver);
            }
        }

        emailMessageRepository.save(
                EmailMessage.builder()
                        .subject(emailDTO.getSubject())
                        .text(emailDTO.getText())
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

        File file = Conversion.multiPartToFile(attachment);
        amazonS3.putObject(new PutObjectRequest(bucketName, filePath, file));

        attachmentFile = new File(filePath);
        attachmentFileName = fullName;
        attachmentFilePath = uuid.toString() + "." + fileExtension;

    }
}
