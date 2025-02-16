package it.unisannio.studenti.qualitag.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.ExpiredJwtException;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import it.unisannio.studenti.qualitag.security.model.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for JwtService.
 */
public class JwtServiceTest {

  private User user;
  private User otherUser;

  private CustomUserDetails customUserDetails;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private JwtService jwtService;

  /**
   * Setup mock objects and initialize the JwtService.
   */
  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    user = new User("testuser", "email@example.com", "hashedPassword", "John", "Doe");
    user.setUserId("userId");

    otherUser = new User("otheruser", "email2@example.com", "hashedPassword", "Johnny", "Doe");
    otherUser.setUserId("userId2");

    customUserDetails = new CustomUserDetails(user);

    when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
        .thenReturn(user);

    // Set the secret key and expiration time using reflection. This is important!
    String jwtSecretKey = "fa7734903ec54999b4c84ad450466280a8ed12b03994409318eb2bd142f50ec0";

    ReflectionTestUtils.setField(jwtService, "jwtSecretKey", jwtSecretKey);
    ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 3600000L); // 1 hour
    ReflectionTestUtils.setField(jwtService, "jwtResetPwMin", 15); // 15 minutes
  }

  @Test
  public void testGenerateToken() {
    String token = jwtService.generateToken(customUserDetails);
    assertNotNull(token);
    assertTrue(token.length() > 0);
    assertTrue(token.contains("."));
    assertTrue(token.split("\\.").length == 3);
  }

  @Test
  public void testExtractUserName() {
    String token = jwtService.generateToken(customUserDetails); // Generate a valid token    
    assertEquals("testuser", jwtService.extractUserName(token));
  }

  @Test
  public void testExtractUserName_expiredToken() {
    // Set to expired
    ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", -1L);

    // Generate a valid token
    String token = jwtService.generateToken(customUserDetails);

    assertThrows(ExpiredJwtException.class, () -> jwtService.extractUserName(token));
  }

  @Test
  public void testIsTokenValid_validToken() {
    String token = jwtService.generateToken(customUserDetails);
    assertTrue(jwtService.isTokenValid(token, customUserDetails));
  }

  @Test
  public void testIsTokenValid_invalidUser() {
    CustomUserDetails otherUserDetails = new CustomUserDetails(otherUser);

    String token = jwtService.generateToken(customUserDetails);
    assertFalse(jwtService.isTokenValid(token, otherUserDetails));
  }

  @Test
  public void testIsTokenValid_expiredToken() {
    // Set to expired
    ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", -1L);

    // Generate a valid token
    String token = jwtService.generateToken(customUserDetails);

    assertThrows(ExpiredJwtException.class, () -> jwtService.extractUserName(token));
  }

  @Test
  public void testGenerateResetToken() {
    String token = jwtService.generateResetToken(customUserDetails);
    assertNotNull(token);
    assertTrue(token.length() > 0);
    assertTrue(token.contains("."));
    assertTrue(token.split("\\.").length == 3);
  }
}
