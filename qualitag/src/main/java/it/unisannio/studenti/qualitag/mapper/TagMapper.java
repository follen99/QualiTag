package it.unisannio.studenti.qualitag.mapper;

import it.unisannio.studenti.qualitag.dto.tag.TagCreateDto;
import it.unisannio.studenti.qualitag.dto.tag.TagResponseDto;
import it.unisannio.studenti.qualitag.model.DefaultColor;
import it.unisannio.studenti.qualitag.model.Tag;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Mapper class for the Tag entity.
 */
@Component
public class TagMapper {

  /**
   * Default constructor.
   */
  // TODO: make this class static!
  public TagMapper() {
  }

  /**
   * Converts a TagCreateDto to a Tag entity.
   *
   * @param dto The TagCreateDto to convert.
   * @return The Tag entity.
   */
  public Tag toEntity(TagCreateDto dto) {
    if (dto == null) {
      return null;
    }
    String colorHex = dto.colorHex();
    if (colorHex == null || colorHex.isEmpty()) {
      colorHex = this.chooseColorRandomlyFromDefaults();
    }
    return new Tag(dto.tagValue().toUpperCase(), dto.createdBy(), colorHex);
  }

  /**
   * Converts a TagResponseDto to a Tag entity.
   *
   * @param dto The TagResponseDto to convert.
   * @return The Tag entity.
   */
  public Tag toEntity(TagResponseDto dto) {
    if (dto == null) {
      return null;
    }
    String colorHex = dto.colorHex();
    if (colorHex == null || colorHex.isEmpty()) {
      colorHex = this.chooseColorRandomlyFromDefaults();
    }
    return new Tag(dto.tagValue().toUpperCase(), dto.createdBy(), colorHex);
  }

  /**
   * Converts a Tag entity to a TagCreateDto.
   *
   * @param entity The Tag entity to convert.
   * @return The TagCreateDto.
   */
  public TagCreateDto getCreateDto(Tag entity) {
    if (entity == null) {
      return null;
    }
    return new TagCreateDto(entity.getTagValue(), entity.getCreatedBy(), entity.getColorHex());
  }

  /**
   * Converts a Tag entity to a TagResponseDto.
   *
   * @param entity The Tag entity to convert.
   * @return The TagResponseDto.
   */
  public TagResponseDto getResponseDto(Tag entity) {
    if (entity == null) {
      return null;
    }
    return new TagResponseDto(entity.getTagId(), entity.getTagValue(), entity.getCreatedBy(),
        entity.getColorHex());
  }

  /**
   * Converts a list of Tag entities to a list of TagResponseDto.
   *
   * @param entities The list of Tag entities to convert.
   * @return The list of TagResponseDto.
   */
  public List<TagResponseDto> getResponseDtoList(List<Tag> entities) {
    return entities.stream().map(this::getResponseDto).toList();
  }

  /**
   * Converts a list of Tag entities to a list of TagCreateDto.
   *
   * @param entities The list of Tag entities to convert.
   * @return The list of TagCreateDto.
   */
  public List<TagCreateDto> getCreateDtoList(List<Tag> entities) {
    return entities.stream().map(this::getCreateDto).toList();
  }

  /**
   * Chooses a color randomly from the default colors.
   *
   * @return The color in hex format.
   */
  private String chooseColorRandomlyFromDefaults() {
    DefaultColor[] colors = DefaultColor.values();
    int randomIndex = (int) (Math.random() * colors.length);
    return colors[randomIndex].getRgb();
  }
}
