package it.unisannio.studenti.qualitag.service;

import it.unisannio.studenti.qualitag.dto.artifact.ArtifactCreationDto;
import it.unisannio.studenti.qualitag.mapper.ArtifactMapper;
import it.unisannio.studenti.qualitag.repository.ArtifactRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ArtifactService {

  private static final int LOG_ROUNDS = 12;

  private final ArtifactRepository artifactRepository;
  private final ArtifactMapper artifactMapper;

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private final Validator validator = factory.getValidator();

  /**
   * Constructs a new ArtifactService
   * @param artifactRepository the artifact repository
   */
  public ArtifactService(ArtifactRepository artifactRepository) {
    this.artifactRepository = artifactRepository;
    this.artifactMapper = new ArtifactMapper(this);
  }

  //DTO validation
  public boolean isValidArtifactCreation(ArtifactCreationDto artifactCreationDto) {
    Set<ConstraintViolation<ArtifactCreationDto>> violations = validator.validate(
        artifactCreationDto);

    return violations.isEmpty();
  }

  /**
   * Creates a new artifact
   *
   * @param artifactCreationDto the artifact creation data
   * @return the response entity with the result of the artifact creation
   */
  public ResponseEntity<?> createArtifact(ArtifactCreationDto artifactCreationDto) {
    // Check if the artifact data is valid
    if (!isValidArtifactCreation(artifactCreationDto)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid artifact data");
    }

    // TODO: Add the artifact to the repository
    return ResponseEntity.status(HttpStatus.OK).body("Artifact created successfully");
  }

  /**
   * Gets all the artifacts
   *
   * @return A response with all the artifacts
   */
  public ResponseEntity<?> getAllArtifacts() {
    return ResponseEntity.status(HttpStatus.OK).body(artifactRepository.findAll());
  }

}
