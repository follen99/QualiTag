package it.unisannio.studenti.qualitag.dto.user;

import java.util.regex.Pattern;

/**
 * DTO used for user registration in the system.
 */
public record UserRegistrationDto(String username, String email, String password, String name,
                                  String surname) {

  // Validation Methods
  /**
   * Checks if the email is valid.
   *
   * @return true if the email is valid, false otherwise.
   */
  public boolean isValidEmail() {
    String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
    Pattern pattern = Pattern.compile(emailRegex);
    return pattern.matcher(this.email).matches();
  }

  /**
   * Checks if the password is valid.
   *
   * @return true if the password is valid, false otherwise.
   */
  public boolean isValidPassword() {
    // Example: Password must be at least 8 characters long
    return this.password != null && this.password.length() >= 8;
  }
}