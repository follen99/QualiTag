package it.unisannio.studenti.qualitag.service;

import it.unisannio.studenti.qualitag.constants.TagConstants;
import it.unisannio.studenti.qualitag.dto.tag.TagCreateDto;
import it.unisannio.studenti.qualitag.dto.tag.TagResponseDto;
import it.unisannio.studenti.qualitag.dto.tag.TagUpdateDto;
import it.unisannio.studenti.qualitag.exception.TagValidationException;
import it.unisannio.studenti.qualitag.mapper.TagMapper;
import it.unisannio.studenti.qualitag.model.Tag;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.TagRepository;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import it.unisannio.studenti.qualitag.security.model.CustomUserDetails;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to manage tags.
 */
@Service
@RequiredArgsConstructor
public class TagService {

  private final TagMapper tagMapper;

  private final TagRepository tagRepository;
  private final UserRepository userRepository;

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private final Validator validator = factory.getValidator();

  /**
   * Adds a tag.
   *
   * @param tagCreateDto The tag to add.
   * @return The response entity.
   */
  @Transactional
  public ResponseEntity<?> createTag(TagCreateDto tagCreateDto) {
    Map<String, Object> response = new HashMap<>();

    // Tag validation
    try {
      TagCreateDto correctTagDto = validateTag(tagCreateDto);
      Tag tag = tagMapper.toEntity(correctTagDto);

      this.tagRepository.save(tag);
      if (this.addTagToUser(tag)) {
        response.put("msg", "Tag added successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
      }

      // If it was impossible to add the tag to the user, rollback
      this.tagRepository.delete(tag);
      response.put("msg", "Tag already exists");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    } catch (TagValidationException e) {
      response.put("msg", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
  }

  /**
   * Adds a tag to a user every time a tag is created.
   *
   * @param tag The tag to add.
   * @return True if the tag was added, false otherwise.
   */
  private boolean addTagToUser(Tag tag) {
    // Retrieve user. Already validated earlier.
    User user = userRepository.findByUserId(tag.getCreatedBy());

    // Check if the user already has a tag with the same value
    List<String> userTagIds = user.getTagIds();
    for (String tagId : userTagIds) {
      Tag userTag = tagRepository.findTagByTagId(tagId);
      if (userTag.getTagValue().equals(tag.getTagValue())) {
        return false;
      }
    }

    // If all checks are passed, add the tag to the user
    user.getTagIds().add(tag.getTagId());
    userRepository.save(user);
    return true;
  }


  /**
   * Get a tag by its id.
   *
   * @param id The id of the tag to retrieve.
   */
  public ResponseEntity<?> getTagById(String id) {
    Map<String, Object> response = new HashMap<>();

    // ID check
    if (id == null || id.isEmpty()) {
      response.put("msg", "Tag id is null or empty");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Retrieve tag
    Tag tag = tagRepository.findById(id).orElse(null);
    if (tag == null) {
      response.put("msg", "Tag not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Check if the logged user is the creator of the tag
    if (!tag.getCreatedBy().equals(getLoggedInUserId())) {
      response.put("msg", "You are not the creator of the tag");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    response.put("msg", "Tag found");
    response.put("tag", tag);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // TODO: Delete reference from artifacts
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
   * Gets a tag by its creator username.
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

    List<TagResponseDto> tags =
        tagMapper.getResponseDtoList(tagRepository.findTagByCreatedBy(createdBy));
    if (tags.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("No tags found for the given creator");
    }

    return ResponseEntity.status(HttpStatus.OK).body(tags);
  }


  // TODO: Move to user
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

    List<TagResponseDto> responseDtos =
        tagMapper.getResponseDtoList(tagRepository.findByTagValueContaining(value.toUpperCase()));
    if (responseDtos.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No tags found for the given value");
    }
    return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
  }

  /**
   * Updates a tag.
   *
   * @param tagUpdateDto The tag to update.
   * @param id The id of the tag to update.
   * @return The response entity.
   */
  public ResponseEntity<?> updateTag(TagUpdateDto tagUpdateDto, String id) {
    Map<String, Object> response = new HashMap<>();

    // ID check
    if (id == null || id.isEmpty()) {
      response.put("msg", "Tag id is null or empty");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    Tag tag = tagRepository.findTagByTagId(id);
    if (tag == null) {
      response.put("msg", "Tag not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Check if the logged user is the creator of the tag
    if (!tag.getCreatedBy().equals(getLoggedInUserId())) {
      response.put("msg", "You are not the creator of the tag");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    try {
      // Validate DTO
      TagUpdateDto correctedDto = validateUpdate(tagUpdateDto);

      // Check that the user doesn't already have a tag with the same value
      User user = userRepository.findByUserId(tag.getCreatedBy());
      List<String> userTagIds = user.getTagIds();
      for (String tagId : userTagIds) {
        Tag userTag = tagRepository.findTagByTagId(tagId);
        if (userTag.getTagValue().equals(correctedDto.tagValue())) {
          throw new TagValidationException("User already has a tag with the same value");
        }
      }

      // Update tag
      tag.setTagValue(correctedDto.tagValue());
      tag.setColorHex(correctedDto.colorHex());

      // Save tag
      tagRepository.save(tag);

      response.put("msg", "Tag updated successfully");
      return ResponseEntity.status(HttpStatus.OK).body(response);

    } catch (TagValidationException e) {
      response.put("msg", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
  }

  /**
   * Validates a tag and corrects it if necessary.
   *
   * @param tagDto The tag to validate.
   * @return The corrected tag.
   */
  private TagCreateDto validateTag(TagCreateDto tagDto) {
    // Validate DTO
    if (!isValidTagCreateDto(tagDto)) {
      throw new TagValidationException("Tag information is not valid");
    }

    // Validate tag value length
    String tagValue = tagDto.tagValue();
    if (tagValue.length() > TagConstants.MAX_TAG_VALUE_LENGTH) {
      throw new TagValidationException(
          "Tag value cannot be longer than " + TagConstants.MAX_TAG_VALUE_LENGTH + " characters");
    }
    if (tagValue.length() < TagConstants.MIN_TAG_VALUE_LENGTH) {
      throw new TagValidationException(
          "Tag value must be at least " + TagConstants.MIN_TAG_VALUE_LENGTH + " characters long");
    }

    // Tag color validation
    // TODO color can be null or empty, just choose randomly, get from the TagMapper
    String tagColor = tagDto.colorHex();
    int dynamicTagColorLength = TagConstants.TAG_COLOR_LENGTH;
    if (tagColor.startsWith("#")) {
      dynamicTagColorLength++;
    } else {
      tagColor = "#" + tagColor;
    }

    // Tag colour are always in uppercase
    tagColor = tagColor.toUpperCase();

    // Remove whitespaces
    tagColor = tagColor.replaceAll("\\s+", "");

    // Check if the tag color is a valid hexadecimal value
    if (tagColor.length() != dynamicTagColorLength) {
      throw new TagValidationException("Tag color cannot be longer than " + dynamicTagColorLength
          + " characters including '#' symbol.");
    }
    if (!tagColor.matches("^#?([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
      throw new TagValidationException("Tag color must be a hexadecimal value");
    }

    // User validation
    String createdBy = tagDto.createdBy();
    if (createdBy == null || createdBy.isEmpty()) {
      throw new TagValidationException("User information is null or empty");
    }
    if (!userRepository.existsByUsername(createdBy) && !userRepository.existsById(createdBy)) {
      throw new TagValidationException("User does not exist");
    }

    return new TagCreateDto(tagValue, createdBy, tagColor);
  }

  private TagUpdateDto validateUpdate(TagUpdateDto tagDto) {
    Set<ConstraintViolation<TagUpdateDto>> violations = validator.validate(tagDto);

    if (!violations.isEmpty()) {
      throw new TagValidationException("Tag information is not valid");
    }

    // Validate tag value length
    String tagValue = tagDto.tagValue();
    if (tagValue.length() > TagConstants.MAX_TAG_VALUE_LENGTH) {
      throw new TagValidationException(
          "Tag value cannot be longer than " + TagConstants.MAX_TAG_VALUE_LENGTH + " characters");
    }
    if (tagValue.length() < TagConstants.MIN_TAG_VALUE_LENGTH) {
      throw new TagValidationException(
          "Tag value must be at least " + TagConstants.MIN_TAG_VALUE_LENGTH + " characters long");
    }

    // Tag color validation
    // TODO color can be null or empty, just choose randomly, get from the TagMapper
    String tagColor = tagDto.colorHex();
    int dynamicTagColorLength = TagConstants.TAG_COLOR_LENGTH;
    if (tagColor.startsWith("#")) {
      dynamicTagColorLength++;
    } else {
      tagColor = "#" + tagColor;
    }

    // Tag colour are always in uppercase
    tagColor = tagColor.toUpperCase();

    // Remove whitespaces
    tagColor = tagColor.replaceAll("\\s+", "");

    // Check if the tag color is a valid hexadecimal value
    if (tagColor.length() != dynamicTagColorLength) {
      throw new TagValidationException("Tag color cannot be longer than " + dynamicTagColorLength
          + " characters including '#' symbol.");
    }
    if (!tagColor.matches("^#?([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
      throw new TagValidationException("Tag color must be a hexadecimal value");
    }

    return new TagUpdateDto(tagValue, tagColor);
  }

  private boolean isValidTagCreateDto(TagCreateDto tagCreateDto) {
    Set<ConstraintViolation<TagCreateDto>> violations = validator.validate(tagCreateDto);

    return violations.isEmpty();
  }

  private String getLoggedInUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new IllegalStateException("No authenticated user found");
    }

    Object principal = authentication.getPrincipal();
    if (principal instanceof CustomUserDetails(User user)) {
      return user.getUserId();
    }
    throw new IllegalStateException(
        "Unexpected authentication principal type: " + principal.getClass());
  }
}
