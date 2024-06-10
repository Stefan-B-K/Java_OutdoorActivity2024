package com.istef.OutdoorActivity2024.service.mail.jakarta;

import com.istef.OutdoorActivity2024.output.DataOutputer;
import com.istef.OutdoorActivity2024.service.mail.MailService;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.io.File;
import java.io.IOException;
import java.util.Date;


/**
 * MailService implementation via the <a href="https://jakarta.ee">Jakarta API</a>
 *
 * @see MailService
 */
public class MailServiceJakarta implements MailService {
    private static final String FROM = "mailtrap@istef.space";
    private final JakartaConfig config;

    /**
     * MailService implementation via the <a href="https://jakarta.ee">Jakarta API</a>
     *
     * @see DataOutputer
     */
    public MailServiceJakarta() {
        this.config = new JakartaConfigMailTrap(); // new JakartaConfigGmail();
    }

    @Override
    public void sendMail(String to, String subject, String[] body, File... attachments) {

        try {
            Session session = config.config();
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(FROM, "Outdoor Activity 2024"));
            InternetAddress[] address = {new InternetAddress(to)};
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(subject);
            msg.setSentDate(new Date());

            Multipart mp = new MimeMultipart();
            for (String text : body) {
                MimeBodyPart mbp = new MimeBodyPart();
                mbp.setText(text);
                mp.addBodyPart(mbp);
            }

            if (attachments != null) {
                for (File file : attachments) {
                    MimeBodyPart mbp = new MimeBodyPart();
                    mbp.attachFile(file);
                    mp.addBodyPart(mbp);
                }
            }

            msg.setContent(mp);

            Transport.send(msg);
            System.out.println("Mail sent.");
        } catch (MessagingException e) {
            System.err.println(e.getMessage());
            Exception ex;
            if ((ex = e.getNextException()) != null) {
                System.err.println(ex.getMessage());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}