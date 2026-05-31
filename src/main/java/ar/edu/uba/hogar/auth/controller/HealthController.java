package ar.edu.uba.hogar.auth.controller;

import ar.edu.uba.hogar.auth.model.generic.StandardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "Estado de la aplicación")
public class HealthController {

  @Operation(summary = "Health check", description = "Verifica que el servicio esté corriendo")
  @ApiResponse(responseCode = "200", description = "Servicio funcionando correctamente")
  @GetMapping
  public ResponseEntity<StandardResponse<Map<String, String>>> health() {
    return ResponseEntity.ok(StandardResponse.of(Map.of("status", "UP")));
  }
}
