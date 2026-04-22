package ar.edu.uba.hogar.auth.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ar.edu.uba.hogar.auth.enums.RolesEnum;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class JwtPayload {

    @Schema(description = "Identificador único del usuario", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @Schema(description = "User email", example = "vonneumann@hotmail.com")
    private String email;

    @Schema(description = "User role", example = "OWNER")
    private RolesEnum role;
}