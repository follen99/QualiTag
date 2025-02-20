package it.unisannio.studenti.qualitag.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unisannio.studenti.qualitag.dto.artifact.AddTagsToArtifactDto;
import it.unisannio.studenti.qualitag.dto.artifact.ArtifactCreateDto;
import it.unisannio.studenti.qualitag.dto.artifact.WholeArtifactDto;
import it.unisannio.studenti.qualitag.dto.tag.TagCreateDto;
import it.unisannio.studenti.qualitag.dto.tag.TagResponseDto;
import it.unisannio.studenti.qualitag.mapper.ArtifactMapper;
import it.unisannio.studenti.qualitag.mapper.TagMapper;
import it.unisannio.studenti.qualitag.model.Artifact;
import it.unisannio.studenti.qualitag.model.Project;
import it.unisannio.studenti.qualitag.model.Tag;
import it.unisannio.studenti.qualitag.model.Team;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.ArtifactRepository;
import it.unisannio.studenti.qualitag.repository.ProjectRepository;
import it.unisannio.studenti.qualitag.repository.TagRepository;
import it.unisannio.studenti.qualitag.repository.TeamRepository;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import it.unisannio.studenti.qualitag.security.model.CustomUserDetails;
import it.unisannio.studenti.qualitag.security.service.AuthenticationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * The service class for the artifact.
 */
@Service
@RequiredArgsConstructor
public class ArtifactService {

  private static final String UPLOAD_DIR = "artifacts/";
  private final ArtifactRepository artifactRepository;
  private final ProjectRepository projectRepository;
  private final TagRepository tagRepository;
  private final TeamRepository teamRepository;
  private final UserRepository userRepository;

  private final PythonClientService pythonClientService;
  private final TagService tagService;

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private final Validator validator = factory.getValidator();

