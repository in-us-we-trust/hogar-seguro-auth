package ar.edu.uba.hogar.auth.model.generic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StandardResponse<T> {

  @Schema(description = "Datos de la respuesta")
  private T data;

  public static <T> StandardResponse<T> of(T data) {
    return new StandardResponse<>(data);
  }
}
