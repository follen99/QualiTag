package it.unisannio.studenti.qualitag.service;

import it.unisannio.studenti.qualitag.dto.artifact.ArtifactCreateDto;
import it.unisannio.studenti.qualitag.mapper.ArtifactMapper;
import it.unisannio.studenti.qualitag.model.Artifact;
import it.unisannio.studenti.qualitag.model.Project;
import it.unisannio.studenti.qualitag.model.Team;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.ArtifactRepository;
import it.unisannio.studenti.qualitag.repository.ProjectRepository;
import it.unisannio.studenti.qualitag.repository.TeamRepository;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import it.unisannio.studenti.qualitag.security.service.AuthenticationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * The service class for the artifact.
 */
@Service
@RequiredArgsConstructor
public class ArtifactService {

  private final ArtifactRepository artifactRepository;
  private final ProjectRepository projectRepository;
  private final TeamRepository teamRepository;
  private final UserRepository userRepository;

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private final Validator validator = factory.getValidator();

  private static final String UPLOAD_DIR = "artifacts/";

  /**
   * Saves the provided multipart file to the server's file system.
   *
   * @param file the multipart file to be saved
   * @return the path to the saved file as a string, or null if an error occurs
   */
  public String saveFile(MultipartFile file) throws IOException {
    // Generate a unique file name by appending a UUID to the original file name.
    String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

    // Construct the path where the file will be saved using the upload directory
    // and the unique file name.
    Path path = Paths.get(System.getProperty("user.dir"), "..", UPLOAD_DIR, uniqueFileName);

    // Create any necessary directories in the path if they do not already exist.
    Files.createDirectories(path.getParent());

    // Transfer the contents of the multipart file to the target file on the file system.
    file.transferTo(path.toFile());

    // Return the path to the saved file as a string.
    return path.toString();
  }

