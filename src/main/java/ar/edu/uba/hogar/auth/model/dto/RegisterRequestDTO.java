package ar.edu.uba.hogar.auth.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

  @Schema(description = "User email", example = "vonneumann@hotmail.com")
  @NotBlank(message = "El email es requerido")
  @Email(message = "El email debe ser válido")
  private String email;

  @Schema(description = "User password (min 8 characters)", example = "MiPassword123!")
  @NotBlank(message = "La contraseña es requerida")
  @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
  private String password;
}
