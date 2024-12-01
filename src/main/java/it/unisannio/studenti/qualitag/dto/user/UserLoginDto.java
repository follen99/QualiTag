package it.unisannio.studenti.qualitag.dto.user;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO used for user login in the system using either username or email and password.
 */
public record UserLoginDto(
    @NotBlank String usernameOrEmail,
    @NotBlank String password) {

  @Override
  public String toString() {
    return "UserLoginDTO{"
        + "usernameOrEmail='" + usernameOrEmail + '\''
        + ", password='[PROTECTED]'"
        + '}';
  }
}