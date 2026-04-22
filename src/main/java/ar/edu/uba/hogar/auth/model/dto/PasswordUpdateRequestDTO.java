package ar.edu.uba.hogar.auth.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateRequestDTO {

    @Schema(description = "Token de reset recibido por email", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "El token es requerido")
    private UUID token;

    @Schema(description = "Nueva contraseña (min 8 caracteres)", example = "NuevoPassword123!")
    @NotBlank(message = "La nueva contraseña es requerida")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String newPassword;
}