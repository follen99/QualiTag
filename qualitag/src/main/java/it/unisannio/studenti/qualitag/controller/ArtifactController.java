package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.dto.artifact.ArtifactCreationDto;
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

  /**
   * Adds an artifact to the repository
   * @param artifactCreationDto the artifact to add to the repository
   *
   */
  @PostMapping("/createNewArtifact")
  public ResponseEntity<?> createArtifact(@RequestBody ArtifactCreationDto artifactCreationDto) {
    return artifactService.createArtifact(artifactCreationDto);
  }

  /**
   * Gets all the artifacts
   * @return the response entity
   */
  @GetMapping("/get")
  public ResponseEntity<?> getAllArtifacts() {
    return artifactService.getAllArtifacts();
  }


}
