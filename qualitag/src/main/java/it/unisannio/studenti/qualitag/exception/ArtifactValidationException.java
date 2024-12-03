package it.unisannio.studenti.qualitag.exception;

public class ArtifactValidationException extends RuntimeException {

  /**
   * Constructs a new ArtifactValidationException.
   * Used to handle exceptions related to artifact validation.
   *
   * @param message
   */
  public ArtifactValidationException(String message) {
    super(message);
  }
}
