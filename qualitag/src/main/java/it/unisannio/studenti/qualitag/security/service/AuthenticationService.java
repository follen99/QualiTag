package it.unisannio.studenti.qualitag.security.service;

import it.unisannio.studenti.qualitag.dto.user.UserLoginDto;
import it.unisannio.studenti.qualitag.dto.user.UserRegistrationDto;
import it.unisannio.studenti.qualitag.mapper.UserMapper;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import it.unisannio.studenti.qualitag.security.model.CustomUserDetails;
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

  /**
   * Registers a new user.
   *
   * @param request The user registration request.
   * @return The response entity.
   */
  public ResponseEntity<?> register(UserRegistrationDto request) {
    User user = userMapper.toEntity(request);
    userRepository.save(user);
    String jwt = jwtService.generateToken(new CustomUserDetails(user));

    return ResponseEntity.status(HttpStatus.CREATED).body(jwt);
  }

  /**
   * Logs in a user.
   *
   * @param request The user login request.
   * @return The response entity.
   */
  public ResponseEntity<?> login(UserLoginDto request) throws IllegalArgumentException {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.usernameOrEmail(), request.password()));

    User user = userRepository.findByUsernameOrEmail(request.usernameOrEmail(),
        request.usernameOrEmail());
    if (user == null) {
      throw new IllegalArgumentException("Invalid email or password.");
    }

    String jwt = jwtService.generateToken(new CustomUserDetails(user));

    return ResponseEntity.status(HttpStatus.OK).body(jwt);
  }
}
