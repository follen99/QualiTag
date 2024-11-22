package it.unisannio.studenti.qualitag.service;

import it.unisannio.studenti.qualitag.dto.user.UserLoginDto;
import it.unisannio.studenti.qualitag.dto.user.UserRegistrationDto;
import it.unisannio.studenti.qualitag.mapper.UserMapper;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

/**
 * The userService class is a service class that provides methods to manage the user entity.
 */
@Service
public class UserService {

  private static final int LOG_ROUNDS = 12; // Adjust this value as needed

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private final Validator validator = factory.getValidator();

  /**
   * Constructs a new UserService.
   *
   * @param userRepository The user repository.
   */
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
    this.userMapper = new UserMapper(this);
  }

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
   * @param hashed   The hashed password to compare against.
   * @return True if the password matches the hashed password, false otherwise.
   */
  public boolean checkPassword(String password, String hashed) {
    return BCrypt.checkpw(password, hashed);
  }

  /**
   * Checks if the email is valid.
   *
   * @param email The email to check.
   * @return true if the email is valid, false otherwise.
   */
  public boolean isValidEmail(String email) {
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
  public boolean isValidPassword(String password) {
    String passwordRegex = "^(?=.*[A-Z])"
        + "(?=.*[a-z])"
        + "(?=.*\\d)"
        + "(?=.*[@$!%*?&])"
        + "[A-Za-z\\d@$!%*?&]{8,}$";
    Pattern pattern = Pattern.compile(passwordRegex);
    return pattern.matcher(password).matches();
  }

  /**
   * Validates the user registration data.
   *
   * @param userRegistrationDto The user registration data to validate.
   * @return true if the user registration data is valid, false otherwise.
   */
  public boolean isValidUserRegistration(UserRegistrationDto userRegistrationDto) {
    Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(
        userRegistrationDto);

    return violations.isEmpty();
  }

  /**
   * Validates the user login data.
   *
   * @param userLoginDto The user login data to validate.
   * @return true if the user login data is valid, false otherwise.
   */
  public boolean isValidUserLogin(UserLoginDto userLoginDto) {
    Set<ConstraintViolation<UserLoginDto>> violations = validator.validate(
        userLoginDto);

    return violations.isEmpty();
  }

  /**
   * Registers a new user.
   *
   * @param userRegistrationDto The user registration data.
   * @return A response entity with the result of the registration.
   */
  public ResponseEntity<?> registerUser(UserRegistrationDto userRegistrationDto) {
    // DTO validation
    if (!isValidUserRegistration(userRegistrationDto)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All fields must be filled.");
    }

    // Email validation
    if (!isValidEmail(userRegistrationDto.email())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email address.");
    }

    // Password validation
    if (!isValidPassword(userRegistrationDto.password())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid password.");
    }

    // Check if the username is already taken
    if (userRepository.existsByUsername(userRegistrationDto.username())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already taken.");
    }

    // Check if the email is already taken
    if (userRepository.existsByEmail(userRegistrationDto.email())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already taken.");
    }

    // Register the user
    assert userMapper != null;
    User user = userMapper.toEntity(userRegistrationDto);

    userRepository.save(user);

    return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
  }

  /**
   * Logs in a user.
   *
   * @param userLoginDto The user login data.
   * @return A response entity with the result of the login.
   */
  public ResponseEntity<?> loginUser(UserLoginDto userLoginDto) {
    // DTO validation
    if (!isValidUserLogin(userLoginDto)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All fields must be filled.");
    }

    // Check if the user exists
    User user = userRepository.findByUsernameOrEmail(userLoginDto.usernameOrEmail(),
        userLoginDto.usernameOrEmail());
    if (user == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found.");
    }

    // Check if the password is correct
    if (!checkPassword(userLoginDto.password(), user.getPasswordHash())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect password.");
    }

    return ResponseEntity.status(HttpStatus.OK).body("User logged in successfully.");
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
