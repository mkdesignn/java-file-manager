package filehandler.project.service;

import filehandler.project.transformer.EmailDTO;
import filehandler.project.transformer.MessageDTO;

public interface MailService {

    MessageDTO send(EmailDTO email) throws Exception;

}
