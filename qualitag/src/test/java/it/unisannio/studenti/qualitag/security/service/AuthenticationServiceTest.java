package it.unisannio.studenti.qualitag.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.ExpiredJwtException;
import it.unisannio.studenti.qualitag.dto.user.ForgotPasswordDto;
import it.unisannio.studenti.qualitag.dto.user.PasswordUpdateDto;
import it.unisannio.studenti.qualitag.dto.user.UserLoginDto;
import it.unisannio.studenti.qualitag.dto.user.UserRegistrationDto;
import it.unisannio.studenti.qualitag.dto.user.UserResponseDto;
import it.unisannio.studenti.qualitag.mapper.ProjectMapper;
import it.unisannio.studenti.qualitag.mapper.UserMapper;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import it.unisannio.studenti.qualitag.security.config.PasswordConfig;
import it.unisannio.studenti.qualitag.security.model.CustomUserDetails;
import it.unisannio.studenti.qualitag.service.GmailService;
import it.unisannio.studenti.qualitag.service.UserService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Test class for the AuthenticationService.
 */
public class AuthenticationServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private JwtService jwtTokenProvider;
  @Mock
  private AuthenticationManager authenticationManager;
  @Mock
  private UserService userService;
  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private AuthenticationService authenticationService;

  private User user;

  private UserRegistrationDto userRegistrationDto;
  private UserLoginDto userLoginDto;
  private ForgotPasswordDto forgotPasswordDto;
  private PasswordUpdateDto passwordUpdateDto;

  private PasswordConfig passwordConfig;
  private PasswordEncoder passwordEncoder;


  /**
   * Set up mock objects before each test.
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    passwordConfig = new PasswordConfig();
    passwordEncoder = passwordConfig.passwordEncoder();
    // We use spy so we can initialize the userMapper
    userMapper = spy(new UserMapper(passwordEncoder));

    // Manually inject the spy into authenticationService
    authenticationService = new AuthenticationService(userRepository, userMapper, jwtTokenProvider,
        userService, authenticationManager);

    String passwordHash = passwordEncoder.encode("pAssword12$");
    user = new User("username", "username@example.com", passwordHash, "Jane", "Doe");
    user.setUserId("6798e2740b80b85362a8ba90");
    LocalDateTime tokenExpiration = LocalDateTime.now().plusHours(24);
    user.setResetTokenExpiration(tokenExpiration);

    userRegistrationDto =
        new UserRegistrationDto("username", "username@example.com", "pAssword12$", "Jane", "Doe");
    userLoginDto = new UserLoginDto("username", "password1");
    forgotPasswordDto = new ForgotPasswordDto("username@example.com");
    passwordUpdateDto = new PasswordUpdateDto("passWord23?", "passWord23?");
  }

  /**
   * Tests a successful execution of the register method.
   */
  @Test
  public void testRegisterSuccess() throws Exception {
    // Arrange
    when(userRepository.existsByUsername(userRegistrationDto.username())).thenReturn(false);
    when(userRepository.existsByEmail(userRegistrationDto.email())).thenReturn(false);
    when(userRepository.save(user)).thenReturn(user);
    String jwt = "eyJjbGllbnRfaWQiOiJZekV6TUdkb01ISm5PSEJpT0cxaWJEaHlOVEE9IiwicmVzcG9uc2Vf"
        + "dHlwZSI6ImNvZGUiLCJzY29wZSI6ImludHJvc2NwZWN0X3Rva2VucywgcmV2b2tlX3Rva2Vu"
        + "cyIsImlzcyI6ImJqaElSak0xY1hwYWEyMXpkV3RJU25wNmVqbE1iazQ0YlRsTlpqazNkWEU9"
        + "Iiwic3ViIjoiWXpFek1HZG9NSEpuT0hCaU9HMWliRGh5TlRBPSIsImF1ZCI6Imh0dHBzOi8v"
        + "bG9jYWxob3N0Ojg0NDMve3RpZH0ve2FpZH0vb2F1dGgyL2F1dGhvcml6ZSIsImp0aSI6IjE1"
        + "MTYyMzkwMjIiLCJleHAiOiIyMDIxLTA1LTE3VDA3OjA5OjQ4LjAwMCswNTQ1In0";
    when(jwtTokenProvider.generateToken(new CustomUserDetails(user))).thenReturn(jwt);
    // Use doReturn() for spies
    doReturn(user).when(userMapper).toEntity(userRegistrationDto);

    try (MockedStatic<ProjectMapper> mockedMapper = mockStatic(ProjectMapper.class);
        MockedConstruction<GmailService> mockedGmail = mockConstruction(GmailService.class, (mock,
            context) -> doNothing().when(mock).sendMail(anyString(), anyString(), anyString()))) {

      // Act
      ResponseEntity<?> response = authenticationService.register(userRegistrationDto);

      // Assert
      assertNotNull(response);
      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("msg", "User registered successfully.");
      responseBody.put("token", jwt);
      responseBody.put("username", user.getUsername());
      assertEquals(responseBody, response.getBody());
    }
  }

  /**
   * Tests an execution of the register method where RegisterDTO is invalid.
   */
  @Test
  public void testRegisterInvalid() throws Exception {
    // Arrange
    userRegistrationDto = new UserRegistrationDto("", "", "", "", "");

    // Act
    ResponseEntity<?> response = authenticationService.register(userRegistrationDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "All fields must be filled.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the register method where the username is invalid.
   */
  @Test
  public void testRegisterInvalidUsername() throws Exception {
    // Arrange
    userRegistrationDto =
        new UserRegistrationDto("u", "username@example.com", "pAssword12$", "Jane", "Doe");

    // Act
    ResponseEntity<?> response = authenticationService.register(userRegistrationDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Invalid username.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the register method where the email is invalid.
   */
  @Test
  public void testRegisterInvalidEmail() throws Exception {
    // Arrange
    userRegistrationDto =
        new UserRegistrationDto("username", "username", "pAssword12$", "Jane", "Doe");

    // Act
    ResponseEntity<?> response = authenticationService.register(userRegistrationDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Invalid email address.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the register method where the password is invalid.
   */
  @Test
  public void testRegisterInvalidPassword() throws Exception {
    // Arrange
    userRegistrationDto =
        new UserRegistrationDto("username", "user@example.com", "password", "Jane", "Doe");

    // Act
    ResponseEntity<?> response = authenticationService.register(userRegistrationDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Invalid password.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the register method where the username is already taken.
   */
  @Test
  public void testRegisterUsernameTaken() throws Exception {
    // Arrange
    when(userRepository.existsByUsername(userRegistrationDto.username())).thenReturn(true);

    // Act
    ResponseEntity<?> response = authenticationService.register(userRegistrationDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Username already taken.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the register method where the email is already taken.
   */
  @Test
  public void testRegisterEmailTaken() throws Exception {
    // Arrange
    when(userRepository.existsByUsername(userRegistrationDto.username())).thenReturn(false);
    when(userRepository.existsByEmail(userRegistrationDto.email())).thenReturn(true);

    // Act
    ResponseEntity<?> response = authenticationService.register(userRegistrationDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Email already taken.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the login method.
   */
  @Test
  public void testLoginSuccess() {
    // Arrange
    when(userRepository.findByUsernameOrEmail(userLoginDto.usernameOrEmail(),
        userLoginDto.usernameOrEmail())).thenReturn(user);
    String jwt = "eyJjbGllbnRfaWQiOiJZekV6TUdkb01ISm5PSEJpT0cxaWJEaHlOVEE9IiwicmVzcG9uc2Vf"
        + "dHlwZSI6ImNvZGUiLCJzY29wZSI6ImludHJvc2NwZWN0X3Rva2VucywgcmV2b2tlX3Rva2Vu"
        + "cyIsImlzcyI6ImJqaElSak0xY1hwYWEyMXpkV3RJU25wNmVqbE1iazQ0YlRsTlpqazNkWEU9"
        + "Iiwic3ViIjoiWXpFek1HZG9NSEpuT0hCaU9HMWliRGh5TlRBPSIsImF1ZCI6Imh0dHBzOi8v"
        + "bG9jYWxob3N0Ojg0NDMve3RpZH0ve2FpZH0vb2F1dGgyL2F1dGhvcml6ZSIsImp0aSI6IjE1"
        + "MTYyMzkwMjIiLCJleHAiOiIyMDIxLTA1LTE3VDA3OjA5OjQ4LjAwMCswNTQ1In0";
    when(jwtTokenProvider.generateToken(new CustomUserDetails(user))).thenReturn(jwt);

    // Act
    ResponseEntity<?> response = authenticationService.login(userLoginDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "User logged in successfully.");
    responseBody.put("token", jwt);

    UserResponseDto userResponseDto = new UserResponseDto(user.getUsername(), user.getEmail(),
        user.getName(), user.getSurname(), user.getProjectIds(), user.getTeamIds(),
        user.getTagIds(), user.getProjectRolesAsString(), user.getUserId());
    responseBody.put("user", userResponseDto);
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Test an execution of the login method with an invalid dto.
   */
  @Test
  public void testLoginInvalid() {
    // Arrange
    userLoginDto = new UserLoginDto("", "");

    // Act
    ResponseEntity<?> response = authenticationService.login(userLoginDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "All fields must be filled.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Test an execution of the login method with an invalid username or email.
   */
  @Test
  public void testLoginInvalidUsernameOrEmail() {
    // Arrange
    userLoginDto = new UserLoginDto("username", "password");

    doThrow(new BadCredentialsException("Bad credentials")).when(authenticationManager)
        .authenticate(any(UsernamePasswordAuthenticationToken.class));

    // Act
    ResponseEntity<?> response = authenticationService.login(userLoginDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Invalid email or password.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the login method where the user is not found.
   */
  @Test
  public void testLoginUserNotFound() {
    // Arrange
    when(userRepository.findByUsernameOrEmail(userLoginDto.usernameOrEmail(),
        userLoginDto.usernameOrEmail())).thenReturn(null);

    // Act
    ResponseEntity<?> response = authenticationService.login(userLoginDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Invalid email or password.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the sendPasswordEmail method.
   */
  @Test
  public void testSendPasswordEmailSuccess() throws Exception {
    // Arrange
    when(userRepository.findByEmail(forgotPasswordDto.email())).thenReturn(user);
    String jwt = "eyJjbGllbnRfaWQiOiJZekV6TUdkb01ISm5PSEJpT0cxaWJEaHlOVEE9IiwicmVzcG9uc2Vf"
        + "dHlwZSI6ImNvZGUiLCJzY29wZSI6ImludHJvc2NwZWN0X3Rva2VucywgcmV2b2tlX3Rva2Vu"
        + "cyIsImlzcyI6ImJqaElSak0xY1hwYWEyMXpkV3RJU25wNmVqbE1iazQ0YlRsTlpqazNkWEU9"
        + "Iiwic3ViIjoiWXpFek1HZG9NSEpuT0hCaU9HMWliRGh5TlRBPSIsImF1ZCI6Imh0dHBzOi8v"
        + "bG9jYWxob3N0Ojg0NDMve3RpZH0ve2FpZH0vb2F1dGgyL2F1dGhvcml6ZSIsImp0aSI6IjE1"
        + "MTYyMzkwMjIiLCJleHAiOiIyMDIxLTA1LTE3VDA3OjA5OjQ4LjAwMCswNTQ1In0";
    when(jwtTokenProvider.generateResetToken(new CustomUserDetails(user))).thenReturn(jwt);
    when(userRepository.save(user)).thenReturn(user);
    try (MockedStatic<ProjectMapper> mockedMapper = mockStatic(ProjectMapper.class);
        MockedConstruction<GmailService> mockedGmail = mockConstruction(GmailService.class, (mock,
            context) -> doNothing().when(mock).sendMail(anyString(), anyString(), anyString()))) {

      // Act
      ResponseEntity<?> response = authenticationService.sendPasswordResetEmail(forgotPasswordDto);

      // Assert
      assertNotNull(response);
      assertEquals(HttpStatus.OK, response.getStatusCode());
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("msg", "Password reset email sent successfully.");
      assertEquals(responseBody, response.getBody());
    }
  }

  /**
   * Tests an execution of the sendPasswordEmail method where the user is not found with the email.
   */
  @Test
  public void testSendPasswordEmailUserNotFound() throws Exception {
    // Arrange
    when(userRepository.findByEmail(forgotPasswordDto.email())).thenReturn(null);

    // Act
    ResponseEntity<?> response = authenticationService.sendPasswordResetEmail(forgotPasswordDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "No user found with the given email.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the resetPassword method.
   */
  @Test
  public void testResetPasswordSuccess() {
    // Arrange
    String jwt = "eyJjbGllbnRfaWQiOiJZekV6TUdkb01ISm5PSEJpT0cxaWJEaHlOVEE9IiwicmVzcG9uc2Vf"
        + "dHlwZSI6ImNvZGUiLCJzY29wZSI6ImludHJvc2NwZWN0X3Rva2VucywgcmV2b2tlX3Rva2Vu"
        + "cyIsImlzcyI6ImJqaElSak0xY1hwYWEyMXpkV3RJU25wNmVqbE1iazQ0YlRsTlpqazNkWEU9"
        + "Iiwic3ViIjoiWXpFek1HZG9NSEpuT0hCaU9HMWliRGh5TlRBPSIsImF1ZCI6Imh0dHBzOi8v"
        + "bG9jYWxob3N0Ojg0NDMve3RpZH0ve2FpZH0vb2F1dGgyL2F1dGhvcml6ZSIsImp0aSI6IjE1"
        + "MTYyMzkwMjIiLCJleHAiOiIyMDIxLTA1LTE3VDA3OjA5OjQ4LjAwMCswNTQ1In0";

    when(jwtTokenProvider.extractUserName(jwt)).thenReturn(user.getUsername());
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
    when(jwtTokenProvider.isTokenValid(jwt, new CustomUserDetails(user))).thenReturn(true);
    when(userRepository.save(user)).thenReturn(user);

    // Act
    ResponseEntity<?> response = authenticationService.resetPassword(jwt, passwordUpdateDto);

    System.out.println(response.getBody());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Password reset successfully.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the resetPassword method where the password is invalid.
   */
  @Test
  public void testResetPasswordInvalidPassword() {
    // Arrange
    PasswordUpdateDto invalidPasswordUpdateDto = new PasswordUpdateDto("password", "password");
    when(userService.isValidPasswordUpdateDto(invalidPasswordUpdateDto))
        .thenReturn("Invalid password.");

    // Act
    ResponseEntity<?> response =
        authenticationService.resetPassword("jwt", invalidPasswordUpdateDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Invalid password.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the resetPassword method where the token is expired.
   */
  @Test
  public void testResetPasswordTokenExpired() {
    // Arrange
    doThrow(new ExpiredJwtException(null, null, "Invalid token.")).when(jwtTokenProvider)
        .extractUserName("jwt");

    // Act
    ResponseEntity<?> response = authenticationService.resetPassword("jwt", passwordUpdateDto);

    System.out.println(response.getBody());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Invalid or expired reset token.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the resetPassword method where the user is not found.
   */
  @Test
  public void testResetPasswordUserNotFound() {
    // Arrange
    String jwt = "eyJjbGllbnRfaWQiOiJZekV6TUdkb01ISm5PSEJpT0cxaWJEaHlOVEE9IiwicmVzcG9uc2Vf"
        + "dHlwZSI6ImNvZGUiLCJzY29wZSI6ImludHJvc2NwZWN0X3Rva2VucywgcmV2b2tlX3Rva2Vu"
        + "cyIsImlzcyI6ImJqaElSak0xY1hwYWEyMXpkV3RJU25wNmVqbE1iazQ0YlRsTlpqazNkWEU9"
        + "Iiwic3ViIjoiWXpFek1HZG9NSEpuT0hCaU9HMWliRGh5TlRBPSIsImF1ZCI6Imh0dHBzOi8v"
        + "bG9jYWxob3N0Ojg0NDMve3RpZH0ve2FpZH0vb2F1dGgyL2F1dGhvcml6ZSIsImp0aSI6IjE1"
        + "MTYyMzkwMjIiLCJleHAiOiIyMDIxLTA1LTE3VDA3OjA5OjQ4LjAwMCswNTQ1In0";
    when(jwtTokenProvider.extractUserName(jwt)).thenReturn("username");
    when(userRepository.findByUsername("username")).thenReturn(null);

    // Act
    ResponseEntity<?> response = authenticationService.resetPassword(jwt, passwordUpdateDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "No user found with the given username.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the resetPassword method where the token is invalid (userTokenExpiration
   * is null).
   */
  @Test
  public void testResetPasswordInvalidToken() {
    // Arrange
    String jwt = "eyJjbGllbnRfaWQiOiJZekV6TUdkb01ISm5PSEJpT0cxaWJEaHlOVEE9IiwicmVzcG9uc2Vf"
        + "dHlwZSI6ImNvZGUiLCJzY29wZSI6ImludHJvc2NwZWN0X3Rva2VucywgcmV2b2tlX3Rva2Vu"
        + "cyIsImlzcyI6ImJqaElSak0xY1hwYWEyMXpkV3RJU25wNmVqbE1iazQ0YlRsTlpqazNkWEU9"
        + "Iiwic3ViIjoiWXpFek1HZG9NSEpuT0hCaU9HMWliRGh5TlRBPSIsImF1ZCI6Imh0dHBzOi8v"
        + "bG9jYWxob3N0Ojg0NDMve3RpZH0ve2FpZH0vb2F1dGgyL2F1dGhvcml6ZSIsImp0aSI6IjE1"
        + "MTYyMzkwMjIiLCJleHAiOiIyMDIxLTA1LTE3VDA3OjA5OjQ4LjAwMCswNTQ1In0";
    user.setResetTokenExpiration(null);
    when(jwtTokenProvider.extractUserName(jwt)).thenReturn(user.getUsername());
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

    // Act
    ResponseEntity<?> response = authenticationService.resetPassword(jwt, passwordUpdateDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Invalid or expired reset token.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the resetPassword method where the token is invalid (token is expired).
   */
  @Test
  public void testResetPasswordInvalidTokenExpired() {
    // Arrange
    String jwt = "eyJjbGllbnRfaWQiOiJZekV6TUdkb01ISm5PSEJpT0cxaWJEaHlOVEE9IiwicmVzcG9uc2Vf"
        + "dHlwZSI6ImNvZGUiLCJzY29wZSI6ImludHJvc2NwZWN0X3Rva2VucywgcmV2b2tlX3Rva2Vu"
        + "cyIsImlzcyI6ImJqaElSak0xY1hwYWEyMXpkV3RJU25wNmVqbE1iazQ0YlRsTlpqazNkWEU9"
        + "Iiwic3ViIjoiWXpFek1HZG9NSEpuT0hCaU9HMWliRGh5TlRBPSIsImF1ZCI6Imh0dHBzOi8v"
        + "bG9jYWxob3N0Ojg0NDMve3RpZH0ve2FpZH0vb2F1dGgyL2F1dGhvcml6ZSIsImp0aSI6IjE1"
        + "MTYyMzkwMjIiLCJleHAiOiIyMDIxLTA1LTE3VDA3OjA5OjQ4LjAwMCswNTQ1In0";
    user.setResetTokenExpiration(LocalDateTime.now().minusHours(1));
    when(jwtTokenProvider.extractUserName(jwt)).thenReturn(user.getUsername());
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

    // Act
    ResponseEntity<?> response = authenticationService.resetPassword(jwt, passwordUpdateDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Invalid or expired reset token.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the resetPassword method where the token is invalid (token is invalid).
   */
  @Test
  public void testResetPasswordInvalidTokenInvalid() {
    // Arrange
    String jwt = "eyJjbGllbnRfaWQiOiJZekV6TUdkb01ISm5PSEJpT0cxaWJEaHlOVEE9IiwicmVzcG9uc2Vf"
        + "dHlwZSI6ImNvZGUiLCJzY29wZSI6ImludHJvc2NwZWN0X3Rva2VucywgcmV2b2tlX3Rva2Vu"
        + "cyIsImlzcyI6ImJqaElSak0xY1hwYWEyMXpkV3RJU25wNmVqbE1iazQ0YlRsTlpqazNkWEU9"
        + "Iiwic3ViIjoiWXpFek1HZG9NSEpuT0hCaU9HMWliRGh5TlRBPSIsImF1ZCI6Imh0dHBzOi8v"
        + "bG9jYWxob3N0Ojg0NDMve3RpZH0ve2FpZH0vb2F1dGgyL2F1dGhvcml6ZSIsImp0aSI6IjE1"
        + "MTYyMzkwMjIiLCJleHAiOiIyMDIxLTA1LTE3VDA3OjA5OjQ4LjAwMCswNTQ1In0";
    when(jwtTokenProvider.extractUserName(jwt)).thenReturn(user.getUsername());
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
    when(jwtTokenProvider.isTokenValid(jwt, new CustomUserDetails(user))).thenReturn(false);

    // Act
    ResponseEntity<?> response = authenticationService.resetPassword(jwt, passwordUpdateDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Invalid or expired reset token.");
    assertEquals(responseBody, response.getBody());
  }

}
