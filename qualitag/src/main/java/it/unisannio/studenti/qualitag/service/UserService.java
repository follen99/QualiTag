package it.unisannio.studenti.qualitag.service;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

/**
 * The userService class is a service class that provides methods to manage the user entity.
 */
@Service
public class UserService {
  private static final int LOG_ROUNDS = 12; // Adjust this value as needed

  /**
   * Hashes a password using BCrypt.
   *
   * @param password The password to hash.
   * @return The hashed password.
   */
  public String hashPassword(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt(LOG_ROUNDS));
  }

  /**
   * Checks if a password matches a hashed password.
   *
   * @param password The password to check.
   * @param hashed The hashed password to compare against.
   * @return True if the password matches the hashed password, false otherwise.
   */
  public boolean checkPassword(String password, String hashed) {
    return BCrypt.checkpw(password, hashed);
  }
}
