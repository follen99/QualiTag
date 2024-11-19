package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.model.Tag;
import it.unisannio.studenti.qualitag.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController // This means that this class is a Controller
public class TagController {
  @Autowired
  TagRepository  tagRepository;

  @PostMapping("/addTag")
  public void addTag(@RequestBody Tag tag) {
    tagRepository.save(tag);
  }
}
