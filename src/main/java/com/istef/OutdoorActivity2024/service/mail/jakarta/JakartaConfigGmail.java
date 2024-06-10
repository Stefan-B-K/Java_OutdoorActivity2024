package com.istef.OutdoorActivity2024.service.mail.jakarta;

import com.istef.OutdoorActivity2024.SecretKeys;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;

/**
 * JakartaConfig implementation via the <a href="https://mail.google.com">Gmail</a>
 *
 * @see JakartaConfig
 */
public class JakartaConfigGmail implements JakartaConfig {
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final boolean debug;

    /**
     * JakartaConfig implementation via the <a href="https://mail.google.com">Gmail</a>
     *
     * @see JakartaConfig
     */
    public JakartaConfigGmail() {
        host = SecretKeys.Gmail.HOST;
        port = SecretKeys.Gmail.PORT;
        username = SecretKeys.Gmail.USER;
        password = SecretKeys.Gmail.PASS;
        debug = false;
    }

    @Override
    @NotNull
    public Session config() {

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");


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
