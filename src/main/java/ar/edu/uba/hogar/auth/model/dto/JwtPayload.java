package ar.edu.uba.hogar.auth.model.dto;

import ar.edu.uba.hogar.auth.enums.UserStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JwtPayload {

  @Schema(
      description = "Identificador único del usuario",
      example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID userId;

  @Schema(description = "User email", example = "vonneumann@hotmail.com")
  private String email;

  @Schema(description = "Nombre del usuario", example = "John")
  private String firstName;

  @Schema(description = "Apellido del usuario", example = "Doe")
  private String lastName;

  @Schema(description = "Estado del usuario", example = "ACTIVE")
  private UserStatusEnum status;
}