  /**
   * Creates a new artifact.
   *
   * @param artifactCreateDto the artifact creation data
   * @return the response entity with the result of the artifact creation
   */
  public ResponseEntity<?> addArtifact(ArtifactCreateDto artifactCreateDto) {
    Map<String, Object> response = new HashMap<>();

    try {
      // Validate the DTO
      Set<ConstraintViolation<ArtifactCreateDto>> violations =
          validator.validate(artifactCreateDto);
      if (!violations.isEmpty()) {
        response.put("msg", "Invalid artifact data");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }

      // Check if the project exists
      Project project = projectRepository.findProjectByProjectId(artifactCreateDto.projectId());
      if (project == null) {
        response.put("msg", "Project not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      // Check if the logged in user is the owner of the project
      User user = userRepository.findByUserId(project.getOwnerId());
      if (AuthenticationService.getAuthority(user.getUsername())) {
        response.put("msg", "User is not the project owner");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }

      // Save the file to the server's file system
      String filePath = saveFile(artifactCreateDto.file());

      // Convert the DTO to an entity
      Artifact artifact = ArtifactMapper.toEntity(artifactCreateDto);
      artifact.setFilePath(filePath);

      // Add the artifact to the project
      project.getArtifactIds().add(artifact.getArtifactId());
      projectRepository.save(project);

      // Find the team with the least artifacts
      String minTeamId = null;
      int minSize = Integer.MAX_VALUE;
      List<String> teamIds = project.getTeamIds();

      for (String teamId : teamIds) {
        Team team = teamRepository.findTeamByTeamId(teamId);
        List<String> artifactIds = team.getArtifactIds();

        if (artifactIds.size() < minSize) {
          minSize = artifactIds.size();
          minTeamId = teamId;
        }
      }

      // Add the artifact to the team with the least artifacts
      Team team = teamRepository.findTeamByTeamId(minTeamId);
      team.getArtifactIds().add(artifact.getArtifactId());
      teamRepository.save(team);
      artifact.setTeamId(team.getTeamId());

      // Save the artifact to the database
      artifactRepository.save(artifact);

    } catch (IOException e) {
      response.put("msg", "File upload failed");
      response.put("error_message", e.getMessage());
      response.put("error", e);
      return ResponseEntity.status(500).body(response);
    } catch (Exception e) {
      response.put("msg", "An error occurred");
      return ResponseEntity.status(500).body(response);
    }

    response.put("msg", "Artifact created successfully");
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Adds a tag to an artifact.
   *
   * @param artifactId the id of the artifact to add the tag
   * @param tagId the id of the tag to add
   * @return the response entity
   */
  public ResponseEntity<?> addTag(String artifactId, String tagId) {
    if (artifactId == null || artifactId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Artifact id is null or empty");
    }
    if (tagId == null || tagId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tag id is null or empty");
    }

    Artifact artifact = artifactRepository.findArtifactByArtifactId(artifactId);
    if (artifact == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artifact not found");
    }

    List<String> tagIds = artifact.getTags();
    if (tagIds.contains(tagId)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tag already exists in artifact");
    }

    tagIds.add(tagId);
    artifact.setTags(tagIds);
    artifactRepository.save(artifact);

    return ResponseEntity.status(HttpStatus.OK).body("Tag added successfully");
  }

  /**
   * Gets all the artifacts.
   *
   * @return The response entity
   */
  public ResponseEntity<?> getAllArtifacts() {
    return ResponseEntity.status(HttpStatus.OK).body(artifactRepository.findAll());
  }

  /**
   * Deletes an artifact by its id.
   *
   * @param id the id of the artifact to delete
   * @return The response entity
   */
  public ResponseEntity<?> deleteArtifact(String id) {
    if (id == null || id.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Artifact id id null");
    }
    if (!artifactRepository.existsById(id)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artifact not found");
    }

    artifactRepository.deleteById(id);
    if (!artifactRepository.existsById(id)) {
      return ResponseEntity.status(HttpStatus.OK).body("Artifact deleted successfully");
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Artifact not deleted");
    }
  }

  /**
   * Deletes a tag of an artifact.
   *
   * @param artifactId the id of the artifact which tag we want to delete
   * @param tagId the id of the tag to delete
   * @return the response entity
   */
  public ResponseEntity<?> deleteTag(String artifactId, String tagId) {
    if (artifactId == null || artifactId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Artifact id is null or empty");
    }
    if (tagId == null || tagId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tag id is null or empty");
    }

    Artifact artifact = artifactRepository.findArtifactByArtifactId(artifactId);
    if (artifact == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artifact not found");
    }

    List<String> tagIds = artifact.getTags();
    if (tagIds == null || tagIds.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artifact has no tags");
    }

    if (!tagIds.contains(tagId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tag not found in artifact");
    }

    tagIds.remove(tagId);
    artifact.setTags(tagIds);
    artifactRepository.save(artifact);

    return ResponseEntity.status(HttpStatus.OK).body("Tag deleted successfully");
  }

  // TODO: Properly implement update method
  /**
   * Modifies an existing artifact.
   *
   * @param artifactModifyDto the dto used to modify the artifact
   * @param artifactId the id of the artifact to modify
   * @return the response entity
   */
  public ResponseEntity<?> updateArtifact(ArtifactCreateDto artifactModifyDto, String artifactId) {
    /*
     * // ID check if (artifactId == null || artifactId.isEmpty()) { return
     * ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Artifact id is null or empty"); }
     * 
     * // Artifact validation try { ArtifactCreateDto correctDto =
     * validateArtifact(artifactModifyDto);
     * 
     * Artifact artifact = artifactRepository.findArtifactByArtifactId(artifactId); if (artifact ==
     * null) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artifact not found"); }
     * 
     * artifact.setArtifactName(correctDto.artifactName());
     * artifact.setContent(correctDto.content()); artifact.setTags(correctDto.tags());
     * 
     * artifactRepository.save(artifact);
     * 
     * return ResponseEntity.status(HttpStatus.OK).body("Artifact updated successfully");
     * 
     * } catch (ArtifactValidationException e) { return
     * ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); }
     */

    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Method not implemented yet");
  }
}
