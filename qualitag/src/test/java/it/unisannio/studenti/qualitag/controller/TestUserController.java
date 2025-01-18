package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.dto.user.PasswordUpdateDto;
import it.unisannio.studenti.qualitag.dto.user.UserModifyDto;
import it.unisannio.studenti.qualitag.service.UserService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TestUserController {

  private MockMvc mockMvc;

  @Mock
  private UserService userService;

  @InjectMocks
  private UserController userController;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
  }

  @Test
  public void testGetUser() throws Exception {
    when(userService.getUser("username")).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/user/username"))
        .andExpect(status().isOk());
    verify(userService, times(1)).getUser("username");
    verifyNoMoreInteractions(userService);
  }

  @Test
  public void testGetUserTags() throws Exception {
    when(userService.getUserTags("username")).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/user/username/tags"))
        .andExpect(status().isOk());
    verify(userService, times(1)).getUserTags("username");
    verifyNoMoreInteractions(userService);
  }

  @Test
  public void testUpdateUser() throws Exception {
    UserModifyDto userModifyDto = new UserModifyDto(
        "username",
        "email",
        "name",
        "surname");

    when(userService.updateUser(userModifyDto, "username"))
        .thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(put("/api/v1/user/username")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"username\",\"email\":\"email\","
                + "\"name\":\"name\",\"surname\":\"surname\"}"))
        .andExpect(status().isOk());
    verify(userService, times(1)).updateUser(userModifyDto, "username");
    verifyNoMoreInteractions(userService);
  }

  @Test
  public void testUpdateUserPassword() throws Exception {
    PasswordUpdateDto passwordUpdateDto = new PasswordUpdateDto(
        "newPassword",
        "newPassword");

    when(userService.updatePassword(passwordUpdateDto, "username"))
        .thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(put("/api/v1/user/username/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"newPassword\":\"newPassword\",\"confirmPassword\":\"newPassword\"}"))
        .andExpect(status().isOk());
    verify(userService, times(1)).updatePassword(passwordUpdateDto, "username");
    verifyNoMoreInteractions(userService);
  }

  @Test
  public void testDeleteUser() throws Exception {
    when(userService.deleteUser("username")).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(delete("/api/v1/user/username"))
        .andExpect(status().isOk());
    verify(userService, times(1)).deleteUser("username");
    verifyNoMoreInteractions(userService);
  }


}
