package ar.edu.uba.hogar.auth.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionContent {

  @Schema(description = "HTTP status code", example = "400")
  private int status;

  @Schema(description = "Error title", example = "User already exists")
  private String title;

  @Schema(
      description = "Detailed error description",
      example = "A user with that email already exists.")
  private String detail;

  @Schema(description = "Request path where the error occurred", example = "/auth/register")
  private String instance;

  @Schema(description = "Error type", example = "about:blank")
  private String type;
}
