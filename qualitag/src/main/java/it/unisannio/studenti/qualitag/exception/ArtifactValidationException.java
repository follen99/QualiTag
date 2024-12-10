package it.unisannio.studenti.qualitag.exception;

/**
 * Exception thrown when an artifact is not valid.
 */
public class ArtifactValidationException extends RuntimeException {

  /**
   * Constructs a new ArtifactValidationException. Used to handle exceptions related to artifact
   * validation.
   *
   * @param message the message to be displayed when the exception is thrown
   */
  public ArtifactValidationException(String message) {
    super(message);
  }
}
