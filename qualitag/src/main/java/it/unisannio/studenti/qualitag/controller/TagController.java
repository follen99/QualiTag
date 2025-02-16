package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.dto.tag.TagCreateDto;
import it.unisannio.studenti.qualitag.dto.tag.TagUpdateDto;
import it.unisannio.studenti.qualitag.service.TagService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Tag controller.
 */
@RestController // This means that this class is a Controller
@RequestMapping("/api/v1/tag") // This means URL's start with /api/v1 (after Application path)
public class TagController {

  private final TagService tagService;

  /**
   * The constructor.
   *
   * @param service The tag service.
   */
  public TagController(TagService service) {
    this.tagService = service;
  }

  /**
   * Creates a new tag.
   *
   * @param tagCreateDto The tag to add.
   * @return The response entity.
   */
  @PostMapping()
  public ResponseEntity<?> createTag(@RequestBody TagCreateDto tagCreateDto) {
    return this.tagService.createTag(tagCreateDto);
  }

  /**
   * Adds tags to an artifact.
   *
   * @param tags       The tags to add.
   * @param artifactId The id of the artifact to add the tags to.
   * @return The response entity.
   */
  // TODO: dovremmo spostare questo metodo in artifactController?
  @PostMapping("/{artifactId}/addtags")
  public ResponseEntity<?> addTags(@RequestBody List<TagCreateDto> tags,
      @PathVariable String artifactId) {
    return this.tagService.addTagsToArtifactAndUser(tags, artifactId);
  }

  // /**
  //  * DO NOT USE, WORK IN PROGRESS.
  //  * Updates the tags of an artifact.
  //  *
  //  * @param tags     The tags to update.
  //  * @param artifactId  The id of the artifact to update the tags.
  //  * @return  The response entity.
  //  */
  // @PostMapping("/{artifactId}/updateTags")
  // public ResponseEntity<?> updateTags(@RequestBody List<TagCreateDto> tags,
  //     @PathVariable String artifactId) {
  //   return this.tagService.updateTagsOfAnArtifact(tags, artifactId);
  // }
  
  /**
   * Get a tag by its id.
   *
   * @param id The id of the tag to get.
   * @return The response entity.
   */
  @GetMapping("/{id}")
  public ResponseEntity<?> getTagById(@PathVariable String id) {
    return this.tagService.getTagById(id);
  }

  /**
   * Get all tags of an user, from all the artifacts.
   *
   * @param userIdOrEmailOrUsername user id or email.
   * @return The response entity containing a list of tags.
   */
  @GetMapping("byuser/{userIdOrEmailOrUsername}/all")
  public ResponseEntity<?> getTagsByUser(@PathVariable String userIdOrEmailOrUsername) {
    return this.tagService.getTagsByUser(userIdOrEmailOrUsername);
  }

  /**
   * Gets tags by their value.
   *
   * @param value The value of the tags to get.
   * @return The response entity.
   */
  @GetMapping("/value/{value}")
  public ResponseEntity<?> getTagsByValue(@PathVariable String value) {
    return this.tagService.getTagsByValue(value);
  }

  /**
   * Update tag given its id.
   *
   * @param id The id of the tag to update.
   * @return The response entity.
   */
  @PutMapping("/{id}")
  public ResponseEntity<?> updateTag(@RequestBody TagUpdateDto tagUpdateDto,
      @PathVariable String id) {
    return this.tagService.updateTag(tagUpdateDto, id);
  }

  /**
   * Deletes a tag by its id.
   *
   * @param id The id of the tag to delete.
   * @return The response entity.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteTag(@PathVariable String id) {
    return this.tagService.deleteTag(id);
  }
}
