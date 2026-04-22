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
public class InviteContactRequestDTO {

    @Schema(description = "Email del contacto a invitar", example = "contacto@hotmail.com")
    @NotBlank(message = "El email del contacto es requerido")
    @Email(message = "El email debe ser válido")
    private String contactEmail;
}