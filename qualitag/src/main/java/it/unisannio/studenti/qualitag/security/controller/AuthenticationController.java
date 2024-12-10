package it.unisannio.studenti.qualitag.security.controller;

import it.unisannio.studenti.qualitag.dto.user.UserLoginDto;
import it.unisannio.studenti.qualitag.dto.user.UserRegistrationDto;
import it.unisannio.studenti.qualitag.security.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
