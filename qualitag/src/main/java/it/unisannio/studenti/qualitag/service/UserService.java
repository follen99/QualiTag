package it.unisannio.studenti.qualitag.service;

import it.unisannio.studenti.qualitag.dto.user.UserModifyDto;
import it.unisannio.studenti.qualitag.mapper.UserMapper;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import it.unisannio.studenti.qualitag.security.service.AuthenticationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * The userService class is a service class that provides methods to manage the user entity.
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private final Validator validator = factory.getValidator();

  /**
   * Validates the user modification data.
   *
   * @param userModifyDto The user modification data to validate.
   * @return true if the user modification data is valid, false otherwise.
   */
  public boolean isValidUserModification(UserModifyDto userModifyDto) {
    Set<ConstraintViolation<UserModifyDto>> violations = validator.validate(
        userModifyDto);

    return violations.isEmpty();
  }

  /**
   * Checks if the username is valid. Requirements: - Only alphanumeric characters and underscores -
   * Length between 3 and 20 characters
   *
   * @param username The username to check.
   * @return true if the username is valid, false otherwise.
   */
  public static boolean isValidUsername(String username) {
    String usernameRegex = "^[A-Za-z0-9_]{3,20}$";
    Pattern pattern = Pattern.compile(usernameRegex);
    return pattern.matcher(username).matches();
  }

  /**
   * Checks if the email is valid.
   *
   * @param email The email to check.
   * @return true if the email is valid, false otherwise.
   */
  public static boolean isValidEmail(String email) {
    String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    Pattern pattern = Pattern.compile(emailRegex);
    return pattern.matcher(email).matches();
  }

  /**
   * Checks if the password is valid. Minimum requirements: - At least one uppercase letter - At
   * least one lowercase letter - At least one digit - At least one special character - Minimum
   * length of 8 characters
   *
   * @param password The password to check.
   * @return true if the password is valid, false otherwise.
   */
  public static boolean isValidPassword(String password) {
    String passwordRegex = "^(?=.*[A-Z])"
        + "(?=.*[a-z])"
        + "(?=.*\\d)"
        + "(?=.*[@$!%*?&])"
        + "[A-Za-z\\d@$!%*?&]{8,}$";
    Pattern pattern = Pattern.compile(passwordRegex);
    return pattern.matcher(password).matches();
  }

  /**
   * Modifies a user.
   *
   * @param userModifyDto The user modification data.
   * @param username      The username of the user to modify.
   * @return A response entity with the result of the modification.
   */
  public ResponseEntity<?> updateUser(UserModifyDto userModifyDto, String username) {
    // Check if the user is trying to modify another user
    if (!AuthenticationService.getAuthority(username)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body("You are not authorized to modify this user.");
    }

    // DTO validation
    if (!isValidUserModification(userModifyDto)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All fields must be filled.");
    }

    // Username validation
    if (!UserService.isValidUsername(userModifyDto.username())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid username.");
    }

    // Email validation
    if (!UserService.isValidEmail(userModifyDto.email())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email address.");
    }

    // Retrieve the existing user
    User existingUser = userRepository.findByUsername(username);
    if (existingUser == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found.");
    }

    // Check if the username is already taken
    if (!existingUser.getUsername().equals(userModifyDto.username())
        && userRepository.existsByUsername(userModifyDto.username())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already taken.");
    }

    // Check if the email is already taken
    if (!existingUser.getEmail().equals(userModifyDto.email())
        && userRepository.existsByEmail(userModifyDto.email())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already taken.");
    }

    // Update the user
    userMapper.updateEntity(userModifyDto, existingUser);

    // Save the updated user
    userRepository.save(existingUser);

    return ResponseEntity.status(HttpStatus.OK).body("User modified successfully.");
  }

  /**
   * Deletes a user.
   *
   * @param username The username of the user to delete.
   * @return A response entity with the result of the deletion.
   */
  public ResponseEntity<?> deleteUser(String username) {
    // Check if the user is trying to delete another user
    if (!AuthenticationService.getAuthority(username)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body("You are not authorized to delete this user.");
    }

    userRepository.deleteByUsername(username);
    return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully.");
  }

  /**
   * Gets all users.
   *
   * @return A response entity with all users.
   */
  public ResponseEntity<?> getAllUsers() {
    return ResponseEntity.status(HttpStatus.OK).body(userRepository.findAll());
  }
}
