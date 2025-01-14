package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.dto.artifact.AddTagsToArtifactDto;
import it.unisannio.studenti.qualitag.dto.artifact.ArtifactCreateDto;
import it.unisannio.studenti.qualitag.service.ArtifactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The controller for the artifact endpoints.
 */
@RestController
@RequestMapping("/api/v1/artifact")
public class ArtifactController {

  private final ArtifactService artifactService;

  /**
   * Constructs a new ArtifactController.
   *
   * @param artifactService the artifact service
   */
  @Autowired
  public ArtifactController(ArtifactService artifactService) {
    this.artifactService = artifactService;
  }

  // POST Methods

  /**
   * Adds an artifact to the repository.
   *
   * @param artifactCreateDto the artifact to add to the repository
   */
  @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> createArtifact(@ModelAttribute ArtifactCreateDto artifactCreateDto) {
    return this.artifactService.addArtifact(artifactCreateDto);
  }

  /**
   * Adds a tag to an artifact.
   *
   * @param dto the dto containing the artifact id and the list of tag ids to add
   * @return the response entity
   */
  @PostMapping("/tag")
  public ResponseEntity<?> addTags(@RequestBody AddTagsToArtifactDto dto) {
    return this.artifactService.addTags(dto);
  }

  // GET Methods

  // PUT Methods

  /**
   * Updates an artifact.
   *
   * @param artifactId        the id of the artifact to update
   * @param artifactCreateDto the updated artifact
   * @return the response entity
   */
  @PutMapping("/{artifactId}")
  public ResponseEntity<?> updateArtifact(@PathVariable String artifactId,
      @RequestBody ArtifactCreateDto artifactCreateDto) {
    return this.artifactService.updateArtifact(artifactCreateDto, artifactId);
  }

  // DELETE Methods

  /**
   * Deletes an artifact from the repository.
   *
   * @param artifactId the id of the artifact to delete
   */
  @DeleteMapping("/{artifactId}")
  public ResponseEntity<?> deleteArtifact(@PathVariable String artifactId) {
    return this.artifactService.deleteArtifact(artifactId);
  }

  /**
   * Removes the associated tag.
   *
   * @param artifactId the id of the artifact to remove the tag from
   * @param tagId      the id of the tag to remove
   * @return the response entity
   */
  @DeleteMapping("/{artifactId}/tag/{tagId}")
  public ResponseEntity<?> removeTag(@PathVariable String artifactId, @PathVariable String tagId) {
    return this.artifactService.removeTag(artifactId, tagId);
  }
}
