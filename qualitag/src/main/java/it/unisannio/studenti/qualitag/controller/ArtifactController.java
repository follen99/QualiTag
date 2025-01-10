package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.dto.artifact.ArtifactCreateDto;
import it.unisannio.studenti.qualitag.service.ArtifactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/v1/artifacts")
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
  @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> createArtifact(@ModelAttribute ArtifactCreateDto artifactCreateDto) {
    return this.artifactService.addArtifact(artifactCreateDto);
  }

  /**
   * Adds a tag to an artifact.
   *
   * @param artifactId the id of the artifact
   * @param tagId      the id of the tag
   * @return the response entity
   */
  @PostMapping("/add/{artifactId}/tag/{tagId}")
  public ResponseEntity<?> addTag(@PathVariable String artifactId, @PathVariable String tagId) {
    return this.artifactService.addTag(artifactId, tagId);
  }

  // GET Methods

  /**
   * Gets all the artifacts.
   *
   * @return the response entity
   */
  @GetMapping("/get")
  public ResponseEntity<?> getAllArtifacts() {
    return artifactService.getAllArtifacts();
  }

  // PUT Methods

  /**
   * Updates an artifact.
   *
   * @param artifactId        the id of the artifact to update
   * @param artifactCreateDto the updated artifact
   * @return the response entity
   */
  @PutMapping("/update/{artifactId}")
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
  @DeleteMapping("/delete/{artifactId}")
  public ResponseEntity<?> deleteArtifact(@PathVariable String artifactId) {
    return this.artifactService.deleteArtifact(artifactId);
  }

  /**
   * Deletes the associated tag.
   *
   * @param artifactId the id of the artifact to delete the tag from
   * @param tagId      the id of the tag to delete
   * @return the response entity
   */
  @DeleteMapping("/delete/{artifactId}/tag/{tagId}")
  public ResponseEntity<?> deleteTag(@PathVariable String artifactId, @PathVariable String tagId) {
    return this.artifactService.deleteTag(artifactId, tagId);
  }

}
