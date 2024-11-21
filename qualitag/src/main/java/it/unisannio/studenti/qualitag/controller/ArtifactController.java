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

  /**
   * Adds an artifact to the repository
   * @param artifact the artifact to add
   */
  @PostMapping("/addArtifact")
  public void addArtifact(@RequestBody Artifact artifact) {
    System.out.println(artifact.toString());
    artifactRepository.save(artifact);
  }

  /**
   * returns all the artifact in the repository
   */
  @GetMapping("/getAllArtifacts")
  public void getAllArtifacts() {
    artifactRepository.findAll();
  }

  /**
   * Returns an artifact with a specific id
   * @param id the id of the artifact to find
   * @return the artifact with the given id
   */
  @GetMapping("/getArtifactById/{id}")
  public Artifact getArtifactById(@PathVariable String id) {
    return artifactRepository.findArtifactByArtifactId(id);
  }

  /**
   * Deletes an artifact with a specific id from the repository
   * @param id the id of the artifact to delete
   */
  @DeleteMapping("/deleteArtifact/{id}")
  public void deleteArtifact(@PathVariable String id) {
    artifactRepository.deleteById(id);
  }

  /**
   * Deletes multiple artifacts from the repository
   * @param ids a list of the ids of the artifacts to delete
   */
  @DeleteMapping("/deleteArtifacts")
  public void deleteArtifacts(@RequestBody List<String> ids) {
    ids.forEach(id -> artifactRepository.deleteById(id));
  }

}
