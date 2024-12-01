package it.unisannio.studenti.qualitag.exception;

public class TagValidationException extends RuntimeException {

  /**
   * Constructs a new TagValidationException.
   * Used to handle exceptions related to tag validation.
   *
   * @param message
   */
    public TagValidationException(String message) {
        super(message);
    }
}
