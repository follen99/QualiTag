package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.dto.tag.TagCreateDto;
import it.unisannio.studenti.qualitag.dto.user.UserModifyDto;
import it.unisannio.studenti.qualitag.model.Tag;
import it.unisannio.studenti.qualitag.repository.TagRepository;
import it.unisannio.studenti.qualitag.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//TODO change mappings to lowercase and mattia's style

@RestController                 // This means that this class is a Controller
@RequestMapping("/api/v1/tag")   // This means URL's start with /api/v1 (after Application path)
public class TagController {
    private final TagService tagService;

    public TagController(TagService service) {
        this.tagService = service;
    }

    /**
     * #######################################################################
     *                              POST MAPPING
     * #######################################################################
     */

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
     * #######################################################################
     *                              GET MAPPING
     * #######################################################################
     */

    /**
     * Gets all tags.
     *
     * @return The response entity.
     */
    @GetMapping("/get")
    public ResponseEntity<?> getAllTags() {
        return this.tagService.getAllTags();
    }

    @GetMapping("/get/createdby/{createdBy}")
    public ResponseEntity<?> getTagsByCreatedBy(@PathVariable String createdBy) {
        return this.tagService.getTagsByCreatedBy(createdBy);
    }

    @GetMapping("/get/value/{value}")
    public ResponseEntity<?> getTagsByValue(@PathVariable String value) {
        return this.tagService.getTagsByValue(value);
    }

    /**
     * #######################################################################
     *                              UPDATE MAPPING
     * #######################################################################
     */

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTag(@RequestBody TagCreateDto tagCreateDto, @PathVariable String id){
        return this.tagService.updateTag(tagCreateDto, id);
    }

    /**
     * #######################################################################
     *                              DELETE MAPPING
     * #######################################################################
     */

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
