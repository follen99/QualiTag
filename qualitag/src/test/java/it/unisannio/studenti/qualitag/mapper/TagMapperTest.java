package it.unisannio.studenti.qualitag.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.unisannio.studenti.qualitag.dto.tag.TagCreateDto;
import it.unisannio.studenti.qualitag.dto.tag.TagResponseDto;
import it.unisannio.studenti.qualitag.model.Tag;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TagMapperTest {

  private Tag entity;
  private TagCreateDto createDto;
  private TagResponseDto responseDto;
  private TagMapper tagMapper;

  /**
   * Initializes the test environment.
   */
  @BeforeEach
  public void setUp() {
    tagMapper = new TagMapper();
    entity = new Tag("tagValue", "userID", "#fff8de");
    entity.setTagId("6744ba6c60e0564864250e89");
    createDto = new TagCreateDto("tagValue", "userID", "#fff8de");
    responseDto = new TagResponseDto("6744ba6c60e0564864250e89",
        "TAGVALUE", "userID", "#fff8de");
  }

  /**
   * Tests the toEntity method of the TagMapper class (TagCreateDto).
   */
  @Test
  public void testToEntityCreate() {
    Tag entityFromDTO = tagMapper.toEntity(createDto);
    assertNotNull(entityFromDTO);
    assertEquals(entity.getTagValue(), entityFromDTO.getTagValue());
    assertEquals(entity.getCreatedBy(), entityFromDTO.getCreatedBy());
    assertEquals(entity.getColorHex(), entityFromDTO.getColorHex());
  }

  /**
   * Tests the toEntity method of the TagMapper class (TagCreateDto) when the dto is null.
   */
  @Test
  public void testToEntityCreateNull() {
    createDto = null;
    Tag entityFromDTO = tagMapper.toEntity(createDto);
    assertNull(entityFromDTO);
  }

  /**
   * Tests the toEntity method of the TagMapper class (TagCreateDto) in the case when the colorHex
   * is null
   */
  @Test
  public void testToEntityCreateNullColorHex() {
    createDto = new TagCreateDto("tagValue", "userID", null);
    Tag entityFromDTO = tagMapper.toEntity(createDto);
    assertNotNull(entityFromDTO);
    assertEquals(entity.getTagValue(), entityFromDTO.getTagValue());
    assertEquals(entity.getCreatedBy(), entityFromDTO.getCreatedBy());
    assertNotNull(entityFromDTO.getColorHex());
  }

  /**
   * Tests the toEntity method of the TagMapper class (TagCreateDto) in the case when the colorHex
   * is empty
   */
  @Test
  public void testToEntityCreateEmptyColorHex() {
    createDto = new TagCreateDto("tagValue", "userID", "");
    Tag entityFromDTO = tagMapper.toEntity(createDto);
    assertNotNull(entityFromDTO);
    assertEquals(entity.getTagValue(), entityFromDTO.getTagValue());
    assertEquals(entity.getCreatedBy(), entityFromDTO.getCreatedBy());
    assertNotNull(entityFromDTO.getColorHex());
  }

  /**
   * Tests the toEntity method of the TagMapper class (TagResponseDto).
   */
  @Test
  public void testToEntityResponse() {
    Tag entityFromDTO = tagMapper.toEntity(responseDto);
    assertNotNull(entityFromDTO);
    assertEquals(entity.getTagValue(), entityFromDTO.getTagValue());
    assertEquals(entity.getCreatedBy(), entityFromDTO.getCreatedBy());
    assertEquals(entity.getColorHex(), entityFromDTO.getColorHex());
  }

  /**
   * Tests the toEntity method of the TagMapper class (TagResponseDto) when the dto is null.
   */
  @Test
  public void testToEntityResponseNull() {
    responseDto = null;
    Tag entityFromDTO = tagMapper.toEntity(responseDto);
    assertNull(entityFromDTO);
  }

  /**
   * Tests the toEntity method of the TagMapper class (TagResponseDto) in the case when the
   * colorHex is null
   */
  @Test
  public void testToEntityResponseNullColorHex() {
    responseDto = new TagResponseDto("6744ba6c60e0564864250e89",
        "TAGVALUE", "userID", null);
    Tag entityFromDTO = tagMapper.toEntity(responseDto);
    assertNotNull(entityFromDTO);
    assertEquals(entity.getTagValue(), entityFromDTO.getTagValue());
    assertEquals(entity.getCreatedBy(), entityFromDTO.getCreatedBy());
    assertNotNull(entityFromDTO.getColorHex());
  }

  /**
   * Tests the toEntity method of the TagMapper class (TagResponseDto) in the case when the
   * colorHex is empty
   */
  @Test
  public void testToEntityResponseEmptyColorHex() {
    responseDto = new TagResponseDto("6744ba6c60e0564864250e89",
        "TAGVALUE", "userID", "");
    Tag entityFromDTO = tagMapper.toEntity(responseDto);
    assertNotNull(entityFromDTO);
    assertEquals(entity.getTagValue(), entityFromDTO.getTagValue());
    assertEquals(entity.getCreatedBy(), entityFromDTO.getCreatedBy());
    assertNotNull(entityFromDTO.getColorHex());
  }

  /**
   * Tests the getCreateDto method of the TagMapper class.
   */
  @Test
  public void testGetCreateDto() {
    TagCreateDto dto = tagMapper.getCreateDto(entity);
    assertNotNull(dto);
    assertEquals(entity.getTagValue(), dto.tagValue());
    assertEquals(entity.getCreatedBy(), dto.createdBy());
    assertEquals(entity.getColorHex(), dto.colorHex());
  }

  /**
   * Tests the getCreateDto method of the TagMapper class when the entity is null.
   */
  @Test
  public void testGetCreateDtoNull() {
    entity = null;
    TagCreateDto dto = tagMapper.getCreateDto(entity);
    assertNull(dto);
  }

  /**
   * Tests the getResponseDto method of the TagMapper class.
   */
  @Test
  public void testGetResponseDto() {
    TagResponseDto dto = tagMapper.getResponseDto(entity);
    assertNotNull(dto);
    assertEquals(entity.getTagId(), dto.tagId());
    assertEquals(entity.getTagValue(), dto.tagValue());
    assertEquals(entity.getCreatedBy(), dto.createdBy());
    assertEquals(entity.getColorHex(), dto.colorHex());
  }

  /**
   * Tests the getResponseDto method of the TagMapper class when the entity is null.
   */
  @Test
  public void testGetResponseDtoNull() {
    entity = null;
    TagResponseDto dto = tagMapper.getResponseDto(entity);
    assertNull(dto);
  }

  /**
   * Tests the getResponseDtoList method of the TagMapper class.
   */
  @Test
  public void testGetResponseDtoList() {
    Tag entity2 = new Tag("tagValue2", "userID2", "#295f98");
    entity2.setTagId("6744ba6c60e0564864250e90");

    List<Tag> entities = new ArrayList<>();
    entities.add(entity);
    entities.add(entity2);

    List<TagResponseDto> dtoList = tagMapper.getResponseDtoList(entities);

    assertNotNull(dtoList);
    assertEquals(2, dtoList.size());
    assertEquals(entity.getTagValue(), dtoList.getFirst().tagValue());
    assertEquals(entity.getCreatedBy(), dtoList.getFirst().createdBy());
    assertEquals(entity.getColorHex(), dtoList.getFirst().colorHex());

    assertEquals(entity2.getTagValue(), dtoList.get(1).tagValue());
    assertEquals(entity2.getCreatedBy(), dtoList.get(1).createdBy());
    assertEquals(entity2.getColorHex(), dtoList.get(1).colorHex());
  }

  /**
   * Tests the getCreateDtoList method of the TagMapper class.
   */
  @Test
  public void testGetCreateDtoList() {
    Tag entity2 = new Tag("tagValue2", "userID2", "#295f98");
    entity2.setTagId("6744ba6c60e0564864250e90");

    List<Tag> entities = new ArrayList<>();
    entities.add(entity);
    entities.add(entity2);

    List<TagCreateDto> dtoList = tagMapper.getCreateDtoList(entities);

    assertNotNull(dtoList);
    assertEquals(2, dtoList.size());
    assertEquals(entity.getTagValue(), dtoList.getFirst().tagValue());
    assertEquals(entity.getCreatedBy(), dtoList.getFirst().createdBy());
    assertEquals(entity.getColorHex(), dtoList.getFirst().colorHex());

    assertEquals(entity2.getTagValue(), dtoList.get(1).tagValue());
    assertEquals(entity2.getCreatedBy(), dtoList.get(1).createdBy());
    assertEquals(entity2.getColorHex(), dtoList.get(1).colorHex());
  }


}
