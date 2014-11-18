package cn.dreampie;

public class FlywayException extends RuntimeException {
  public FlywayException(String message) {
    super(message);
  }

  public FlywayException(String message, Throwable cause) {
    super(message, cause);
  }
}
