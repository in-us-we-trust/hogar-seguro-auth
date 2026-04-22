package ar.edu.uba.hogar.auth.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequestDTO {

    @Schema(description = "Email del usuario que quiere resetear su contraseña", example = "vonneumann@hotmail.com")
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe ser válido")
    private String email;
}
