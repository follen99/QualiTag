package it.unisannio.studenti.qualitag.security.service;

import it.unisannio.studenti.qualitag.dto.user.UserLoginDto;
import it.unisannio.studenti.qualitag.dto.user.UserRegistrationDto;
import it.unisannio.studenti.qualitag.mapper.UserMapper;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import it.unisannio.studenti.qualitag.security.model.CustomUserDetails;
import it.unisannio.studenti.qualitag.service.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * The AuthenticationService class provides methods to authenticate users.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private final Validator validator = factory.getValidator();

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
   * @param request The user registration request.
   * @return The response entity.
   */
  public ResponseEntity<?> register(UserRegistrationDto request) {
    // DTO validation
    if (!isValidUserRegistration(request)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All fields must be filled.");
    }

    // Username validation
    if (!UserService.isValidUsername(request.username())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid username.");
    }

    // Email validation
    if (!UserService.isValidEmail(request.email())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email address.");
    }

    // Password validation
    if (!UserService.isValidPassword(request.password())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid password.");
    }

    // Check if the username is already taken
    if (userRepository.existsByUsername(request.username())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already taken.");
    }

    // Check if the email is already taken
    if (userRepository.existsByEmail(request.email())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already taken.");
    }

    // Map the new user from the request
    User user = userMapper.toEntity(request);

    // Save the user in the database
    userRepository.save(user);

    // Generate a JWT token
    String jwt = jwtService.generateToken(new CustomUserDetails(user));

    // Return the CREATED status and the JWT token
    return ResponseEntity.status(HttpStatus.CREATED).body(jwt);
  }

  /**
   * Logs in a user.
   *
   * @param request The user login request.
   * @return The response entity.
   */
  public ResponseEntity<?> login(UserLoginDto request) throws IllegalArgumentException {
    // DTO validation
    if (!isValidUserLogin(request)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All fields must be filled.");
    }

    // Authenticate the user
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.usernameOrEmail(), request.password()));

    User user = userRepository.findByUsernameOrEmail(request.usernameOrEmail(),
        request.usernameOrEmail());
    if (user == null) {
      throw new IllegalArgumentException("Invalid email or password.");
    }

    // Generate a JWT token
    String jwt = jwtService.generateToken(new CustomUserDetails(user));

    // Return the OK status and the JWT token
    return ResponseEntity.status(HttpStatus.OK).body(jwt);
  }
}
