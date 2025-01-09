package it.unisannio.studenti.qualitag.service;

import it.unisannio.studenti.qualitag.dto.artifact.ArtifactCreateDto;
import it.unisannio.studenti.qualitag.exception.ArtifactValidationException;
import it.unisannio.studenti.qualitag.mapper.ArtifactMapper;
import it.unisannio.studenti.qualitag.model.Artifact;
import it.unisannio.studenti.qualitag.repository.ArtifactRepository;
import it.unisannio.studenti.qualitag.repository.TagRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * The service class for the artifact.
 */
@Service
public class ArtifactService {

  private final ArtifactRepository artifactRepository;
  private final TagRepository tagRepository;

  /**
   * Constructs a new ArtifactService.
   *
   * @param artifactRepository the artifact repository
   */
  public ArtifactService(ArtifactRepository artifactRepository, TagRepository tagRepository) {
    this.artifactRepository = artifactRepository;
    this.tagRepository = tagRepository;
  }

  /**
   * Creates a new artifact.
   *
   * @param artifactCreateDto the artifact creation data
   * @return the response entity with the result of the artifact creation
   */
  public ResponseEntity<?> addArtifact(ArtifactCreateDto artifactCreateDto) {
    try {
      ArtifactCreateDto correctArtifactDto = validateArtifact(artifactCreateDto);

      Artifact artifact = ArtifactMapper.toEntity(correctArtifactDto);
      this.artifactRepository.save(artifact);
      return ResponseEntity.status(HttpStatus.CREATED).body("Artifact added successfully");

    } catch (ArtifactValidationException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  /**
   * Adds a tag to an artifact.
   *
   * @param artifactId the id of the artifact to add the tag
   * @param tagId      the id of the tag to add
   * @return the response entity
   */
  public ResponseEntity<?> addTag(String artifactId, String tagId) {
    if (artifactId == null || artifactId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Artifact id is null or empty");
    }
    if (tagId == null || tagId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tag id is null or empty");
    }

    Artifact artifact = artifactRepository.findArtifactByArtifactId(artifactId);
    if (artifact == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artifact not found");
    }

    List<String> tagIds = artifact.getTags();
    if (tagIds.contains(tagId)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tag already exists in artifact");
    }

    tagIds.add(tagId);
    artifact.setTags(tagIds);
    artifactRepository.save(artifact);

    return ResponseEntity.status(HttpStatus.OK).body("Tag added successfully");
  }

  /**
   * Gets all the artifacts.
   *
   * @return The response entity
   */
  public ResponseEntity<?> getAllArtifacts() {
    return ResponseEntity.status(HttpStatus.OK).body(artifactRepository.findAll());
  }

  /**
   * Deletes an artifact by its id.
   *
   * @param id the id of the artifact to delete
   * @return The response entity
   */
  public ResponseEntity<?> deleteArtifact(String id) {
    if (id == null || id.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Artifact id id null");
    }
    if (!artifactRepository.existsById(id)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artifact not found");
    }

    artifactRepository.deleteById(id);
    if (!artifactRepository.existsById(id)) {
      return ResponseEntity.status(HttpStatus.OK).body("Artifact deleted successfully");
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Artifact not deleted");
    }
  }

  /**
   * Deletes a tag of an artifact.
   *
   * @param artifactId the id of the artifact which tag we want to delete
   * @param tagId      the id of the tag to delete
   * @return the response entity
   */
  public ResponseEntity<?> deleteTag(String artifactId, String tagId) {
    if (artifactId == null || artifactId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Artifact id is null or empty");
    }
    if (tagId == null || tagId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tag id is null or empty");
    }

    Artifact artifact = artifactRepository.findArtifactByArtifactId(artifactId);
    if (artifact == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artifact not found");
    }

    List<String> tagIds = artifact.getTags();
    if (tagIds == null || tagIds.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artifact has no tags");
    }

    if (!tagIds.contains(tagId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tag not found in artifact");
    }

    tagIds.remove(tagId);
    artifact.setTags(tagIds);
    artifactRepository.save(artifact);

    return ResponseEntity.status(HttpStatus.OK).body("Tag deleted successfully");
  }

  /**
   * Modifies an existing artifact.
   *
   * @param artifactModifyDto the dto used to modify the artifact
   * @param artifactId        the id of the artifact to modify
   * @return the response entity
   */
  public ResponseEntity<?> updateArtifact(ArtifactCreateDto artifactModifyDto, String artifactId) {
    // ID check
    if (artifactId == null || artifactId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Artifact id is null or empty");
    }

    // Artifact validation
    try {
      ArtifactCreateDto correctDto = validateArtifact(artifactModifyDto);

      Artifact artifact = artifactRepository.findArtifactByArtifactId(artifactId);
      if (artifact == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artifact not found");
      }

      artifact.setArtifactName(correctDto.artifactName());
      artifact.setContent(correctDto.content());
      artifact.setTags(correctDto.tags());

      artifactRepository.save(artifact);

      return ResponseEntity.status(HttpStatus.OK).body("Artifact updated successfully");

    } catch (ArtifactValidationException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

  }

  /**
   * Validates an artifact.
   *
   * @param artifactDto the dto used to create the artifact
   * @return the validated artifact
   */
  private ArtifactCreateDto validateArtifact(ArtifactCreateDto artifactDto) {
    if (artifactDto == null) {
      throw new ArtifactValidationException("ArtifactCreateDto is null");
    }
    String artifactName = artifactDto.artifactName();
    String artifactContent = artifactDto.content();
    List<String> artifactTags = artifactDto.tags();

    //artifact name validation
    if (artifactName == null || artifactName.isEmpty()) {
      throw new ArtifactValidationException("Artifact name cannot be null or empty");
    }

    //artifact content validation
    if (artifactContent == null || artifactContent.isEmpty()) {
      throw new ArtifactValidationException("Artifact content cannot be null or empty");
    }

    //artifact tags validation
    if (artifactTags == null || artifactTags.isEmpty()) {
      throw new ArtifactValidationException("Artifact tags cannot be null or empty");
    }

    for (String currentTagId : artifactTags) {
      if (currentTagId == null || currentTagId.trim().isEmpty()) {
        throw new ArtifactValidationException("There is an empty tag in this list. Remove it");
      }
      currentTagId = currentTagId.trim(); //remove leading and trailing whitespaces
      if (!tagRepository.existsById(currentTagId)) {
        throw new ArtifactValidationException("Tag with id " + currentTagId + " does not exist");
      }
      //TODO Checks other constraints about tags

    }

    return new ArtifactCreateDto(artifactName, artifactContent, artifactTags);
  }
}
