package it.unisannio.studenti.qualitag.exception;

public class ProjectValidationException extends RuntimeException {

  /**
   * Constructs a new ProjectValidationException.
   * Used to handle exceptions related to project validation.
   *
   * @param message
   */
  public ProjectValidationException(String message) {
    super(message);
  }
}
