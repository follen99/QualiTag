package it.unisannio.studenti.qualitag.security.controller;

import it.unisannio.studenti.qualitag.dto.user.ForgotPasswordDto;
import it.unisannio.studenti.qualitag.dto.user.PasswordUpdateDto;
import it.unisannio.studenti.qualitag.dto.user.UserLoginDto;
import it.unisannio.studenti.qualitag.dto.user.UserRegistrationDto;
import it.unisannio.studenti.qualitag.security.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The AuthenticationController class is a REST controller that provides endpoints for
 * authentication.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  /**
   * Registers a new user.
   *
   * @param userRegistrationDto The user registration DTO.
   * @return The response entity.
   */
  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto userRegistrationDto)
      throws Exception {
    return authenticationService.register(userRegistrationDto);
  }

  /**
   * Logs in a user.
   *
   * @param userLoginDto The user login DTO.
   * @return The response entity.
   */
  @PostMapping("/login")
  public ResponseEntity<?> loginUser(@RequestBody UserLoginDto userLoginDto) {
    return authenticationService.login(userLoginDto);
  }

  /**
   * Sends a password reset email.
   *
   * @param email The email.
   * @return The response entity.
   */
  @PostMapping("/forgot-password")
  public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDto email) throws Exception {
    return authenticationService.sendPasswordResetEmail(email);
  }

  /**
   * Resets the password.
   *
   * @param token The token.
   * @param passwordUpdateDto The password update DTO.
   * @return The response entity.
   */
  @PostMapping("/reset-password")
  public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
      @RequestBody PasswordUpdateDto passwordUpdateDto) {
    return authenticationService.resetPassword(token, passwordUpdateDto);
  }

  /**
   * Checks the token.
   */
  @GetMapping("/check-token")
  public ResponseEntity<?> checkToken() {
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
