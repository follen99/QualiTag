package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.dto.tag.TagCreateDto;
import it.unisannio.studenti.qualitag.model.Tag;
import it.unisannio.studenti.qualitag.repository.TagRepository;
import it.unisannio.studenti.qualitag.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController                 // This means that this class is a Controller
@RequestMapping("/api/v1")   // This means URL's start with /api/v1 (after Application path)
public class TagController {
    private final TagService tagService;

    public TagController(TagService service) {
        this.tagService = service;
    }

    @PostMapping("/addTag")
    public ResponseEntity<?> addTag(@RequestBody TagCreateDto tagCreateDto) {
        return this.tagService.addTag(tagCreateDto);
    }

    @GetMapping("/getAllTags")
    public ResponseEntity<?> getAllTags() {
        return this.tagService.getAllTags();
    }

    /*@GetMapping("/getAllTags")
    public Iterable<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @DeleteMapping("/deleteTag/{id}")
    public void deleteTag(@PathVariable String id) {
        tagRepository.deleteById(id);
    }

    @DeleteMapping("/deleteTags")
    public void deleteTags(@RequestBody List<String> ids) {
        ids.forEach(id -> tagRepository.deleteById(id));
    }

    @GetMapping("/getTagsByUserId/{userId}")
    public List<Tag> getTagsByUserId(@PathVariable String userId) {
        return tagRepository.findTagsByCreatedBy(userId);
    }*/

}
