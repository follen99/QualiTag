package it.unisannio.studenti.qualitag.exception;

/**
 * ProjectValidationException is a custom exception class that extends RuntimeException.
 * It is used to handle exceptions related to project validation.
 */
public class ProjectValidationException extends RuntimeException {

  /**
   * Constructs a new ProjectValidationException.
   * Used to handle exceptions related to project validation.
   *
   * @param message the message to be displayed when the exception is thrown
   */
  public ProjectValidationException(String message) {
    super(message);
  }
}
