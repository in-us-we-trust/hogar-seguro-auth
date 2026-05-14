package ar.edu.uba.hogar.auth.service.impl;

import ar.edu.uba.hogar.auth.enums.UserStatusEnum;
import ar.edu.uba.hogar.auth.exception.DoorbellException;
import ar.edu.uba.hogar.auth.exception.ExceptionEnum;
import ar.edu.uba.hogar.auth.model.dto.*;
import ar.edu.uba.hogar.auth.model.entity.AuthUser;
import ar.edu.uba.hogar.auth.model.entity.PasswordReset;
import ar.edu.uba.hogar.auth.model.entity.RefreshToken;
import ar.edu.uba.hogar.auth.repository.AuthUserRepository;
import ar.edu.uba.hogar.auth.repository.PasswordResetRepository;
import ar.edu.uba.hogar.auth.repository.RefreshTokenRepository;
import ar.edu.uba.hogar.auth.service.AuthService;
import ar.edu.uba.hogar.auth.service.EmailService;
import ar.edu.uba.hogar.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthUserRepository      authUserRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final RefreshTokenRepository  refreshTokenRepository;
    private final PasswordEncoder         passwordEncoder;
    private final JwtService              jwtService;
    private final EmailService            emailService;

    private static final int PASSWORD_RESET_EXPIRATION_MINUTES = 60;
    private static final int REFRESH_TOKEN_EXPIRATION_DAYS     = 30;

    // ──────────────────────────────────────────────
    // REGISTRO
    // ──────────────────────────────────────────────
    @Override
    @Transactional
    public RegisterResponseDTO registerUser(RegisterRequestDTO request) {
        if (authUserRepository.existsByEmail(request.getEmail())) {
            throw new DoorbellException(ExceptionEnum.USER_ALREADY_EXISTS);
        }

        AuthUser user = new AuthUser();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatusEnum.ACTIVE);

        AuthUser saved = authUserRepository.save(user);
        log.info("New user registered: {}", saved.getEmail());

        return toRegisterResponse(saved);
    }

    // ──────────────────────────────────────────────
    // LOGIN — devuelve accessToken + refreshToken
    // ──────────────────────────────────────────────
    @Override
    @Transactional
    public LoginResponseDTO loginUser(LoginRequestDTO request) {
        AuthUser user = authUserRepository.findActiveUserByEmail(request.getEmail())
                .orElseThrow(() -> new DoorbellException(ExceptionEnum.USER_NOT_FOUND));

        if (user.getStatus() == UserStatusEnum.BLOCKED) {
            throw new DoorbellException(ExceptionEnum.USER_BLOCKED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new DoorbellException(ExceptionEnum.INVALID_CREDENTIALS);
        }

        user.setLastLogin(LocalDateTime.now());
        authUserRepository.save(user);

        // Generam el JWT (corta duración, definida en yml)
        JwtPayload payload = JwtPayload.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .build();
        String accessToken = jwtService.generateToken(payload);

        // Refresh token (larga duración, 30 días)
        String refreshToken = generateAndSaveRefreshToken(user);

        log.info("User logged in: {}", user.getEmail());
        return new LoginResponseDTO(accessToken, refreshToken);
    }

    // ──────────────────────────────────────────────
    // VALIDAR TOKEN
    // ──────────────────────────────────────────────
    @Override
    public JwtPayload validateToken(String token) {
        return jwtService.validateToken(token);
    }

    // ──────────────────────────────────────────────
    // REFRESH TOKEN — renueva el accessToken
    // ──────────────────────────────────────────────
    @Override
    @Transactional
    public LoginResponseDTO refreshToken(RefreshRequestDTO request) {
        // 1. Buscamos el refresh token en la BD
        RefreshToken stored = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new DoorbellException(ExceptionEnum.TOKEN_INVALID));

        // 2. Verificamos que no haya expirado
        if (LocalDateTime.now().isAfter(stored.getExpiresAt())) {
            refreshTokenRepository.delete(stored);
            throw new DoorbellException(ExceptionEnum.TOKEN_EXPIRED);
        }

        AuthUser user = stored.getAuthUser();

        if (user.getStatus() == UserStatusEnum.BLOCKED) {
            throw new DoorbellException(ExceptionEnum.USER_BLOCKED);
        }

        // 3. Generamos nuevo accessToken
        JwtPayload payload = JwtPayload.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .build();
        String newAccessToken = jwtService.generateToken(payload);

        // 4. Rotamos el refresh token (buena práctica de seguridad:
        //    cada refresh genera un token nuevo, el viejo queda inválido)
        refreshTokenRepository.delete(stored);
        String newRefreshToken = generateAndSaveRefreshToken(user);

        log.info("Token refreshed for: {}", user.getEmail());
        return new LoginResponseDTO(newAccessToken, newRefreshToken);
    }

    // ──────────────────────────────────────────────
    // LOGOUT — invalida el refresh token
    // ──────────────────────────────────────────────
    @Override
    @Transactional
    public void logout(RefreshRequestDTO request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new DoorbellException(ExceptionEnum.TOKEN_INVALID));

        refreshTokenRepository.delete(stored);
        log.info("User logged out: {}", stored.getAuthUser().getEmail());
    }

    // ──────────────────────────────────────────────
    // SOLICITAR RESET DE CONTRASEÑA
    // ──────────────────────────────────────────────
    @Override
    @Transactional
    public PasswordResetResponseDTO requestPasswordReset(PasswordResetRequestDTO request) {
        AuthUser user = authUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new DoorbellException(ExceptionEnum.USER_NOT_FOUND));

        if (user.getStatus() == UserStatusEnum.BLOCKED) {
            throw new DoorbellException(ExceptionEnum.USER_BLOCKED);
        }

        // Si ya tenía un reset pendiente, lo reemplazamos
        passwordResetRepository.findByAuthUser(user)
                .ifPresent(passwordResetRepository::delete);
        passwordResetRepository.flush();

        PasswordReset reset = new PasswordReset();
        reset.setAuthUser(user);
        reset.setToken(UUID.randomUUID());
        reset.setCreatedAt(LocalDateTime.now());
        passwordResetRepository.save(reset);

        // Enviamos el email (real con Brevo, o dummy en local)
        emailService.sendPasswordResetEmail(user.getEmail(), reset.getToken().toString());

        return new PasswordResetResponseDTO("Password reset email sent successfully");
    }

    // ──────────────────────────────────────────────
    // ACTUALIZAR CONTRASEÑA
    // ──────────────────────────────────────────────
    @Override
    @Transactional
    public void updatePassword(PasswordUpdateRequestDTO request) {
        PasswordReset reset = passwordResetRepository.findByToken(request.getToken())
                .orElseThrow(() -> new DoorbellException(ExceptionEnum.PASSWORD_TOKEN_NOT_FOUND));

        LocalDateTime expiration = reset.getCreatedAt()
                .plusMinutes(PASSWORD_RESET_EXPIRATION_MINUTES);
        if (LocalDateTime.now().isAfter(expiration)) {
            passwordResetRepository.delete(reset);
            throw new DoorbellException(ExceptionEnum.PASSWORD_TOKEN_EXPIRED);
        }

        AuthUser user = reset.getAuthUser();

        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new DoorbellException(ExceptionEnum.ALREADY_USED_PASSWORD);
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setUpdatedAt(LocalDateTime.now());
        authUserRepository.save(user);

        passwordResetRepository.delete(reset);
        log.info("Password updated for: {}", user.getEmail());
    }

    // ──────────────────────────────────────────────
    // HELPERS PRIVADOS
    // ──────────────────────────────────────────────
    private String generateAndSaveRefreshToken(AuthUser user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setAuthUser(user);
        refreshToken.setToken(UUID.randomUUID());
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRATION_DAYS));
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken().toString();
    }

    private RegisterResponseDTO toRegisterResponse(AuthUser user) {
        return RegisterResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .status(user.getStatus())
                .build();
    }
}