package it.unisannio.studenti.qualitag.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.unisannio.studenti.qualitag.dto.user.UserRegistrationDto;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class UserServiceTest {

  private UserRepository userRepository;
  private UserService userService;

  @BeforeEach
  public void setUp() {
    userRepository = mock(UserRepository.class);
    userService = new UserService(userRepository);
  }

  @Test
  public void testHashPassword() {
    String password = "password123";
    String hashedPassword = userService.hashPassword(password);
    assertTrue(BCrypt.checkpw(password, hashedPassword));
  }

  @Test
  public void testCheckPassword() {
    String password = "password123";
    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
    assertTrue(userService.checkPassword(password, hashedPassword));
    assertFalse(userService.checkPassword("wrongPassword", hashedPassword));
  }

  @Test
  public void testIsValidEmail() {
    assertTrue(userService.isValidEmail("test@example.com"));
    assertFalse(userService.isValidEmail("invalid-email"));
  }

  @Test
  public void testIsValidPassword() {
    assertTrue(userService.isValidPassword("Valid1@password"));
    assertFalse(userService.isValidPassword("short"));
  }

  @Test
  public void testIsValidUserRegistration() {
    UserRegistrationDto validDto = new UserRegistrationDto("username", "test@example.com", "Valid1@password", "John", "Doe");
    assertTrue(userService.isValidUserRegistration(validDto));

    UserRegistrationDto invalidDto = new UserRegistrationDto("", "test@example.com", "short", "John", "Doe");
    assertFalse(userService.isValidUserRegistration(invalidDto));
  }

  @Test
  public void testRegisterUser() {
    UserRegistrationDto validDto = new UserRegistrationDto("username", "test@example.com", "Valid1@password", "John", "Doe");

    when(userRepository.existsByUsername(validDto.username())).thenReturn(false);
    when(userRepository.existsByEmail(validDto.email())).thenReturn(false);

    ResponseEntity<?> response = userService.registerUser(validDto);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("User registered successfully.", response.getBody());

    UserRegistrationDto invalidDto = new UserRegistrationDto("username", "invalid-email", "short", "John", "Doe");
    response = userService.registerUser(invalidDto);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }
}