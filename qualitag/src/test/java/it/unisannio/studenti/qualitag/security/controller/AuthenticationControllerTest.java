package it.unisannio.studenti.qualitag.security.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.unisannio.studenti.qualitag.dto.user.ForgotPasswordDto;
import it.unisannio.studenti.qualitag.dto.user.PasswordUpdateDto;
import it.unisannio.studenti.qualitag.dto.user.UserLoginDto;
import it.unisannio.studenti.qualitag.dto.user.UserRegistrationDto;
import it.unisannio.studenti.qualitag.security.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Test class for AuthenticationController.
 */
public class AuthenticationControllerTest {

  private MockMvc mockMvc;

  @Mock
  private AuthenticationService authenticationService;

  @InjectMocks
  private AuthenticationController authenticationController;

  /**
   * Set up the test environment.
   */
  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
  }

  @Test
  public void testRegisterUser() throws Exception {
    UserRegistrationDto userRegistrationDto = new UserRegistrationDto(
        "username", 
        "email@example.com", 
        "Password@1", 
        "John", 
        "Doe"
    );

    when(authenticationService.register(userRegistrationDto))
        .thenReturn(ResponseEntity.ok().build());
    
    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                "{" 
                + "\"username\":\"" + userRegistrationDto.username() + "\"," 
                + "\"email\":\""  + userRegistrationDto.email() + "\","
                + "\"password\":\"" + userRegistrationDto.password() + "\","
                + "\"name\":\"" + userRegistrationDto.name() + "\","
                + "\"surname\":\"" + userRegistrationDto.surname() + "\""
                + "}"
            ))
        .andExpect(status().isOk());
    
    verify(authenticationService, times(1)).register(userRegistrationDto);
    verifyNoMoreInteractions(authenticationService);
  }

  @Test
  public void testLoginUser() throws Exception {
    UserLoginDto userLoginDto = new UserLoginDto(
        "username",
        "Password@1"
    );

    when(authenticationService.login(userLoginDto))
        .thenReturn(ResponseEntity.ok().build());
    
    mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                "{" 
                + "\"usernameOrEmail\":\"" + userLoginDto.usernameOrEmail() + "\"," 
                + "\"password\":\"" + userLoginDto.password() + "\""
                + "}"
            ))
        .andExpect(status().isOk());

    verify(authenticationService, times(1)).login(userLoginDto);
    verifyNoMoreInteractions(authenticationService);
  }

  @Test
  public void testForgotPassword() throws Exception {
    ForgotPasswordDto forgotPasswordDto = new ForgotPasswordDto("email@example.com");

    when(authenticationService.sendPasswordResetEmail(forgotPasswordDto))
        .thenReturn(ResponseEntity.ok().build());

    mockMvc.perform(post("/api/v1/auth/forgot-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"" + forgotPasswordDto.email() + "\"}"))
        .andExpect(status().isOk());
    
    verify(authenticationService, times(1)).sendPasswordResetEmail(forgotPasswordDto);
    verifyNoMoreInteractions(authenticationService);
  }

  @Test
  public void testResetPassword() throws Exception {
    String token = "token";
    PasswordUpdateDto passwordUpdateDto = new PasswordUpdateDto("Password@1", "Password@1");

    when(authenticationService.resetPassword(token, passwordUpdateDto))
        .thenReturn(ResponseEntity.ok().build());
    
    mockMvc.perform(post("/api/v1/auth/reset-password?token=" + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                "{" 
                + "\"newPassword\":\"" + passwordUpdateDto.newPassword() + "\"," 
                + "\"confirmPassword\":\"" + passwordUpdateDto.confirmPassword() + "\""
                + "}"
            ))
        .andExpect(status().isOk());
    
    verify(authenticationService, times(1)).resetPassword(token, passwordUpdateDto);
    verifyNoMoreInteractions(authenticationService);
  }

  @Test
  public void testCheckToken() throws Exception {
    mockMvc.perform(get("/api/v1/auth/check-token"))
        .andExpect(status().isOk());
  }
}
