package it.unisannio.studenti.qualitag.service;

import it.unisannio.studenti.qualitag.constants.TagConstants;
import it.unisannio.studenti.qualitag.dto.tag.TagCreateDto;
import it.unisannio.studenti.qualitag.dto.tag.TagResponseDto;
import it.unisannio.studenti.qualitag.exception.TagValidationException;
import it.unisannio.studenti.qualitag.mapper.TagMapper;
import it.unisannio.studenti.qualitag.model.Tag;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.TagRepository;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TagService {

  private final TagRepository tagRepository;
  private final TagMapper tagMapper;
  private final UserRepository userRepository;

  /**
   * Constructs a new TagService.
   *
   * @param tagRepository The tag repository.
   * @param tagMapper     The tag mapper.
   */
  public TagService(TagRepository tagRepository, TagMapper tagMapper,
      UserRepository userRepository) {
    this.tagRepository = tagRepository;
    this.tagMapper = tagMapper;
    this.userRepository = userRepository;
  }

  /**
   * Adds a tag.
   *
   * @param tagCreateDto The tag to add.
   * @return The response entity.
   */
  public ResponseEntity<?> addTag(TagCreateDto tagCreateDto) {
    // tag validation
    //Map<String, Object> response = new HashMap<>();
    try {
      TagCreateDto correctTagDto = validateTag(tagCreateDto);

      Tag tag = tagMapper.toEntity(correctTagDto);
      //this.tagRepository.save(tag);
      //response.put("msg", "Tag added successfully");
      //return ResponseEntity.status(HttpStatus.CREATED).body(response);
      return ResponseEntity.status(HttpStatus.CREATED).body("Tag added successfully");
    } catch (TagValidationException e) {
      //response.put("msg", e.getMessage());
      //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  /**
   * Gets all tags.
   *
   * @return The response entity.
   */
  public ResponseEntity<?> getAllTags() {
    List<Tag> tags = tagRepository.findAll();
    return ResponseEntity.status(HttpStatus.OK).body(tags);
  }

  /**
   * Deletes a tag by its id.
   *
   * @param id The id of the tag to delete.
   * @return The response entity.
   */
  public ResponseEntity<?> deleteTag(String id) {
    if (id == null || id.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tag id is null");
    }

    if (!tagRepository.existsById(id)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tag not found");
    }

    tagRepository.deleteById(id);
    if (!tagRepository.existsById(id)) {
      return ResponseEntity.status(HttpStatus.OK).body("Tag deleted successfully");
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Tag not deleted");
  }

  /**
   * Gets a tag by its creator username
   *
   * @param createdBy The username of the user that created the tag.
   * @return The response entity.
   */
  public ResponseEntity<?> getTagsByCreatedByUsername(String createdBy) {
    if (createdBy == null || createdBy.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("User information is null or empty");
    }

    if (!userRepository.existsByUsername(createdBy)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
    }

    List<TagResponseDto> tags = tagMapper.getResponseDtoList(
        tagRepository.findTagByCreatedBy(createdBy)
    );
    if (tags.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("No tags found for the given creator");
    }

    return ResponseEntity.status(HttpStatus.OK).body(tags);
  }

  /**
   * Gets a tag by its creator user id
   *
   * @param userId The id of the user that created the tag.
   * @return The response entity.
   */
  public ResponseEntity<?> getTagsByCreatedByUserId(String userId) {
    if (userId == null || userId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("User information is null or empty");
    }

    if (!userRepository.existsById(userId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
    }

    // find the user by his id
    User user = userRepository.findById(userId).orElse(null);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    // find the tags created by the user
    List<TagResponseDto> tags = tagMapper.getResponseDtoList(
        tagRepository.findTagByCreatedBy(user.getUsername())
    );
    if (tags.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("No tags found for the given creator");
    }

    return ResponseEntity.status(HttpStatus.OK).body(tags);
  }

  /**
   * Gets tags by their value.
   *
   * @param value The value to search for.
   * @return The response entity.
   */
  public ResponseEntity<?> getTagsByValue(String value) {
    if (value == null || value.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tag value is null or empty");
    }

    List<TagResponseDto> responseDtos = tagMapper.getResponseDtoList(
        tagRepository.findByTagValueContaining(value));
    if (responseDtos.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No tags found for the given value");
    }
    return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
  }

  /**
   * ####################################################################### UPDATE
   * #######################################################################
   */

  public ResponseEntity<?> updateTag(TagCreateDto tagModifyDto, String id) {
    // id check
    if (id == null || id.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tag id is null or empty");
    }

    // tag validation
    try {
      TagCreateDto correctDto = validateTag(tagModifyDto);

      Tag tag = tagRepository.findById(id).orElse(null);
      if (tag == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tag not found");
      }

      tag.setTagValue(correctDto.tagValue());
      tag.setColorHex(correctDto.colorHex());
      tag.setCreatedBy(correctDto.createdBy());

      tagRepository.save(tag);

      return ResponseEntity.status(HttpStatus.OK).body("Tag updated successfully");

    } catch (TagValidationException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }


  /**
   * Validates a tag and corrects it if necessary.
   *
   * @param tagDto The tag to validate.
   * @return The corrected tag.
   */
  private TagCreateDto validateTag(TagCreateDto tagDto) {
    if (tagDto == null) {
      throw new TagValidationException("TagCreateDto is null");
    }
    String tagValue = tagDto.tagValue();
    String tagColor = tagDto.colorHex();
    String createdBy = tagDto.createdBy();

    if (tagDto.tagValue() == null || tagDto.tagValue().isEmpty()) {
      throw new TagValidationException("Tag name is null");
    }

    // tag value valiation
    if (!tagValue.matches("\\w+")) {
      throw new TagValidationException("Tag value must be a single word");
    }
    if (tagValue.length() > TagConstants.MAX_TAG_VALUE_LENGTH) {
      throw new TagValidationException(
          "Tag value cannot be longer than " + TagConstants.MAX_TAG_VALUE_LENGTH + " characters");
    }
    if (tagValue.length() < TagConstants.MIN_TAG_VALUE_LENGTH) {
      throw new TagValidationException(
          "Tag value must be at least " + TagConstants.MIN_TAG_VALUE_LENGTH + " characters long");
    }

    // tag color validation
    // TODO color can be null or empty, just choose randomly, get from the TagMapper
    if (tagColor == null || tagColor.isEmpty()) {
      throw new TagValidationException("Tag color cannot be null or empty");
    }

    int dynamicTagColorLength = TagConstants.TAG_COLOR_LENGTH;
    if (tagColor.startsWith("#")) {
      dynamicTagColorLength++;
    } else {
      tagColor = "#" + tagColor;
    }

    tagColor = tagColor.toUpperCase();      // tag colors are always uppercase
    tagColor = tagColor.replaceAll("\\s+", ""); // remove whitespaces

    if (tagColor.length() != dynamicTagColorLength) {
      throw new TagValidationException("Tag color cannot be longer than " + dynamicTagColorLength
          + " characters including '#' symbol.");
    }
    if (!tagColor.matches("^#?([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
      throw new TagValidationException("Tag color must be a hexadecimal value");
    }

    // user validation
    if (createdBy == null || createdBy.isEmpty()) {
      throw new TagValidationException("User information is null or empty");
    }
    if (!userRepository.existsByUsername(createdBy)) {
      throw new TagValidationException("User does not exist");
    }

    return new TagCreateDto(tagValue, createdBy, tagColor);
  }
}
