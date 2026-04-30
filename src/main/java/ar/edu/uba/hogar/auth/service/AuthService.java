package ar.edu.uba.hogar.auth.service;

import ar.edu.uba.hogar.auth.model.dto.*;

public interface AuthService {

    RegisterResponseDTO registerUser(RegisterRequestDTO request);

    LoginResponseDTO loginUser(LoginRequestDTO request);

    JwtPayload validateToken(String token);

    PasswordResetResponseDTO requestPasswordReset(PasswordResetRequestDTO request);

    void updatePassword(PasswordUpdateRequestDTO request);
}