  // POST

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
      Set<ConstraintViolation<ArtifactCreateDto>> violations = validator.validate(
          artifactCreateDto);
      if (!violations.isEmpty()) {
        response.put("msg", "Invalid artifact data.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }

      // Check if the project exists
      Project project = projectRepository.findProjectByProjectId(artifactCreateDto.projectId());
      if (project == null) {
        response.put("msg", "Project not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      // Check if the logged-in user is the owner of the project
      User user = userRepository.findByUserId(project.getOwnerId());
      if (AuthenticationService.getAuthority(user.getUsername())) {
        response.put("msg", "User is not the project owner.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }

      // Save the file to the server's file system
      String filePath = saveFile(artifactCreateDto.file());

      // Convert the DTO to an entity
      Artifact artifact = ArtifactMapper.toEntity(artifactCreateDto);
      artifact.setFilePath(filePath);
      artifact.setTaggingOpen(true);

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
      artifact.setTeamId(team.getTeamId());

      // Save the artifact to the database
      artifactRepository.save(artifact);

      // Add the artifact to the project
      project.getArtifactIds().add(artifact.getArtifactId());
      projectRepository.save(project);

      // Add the artifact to the team
      team.getArtifactIds().add(artifact.getArtifactId());
      teamRepository.save(team);
    } catch (IOException e) {
      response.put("msg", "File upload failed.");
      response.put("error_message", e.getMessage());
      response.put("error", e);
      return ResponseEntity.status(500).body(response);
    } catch (Exception e) {
      e.printStackTrace();
      response.put("msg", "An error occurred.");
      return ResponseEntity.status(500).body(response);
    }

    response.put("msg", "Artifact created successfully.");
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // GET

  /**
   * Retrieves the file of the artifact given its ID.
   *
   * @param artifactId the ID of the artifact to retrieve
   * @return the response entity with the file of the artifact
   */
  public ResponseEntity<?> getArtifact(String artifactId) {
    Map<String, Object> response = new HashMap<>();

    // Retrieve the artifact data
    Artifact artifact = artifactRepository.findArtifactByArtifactId(artifactId);
    if (artifact == null) {
      response.put("msg", "Artifact not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Check if the user is authorized to view the artifact
    User user = userRepository.findByUserId(getLoggedInUserId());
    Project project = projectRepository.findProjectByProjectId(artifact.getProjectId());
    if (user == null) {
      response.put("msg", "User not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    if (!(project.getOwnerId().equals(user.getUserId()) || user.getTeamIds()
        .contains(artifact.getTeamId()))) {
      response.put("msg", "User is not authorized to view this artifact.");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // Check if the file exists
    Path filePath = Paths.get(artifact.getFilePath());
    if (!Files.exists(filePath)) {
      response.put("msg", "File not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Retrieve and return the file
    try {
      Resource file = new FileSystemResource(filePath);
      String contentType = Files.probeContentType(filePath);

      // Fallback if it cannot determine the content type
      if (contentType == null) {
        contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
      }

      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_DISPOSITION,
          "attachment; filename=" + file.getFilename() + "\"");
      headers.setContentType(MediaType.parseMediaType(contentType));
      headers.setContentLength(Files.size(filePath));

      // return new ResponseEntity<>(file, headers, HttpStatus.OK);
      return ResponseEntity.ok().headers(headers).body(file);
    } catch (IOException e) {
      // Log the exception properly
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Retrieves the metadata on an artifact given its ID.
   *
   * @param artifactId the ID of the artifact to retrieve
   * @return the response entity with the metadata of the artifact
   */
  public ResponseEntity<?> getArtifactMetadata(String artifactId) {
    Map<String, Object> response = new HashMap<>();

    // Retrieve the artifact
    Artifact artifact = artifactRepository.findArtifactByArtifactId(artifactId);
    if (artifact == null) {
      response.put("msg", "Artifact not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Check if the user is authorized to view the artifact
    User user = userRepository.findByUserId(getLoggedInUserId());
    Project project = projectRepository.findProjectByProjectId(artifact.getProjectId());
    if (user == null) {
      response.put("msg", "User not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    if (!(project.getOwnerId().equals(user.getUserId()) || user.getTeamIds()
        .contains(artifact.getTeamId()))) {
      response.put("msg", "User is not authorized to view this artifact.");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // Convert the entity to a DTO
    WholeArtifactDto artifactDto = ArtifactMapper.toWholeArtifactDto(artifact);

    response.put("msg", "Artifact metadata retrieved successfully.");
    response.put("artifact", artifactDto);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // PUT

  // TODO: Properly implement update method

  /**
   * Modifies an existing artifact.
   *
   * @param artifactModifyDto the dto used to modify the artifact
   * @param artifactId        the id of the artifact to modify
   * @return the response entity
   */
  public ResponseEntity<?> updateArtifact(ArtifactCreateDto artifactModifyDto, String artifactId) {
    Map<String, Object> response = new HashMap<>();
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

    response.put("msg", "Method not implemented yet.");
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(response);
  }

  /**
   * Adds a tag to an artifact.
   *
   * @param dto the DTO containing the artifact id and the list of tag ids to add
   * @return the response entity
   */
  public ResponseEntity<?> addTags(String artifactId, AddTagsToArtifactDto dto) {
    Map<String, Object> response = new HashMap<>();

    // Check artifact first
    if (artifactId == null || artifactId.isEmpty()) {
      response.put("msg", "Artifact id is null or empty.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    // Retrieve the artifact
    Artifact artifact = artifactRepository.findArtifactByArtifactId(artifactId);
    if (artifact == null) {
      response.put("msg", "Artifact not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Get the logged in userId
    String loggedInUserId = getLoggedInUserId();

    // retrieve the project
    Project project = projectRepository.findProjectByProjectId(artifact.getProjectId());
    if (project == null) {
      response.put("msg", "Project not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // if the user is not the owner of the project, check if tagging is open
    // if the user is the owner of the project, skip this check
    if (!project.getOwnerId().equals(loggedInUserId)) {
      // if tagging is not open, return forbidden
      if (!artifact.isTaggingOpen()) {
        response.put("msg", "Tagging is not open for this artifact.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }
    }

    // Validate the DTO
    Set<ConstraintViolation<AddTagsToArtifactDto>> violations = validator.validate(dto);
    if (!violations.isEmpty()) {
      response.put("msg", "Invalid data.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Check tags
    for (String tagId : dto.tagIds()) {
      // Check if the tag id is null or empty
      if (tagId == null || tagId.isEmpty()) {
        response.put("msg", "Tag id is null or empty.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }

      // Retrieve the tag
      Tag tag = tagRepository.findTagByTagId(tagId);
      if (tag == null) {
        response.put("msg", "Tag not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      // Check if the tag is already in the artifact
      if (artifact.getTags().contains(tagId)) {
        response.put("msg", "Tag already exists in artifact.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }

      // Check on the user adding the tag
      User user = userRepository.findByUserId(getLoggedInUserId());
      Team team = teamRepository.findTeamByTeamId(artifact.getTeamId());

      if (!(project.getOwnerId().equals(user.getUserId()) || (
          team.getUserIds().contains(user.getUserId()) && tag.getCreatedBy()
              .equals(user.getUserId())))) {
        if (!project.getOwnerId().equals(user.getUserId())) {
          response.put("msg", "User is not the project owner.");
          return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } else {
          response.put("msg", "User is not authorized to add this tag to the artifact.");
          return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
      }
    }

    // If all checks pass, add the tags to the artifact
    for (String tagId : dto.tagIds()) {
      Tag tag = tagRepository.findTagByTagId(tagId);
      tag.getArtifactIds().add(artifact.getArtifactId());
      tagRepository.save(tag);

      artifact.getTags().add(tagId);
      artifactRepository.save(artifact);
    }

    response.put("msg", "Tags added successfully.");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * Processes the tags of an artifact.
   *
   * @param artifactId the ID of the artifact to process the tags
   * @return the response entity with the result of the tag processing
   */
  public ResponseEntity<?> processTags(String artifactId) {
    Map<String, Object> response = new HashMap<>();

    // Retrieve the artifact
    Artifact artifact = artifactRepository.findArtifactByArtifactId(artifactId);
    if (artifact == null) {
      response.put("msg", "Artifact not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Check if the user is authorized to view the artifact
    User user = userRepository.findByUserId(getLoggedInUserId());
    Project project = projectRepository.findProjectByProjectId(artifact.getProjectId());
    if (user == null) {
      response.put("msg", "User not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    if (!project.getOwnerId().equals(user.getUserId())) {
      response.put("msg", "User is not authorized to view this artifact.");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // Get the value of all the tags
    List<String> tagValues = new ArrayList<>();
    for (String tagId : artifact.getTags()) {
      Tag tag = tagRepository.findTagByTagId(tagId);
      tagValues.add(tag.getTagValue());
    }

    // Remove tags from artifact
    for (String tagId : artifact.getTags()) {
      ResponseEntity<?> removeTagResponse = removeTag(artifactId, tagId);
      if (removeTagResponse.getStatusCode() != HttpStatus.OK) {
        response.put("msg", "Error removing tags from artifact.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
      }
    }

    // Call the Python service to process the tags
    String processedTags = pythonClientService.processTags(tagValues);
    if (processedTags == null) {
      response.put("msg", "Error processing tags.");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Parse the processed tags and put them in a list
    List<String> processedTagsList = new ArrayList<>();
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonNode = objectMapper.readTree(processedTags);

      JsonNode resultNode = jsonNode.get("result");

      if (resultNode.isArray()) {
        for (JsonNode element : resultNode) { // Iterate through the array
          processedTagsList.add(element.asText()); // Add each element as a string to the list
        }
      } else {
        response.put("msg", "Error: 'result' is not an array.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
      }
    } catch (JsonProcessingException e) {
      response.put("msg", "Error processing JSON.");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(response + e.getMessage());
    }

    // Create tags from the processed tags and add them
    List<String> tagIds = new ArrayList<>();
    for (String tagValue : processedTagsList) {
      ResponseEntity<?> responseRequest = tagService.createTag(
          new TagCreateDto(tagValue, this.getLoggedInUserId(), "#000000"));

      if (responseRequest.getStatusCode() != HttpStatus.CREATED
          && responseRequest.getStatusCode() != HttpStatus.CONFLICT) {
        response.put("msg", "Error creating tags from processed tags.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
      }

      // Insert tag ID in tagIds
      Map<String, String> map = new HashMap<>();
      String responseBody = responseRequest.getBody().toString();

      // Remove curly braces and split into key-value pairs
      String[] pairs = responseBody.substring(1, responseBody.length() - 1).split(",");

      for (String pair : pairs) {
        String[] keyValue = pair.split("=");
        if (keyValue.length == 2) { // Handle cases where a key might not have a value
          map.put(keyValue[0].trim(), keyValue[1].trim()); // Trim whitespace
        } else if (keyValue.length == 1) {
          map.put(keyValue[0].trim(), ""); // Handle missing values
        }
      }

      tagIds.add(map.get("tagId"));
    }

    // Add tags to artifact
    this.addTags(artifactId, new AddTagsToArtifactDto(tagIds));

    response.put("msg", "Tags processed successfully.");
    response.put("processedTags", processedTagsList);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // DELETE

  /**
   * Deletes an artifact by its id.
   *
   * @param id the id of the artifact to delete
   * @return The response entity
   */
  public ResponseEntity<?> deleteArtifact(String id) {
    Map<String, Object> response = new HashMap<>();

    // Check if the id is null or empty
    if (id == null || id.isEmpty()) {
      response.put("msg", "Artifact id is null or empty.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Check if the artifact exists and retrieve it
    Artifact artifact = artifactRepository.findArtifactByArtifactId(id);
    if (artifact == null) {
      response.put("msg", "Artifact not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Retrieve the project the artifact belongs to
    Project project = projectRepository.findProjectByProjectId(artifact.getProjectId());
    if (project == null) {
      response.put("msg", "Project not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Check if the project owner is the one deleting the artifact
    if (!project.getOwnerId().equals(getLoggedInUserId())) {
      response.put("msg", "User is not the project owner.");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // Retrieve the team the artifact belongs to
    Team team = teamRepository.findTeamByTeamId(artifact.getTeamId());
    if (team == null) {
      response.put("msg", "Team not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Remove the artifact from the project
    project.getArtifactIds().remove(artifact.getArtifactId());
    projectRepository.save(project);

    // Remove the artifact from the team
    team.getArtifactIds().remove(artifact.getArtifactId());
    teamRepository.save(team);

    // Remove the artifact from the tags
    for (String tagId : artifact.getTags()) {
      Tag tag = tagRepository.findTagByTagId(tagId);
      tag.getArtifactIds().remove(artifact.getArtifactId());
      tagRepository.save(tag);
    }

    // Delete the artifact from the database
    artifactRepository.deleteArtifactByArtifactId(id);

    // Delete the file from the system
    try {
      Path filePath = Paths.get(artifact.getFilePath());
      Files.delete(filePath);
    } catch (IOException e) {
      response.put("msg", "File deletion failed.");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Check if the artifact was deleted
    if (!artifactRepository.existsById(id)) {
      response.put("msg", "Artifact deleted successfully.");
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } else {
      response.put("msg", "Artifact not deleted.");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  /**
   * Removes a tag of an artifact.
   *
   * @param artifactId the id of the artifact which tag we want to remove
   * @param tagId      the id of the tag to remove
   * @return the response entity
   */
  public ResponseEntity<?> removeTag(String artifactId, String tagId) {
    Map<String, Object> response = new HashMap<>();

    // Check if the IDs are null or empty
    if (artifactId == null || artifactId.isEmpty()) {
      response.put("msg", "Artifact id is null or empty.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    if (tagId == null || tagId.isEmpty()) {
      response.put("msg", "Tag id is null or empty.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Retrieve the artifact
    Artifact artifact = artifactRepository.findArtifactByArtifactId(artifactId);
    if (artifact == null) {
      response.put("msg", "Artifact not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Check if the user is allowed to remove the tag (project owner or tag creator)
    User user = userRepository.findByUserId(getLoggedInUserId());
    Project project = projectRepository.findProjectByProjectId(artifact.getProjectId());
    Team team = teamRepository.findTeamByTeamId(artifact.getTeamId());
    Tag tag = tagRepository.findTagByTagId(tagId);
    if (!(project.getOwnerId().equals(user.getUserId()) || (
        team.getUserIds().contains(user.getUserId()) && tag.getCreatedBy()
            .equals(user.getUserId())))) {
      response.put("msg", "User is not authorized to remove this tag from the artifact.");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // Check if the tag is in the artifact
    List<String> tagIds = artifact.getTags();
    if (!tagIds.contains(tagId)) {
      response.put("msg", "Tag not found in artifact.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Remove the artifact from the tag
    tag.getArtifactIds().remove(artifactId);
    tagRepository.save(tag);

    // Remove the tag from the artifact
    artifact.getTags().remove(tagId);
    artifactRepository.save(artifact);

    response.put("msg", "Tag removed successfully.");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // UTILITY METHODS

  private String getLoggedInUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new IllegalStateException("No authenticated user found.");
    }

    Object principal = authentication.getPrincipal();
    if (principal instanceof CustomUserDetails(User user)) {
      return user.getUserId();
    }
    throw new IllegalStateException(
        "Unexpected authentication principal type: " + principal.getClass() + ".");
  }

  private String saveFile(MultipartFile file) throws IOException {
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
   * Retrieves the tags created by a user for a specific artifact.
   *
   * @param artifactId              the id of the artifact
   * @param userIdOrEmailOrUsername the id, email, or username of the user
   * @return the response entity
   */
  public ResponseEntity<?> getTagsByUser(String artifactId, String userIdOrEmailOrUsername) {
    Map<String, Object> response = new HashMap<>();
    User user = null;

    if (userIdOrEmailOrUsername.length() != 24) {
      user = userRepository.findByUsername(userIdOrEmailOrUsername);
    } else {
      if (userIdOrEmailOrUsername.contains("@")) {
        user = userRepository.findByEmail(userIdOrEmailOrUsername);
      } else {
        user = userRepository.findByUserId(userIdOrEmailOrUsername);
      }
    }

    if (user == null) {
      response.put("msg", "User not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Retrieve the artifact
    Artifact artifact = artifactRepository.findArtifactByArtifactId(artifactId);
    if (artifact == null) {
      response.put("msg", "Artifact not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Check if the user is authorized to view the artifact
    Project project = projectRepository.findProjectByProjectId(artifact.getProjectId());
    if (!(project.getOwnerId().equals(user.getUserId()) || user.getTeamIds()
        .contains(artifact.getTeamId()))) {
      response.put("msg", "User is not authorized to view this artifact.");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    List<String> tagIds = artifact.getTags();
    List<Tag> tags = new ArrayList<>();

    for (String tagId : tagIds) {
      Tag tag = tagRepository.findTagByTagId(tagId);
      if (tag.getCreatedBy().equals(user.getUserId())) {
        tags.add(tag);
      }
    }

    response.put("msg", "Tags retrieved successfully.");
    response.put("tags", tags);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * Retrieves all the tags of an artifact.
   *
   * @param artifactId the id of the artifact
   * @return the response entity
   */
  public ResponseEntity<?> getAllTags(String artifactId) {
    Map<String, Object> response = new HashMap<>();
    if (artifactId == null || artifactId.isEmpty()) {
      response.put("msg", "Artifact id is null or empty.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    Artifact artifact = artifactRepository.findArtifactByArtifactId(artifactId);
    if (artifact == null) {
      response.put("msg", "Artifact not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    List<String> tagIds = artifact.getTags();
    List<TagResponseDto> tags = new ArrayList<>();
    for (String tagId : tagIds) {
      Tag tag = tagRepository.findTagByTagId(tagId);
      if (tag == null) {
        response.put("msg", "Tag not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }
      User user = userRepository.findByUserId(tag.getCreatedBy());
      if (user == null) {
        response.put("msg", "User not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      // swapping userId with username for convenience
      tag.setCreatedBy(user.getUsername());

      TagMapper tagMapper = new TagMapper();
      tags.add(tagMapper.getResponseDto(tag));
    }

    response.put("msg", "Tags retrieved successfully.");
    response.put("tags", tags);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * Starts the tagging process for an artifact.
   *
   * @param artifactId the id of the artifact to start tagging
   * @return the response entity
   */
  public ResponseEntity<?> startTagging(String artifactId) {
    Map<String, Object> response = new HashMap<>();

    if (artifactId == null || artifactId.isEmpty()) {
      response.put("msg", "Artifact id is null or empty.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    Artifact artifact = artifactRepository.findArtifactByArtifactId(artifactId);
    if (artifact == null) {
      response.put("msg", "Artifact not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    artifact.setTaggingOpen(true);

    artifactRepository.save(artifact);

    response.put("msg", "Tagging started successfully.");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * Starts the tagging process for a list of artifacts.
   *
   * @param artifactIds the list of artifact ids to start tagging
   * @return the response entity
   */
  public ResponseEntity<?> startTagging(List<String> artifactIds) {
    Map<String, Object> response = new HashMap<>();

    if (artifactIds == null || artifactIds.isEmpty()) {
      response.put("msg", "Artifact id is null or empty.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    for (String id : artifactIds) {
      Artifact artifact = artifactRepository.findArtifactByArtifactId(id);
      if (artifact == null) {
        response.put("msg", "Artifact not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      artifact.setTaggingOpen(true);

      artifactRepository.save(artifact);
    }

    response.put("msg", "Tagging started successfully for all artifacts.");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * Stops the tagging process for an artifact.
   *
   * @param artifactId the id of the artifact to stop tagging
   * @return the response entity
   */
  public ResponseEntity<?> stopTagging(String artifactId) {
    Map<String, Object> response = new HashMap<>();

    if (artifactId == null || artifactId.isEmpty()) {
      response.put("msg", "Artifact id is null or empty.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    Artifact artifact = artifactRepository.findArtifactByArtifactId(artifactId);
    if (artifact == null) {
      response.put("msg", "Artifact not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    artifact.setTaggingOpen(false);

    artifactRepository.save(artifact);

    response.put("msg", "Tagging stopped successfully.");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * Stops the tagging process for a list of artifacts.
   *
   * @param artifactIds the list of artifact ids to stop tagging
   * @return the response entity
   */
  public ResponseEntity<?> stopTagging(List<String> artifactIds) {
    Map<String, Object> response = new HashMap<>();

    if (artifactIds == null || artifactIds.isEmpty()) {
      response.put("msg", "Artifact id is null or empty.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    for (String id : artifactIds) {
      Artifact artifact = artifactRepository.findArtifactByArtifactId(id);
      if (artifact == null) {
        response.put("msg", "Artifact not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      artifact.setTaggingOpen(false);

      artifactRepository.save(artifact);
    }

    response.put("msg", "Tagging stopped successfully for all artifacts.");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }


}
