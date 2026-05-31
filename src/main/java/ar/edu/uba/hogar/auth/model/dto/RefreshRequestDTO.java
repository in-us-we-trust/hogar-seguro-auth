package ar.edu.uba.hogar.auth.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshRequestDTO {

  @Schema(
      description = "Refresh token recibido al hacer login",
      example = "550e8400-e29b-41d4-a716-446655440000")
  @NotNull(message = "El refresh token es requerido")
  private UUID refreshToken;
}
