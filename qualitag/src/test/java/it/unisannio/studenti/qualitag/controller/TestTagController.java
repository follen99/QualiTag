package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.dto.tag.TagCreateDto;
import it.unisannio.studenti.qualitag.dto.tag.TagUpdateDto;
import it.unisannio.studenti.qualitag.service.TagService;
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


public class TestTagController {

  private MockMvc mockMvc;

  @Mock
  private TagService tagService;

  @InjectMocks
  private TagController tagController;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(tagController).build();
  }

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
                "{\"tagValue\":\"tagValue\",\"createdBy\":\"createdBy\",\"colorHex\":\"colorHex\"}"))
        .andExpect(status().isOk());

    verify(tagService, times(1)).createTag(tagCreateDto);
    verifyNoMoreInteractions(tagService);
  }

  @Test
  public void testGetTagById() throws Exception {
    String tagId = "tagId";
    when(tagService.getTagById(tagId)).thenReturn(ResponseEntity.ok().build());

    mockMvc.perform(get("/api/v1/tag/tagId"))
        .andExpect(status().isOk());
    verify(tagService, times(1)).getTagById(tagId);
  }

  @Test
  public void testGetTagsByValue() throws Exception {
    String tagValue = "tagValue";
    when(tagService.getTagsByValue(tagValue)).thenReturn(ResponseEntity.ok().build());

    mockMvc.perform(get("/api/v1/tag/value/tagValue"))
        .andExpect(status().isOk());
    verify(tagService, times(1)).getTagsByValue(tagValue);
  }

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

  @Test
  public void testDeleteTag() throws Exception {
    String tagId = "tagId";
    when(tagService.deleteTag(tagId)).thenReturn(ResponseEntity.ok().build());
    mockMvc.perform(delete("/api/v1/tag/" + tagId))
        .andExpect(status().isOk());
    verify(tagService, times(1)).deleteTag(tagId);
  }

}
