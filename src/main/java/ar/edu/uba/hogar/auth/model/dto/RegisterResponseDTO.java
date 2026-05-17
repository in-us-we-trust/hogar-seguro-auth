package ar.edu.uba.hogar.auth.model.dto;

import ar.edu.uba.hogar.auth.enums.UserStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDTO {
    @Schema(description = "Identificador único del usuario", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "User email", example = "vonneumann@hotmail.com")
    private String email;

    @Schema(description = "User status", example = "ACTIVE")
    private UserStatusEnum status;
}