package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.model.Tag;
import it.unisannio.studenti.qualitag.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // This means that this class is a Controller
public class TagController {
    @Autowired
    TagRepository tagRepository;

    @PostMapping("/addTag")
    public void addTag(@RequestBody Tag tag) {
        if (tag.getColorHex() == null || tag.getColorHex().isEmpty()) {
            tag.setTagValue("");
        }
        System.out.println(tag);
        tagRepository.save(tag);
    }

    @GetMapping("/getAllTags")
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
        return tagRepository.findTagsByUserId(userId);
    }

}
