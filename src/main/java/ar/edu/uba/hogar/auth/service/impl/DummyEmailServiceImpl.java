package ar.edu.uba.hogar.auth.service.impl;

import ar.edu.uba.hogar.auth.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// Se activa cuando email.provider=dummy (o cuando no está configurado)
@Service
@ConditionalOnProperty(name = "email.provider", havingValue = "dummy", matchIfMissing = true)
@Slf4j
public class DummyEmailServiceImpl implements EmailService {

  @Override
  public void sendPasswordResetEmail(String toEmail, String resetToken) {
    log.info("==================================================");
    log.info("[DUMMY EMAIL] Para: {}", toEmail);
    log.info("[DUMMY EMAIL] Token de reset: {}", resetToken);
    log.info("[DUMMY EMAIL] Usá este token en /auth/password-update");
    log.info("==================================================");
  }
}
