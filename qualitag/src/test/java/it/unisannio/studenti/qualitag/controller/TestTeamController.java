package it.unisannio.studenti.qualitag.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unisannio.studenti.qualitag.dto.team.TeamCreateDto;
import it.unisannio.studenti.qualitag.service.TeamService;
import java.util.List;
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
 * Test class for TeamController.
 */
public class TestTeamController {

  private MockMvc mockMvc;

  @Mock
  private TeamService teamService;

  @InjectMocks
  private TeamController teamController;

  /**
   * Set up the test environment.
   */
  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(teamController).build();
  }

  /**
   * Test the creation of a team.
   * @throws Exception
   */
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

  /**
   * Test the retrieval of teams by project ID.
   * @throws Exception
   */
  @Test
  public void testGetTeamsByProjectId() throws Exception {
    String projectId = "projectId";
    when(teamService.getTeamsByProject(projectId)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/team/get/byproject/{projectId}", projectId))
        .andExpect(status().isOk());
    verify(teamService, times(1)).getTeamsByProject(projectId);
    verifyNoMoreInteractions(teamService);
  }

  /**
   * Test the retrieval of teams by user ID.
   * @throws Exception
   */
  @Test
  public void testGetTeamsByUserId() throws Exception {
    String userId = "userId";
    when(teamService.getTeamsByUser(userId)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/team/get/byuser/{userId}", userId))
        .andExpect(status().isOk());
    verify(teamService, times(1)).getTeamsByUser(userId);
    verifyNoMoreInteractions(teamService);
  }

  /**
   * Test the retrieval of team IRR.
   * @throws Exception
   */
  @Test
  public void testGetTeamIrr() throws Exception {
    String teamId = "teamId";
    when(teamService.getTeamIrr(teamId)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/team/{teamId}/irr", teamId))
        .andExpect(status().isOk());
    verify(teamService, times(1)).getTeamIrr(teamId);
    verifyNoMoreInteractions(teamService);
  }

  /**
   * Test the deletion of a team.
   * @throws Exception
   */
  @Test
  public void testDeleteTeam() throws Exception {
    String teamId = "teamId";
    when(teamService.deleteTeam(teamId)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(delete("/api/v1/team/{teamId}", teamId))
        .andExpect(status().isOk());
    verify(teamService, times(1)).deleteTeam(teamId);
    verifyNoMoreInteractions(teamService);
  }

  /**
   * Tests the update of a team.
   */
  @Test
  public void testUpdateTeamUsers() throws Exception {
    List<String> userEmails = List.of("user1@example.com", "user3@example.com");
    when(teamService.updateTeamUsers("teamId", userEmails)).
        thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(put("/api/v1/team/teamId/updateusers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(userEmails)))
        .andExpect(status().isOk());
    verify(teamService, times(1)).updateTeamUsers("teamId", userEmails);
    verifyNoMoreInteractions(teamService);
  }

}
