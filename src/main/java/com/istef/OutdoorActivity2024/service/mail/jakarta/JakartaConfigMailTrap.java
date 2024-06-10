package com.istef.OutdoorActivity2024.service.mail.jakarta;

import com.istef.OutdoorActivity2024.SecretKeys;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;

/**
 * JakartaConfig implementation via the <a href="https://mailtrap.io">Mailtrap</a> Email Delivery Platform
 *
 * @see JakartaConfig
 */
public class JakartaConfigMailTrap implements JakartaConfig {
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final boolean debug;

    /**
     * JakartaConfig implementation via the <a href="https://mailtrap.io">Mailtrap</a> Email Delivery Platform
     *
     * @see JakartaConfig
     */
    public JakartaConfigMailTrap() {
        host = SecretKeys.MailTrap.HOST;
        port = SecretKeys.MailTrap.PORT;
        username = SecretKeys.MailTrap.USER;
        password = SecretKeys.MailTrap.PASS;
        debug = false;
    }

    @Override
    @NotNull
    public Session config() {

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };

        final Session session = Session.getInstance(props, auth);
        session.setDebug(debug);
        return session;
    }
}
