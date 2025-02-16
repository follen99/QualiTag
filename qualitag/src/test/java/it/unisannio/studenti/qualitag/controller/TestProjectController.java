package it.unisannio.studenti.qualitag.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.unisannio.studenti.qualitag.dto.project.ProjectCreateDto;
import it.unisannio.studenti.qualitag.service.ProjectService;
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
 * Test class for ProjectController.
 */
public class TestProjectController {

  private MockMvc mockMvc;

  @Mock
  private ProjectService projectService;

  @InjectMocks
  private ProjectController projectController;

  /**
   * Set up the test environment.
   */
  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
  }

  /**
   * Test the creation of a project.
   * @throws Exception
   */
  @Test
  public void testCreateProject() throws Exception {
    ProjectCreateDto projectCreateDto = new ProjectCreateDto(
        "projectName",
        "projectDescription",
        "2023-12-31",
        List.of("user1@example.com", "user2@example.com")
    );
    when(projectService.createProject(projectCreateDto)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(post("/api/v1/project")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                "{\"projectName\":\"projectName\",\"projectDescription\":"
                + "\"projectDescription\",\"deadlineDate\":\"2023-12-31\","
                + "\"userEmails\":[\"user1@example.com\",\"user2@example.com\"]}"))
        .andExpect(status().isOk());
    verify(projectService, times(1)).createProject(projectCreateDto);
    verifyNoMoreInteractions(projectService);
  }

  /**
   * Test the closure of a project.
   * @throws Exception
   */
  @Test
  public void testCloseProject() throws Exception {
    String projectId = "projectId";
    when(projectService.closeProject(projectId)).thenReturn(ResponseEntity.ok().build());

    mockMvc.perform(post("/api/v1/project/" + projectId + "/close")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(projectService, times(1)).closeProject(projectId);
    verifyNoMoreInteractions(projectService);
  }

  /**
   * Test the retrieval of projects by their IDs.
   * @throws Exception
   */
  @Test
  public void testGetProjectsByIds() throws Exception {
    List<String> projectIds = List.of("projectId1", "projectId2");
    when(projectService.getProjectsByIds(projectIds)).thenReturn(ResponseEntity.ok().build());

    mockMvc.perform(post("/api/v1/project/get-by-ids")
            .contentType(MediaType.APPLICATION_JSON)
            .content("[\"projectId1\",\"projectId2\"]"))
        .andExpect(status().isOk());

    verify(projectService, times(1)).getProjectsByIds(projectIds);
    verifyNoMoreInteractions(projectService);
  }

  /**
   * Tests the case when a projectId passed to getProjectsByIds is null.
   */
  @Test
  public void testGetProjectsByIdsNull() throws Exception {
    mockMvc.perform(post("/api/v1/project/get-by-ids")
            .contentType(MediaType.APPLICATION_JSON)
            .content("null"))
        .andExpect(status().isBadRequest());
  }

  /**
   * Tests the case when a projectId passed to getProjectsByIds is empty.
   */
  @Test
  public void testGetProjectsByIdsEmpty() throws Exception {
    mockMvc.perform(post("/api/v1/project/get-by-ids")
            .contentType(MediaType.APPLICATION_JSON)
            .content("[]"))
        .andExpect(status().isBadRequest());
  }

  /**
   * Test the retrieval of the human-readable status of a project.
   * @throws Exception
   */
  @Test
  public void testGetHumanReadableProjectStatus() throws Exception {
    String projectId = "projectId";
    when(projectService.getHumanReadableProjectStatus(projectId)).thenReturn(
        ResponseEntity.ok().build());

    mockMvc.perform(get("/api/v1/project/" + projectId + "/status/whole")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(projectService, times(1)).getHumanReadableProjectStatus(projectId);
    verifyNoMoreInteractions(projectService);
  }

  /**
   * Test the retrieval of a project by user ID.
   * @throws Exception
   */
  @Test
  public void testGetProjectByProjectId() throws Exception {
    String projectId = "projectId";
    when(projectService.getProjectById(projectId)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/project/projectId"))
        .andExpect(status().isOk());
    verify(projectService, times(1)).getProjectById(projectId);
    verifyNoMoreInteractions(projectService);
  }

  /**
   * Test the retrieval of tags of a project.
   * @throws Exception
   */
  @Test
  public void testGetProjectTags() throws Exception {
    String projectId = "projectId";
    when(projectService.getProjectsTags(projectId)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/project/projectId/tags"))
        .andExpect(status().isOk());
    verify(projectService, times(1)).getProjectsTags(projectId);
    verifyNoMoreInteractions(projectService);
  }

  /**
   * Tests the retrieval of artifacts of a project.
   * @throws Exception
   */
  @Test
  public void testGetProjectArtifacts() throws Exception {
    String projectId = "projectId";
    when(projectService.getProjectsArtifacts(projectId)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/project/projectId/artifacts"))
        .andExpect(status().isOk());
    verify(projectService, times(1)).getProjectsArtifacts(projectId);
    verifyNoMoreInteractions(projectService);
  }

  /**
   * Test the deletion of a project.
   * @throws Exception
   */
  @Test
  public void testDeleteProject() throws Exception {
    String projectId = "projectId";
    when(projectService.deleteProject(projectId)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(delete("/api/v1/project/projectId"))
        .andExpect(status().isOk());
    verify(projectService, times(1)).deleteProject(projectId);
    verifyNoMoreInteractions(projectService);
  }

  /**
   * Tests the update of a project.
   */
  @Test
  public void testUpdateProject() throws Exception {
    ProjectCreateDto projectCreateDto = new ProjectCreateDto(
        "projectNewName",
        "projectDescription",
        "2023-12-31",
        List.of("user1@example.com", "user2@example.com")
    );
    when(projectService.updateProject(projectCreateDto, "projectId")).thenReturn(
        ResponseEntity.ok().build());
    mockMvc.perform(put("/api/v1/project/projectId")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                "{\"projectName\":\"projectNewName\",\"projectDescription\":"
                    + "\"projectDescription\",\"deadlineDate\":\"2023-12-31\","
                    + "\"userEmails\":[\"user1@example.com\",\"user2@example.com\"]}"))
        .andExpect(status().isOk());
    verify(projectService, times(1)).updateProject(projectCreateDto, "projectId");
    verifyNoMoreInteractions(projectService);
  }

  /**
   * Tests the retrieval of the project Teams.
   */
  @Test
  public void testGetProjectsTeams() throws Exception {
    String projectId = "projectId";
    when(projectService.getProjectsTeams(projectId)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/project/projectId/teams"))
        .andExpect(status().isOk());
    verify(projectService, times(1)).getProjectsTeams(projectId);
    verifyNoMoreInteractions(projectService);
  }

}
