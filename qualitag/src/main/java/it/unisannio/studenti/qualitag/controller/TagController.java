package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.dto.tag.TagCreateDto;
import it.unisannio.studenti.qualitag.service.TagService;
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
@RestController                 // This means that this class is a Controller
@RequestMapping("/api/v1/tag")   // This means URL's start with /api/v1 (after Application path)
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
   * Adds a tag.
   *
   * @param tagCreateDto The tag to add.
   * @return The response entity.
   */
  @PostMapping("/add")
  public ResponseEntity<?> addTag(@RequestBody TagCreateDto tagCreateDto) {
    return this.tagService.addTag(tagCreateDto);
  }

  /**
   * Gets all tags.
   *
   * @return The response entity.
   */
  @GetMapping("/get")
  public ResponseEntity<?> getAllTags() {
    return this.tagService.getAllTags();
  }

  /**
   * Get tags by the username of their creator.
   *
   * @param username The username of creator of the tags.
   * @return The response entity.
   */
  @GetMapping("/get/createdby/username/{username}")
  public ResponseEntity<?> getTagsByCreatedByUsername(@PathVariable String username) {
    return this.tagService.getTagsByCreatedByUsername(username);
  }

  /**
   * Gets tags by their value.
   *
   * @param value The value of the tags to get.
   * @return The response entity.
   */
  @GetMapping("/get/value/{value}")
  public ResponseEntity<?> getTagsByValue(@PathVariable String value) {
    return this.tagService.getTagsByValue(value);
  }

  /**
   * Update tag given its id.
   *
   * @param id The id of the tag to update.
   * @return The response entity.
   */
  @PutMapping("/update/{id}")
  public ResponseEntity<?> updateTag(@RequestBody TagCreateDto tagCreateDto,
      @PathVariable String id) {
    return this.tagService.updateTag(tagCreateDto, id);
  }

  /**
   * Deletes a tag by its id.
   *
   * @param id The id of the tag to delete.
   * @return The response entity.
   */
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> deleteTag(@PathVariable String id) {
    return this.tagService.deleteTag(id);
  }
}
