package ar.edu.uba.hogar.auth.controller;

import ar.edu.uba.hogar.auth.exception.ExceptionContent;
import ar.edu.uba.hogar.auth.model.dto.*;
import ar.edu.uba.hogar.auth.model.generic.StandardResponse;
import ar.edu.uba.hogar.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Registro, login y gestión de tokens")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Registrar usuario", description = "Crea una nueva cuenta con email y contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El usuario ya existe o datos inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionContent.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<StandardResponse<RegisterResponseDTO>> register(
            @Valid @RequestBody RegisterRequestDTO request) {
        var response = authService.registerUser(request);
        return ResponseEntity.status(201).body(StandardResponse.of(response));
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y devuelve un JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionContent.class))),
            @ApiResponse(responseCode = "403", description = "Usuario bloqueado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionContent.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionContent.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<StandardResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request) {
        var response = authService.loginUser(request);
        return ResponseEntity.ok(StandardResponse.of(response));
    }

    @Operation(summary = "Validar token", description = "Verifica que el JWT sea válido y devuelve su payload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token válido"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionContent.class)))
    })
    @GetMapping("/validate")
    public ResponseEntity<StandardResponse<JwtPayload>> validate(
            @RequestHeader("Authorization") String authHeader) {
        // El header llega como "Bearer eyJhbG..." — sacamos el prefijo
        String token = authHeader.replace("Bearer ", "");
        var response = authService.validateToken(token);
        return ResponseEntity.ok(StandardResponse.of(response));
    }

    @Operation(summary = "Solicitar reset de contraseña",
            description = "Envía un email con el link para resetear la contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email enviado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionContent.class))),
            @ApiResponse(responseCode = "403", description = "Usuario bloqueado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionContent.class)))
    })
    @PostMapping("/password-reset")
    public ResponseEntity<StandardResponse<PasswordResetResponseDTO>> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequestDTO request) {
        var response = authService.requestPasswordReset(request);
        return ResponseEntity.ok(StandardResponse.of(response));
    }

    @Operation(summary = "Actualizar contraseña",
            description = "Establece una nueva contraseña usando el token recibido por email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Token inválido, expirado o contraseña ya usada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionContent.class))),
            @ApiResponse(responseCode = "404", description = "Token no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionContent.class)))
    })
    @PutMapping("/password-update")
    public ResponseEntity<StandardResponse<String>> updatePassword(
            @Valid @RequestBody PasswordUpdateRequestDTO request) {
        authService.updatePassword(request);
        return ResponseEntity.ok(StandardResponse.of("Password updated successfully"));
    }
}