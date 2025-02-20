package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.dto.artifact.AddTagsToArtifactDto;
import it.unisannio.studenti.qualitag.dto.artifact.ArtifactCreateDto;
import it.unisannio.studenti.qualitag.service.ArtifactService;
import java.util.List;
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

  // GET Methods

  /**
   * Gets the artifact file when given the ID.
   *
   * @param artifactId the id of the artifact to get
   * @return the response entity
   */
  @GetMapping("/{artifactId}")
  public ResponseEntity<?> getArtifact(@PathVariable String artifactId) {
    return this.artifactService.getArtifact(artifactId);
  }

  /**
   * Gets all the tags of an artifact.
   *
   * @param artifactId the id of the artifact to get the tags of
   * @return the response entity
   */
  @GetMapping("/{artifactId}/tags")
  public ResponseEntity<?> getTags(@PathVariable String artifactId) {
    return this.artifactService.getAllTags(artifactId);
  }

  /**
   * Gets the metadata of an artifact.
   *
   * @param artifactId the id of the artifact to get the metadata of
   * @return the response entity
   */
  @GetMapping("/{artifactId}/metadata")
  public ResponseEntity<?> getArtifactMetadata(@PathVariable String artifactId) {
    return this.artifactService.getArtifactMetadata(artifactId);
  }

  /**
   * Gets the tags of an artifact by a user.
   *
   * @param artifactId              the id of the artifact to get the tags of
   * @param userIdOrEmailOrUsername the id, email, or username of the user to get the tags of
   * @return the response entity
   */
  @GetMapping("/{artifactId}/{userIdOrEmailOrUsername}/tags")
  public ResponseEntity<?> getTagsByUser(@PathVariable String artifactId,
      @PathVariable String userIdOrEmailOrUsername) {
    return this.artifactService.getTagsByUser(artifactId, userIdOrEmailOrUsername);
  }

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

  /**
   * Adds a tag to an artifact.
   *
   * @param dto the dto containing the artifact id and the list of tag ids to add
   * @return the response entity
   */
  @PutMapping("/{artifactId}/tag")
  public ResponseEntity<?> addTags(@PathVariable String artifactId,
      @RequestBody AddTagsToArtifactDto dto) {
    return this.artifactService.addTags(artifactId, dto);
  }

  /**
   * Starts the tagging process for an artifact.
   *
   * @param artifactId the id of the artifact to start tagging
   * @return the response entity
   */
  @PutMapping("/{artifactId}/starttagging")
  public ResponseEntity<?> startTagging(@PathVariable String artifactId) {
    return this.artifactService.startTagging(artifactId);
  }

  /**
   * Starts the tagging process for a list of artifacts.
   *
   * @param artifactIds the list of artifact ids to start tagging
   * @return the response entity
   */
  @PutMapping("/starttagging")
  public ResponseEntity<?> startTaggingList(@RequestBody List<String> artifactIds) {
    return this.artifactService.startTagging(artifactIds);
  }

  /**
   * Stops the tagging process for an artifact.
   *
   * @param artifactId the id of the artifact to stop tagging
   * @return the response entity
   */
  @PutMapping("/{artifactId}/stoptagging")
  public ResponseEntity<?> stopTagging(@PathVariable String artifactId) {
    return this.artifactService.stopTagging(artifactId);
  }

  /**
   * Stops the tagging process for a list of artifacts.
   *
   * @param artifactIds the list of artifact ids to stop tagging
   * @return the response entity
   */
  @PutMapping("/stoptagging")
  public ResponseEntity<?> stopTaggingList(@RequestBody List<String> artifactIds) {
    return this.artifactService.stopTagging(artifactIds);
  }

  /**
   * Processes the tags of an artifact.
   *
   * @param artifactId the id of the artifact to process the tags of
   * @return the response entity
   */
  @PutMapping("/{artifactId}/process-tags")
  public ResponseEntity<?> processTags(@PathVariable String artifactId) {
    return this.artifactService.processTags(artifactId);
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
