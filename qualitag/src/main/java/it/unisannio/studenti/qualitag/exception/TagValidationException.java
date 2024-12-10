package it.unisannio.studenti.qualitag.exception;

/**
 * TagValidationException is a custom exception class that extends RuntimeException. It is used to
 * handle exceptions related to tag validation.
 */
public class TagValidationException extends RuntimeException {

  /**
   * Constructs a new TagValidationException. Used to handle exceptions related to tag validation.
   *
   * @param message the message to be displayed when the exception is thrown
   */
  public TagValidationException(String message) {
    super(message);
  }
}
