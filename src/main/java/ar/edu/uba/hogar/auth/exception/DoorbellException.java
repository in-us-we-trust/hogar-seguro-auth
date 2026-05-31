package ar.edu.uba.hogar.auth.exception;

import org.springframework.http.HttpStatus;

public class DoorbellException extends RuntimeException {

  private final String title;
  private final String detail;
  private final HttpStatus httpStatus;

  public DoorbellException(ExceptionEnum exceptionEnum) {
    super(exceptionEnum.getDetail());
    this.title = exceptionEnum.getTitle();
    this.detail = exceptionEnum.getDetail();
    this.httpStatus = exceptionEnum.getStatus();
  }

  // Constructor con detalle personalizado (ej: incluir el email en el mensaje)
  public DoorbellException(ExceptionEnum exceptionEnum, String customDetail) {
    super(customDetail);
    this.title = exceptionEnum.getTitle();
    this.detail = customDetail;
    this.httpStatus = exceptionEnum.getStatus();
  }

  public String getTitle() {
    return title;
  }

  public String getDetail() {
    return detail;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
}
