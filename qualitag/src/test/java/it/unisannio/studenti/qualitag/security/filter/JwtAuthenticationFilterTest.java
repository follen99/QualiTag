package it.unisannio.studenti.qualitag.security.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import it.unisannio.studenti.qualitag.security.model.CustomUserDetails;
import it.unisannio.studenti.qualitag.security.service.CustomUserDetailService;
import it.unisannio.studenti.qualitag.security.service.JwtService;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Test class for JwtAuthenticationFilter.
 */
public class JwtAuthenticationFilterTest {

  private String jwt; // Store the generated JWT
  private User user;
  private CustomUserDetails customUserDetails;
  private CustomUserDetailService customUserDetailService;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private JwtService jwtService;

  private JwtAuthenticationFilter jwtAuthenticationFilter;

  /**
   * Set up the test environment.
   */
  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    customUserDetailService = new CustomUserDetailService(userRepository);
    jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, customUserDetailService);

    user = new User("testuser", "email@example.com", "hashedPassword", "John", "Doe");
    user.setUserId("userId");

    customUserDetails = new CustomUserDetails(user);

    when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
        .thenReturn(user);

    // Set the secret key and expiration time using reflection. This is important!
    String jwtSecretKey = "fa7734903ec54999b4c84ad450466280a8ed12b03994409318eb2bd142f50ec0";

    ReflectionTestUtils.setField(jwtService, "jwtSecretKey", jwtSecretKey);
    ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 3600000L); // 1 hour
    ReflectionTestUtils.setField(jwtService, "jwtResetPwMin", 15); // 15 minutes

    jwt = jwtService.generateToken(customUserDetails);
    System.out.println("\n\nGenerated JWT: " + jwt + "\n\n");
  }

  /**
   * Clear the security context after each test.
   */
  @AfterEach
  public void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void testSuccessfulAuthentication() throws ServletException, IOException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + jwt);

    MockHttpServletResponse response = new MockHttpServletResponse();

    MockFilterChain filterChain = new MockFilterChain();
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getName());
  }

  @Test
  public void testNoAuthHeader() throws ServletException, IOException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    MockFilterChain filterChain = new MockFilterChain();
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  public void testAuthHeaderDoesNotStartWithBearer() throws ServletException, IOException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.addHeader("Authorization", "SomethingElse");


    MockFilterChain filterChain = new MockFilterChain();
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  public void testExpiredJwt() throws ServletException, IOException {
    ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", -1L);
    String expiredJwt = jwtService.generateToken(customUserDetails);

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.addHeader("Authorization", "Bearer " + expiredJwt);


    MockFilterChain filterChain = new MockFilterChain();
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  public void testEmptyUserDetails() throws ServletException, IOException {
    User nullUser = new User();
    CustomUserDetails nullUserDetails = new CustomUserDetails(nullUser);
    System.out.println(nullUserDetails);

    String nullJwt = jwtService.generateToken(nullUserDetails);
    System.out.println("\n\nGenerated JWT: " + nullJwt + "\n\n");

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.addHeader("Authorization", "Bearer " + nullJwt);


    MockFilterChain filterChain = new MockFilterChain();
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }
}
