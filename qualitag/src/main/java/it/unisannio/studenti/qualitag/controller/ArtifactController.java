package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.dto.artifact.ArtifactCreateDto;
import it.unisannio.studenti.qualitag.service.ArtifactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/artifacts")
public class ArtifactController {

  private final ArtifactService artifactService;

  /**
   * Constructs a new ArtifactController
   *
   * @param artifactService the artifact service
   */
  @Autowired
  public ArtifactController(ArtifactService artifactService) {
    this.artifactService = artifactService;
  }


//POST
  /**
   * Adds an artifact to the repository
   *
   * @param artifactCreateDto the artifact to add to the repository
   */
  @PostMapping("/add")
  public ResponseEntity<?> createArtifact(@RequestBody ArtifactCreateDto artifactCreateDto) {
    return this.artifactService.addArtifact(artifactCreateDto);
  }

//GET
  /**
   * Gets all the artifacts
   *
   * @return the response entity
   */
  @GetMapping("/get")
  public ResponseEntity<?> getAllArtifacts() {
    return artifactService.getAllArtifacts();
  }

  //TODO see other ways to get the artifacts

//DELETE
  /**
   * Deletes an artifact from the repository
   *
   * @param artifactId the id of the artifact to delete
   */
  @DeleteMapping("/delete/{artifactId}")
  public ResponseEntity<?> deleteArtifact(@PathVariable String artifactId) {
    return this.artifactService.deleteArtifact(artifactId);
  }

}
