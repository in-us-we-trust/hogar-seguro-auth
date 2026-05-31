package ar.edu.uba.hogar.auth.service.impl;

import ar.edu.uba.hogar.auth.exception.DoorbellException;
import ar.edu.uba.hogar.auth.exception.ExceptionEnum;
import ar.edu.uba.hogar.auth.service.EmailService;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// Solo se activa cuando email.provider=brevo en el yml
@Service
@ConditionalOnProperty(name = "email.provider", havingValue = "brevo")
@Slf4j
public class BrevoEmailServiceImpl implements EmailService {

  private final RestTemplate restTemplate;
  private final String apiKey;
  private final String apiUrl;
  private final String senderName;
  private final String senderEmail;
  private final String resetPasswordUrl;

  public BrevoEmailServiceImpl(
      RestTemplate restTemplate,
      @Value("${brevo.api.key}") String apiKey,
      @Value("${brevo.api.url}") String apiUrl,
      @Value("${brevo.sender.name}") String senderName,
      @Value("${brevo.sender.email}") String senderEmail,
      @Value("${app.reset-password-url}") String resetPasswordUrl) {
    this.restTemplate = restTemplate;
    this.apiKey = apiKey;
    this.apiUrl = apiUrl;
    this.senderName = senderName;
    this.senderEmail = senderEmail;
    this.resetPasswordUrl = resetPasswordUrl;
  }

  @Override
  public void sendPasswordResetEmail(String toEmail, String resetToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("api-key", apiKey);

    String resetLink = resetPasswordUrl + "?token=" + resetToken;

    String htmlContent =
        """
        <h2>Recuperación de contraseña</h2>
        <p>Recibimos una solicitud para restablecer tu contraseña.</p>
        <p>Hacé clic en el siguiente link para crear una nueva contraseña:</p>
        <a href="%s" style="background-color:#4CAF50;color:white;padding:12px 20px;
           text-decoration:none;border-radius:4px;">Restablecer contraseña</a>
        <p>Este link expira en 1 hora.</p>
        <p>Si no solicitaste esto, ignorá este email.</p>
        """
            .formatted(resetLink);

    Map<String, Object> body =
        Map.of(
            "sender",
            Map.of("name", senderName, "email", senderEmail),
            "to",
            new Object[] {Map.of("email", toEmail)},
            "subject",
            "Restablecer tu contraseña",
            "htmlContent",
            htmlContent);

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

    try {
      restTemplate.postForObject(apiUrl, request, String.class);
      log.info("Password reset email sent to: {}", toEmail);
    } catch (Exception e) {
      log.error("Error sending email to {}: {}", toEmail, e.getMessage());
      throw new DoorbellException(ExceptionEnum.SEND_EMAIL_ERROR);
    }
  }
}
