package ar.edu.uba.hogar.auth.exception;

import org.springframework.http.HttpStatus;

public enum ExceptionEnum {
  USER_ALREADY_EXISTS(
      "User already exists", HttpStatus.BAD_REQUEST, "A user with that email already exists."),
  USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND, "The user is not registered."),
  INVALID_CREDENTIALS(
      "Invalid credentials", HttpStatus.UNAUTHORIZED, "The email or password is incorrect."),
  USER_BLOCKED("User blocked", HttpStatus.FORBIDDEN, "The user account is blocked."),
  TOKEN_INVALID("Invalid token", HttpStatus.UNAUTHORIZED, "The token is invalid or malformed."),
  TOKEN_EXPIRED("Token expired", HttpStatus.UNAUTHORIZED, "The token has expired."),
  TOKEN_ERROR("Token error", HttpStatus.UNAUTHORIZED, "There was an error validating the token."),
  PASSWORD_TOKEN_NOT_FOUND(
      "Password reset token not found",
      HttpStatus.NOT_FOUND,
      "The password reset token was not found."),
  PASSWORD_TOKEN_EXPIRED(
      "Password reset token expired",
      HttpStatus.BAD_REQUEST,
      "The password reset token has expired."),
  ALREADY_USED_PASSWORD(
      "Password already used",
      HttpStatus.BAD_REQUEST,
      "The new password must be different from the current one."),
  SEND_EMAIL_ERROR(
      "Error sending email",
      HttpStatus.INTERNAL_SERVER_ERROR,
      "There was an error sending the email. Please try again later.");

  private final String title;
  private final HttpStatus status;
  private final String detail;

  ExceptionEnum(String title, HttpStatus status, String detail) {
    this.title = title;
    this.status = status;
    this.detail = detail;
  }

  public String getTitle() {
    return title;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getDetail() {
    return detail;
  }
}
