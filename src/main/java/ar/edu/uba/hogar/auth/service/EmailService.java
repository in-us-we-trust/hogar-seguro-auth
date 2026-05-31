package ar.edu.uba.hogar.auth.service;

public interface EmailService {

  void sendPasswordResetEmail(String toEmail, String resetToken);
}
