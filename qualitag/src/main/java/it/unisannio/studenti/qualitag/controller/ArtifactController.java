package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.model.Artifact;
import it.unisannio.studenti.qualitag.repository.ArtifactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ArtifactController {
  @Autowired
  ArtifactRepository artifactRepository;

  @PostMapping("/addArtifact")
  public void addArtifact(@RequestBody Artifact artifact) {
    System.out.println(artifact.toString());
    artifactRepository.save(artifact);
  }

  @GetMapping("/getAllArtifacts")
  public void getAllArtifacts() {
    artifactRepository.findAll();
  }

  @GetMapping("/getArtifactById/{id}")
  public Artifact getArtifactById(@PathVariable String id) {
    return artifactRepository.findArtifactByArtifactId(id);
  }

  @GetMapping("/getArtifactsByUserId/{userId}")
  public List<Artifact> getArtifactsByUserId(@PathVariable String userId) {
    return artifactRepository.findArtifactsByUserId(userId);
  }

  @DeleteMapping("/deleteArtifact/{id}")
  public void deleteArtifact(@PathVariable String id) {
    artifactRepository.deleteById(id);
  }

  @DeleteMapping("/deleteArtifacts")
  public void deleteArtifacts(@RequestBody List<String> ids) {
    ids.forEach(id -> artifactRepository.deleteById(id));
  }

}
