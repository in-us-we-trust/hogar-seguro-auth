package ar.edu.uba.hogar.auth.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

  @Schema(
      description = "JWT de corta duración para autenticar requests",
      example = "eyJhbGciOiJIUzI1NiJ9...")
  private String accessToken;

  @Schema(
      description = "Token de larga duración para renovar el accessToken",
      example = "550e8400-e29b-41d4-a716-446655440000")
  private String refreshToken;
}
