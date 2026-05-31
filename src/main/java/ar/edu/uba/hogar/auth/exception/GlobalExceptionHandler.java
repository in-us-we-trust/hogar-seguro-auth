package ar.edu.uba.hogar.auth.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  // Captura excepciones custom (DoorbellException)
  @ExceptionHandler(DoorbellException.class)
  public ResponseEntity<ExceptionContent> handleDoorbellException(
      DoorbellException ex, HttpServletRequest request) {

    ExceptionContent body =
        ExceptionContent.builder()
            .status(ex.getHttpStatus().value())
            .title(ex.getTitle())
            .detail(ex.getDetail())
            .instance(request.getRequestURI())
            .type("about:blank")
            .build();

    log.error("DoorbellException at {}: {}", request.getRequestURI(), ex.getDetail());
    return new ResponseEntity<>(body, ex.getHttpStatus());
  }

  // Captura errores de validación (@NotBlank, @Email, @Size, etc.)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionContent> handleValidationException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    StringBuilder details = new StringBuilder();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      details.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
    }

    ExceptionContent body =
        ExceptionContent.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .title("Validation error")
            .detail(details.toString())
            .instance(request.getRequestURI())
            .type("about:blank")
            .build();

    log.error("Validation error at {}: {}", request.getRequestURI(), details);
    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  // Captura cualquier otra excepción no esperada
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionContent> handleGenericException(
      Exception ex, HttpServletRequest request) {

    ExceptionContent body =
        ExceptionContent.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .title("Internal Server Error")
            .detail(ex.getMessage())
            .instance(request.getRequestURI())
            .type("about:blank")
            .build();

    log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
