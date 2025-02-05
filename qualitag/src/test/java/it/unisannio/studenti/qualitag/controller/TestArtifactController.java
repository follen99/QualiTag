package it.unisannio.studenti.qualitag.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.unisannio.studenti.qualitag.dto.artifact.AddTagsToArtifactDto;
import it.unisannio.studenti.qualitag.dto.artifact.ArtifactCreateDto;
import it.unisannio.studenti.qualitag.service.ArtifactService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Test class for ArtifactController.
 */
public class TestArtifactController {

  private MockMvc mockMvc;

  @Mock
  private ArtifactService artifactService;

  @InjectMocks
  private ArtifactController artifactController;

  /**
   * Set up the test environment.
   */
  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(artifactController).build();
  }

  @Test
  public void testCreateArtifact() throws Exception {
    MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain",
        "test data".getBytes());
    ArtifactCreateDto artifactCreateDto = new ArtifactCreateDto("artifactName", "description",
        "projectId", "teamId", file);
    when(artifactService.addArtifact(artifactCreateDto)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(multipart("/api/v1/artifact")
            .file(file)
            .param("artifactName", "artifactName")
            .param("description", "description")
            .param("projectId", "projectId")
            .param("teamId", "teamId"))
        .andExpect(status().isOk());
    verify(artifactService, times(1)).addArtifact(artifactCreateDto);
    verifyNoMoreInteractions(artifactService);
  }

  @Test
  public void testGetArtifact() throws Exception {
    when(artifactService.getArtifact("artifactId")).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/artifact/artifactId"))
        .andExpect(status().isOk());
    verify(artifactService, times(1)).getArtifact("artifactId");
    verifyNoMoreInteractions(artifactService);
  }

  @Test
  public void testGetArtifactMetadata() throws Exception {
    when(artifactService.getArtifactMetadata("artifactId")).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/artifact/artifactId/metadata"))
        .andExpect(status().isOk());
    verify(artifactService, times(1)).getArtifactMetadata("artifactId");
    verifyNoMoreInteractions(artifactService);
  }

  @Test
  public void testAddTagsToArtifact() throws Exception {
    AddTagsToArtifactDto addTagsToArtifactDto = new AddTagsToArtifactDto(
        List.of("tagId1", "tagId2"));
    when(artifactService.addTags("artifactId", addTagsToArtifactDto)).thenReturn(
        ResponseEntity.ok().build());
    mockMvc.perform(put("/api/v1/artifact/artifactId/tag")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"tagIds\":[\"tagId1\", \"tagId2\"]}"))
        .andExpect(status().isOk());
    verify(artifactService, times(1)).addTags("artifactId", addTagsToArtifactDto);
    verifyNoMoreInteractions(artifactService);
  }

  @Test
  public void testRemoveTagsFromArtifact() throws Exception {
    when(artifactService.removeTag("artifactId", "tagId")).thenReturn(
        ResponseEntity.ok().build());
    mockMvc.perform(delete("/api/v1/artifact/artifactId/tag/tagId")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    verify(artifactService, times(1)).removeTag("artifactId", "tagId");
    verifyNoMoreInteractions(artifactService);
  }

  @Test
  public void testeDeleteArtifact() throws Exception {
    when(artifactService.deleteArtifact("artifactId")).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(delete("/api/v1/artifact/artifactId"))
        .andExpect(status().isOk());
    verify(artifactService, times(1)).deleteArtifact("artifactId");
    verifyNoMoreInteractions(artifactService);
  }


}

