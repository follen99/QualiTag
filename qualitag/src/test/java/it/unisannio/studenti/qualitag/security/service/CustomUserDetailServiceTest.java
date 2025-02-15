package it.unisannio.studenti.qualitag.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import it.unisannio.studenti.qualitag.security.model.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Unit tests for CustomUserDetailService.
 */
public class CustomUserDetailServiceTest {

  private User user;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private CustomUserDetailService userDetailsService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    userDetailsService = new CustomUserDetailService(userRepository);

    user = new User("testuser", "email@example.com", "hashedPassword", "John", "Doe");
    user.setUserId("userId");

    when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
        .thenReturn(user);
    when(userRepository.findByUsernameOrEmail("email@example.com", "email@example.com"))
        .thenReturn(user);
  }

  @Test
  void loadUserByUsernameUserFoundByUsername() {
    // Arrange
    String identifier = "testuser";

    // Act
    UserDetails userDetails = userDetailsService.loadUserByUsername(identifier);

    // Assert
    assertNotNull(userDetails);
    assertTrue(userDetails instanceof CustomUserDetails);
    assertEquals(userDetails, new CustomUserDetails(user));
    assertEquals("testuser", userDetails.getUsername());
    assertEquals("hashedPassword", userDetails.getPassword());
    assertEquals(userDetails.getAuthorities(), user.getAuthorities());
    assertTrue(userDetails.isAccountNonExpired());
    assertTrue(userDetails.isAccountNonLocked());
    assertTrue(userDetails.isCredentialsNonExpired());
    assertTrue(userDetails.isEnabled());
  }

  @Test
  void loadUserByUsernameUserFoundByEmail() {
    // Arrange
    String identifier = "email@example.com";

    // Act
    UserDetails userDetails = userDetailsService.loadUserByUsername(identifier);

    // Assert
    assertNotNull(userDetails);
    assertTrue(userDetails instanceof CustomUserDetails);
    assertEquals(userDetails, new CustomUserDetails(user));
    assertEquals("testuser", userDetails.getUsername());
    assertEquals("hashedPassword", userDetails.getPassword());
    assertEquals(userDetails.getAuthorities(), user.getAuthorities());
    assertTrue(userDetails.isAccountNonExpired());
    assertTrue(userDetails.isAccountNonLocked());
    assertTrue(userDetails.isCredentialsNonExpired());
    assertTrue(userDetails.isEnabled());
  }

  @Test
  void loadUserByUsernameUserNotFound() {
    // Arrange
    String identifier = "nothing";

    // Act & Assert
    assertThrows(UsernameNotFoundException.class,
        () -> userDetailsService.loadUserByUsername(identifier));
  }

  @Test
  void loadUserByUsernameUserNotFoundNull() {
    // Arrange
    String identifier = null;

    // Act & Assert
    assertThrows(UsernameNotFoundException.class,
        () -> userDetailsService.loadUserByUsername(identifier));
  }
}
