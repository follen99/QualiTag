package it.unisannio.studenti.qualitag.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unisannio.studenti.qualitag.dto.user.UserLoginDto;
import it.unisannio.studenti.qualitag.dto.user.UserRegistrationDto;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import java.util.List;
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
    assertFalse(userService.isValidPassword("no@uppercase123"));
    assertFalse(userService.isValidPassword("NO@LOWERCASE123"));
    assertFalse(userService.isValidPassword("No@number"));
    assertFalse(userService.isValidPassword("NoSpecialCharacter123"));
    assertFalse(userService.isValidPassword("$h0rt"));
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
    assertEquals("Invalid email address.", response.getBody());
  }

  @Test
  public void testRegisterUserWithExistingUsername() {
    UserRegistrationDto validDto = new UserRegistrationDto("username", "test@example.com", "Valid1@password", "John", "Doe");

    when(userRepository.existsByUsername(validDto.username())).thenReturn(true);
    when(userRepository.existsByEmail(validDto.email())).thenReturn(false);

    ResponseEntity<?> response = userService.registerUser(validDto);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Username already taken.", response.getBody());
  }

  @Test
  public void testRegisterUserWithExistingEmail() {
    UserRegistrationDto validDto = new UserRegistrationDto("username", "test@example.com", "Valid1@password", "John", "Doe");

    when(userRepository.existsByUsername(validDto.username())).thenReturn(false);
    when(userRepository.existsByEmail(validDto.email())).thenReturn(true);

    ResponseEntity<?> response = userService.registerUser(validDto);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Email already taken.", response.getBody());
  }

  @Test
  public void testLoginUser() {
    UserLoginDto validLoginDto = new UserLoginDto("username", "Valid1@password");
    User user = new User();
    user.setUsername("username");
    user.setPasswordHash(BCrypt.hashpw("Valid1@password", BCrypt.gensalt()));

    when(userRepository.findByUsernameOrEmail(validLoginDto.usernameOrEmail(), validLoginDto.usernameOrEmail())).thenReturn(user);

    ResponseEntity<?> response = userService.loginUser(validLoginDto);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("User logged in successfully.", response.getBody());
  }

  @Test
  public void testLoginUserWithInvalidDto() {
    UserLoginDto invalidLoginDto = new UserLoginDto("", "");

    ResponseEntity<?> response = userService.loginUser(invalidLoginDto);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("All fields must be filled.", response.getBody());
  }

  @Test
  public void testLoginUserWithNonExistentUser() {
    UserLoginDto validLoginDto = new UserLoginDto("username", "Valid1@password");

    when(userRepository.findByUsernameOrEmail(validLoginDto.usernameOrEmail(), validLoginDto.usernameOrEmail())).thenReturn(null);

    ResponseEntity<?> response = userService.loginUser(validLoginDto);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("User not found.", response.getBody());
  }

  @Test
  public void testLoginUserWithIncorrectPassword() {
    UserLoginDto validLoginDto = new UserLoginDto("username", "Valid1@password");
    User user = new User();
    user.setUsername("username");
    user.setPasswordHash(BCrypt.hashpw("DifferentPassword", BCrypt.gensalt()));

    when(userRepository.findByUsernameOrEmail(validLoginDto.usernameOrEmail(), validLoginDto.usernameOrEmail())).thenReturn(user);

    ResponseEntity<?> response = userService.loginUser(validLoginDto);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Incorrect password.", response.getBody());
  }

  @Test
  public void testGetAllUsers() {
    when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));

    ResponseEntity<?> response = userService.getAllUsers();
    assertEquals(HttpStatus.OK, response.getStatusCode());

    ObjectMapper objectMapper = new ObjectMapper();
    List<User> users = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
    });
    assertEquals(2, users.size());
  }
}