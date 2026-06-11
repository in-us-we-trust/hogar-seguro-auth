package ar.edu.uba.hogar.auth.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

  @Schema(description = "User email", example = "vonneumann@hotmail.com")
  @NotBlank(message = "El email es requerido")
  @Email(message = "El email debe ser válido")
  private String email;

  @Schema(description = "User password", example = "MiPassword123!")
  @NotBlank(message = "La contraseña es requerida")
  private String password;

  @Schema(description = "Datos del usuario provenientes de otro servicio, se firman en el JWT")
  private Map<String, Object> user;
}
