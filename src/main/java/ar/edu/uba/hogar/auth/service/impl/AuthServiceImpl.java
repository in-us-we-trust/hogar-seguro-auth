package ar.edu.uba.hogar.auth.service.impl;

import ar.edu.uba.hogar.auth.enums.RolesEnum;
import ar.edu.uba.hogar.auth.enums.UserStatusEnum;
import ar.edu.uba.hogar.auth.exception.DoorbellException;
import ar.edu.uba.hogar.auth.exception.ExceptionEnum;
import ar.edu.uba.hogar.auth.model.dto.*;
import ar.edu.uba.hogar.auth.model.entity.AuthUser;
import ar.edu.uba.hogar.auth.model.entity.PasswordReset;
import ar.edu.uba.hogar.auth.repository.AuthUserRepository;
import ar.edu.uba.hogar.auth.repository.PasswordResetRepository;
import ar.edu.uba.hogar.auth.service.AuthService;
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

    private final AuthUserRepository   authUserRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final PasswordEncoder      passwordEncoder;
    private final JwtService           jwtService;

    // Tiempo de expiración del token de reset: 1 hora
    private static final int PASSWORD_RESET_EXPIRATION_MINUTES = 60;

    // ──────────────────────────────────────────────
    // REGISTRO
    // ──────────────────────────────────────────────
    @Override
    @Transactional
    public RegisterResponseDTO registerUser(RegisterRequestDTO request) {
        // 1. Verificamos que el email no esté ya registrado
        if (authUserRepository.existsByEmail(request.getEmail())) {
            throw new DoorbellException(ExceptionEnum.USER_ALREADY_EXISTS);
        }

        // 2. Creamos el usuario con rol OWNER y contraseña hasheada
        AuthUser user = new AuthUser();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(RolesEnum.OWNER);
        user.setStatus(UserStatusEnum.ACTIVE);

        AuthUser saved = authUserRepository.save(user);
        log.info("New OWNER registered: {}", saved.getEmail());

        return toRegisterResponse(saved);
    }

    // ──────────────────────────────────────────────
    // LOGIN
    // ──────────────────────────────────────────────
    @Override
    public LoginResponseDTO loginUser(LoginRequestDTO request) {
        // 1. Buscamos el usuario por email (solo activos/bloqueados, no eliminados)
        AuthUser user = authUserRepository.findActiveUserByEmail(request.getEmail())
                .orElseThrow(() -> new DoorbellException(ExceptionEnum.USER_NOT_FOUND));

        // 2. Verificamos que no esté bloqueado
        if (user.getStatus() == UserStatusEnum.BLOCKED) {
            throw new DoorbellException(ExceptionEnum.USER_BLOCKED);
        }

        // 3. Verificamos la contraseña con BCrypt
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new DoorbellException(ExceptionEnum.INVALID_CREDENTIALS);
        }

        // 4. Actualizamos el último login
        user.setLastLogin(LocalDateTime.now());
        authUserRepository.save(user);

        // 5. Generamos el JWT con los datos del usuario
        JwtPayload payload = JwtPayload.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        String token = jwtService.generateToken(payload);
        log.info("User logged in: {}", user.getEmail());

        return new LoginResponseDTO(token);
    }

    // ──────────────────────────────────────────────
    // VALIDAR TOKEN
    // ──────────────────────────────────────────────
    @Override
    public JwtPayload validateToken(String token) {
        // Delega al JwtService que verifica la firma y expiración
        return jwtService.validateToken(token);
    }

    // ──────────────────────────────────────────────
    // INVITAR CONTACTO (el OWNER agrega un TRUSTED_CONTACT)
    // ──────────────────────────────────────────────
    @Override
    @Transactional
    public RegisterResponseDTO inviteContact(InviteContactRequestDTO request) {
        // 1. Verificamos que el email no esté ya registrado
        if (authUserRepository.existsByEmail(request.getContactEmail())) {
            throw new DoorbellException(ExceptionEnum.USER_ALREADY_EXISTS);
        }

        // 2. Creamos el usuario con rol TRUSTED_CONTACT
        //    Sin contraseña por ahora: el contacto la establece via reset de contraseña
        AuthUser contact = new AuthUser();
        contact.setEmail(request.getContactEmail());
        contact.setPasswordHash(""); // se establece cuando el contacto hace el reset
        contact.setRole(RolesEnum.TRUSTED_CONTACT);
        contact.setStatus(UserStatusEnum.INACTIVE); // queda inactivo hasta que establezca su contraseña

        AuthUser saved = authUserRepository.save(contact);
        log.info("TRUSTED_CONTACT invited: {}", saved.getEmail());

        // TODO: acá iría el envío del email de invitación (paso 8)

        return toRegisterResponse(saved);
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

        // Generamos un token UUID único para el reset
        PasswordReset reset = new PasswordReset();
        reset.setAuthUser(user);
        reset.setToken(UUID.randomUUID());
        reset.setCreatedAt(LocalDateTime.now());

        // Si ya tenía un reset pendiente, lo reemplazamos
        if (user.getPasswordReset() != null) {
            passwordResetRepository.delete(user.getPasswordReset());
        }
        passwordResetRepository.save(reset);

        log.info("Password reset requested for: {}", user.getEmail());

        // TODO: acá iría el envío del email con el token (paso 8)
        // Por ahora lo logueamos para poder probarlo
        log.info("Password reset token (DEV ONLY): {}", reset.getToken());

        return new PasswordResetResponseDTO("Password reset email sent successfully");
    }

    // ──────────────────────────────────────────────
    // ACTUALIZAR CONTRASEÑA
    // ──────────────────────────────────────────────
    @Override
    @Transactional
    public void updatePassword(PasswordUpdateRequestDTO request) {
        // 1. Buscamos el token de reset
        PasswordReset reset = passwordResetRepository.findByToken(request.getToken())
                .orElseThrow(() -> new DoorbellException(ExceptionEnum.PASSWORD_TOKEN_NOT_FOUND));

        // 2. Verificamos que no haya expirado
        LocalDateTime expiration = reset.getCreatedAt()
                .plusMinutes(PASSWORD_RESET_EXPIRATION_MINUTES);
        if (LocalDateTime.now().isAfter(expiration)) {
            passwordResetRepository.delete(reset);
            throw new DoorbellException(ExceptionEnum.PASSWORD_TOKEN_EXPIRED);
        }

        AuthUser user = reset.getAuthUser();

        // 3. Verificamos que la nueva contraseña sea diferente a la actual
        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new DoorbellException(ExceptionEnum.ALREADY_USED_PASSWORD);
        }

        // 4. Actualizamos la contraseña y activamos el usuario (por si era INACTIVE)
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setUpdatedAt(LocalDateTime.now());
        authUserRepository.save(user);

        // 5. Eliminamos el token de reset usado
        passwordResetRepository.delete(reset);

        log.info("Password updated for: {}", user.getEmail());
    }

    // ──────────────────────────────────────────────
    // HELPER
    // ──────────────────────────────────────────────
    private RegisterResponseDTO toRegisterResponse(AuthUser user) {
        return RegisterResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}