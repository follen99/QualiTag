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

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unisannio.studenti.qualitag.dto.tag.TagCreateDto;
import it.unisannio.studenti.qualitag.dto.tag.TagUpdateDto;
import it.unisannio.studenti.qualitag.service.TagService;
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
 * Test class for TagController.
 */
public class TestTagController {

  private MockMvc mockMvc;

  @Mock
  private TagService tagService;

  @InjectMocks
  private TagController tagController;

  /**
   * Set up the test environment.
   */
  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(tagController).build();
  }

  /**
   * Test the creation of a tag.
   *
   * @throws Exception if an error occurs
   */
  @Test
  public void testCreateTag() throws Exception {
    TagCreateDto tagCreateDto = new TagCreateDto(
        "tagValue",
        "createdBy",
        "colorHex");
    when(tagService.createTag(tagCreateDto)).thenReturn(ResponseEntity.ok().build());

    mockMvc.perform(post("/api/v1/tag")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                "{\"tagValue\":\"tagValue\",\"createdBy\":"
                + "\"createdBy\",\"colorHex\":\"colorHex\"}"))
        .andExpect(status().isOk());

    verify(tagService, times(1)).createTag(tagCreateDto);
    verifyNoMoreInteractions(tagService);
  }

  /**
   * Test getting a tag by ID.
   *
   * @throws Exception if an error occurs
   */
  @Test
  public void testGetTagById() throws Exception {
    String tagId = "tagId";
    when(tagService.getTagById(tagId)).thenReturn(ResponseEntity.ok().build());

    mockMvc.perform(get("/api/v1/tag/tagId"))
        .andExpect(status().isOk());
    verify(tagService, times(1)).getTagById(tagId);
  }

  /**
   * Test getting tags by value.
   *
   * @throws Exception if an error occurs
   */
  @Test
  public void testGetTagsByValue() throws Exception {
    String tagValue = "tagValue";
    when(tagService.getTagsByValue(tagValue)).thenReturn(ResponseEntity.ok().build());

    mockMvc.perform(get("/api/v1/tag/value/tagValue"))
        .andExpect(status().isOk());
    verify(tagService, times(1)).getTagsByValue(tagValue);
  }

  /**
   * Test updating a tag.
   *
   * @throws Exception if an error occurs
   */
  @Test
  public void testUpdateTag() throws Exception {
    String tagId = "tagId";
    TagUpdateDto tagUpdateDto = new TagUpdateDto(
        "tagValue",
        "colorHex");
    when(tagService.updateTag(tagUpdateDto, tagId)).thenReturn(ResponseEntity.ok().build());

    mockMvc.perform(put("/api/v1/tag/" + tagId)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"tagValue\":\"tagValue\",\"colorHex\":\"colorHex\"}"))
        .andExpect(status().isOk());

    verify(tagService, times(1)).updateTag(tagUpdateDto, tagId);
    verifyNoMoreInteractions(tagService);
  }

  /**
   * Test deleting a tag.
   *
   * @throws Exception if an error occurs
   */
  @Test
  public void testDeleteTag() throws Exception {
    String tagId = "tagId";
    when(tagService.deleteTag(tagId)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(delete("/api/v1/tag/" + tagId))
        .andExpect(status().isOk());
    verify(tagService, times(1)).deleteTag(tagId);
  }

  /**
   * Test adding a list of tags to an artifact.
   */
  @Test
  public void testAddTags() throws Exception {
    TagCreateDto tagCreateDto1 = new TagCreateDto(
        "TAGVALUE",
        "userId",
        "#fff8de");
    TagCreateDto tagCreateDto2 = new TagCreateDto(
        "TAGVALUE2",
        "userId",
        "#295f98");
    List<TagCreateDto> tags = List.of(tagCreateDto1, tagCreateDto2);
    when(tagService.addTagsToArtifactAndUser(tags, "artifactId"))
        .thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(post("/api/v1/tag/artifactId/addtags")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(tags)))
        .andExpect(status().isOk());
    verify(tagService, times(1)).addTagsToArtifactAndUser(tags, "artifactId");
    verifyNoMoreInteractions(tagService);
  }

  /**
   * Test getting tags by user ID.
   */
  @Test
  public void testGetTagsByUserId() throws Exception {
    String userId = "userId";
    when(tagService.getTagsByUser(userId)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(get("/api/v1/tag/byuser/userId/all"))
        .andExpect(status().isOk());
    verify(tagService, times(1)).getTagsByUser(userId);
    verifyNoMoreInteractions(tagService);
  }

}
