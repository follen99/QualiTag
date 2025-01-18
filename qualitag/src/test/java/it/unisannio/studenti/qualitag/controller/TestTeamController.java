package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.dto.team.TeamCreateDto;
import it.unisannio.studenti.qualitag.service.TeamService;
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

public class TestTeamController {

  private MockMvc mockMvc;

  @Mock
  private TeamService teamService;

  @InjectMocks
  private TeamController teamController;


  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(teamController).build();
  }

  @Test
  public void testAddTeam() throws Exception {
    TeamCreateDto teamCreateDto = new TeamCreateDto(
        "teamName",
        "teamDescription",
        "projectId",
        List.of("user1@example.com", "user2@example.com"));

    when(teamService.addTeam(teamCreateDto)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(post("/api/v1/team")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"teamName\":\"teamName\",\"teamDescription\":\"teamDescription\","
                + "\"projectId\":\"projectId\","
                + "\"userEmails\":[\"user1@example.com\",\"user2@example.com\"]}"))
        .andExpect(status().isOk());
    verify(teamService, times(1)).addTeam(any());
    verifyNoMoreInteractions(teamService);
  }

  @Test
  public void testGetTeamsByProjectId() throws Exception {
    String projectId = "projectId";
    when(teamService.getTeamsByProject(projectId)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/team/get/byproject/{projectId}", projectId))
        .andExpect(status().isOk());
    verify(teamService, times(1)).getTeamsByProject(projectId);
    verifyNoMoreInteractions(teamService);
  }

  @Test
  public void testGetTeamsByUserId() throws Exception {
    String userId = "userId";
    when(teamService.getTeamsByUser(userId)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/team/get/byuser/{userId}", userId))
        .andExpect(status().isOk());
    verify(teamService, times(1)).getTeamsByUser(userId);
    verifyNoMoreInteractions(teamService);
  }

  @Test
  public void testGetTeamIrr() throws Exception {
    String teamId = "teamId";
    when(teamService.getTeamIrr(teamId)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/team/{teamId}/irr", teamId))
        .andExpect(status().isOk());
    verify(teamService, times(1)).getTeamIrr(teamId);
    verifyNoMoreInteractions(teamService);
  }

  @Test
  public void testDeleteTeam() throws Exception {
    String teamId = "teamId";
    when(teamService.deleteTeam(teamId)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(delete("/api/v1/team/{teamId}", teamId))
        .andExpect(status().isOk());
    verify(teamService, times(1)).deleteTeam(teamId);
    verifyNoMoreInteractions(teamService);
  }

}
