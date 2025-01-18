package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.dto.project.ProjectCreateDto;
import it.unisannio.studenti.qualitag.service.ProjectService;
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

public class TestProjectController {

  private MockMvc mockMvc;

  @Mock
  private ProjectService projectService;

  @InjectMocks
  private ProjectController projectController;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
  }

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
                "{\"projectName\":\"projectName\",\"projectDescription\":\"projectDescription\",\"deadlineDate\":\"2023-12-31\",\"userEmails\":[\"user1@example.com\",\"user2@example.com\"]}"))
        .andExpect(status().isOk());
    verify(projectService, times(1)).createProject(projectCreateDto);
    verifyNoMoreInteractions(projectService);
  }

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

  @Test
  public void testGetProjectByProjectId() throws Exception {
    String projectId = "projectId";
    when(projectService.getProjectById(projectId)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/project/projectId"))
        .andExpect(status().isOk());
    verify(projectService, times(1)).getProjectById(projectId);
    verifyNoMoreInteractions(projectService);
  }

  //TODO: Create a project with tags and test this method
  @Test
  public void testGetProjectTags() throws Exception {
    String projectId = "projectId";
    when(projectService.getProjectsTags(projectId)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/project/projectId/tags"))
        .andExpect(status().isOk());
    verify(projectService, times(1)).getProjectsTags(projectId);
    verifyNoMoreInteractions(projectService);
  }

  //TODO: Create a project with artifacts and test this method
  @Test
  public void testGetProjectArtifacts() throws Exception {
    String projectId = "projectId";
    when(projectService.getProjectsArtifacts(projectId)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/project/projectId/artifacts"))
        .andExpect(status().isOk());
    verify(projectService, times(1)).getProjectsArtifacts(projectId);
    verifyNoMoreInteractions(projectService);
  }

  @Test
  public void testDeleteProject() throws Exception {
    String projectId = "projectId";
    when(projectService.deleteProject(projectId)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(delete("/api/v1/project/projectId"))
        .andExpect(status().isOk());
    verify(projectService, times(1)).deleteProject(projectId);
    verifyNoMoreInteractions(projectService);
  }

}
