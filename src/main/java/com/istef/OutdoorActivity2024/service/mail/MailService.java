package com.istef.OutdoorActivity2024.service.mail;

import java.io.File;

/**
 * Definition of mail service
 * compatible for use in the OutdoorActivity2024 project
 */
public interface MailService {

    /**
     * @param to          recipient's e-mail
     * @param subject     text to be filled in the mail's subject field
     * @param body        array of texts, to be included in the body of the mail
     *                    (each text can be formatted differently)
     * @param attachments files to be attached to the mail
     */
    void sendMail(String to, String subject, String[] body, File... attachments);
}
