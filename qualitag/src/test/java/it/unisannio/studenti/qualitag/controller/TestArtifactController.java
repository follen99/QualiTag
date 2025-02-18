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

import com.fasterxml.jackson.databind.ObjectMapper;
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

  /**
   * Test the creation of an artifact.
   *
   * @throws Exception if an error occurs during the test.
   */
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

  /**
   * Test the retrieval of an artifact.
   *
   * @throws Exception if an error occurs during the test.
   */
  @Test
  public void testGetArtifact() throws Exception {
    when(artifactService.getArtifact("artifactId")).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/artifact/artifactId"))
        .andExpect(status().isOk());
    verify(artifactService, times(1)).getArtifact("artifactId");
    verifyNoMoreInteractions(artifactService);
  }

  /**
   * Test the retrieval of the metadata of an artifact.
   *
   * @throws Exception if an error occurs during the test.
   */
  @Test
  public void testGetArtifactMetadata() throws Exception {
    when(artifactService.getArtifactMetadata("artifactId")).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/artifact/artifactId/metadata"))
        .andExpect(status().isOk());
    verify(artifactService, times(1)).getArtifactMetadata("artifactId");
    verifyNoMoreInteractions(artifactService);
  }

  /**
   * Test the addition of tahs to an artifact.
   *
   * @throws Exception if an error occurs during the test.
   */
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

  /**
   * Test the removal of tags from an artifact.
   *
   * @throws Exception if an error occurs during the test.
   */
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

  /**
   * Test the deletion of an artifact.
   *
   * @throws Exception if an error occurs during the test.
   */
  @Test
  public void testeDeleteArtifact() throws Exception {
    when(artifactService.deleteArtifact("artifactId")).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(delete("/api/v1/artifact/artifactId"))
        .andExpect(status().isOk());
    verify(artifactService, times(1)).deleteArtifact("artifactId");
    verifyNoMoreInteractions(artifactService);
  }

  /**
   * Tests the retrieval of tags of an artifact.
   */
  @Test
  public void testGetTagsOfArtifact() throws Exception {
    when(artifactService.getAllTags("artifactId")).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/artifact/artifactId/tags"))
        .andExpect(status().isOk());
    verify(artifactService, times(1)).getAllTags("artifactId");
    verifyNoMoreInteractions(artifactService);
  }

  /**
   * Tests the retrieval of tags of an artifact by userId.
   */
  @Test
  public void testGetTagsOfArtifactByUserId() throws Exception {
    when(artifactService.getTagsByUser("artifactId", "userId")).thenReturn(
        ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/artifact/artifactId/userId/tags"))
        .andExpect(status().isOk());
    verify(artifactService, times(1)).getTagsByUser("artifactId", "userId");
    verifyNoMoreInteractions(artifactService);
  }

  /**
   * Tests the start of tagging of an artifact.
   */
  @Test
  public void testStartTagging() throws Exception {
    when(artifactService.startTagging("artifactId")).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(put("/api/v1/artifact/artifactId/starttagging"))
        .andExpect(status().isOk());
    verify(artifactService, times(1)).startTagging("artifactId");
    verifyNoMoreInteractions(artifactService);
  }

  /**
   * Tests the start tagging of a list of artifacts.
   */
  @Test
  public void testStartTaggingList() throws Exception {
    List<String> artifactIds = List.of("artifactId1", "artifactId2");
    when(artifactService.startTagging(artifactIds)).thenReturn(
        ResponseEntity.ok().build());
    mockMvc.perform(put("/api/v1/artifact/starttagging")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(artifactIds)))
        .andExpect(status().isOk());
    verify(artifactService, times(1)).startTagging(artifactIds);
    verifyNoMoreInteractions(artifactService);
  }

  /**
   * Tests the end of tagging of an artifact.
   */
  @Test
  public void testStopTagging() throws Exception {
    when(artifactService.stopTagging("artifactId")).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(put("/api/v1/artifact/artifactId/stoptagging"))
        .andExpect(status().isOk());
    verify(artifactService, times(1)).stopTagging("artifactId");
    verifyNoMoreInteractions(artifactService);
  }

  /**
   * Tests the end of tagging of a list of artifacts.
   */
  @Test
  public void testStopTaggingList() throws Exception {
    List<String> artifactIds = List.of("artifactId1", "artifactId2");
    when(artifactService.stopTagging(artifactIds)).thenReturn(
        ResponseEntity.ok().build());
    mockMvc.perform(put("/api/v1/artifact/stoptagging")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(artifactIds)))
        .andExpect(status().isOk());
    verify(artifactService, times(1)).stopTagging(artifactIds);
    verifyNoMoreInteractions(artifactService);
  }

  /**
   * Tests the process of tagging of an artifact.
   */
  @Test
  public void testProcessTagging() throws Exception {
    when(artifactService.processTags("artifactId")).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(put("/api/v1/artifact/artifactId/process-tags"))
        .andExpect(status().isOk());
    verify(artifactService, times(1)).processTags("artifactId");
    verifyNoMoreInteractions(artifactService);
  }
}

