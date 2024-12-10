package it.unisannio.studenti.qualitag.exception;

/**
 * Exception thrown when a team validation fails.
 */
public class TeamValidationException extends RuntimeException {

  /**
   * Constructs a new TeamValidationException.
   *
   * @param message The exception message.
   */
  public TeamValidationException(String message) {
    super(message);
  }
}
