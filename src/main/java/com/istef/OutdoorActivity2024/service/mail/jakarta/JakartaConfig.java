package com.istef.OutdoorActivity2024.service.mail.jakarta;

import jakarta.mail.Session;
import org.jetbrains.annotations.NotNull;

/**
 * Definition of mail configurator compatible with MailServiceJakarta
 *
 * @see Session
 * @see MailServiceJakarta
 */
public interface JakartaConfig {
    @NotNull
    Session config();
}
