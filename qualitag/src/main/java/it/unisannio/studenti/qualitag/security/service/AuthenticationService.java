package it.unisannio.studenti.qualitag.security.service;

import io.jsonwebtoken.ExpiredJwtException;
import it.unisannio.studenti.qualitag.dto.user.ForgotPasswordDto;
import it.unisannio.studenti.qualitag.dto.user.PasswordUpdateDto;
import it.unisannio.studenti.qualitag.dto.user.UserLoginDto;
import it.unisannio.studenti.qualitag.dto.user.UserRegistrationDto;
import it.unisannio.studenti.qualitag.dto.user.UserResponseDto;
import it.unisannio.studenti.qualitag.mapper.UserMapper;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import it.unisannio.studenti.qualitag.security.model.CustomUserDetails;
import it.unisannio.studenti.qualitag.service.GmailService;
import it.unisannio.studenti.qualitag.service.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
  private final UserService userService;
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
    Set<ConstraintViolation<UserRegistrationDto>> violations =
        validator.validate(userRegistrationDto);

    return violations.isEmpty();
  }

  /**
   * Checks if the currently authenticated user has the authority to access the user with the
   * specified username.
   *
   * @param username The username of the user to check.
   * @return true if the currently authenticated user has the authority to access the user with the
   *         specified username, false otherwise.
   */
  public static boolean getAuthority(String username) {
    // Get the currently authenticated user
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String authenticatedUsername = authentication.getName();

    // Check if the authenticated user is the same as the logged user
    return !authenticatedUsername.equals(username);
  }

  /**
   * Validates the user login data.
   *
   * @param userLoginDto The user login data to validate.
   * @return true if the user login data is valid, false otherwise.
   */
  public boolean isValidUserLogin(UserLoginDto userLoginDto) {
    Set<ConstraintViolation<UserLoginDto>> violations = validator.validate(userLoginDto);

    return violations.isEmpty();
  }

  /**
   * Registers a new user.
   *
   * @param request The user registration request.
   * @return The response entity.
   */
  public ResponseEntity<?> register(UserRegistrationDto request) throws Exception {
    Map<String, Object> response = new HashMap<>();

    // DTO validation
    if (!isValidUserRegistration(request)) {
      response.put("msg", "All fields must be filled.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Username validation
    if (UserService.isNotValidUsername(request.username())) {
      response.put("msg", "Invalid username.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Email validation
    if (UserService.isNotValidEmail(request.email())) {
      response.put("msg", "Invalid email address.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Password validation
    if (UserService.isNotValidPassword(request.password())) {
      response.put("msg", "Invalid password.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Check if the username is already taken
    if (userRepository.existsByUsername(request.username())) {
      response.put("msg", "Username already taken.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Check if the email is already taken
    if (userRepository.existsByEmail(request.email())) {
      response.put("msg", "Email already taken.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Send email
    new GmailService().sendMail("QualiTag Registration", request.email(),
        "Thank you for registering to QualiTag!");

    // Map the new user from the request
    User user = userMapper.toEntity(request);

    // Save the user in the database
    userRepository.save(user);

    // Generate a JWT token
    String jwt = jwtService.generateToken(new CustomUserDetails(user));

    // Return the CREATED status and the JWT token
    response.put("msg", "User registered successfully.");
    response.put("token", jwt);
    response.put("username", user.getUsername());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Logs in a user.
   *
   * @param request The user login request.
   * @return The response entity.
   */
  public ResponseEntity<?> login(UserLoginDto request) {
    Map<String, Object> response = new HashMap<>();

    // DTO validation
    if (!isValidUserLogin(request)) {
      response.put("msg", "All fields must be filled.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Authenticate the user
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.usernameOrEmail(), request.password()));
    } catch (BadCredentialsException ex) {
      // Handle incorrect username/email or password
      response.put("msg", "Invalid email or password.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    User user =
        userRepository.findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail());
    if (user == null) {
      response.put("msg", "Invalid email or password.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Generate a JWT token
    String jwt = jwtService.generateToken(new CustomUserDetails(user));

    // Return all the user data
    /*
     * response.put("msg", "User logged in successfully."); response.put("token", jwt);
     * response.put("username", user.getUsername()); response.put("user-email", user.getEmail());
     * response.put("user-role", user.getProjectRoles()); response.put("user-firstname",
     * user.getName()); response.put("user-lastname", user.getSurname());
     * response.put("user-projectIds", user.getProjectIds()); response.put("user-teamIds",
     * user.getTeamIds()); response.put("user-tagIds", user.getTeamIds());
     */
    UserResponseDto returnDto = new UserResponseDto(user.getUsername(), user.getEmail(),
        user.getName(), user.getSurname(), user.getProjectIds(), user.getTeamIds(),
        user.getTagIds(), user.getProjectRolesAsString());

    response.put("msg", "User logged in successfully.");
    response.put("token", jwt);
    response.put("user", returnDto);



    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * Sends a password reset email to the user.
   *
   * @param dto The DTO containing email of the user.
   * @return The response entity.
   */
  public ResponseEntity<?> sendPasswordResetEmail(ForgotPasswordDto dto) throws Exception {
    Map<String, Object> response = new HashMap<>();

    User user = userRepository.findByEmail(dto.email());
    if (user == null) {
      response.put("msg", "No user found with the given email.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Generate JWT token
    String resetToken = jwtService.generateResetToken(new CustomUserDetails(user));

    // Update reset token expiration in the database
    user.setResetTokenExpiration(LocalDateTime.now().plusMinutes(jwtService.getJwtResetPwMin()));
    userRepository.save(user);

    // Send email with reset link
    String resetLink = "http://localhost:8080/reset-password?token=" + resetToken;
    new GmailService().sendMail("QualiTag Password Reset", dto.email(),
        "Click the following link to reset your password: " + resetLink);

    response.put("msg", "Password reset email sent successfully.");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * Resets the password of the user.
   *
   * @param token The reset token.
   * @param dto The new password.
   * @return The response entity.
   */
  public ResponseEntity<?> resetPassword(String token, PasswordUpdateDto dto) {
    Map<String, Object> response = new HashMap<>();

    // Validate the password update data
    String msg = userService.isValidPasswordUpdateDto(dto);
    if (msg != null) {
      response.put("msg", msg);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Try to extract the username from the token and throw an exception if the token is
    // invalid or expired
    String username;
    try {
      username = jwtService.extractUserName(token);
    } catch (ExpiredJwtException ex) {
      response.put("msg", "Invalid or expired reset token.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Find the user by username
    User user = userRepository.findByUsername(username);
    if (user == null) {
      response.put("msg", "No user found with the given username.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Check if the reset token is valid or expired
    if (user.getResetTokenExpiration() == null
        || user.getResetTokenExpiration().isBefore(LocalDateTime.now())
        || !jwtService.isTokenValid(token, new CustomUserDetails(user))) {
      response.put("msg", "Invalid or expired reset token.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Update the password
    userMapper.updateEntity(dto, user);
    user.setResetTokenExpiration(null);
    userRepository.save(user);

    response.put("msg", "Password reset successfully.");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
