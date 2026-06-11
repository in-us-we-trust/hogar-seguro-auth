package ar.edu.uba.hogar.auth.service;

import ar.edu.uba.hogar.auth.model.dto.*;
import java.util.Map;

public interface AuthService {

  RegisterResponseDTO registerUser(RegisterRequestDTO request);

  LoginResponseDTO loginUser(LoginRequestDTO request);

  Map<String, Object> validateToken(String token);

  LoginResponseDTO refreshToken(RefreshRequestDTO request);

  void logout(RefreshRequestDTO request);

  PasswordResetResponseDTO requestPasswordReset(PasswordResetRequestDTO request);

  void updatePassword(PasswordUpdateRequestDTO request);
}
