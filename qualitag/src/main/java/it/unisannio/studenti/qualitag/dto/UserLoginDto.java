package it.unisannio.studenti.qualitag.dto;

/**
 * DTO used for user login in the system using either username or email and password.
 */
public record UserLoginDto(String usernameOrEmail, String password) {
  @Override
  public String toString() {
    return "UserLoginDTO{"
        + "usernameOrEmail='" + usernameOrEmail + '\''
        + ", password='[PROTECTED]'"
        + '}';
  }
}