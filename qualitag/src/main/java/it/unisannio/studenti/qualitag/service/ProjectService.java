package it.unisannio.studenti.qualitag.service;

import it.unisannio.studenti.qualitag.constants.ProjectConstants;
import it.unisannio.studenti.qualitag.dto.project.CompletedProjectCreationDto;
import it.unisannio.studenti.qualitag.dto.project.ProjectCreateDto;
import it.unisannio.studenti.qualitag.dto.user.UserModifyDto;
import it.unisannio.studenti.qualitag.exception.ProjectValidationException;
import it.unisannio.studenti.qualitag.exception.TeamValidationException;
import it.unisannio.studenti.qualitag.mapper.ProjectMapper;
import it.unisannio.studenti.qualitag.model.Artifact;
import it.unisannio.studenti.qualitag.model.Project;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.ArtifactRepository;
import it.unisannio.studenti.qualitag.repository.ProjectRepository;
import it.unisannio.studenti.qualitag.repository.TeamRepository;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import it.unisannio.studenti.qualitag.security.model.CustomUserDetails;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator;
import org.springframework.stereotype.Service;


/**
 * The ProjectService class is a service class that provides methods to manage the project entity.
 */
@Service
public class ProjectService {

  private final ProjectRepository projectRepository;
  private final ProjectMapper projectMapper;
  private final UserRepository usersRepository;
  private final TeamRepository teamsRepository;
  private final ArtifactRepository artifactsRepository;

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private final Validator validator = factory.getValidator();

  /**
   * Constructs a new ProjectService.
   *
   * @param projectRepository the project repository
   * @param usersRepository   the user repository
   */
  public ProjectService(ProjectRepository projectRepository, UserRepository usersRepository,
      TeamRepository teamsRepository, ArtifactRepository artifactsRepository) {
    this.projectRepository = projectRepository;
    this.usersRepository = usersRepository;
    this.teamsRepository = teamsRepository;
    this.artifactsRepository = artifactsRepository;
    this.projectMapper = new ProjectMapper(this);
  }

  // POST

  /**
   * Creates a new project.
   *
   * @param projectCreateDto the DTO used to create a project
   * @return the response entity
   */
  public ResponseEntity<?> createProject(ProjectCreateDto projectCreateDto) {
    Map<String, Object> response = new HashMap<>();

    //Project validation
    try {
      CompletedProjectCreationDto correctProjectDto = validateProject(projectCreateDto);

      Project project = projectMapper.toEntity(correctProjectDto);

      // TODO: create new team and add it to the project

      this.projectRepository.save(project);
      this.addProjectsToUsers(project);
      return ResponseEntity.status(HttpStatus.CREATED).body("Project created successfully");
    } catch (ProjectValidationException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void addProjectsToUsers(Project project) {
    List<String> userList = project.getUsers();    // Get the list of user IDs

    for (String userId : userList) {
      User user = usersRepository.findByUsername(userId);
      System.out.println("User: " + user);
      System.out.println("User ID: " + userId);

      // if user is not found, try to find it by id
      // TODO add a list of users that were not found, then throw an exception returning the list
      // TODO check if a user is mentioned more than once (both by username and by id!!!)
      if (user == null) {
        System.out.println("ENTERING");
        Optional<User> optionalUser = usersRepository.findById(userId);
        if (optionalUser.isPresent()) {

          User currentUser = optionalUser.get();
          System.out.println("Current user: " + currentUser);
          List<String> oldProjectIds = currentUser.getProjectIds();
          oldProjectIds.add(project.getProjectId());

          currentUser.setProjectIds(oldProjectIds);
          usersRepository.save(currentUser);
        } else {
          /*if we cannot find user even by id, return
           * even a single user is not found, the project is not added to any user
           * */
          return;
        }
      } else {
        List<String> oldProjectIds = user.getProjectIds();
        oldProjectIds.add(project.getProjectId());

        user.setProjectIds(oldProjectIds);
        usersRepository.save(user);
      }
    }
  }

  /**
   * Adds an artifact to a project.
   *
   * @param projectId  the id of the project
   * @param artifactId the id of the artifact
   * @return the response entity
   */
  public ResponseEntity<?> addArtifact(String projectId, String artifactId) {
    if (projectId == null || projectId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Project id is null or empty");
    }

    Project project = projectRepository.findProjectByProjectId(projectId);
    if (project == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
    }

    String currentUserId = getLoggedInUserId();
    if (!project.getOwnerId().equals(currentUserId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Only the project owner can add a new artifact!");
    }

    if (artifactId == null || artifactId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Artifact ID cannot be null or empty");
    }
    Artifact artifact = artifactsRepository.findArtifactByArtifactId(artifactId);
    if (artifact == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artifact not found");
    }

    List<String> artifacts = project.getArtifacts();
    artifacts.add(artifactId);
    project.setArtifacts(artifacts);

    projectRepository.save(project);

    return ResponseEntity.status(HttpStatus.OK).body("Artifact added to the project successfully");
  }

  // GET

  /**
   * Return all the projects in the database.
   *
   * @return the response entity
   */
  public ResponseEntity<?> getAllProjects() {
    return ResponseEntity.status(HttpStatus.OK).body(projectRepository.findAll());
  }

  /**
   * Returns all the projects created by a specific owner.
   *
   * @param ownerId The id of the owner
   * @return the response entity
   */
  public ResponseEntity<?> getProjectsByOwner(String ownerId) {
    if (ownerId == null || ownerId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Owner ID cannot be null or empty");
    }
    if (!usersRepository.existsById(ownerId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Owner not found");
    }
    if (!projectRepository.existsByOwnerId(ownerId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Project with owner " + ownerId + " not found");
    }
    return ResponseEntity.status(HttpStatus.OK)
        .body(projectRepository.findProjectsByOwnerId(ownerId));
  }

  /**
   * Searches for a project with a specific id.
   *
   * @param projectId the id of the project to search
   * @return the response entity
   */
  public ResponseEntity<?> getProjectById(String projectId) {
    if (projectId == null || projectId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Project ID cannot be null or empty");
    }
    if (!projectRepository.existsById(projectId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
    }
    return ResponseEntity.status(HttpStatus.OK)
        .body(projectRepository.findProjectByProjectId(projectId));
  }

  /**
   * Searches for the tags of the artifacts of a project.
   *
   * @param projectId the id of the project
   * @return the list of the tags associated to the artifacts of the project
   */
  public ResponseEntity<?> getProjectsTags(String projectId) {

    if (projectId == null || projectId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project ID cannot be null or empty");
    }

    Project project = projectRepository.findProjectByProjectId(projectId);
    if (project == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
    }

    // Retrieve the logged-in user's ID
    String currentUserId = getLoggedInUserId();
    if (!project.getOwnerId().equals(currentUserId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Only the project owner can see the tags!");
    }

    //retrieve the project's artifacts
    List<String> artifactIds = project.getArtifacts();
    if (artifactIds == null || artifactIds.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No artifacts found for the project");
    }

    //retrive the tags of the artifacts
    List<String> tags = new ArrayList<>();
    for (String artifactId : artifactIds) {
      Artifact artifact = artifactsRepository.findArtifactByArtifactId(artifactId);
      if (artifact == null) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Artifact with ID " + artifactId + " not found");
      }
      tags.addAll(artifact.getTags());
    }

    return ResponseEntity.status(HttpStatus.OK).body(tags);
  }

  /**
   * Gets all the artifacts of a project.
   *
   * @param projectId the id of the project
   * @return the response entity
   */
  public ResponseEntity<?> getProjectsArtifacts(String projectId) {
    if (projectId == null || projectId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Project id is null or empty");
    }

    Project project = projectRepository.findProjectByProjectId(projectId);
    if (project == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
    }

    List<String> artifactIds = project.getArtifacts();
    if (artifactIds == null || artifactIds.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No artifacts found for the project");
    }

    List<Artifact> artifacts = new ArrayList<>();
    for (String artifactId : artifactIds) {
      Artifact artifact = artifactsRepository.findArtifactByArtifactId(artifactId);
      if (artifact == null) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Artifact with ID " + artifactId + " not found");
      }
      artifacts.add(artifact);
    }

    return ResponseEntity.status(HttpStatus.OK).body(artifacts);
  }

  // DELETE

  /**
   * Deletes a project.
   *
   * @param projectId the id of the project to delete
   * @return the response entity
   */
  public ResponseEntity<?> deleteProject(String projectId) {
    if (projectId == null || projectId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Project ID cannot be null or empty");
    }
    if (!projectRepository.existsById(projectId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
    }

    String currentUserId = getLoggedInUserId();

    if (!currentUserId.equals(projectRepository.findProjectByProjectId(projectId).getOwnerId())) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Only the owner can delete the project!");
    }

    projectRepository.deleteById(projectId);
    if (projectRepository.existsById(projectId)) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Project not deleted");
    }

    return ResponseEntity.status(HttpStatus.OK).body("Project deleted successfully");
  }

  // UPDATE

  /**
   * Modifies an existing projects.
   *
   * @param projectModifyDto the DTO used to modify the project
   * @param projectId        the id of the project to modify
   * @return the response entity
   */
  public ResponseEntity<?> updateProject(ProjectCreateDto projectModifyDto, String projectId) {
//    //id check
//    if (projectId == null || projectId.isEmpty()) {
//      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Project id is null or empty");
//    }
//
//    //project validation
//    try {
//      ProjectCreateDto correctProjectDto = validateProject(projectModifyDto);
//
//      Project project = projectRepository.findProjectByProjectId(projectId);
//      if (project == null) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
//      }
//
//      project.setProjectName(correctProjectDto.projectName());
//      project.setProjectDescription(correctProjectDto.projectDescription());
//      project.setProjectDeadline(correctProjectDto.deadlineDate());
//      project.setUsers(correctProjectDto.users());
//      project.setTeams(correctProjectDto.teams());
//      project.setArtifacts(correctProjectDto.artifacts());
//      //TODO checks if other parameters should be changed
//
//      this.projectRepository.save(project);
//
//      return ResponseEntity.status(HttpStatus.OK).body("Project updated successfully");
//
//    } catch (ProjectValidationException e) {
//      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//    }

    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Method not implemented yet");
  }

  //UTILITY METHODS

  /**
   * Private method that returns the ID of the logged-in user.
   *
   * @return the ID of the logged-in user
   */
  private String getLoggedInUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new IllegalStateException("No authenticated user found");
    }

    Object principal = authentication.getPrincipal();
    if (principal instanceof CustomUserDetails(User user)) {
      return user.getUserId();
    }
    throw new IllegalStateException(
        "Unexpected authentication principal type: " + principal.getClass());
  }

  private boolean isValidProjectCreateDto(ProjectCreateDto dto) {
    Set<ConstraintViolation<ProjectCreateDto>> violations = validator.validate(dto);

    return violations.isEmpty();
  }

  /**
   * Validates a project.
   *
   * @param projectCreateDto the dto used to create the project
   * @return the validated project
   */
  private CompletedProjectCreationDto validateProject(ProjectCreateDto projectCreateDto)
      throws Exception {
    if (!isValidProjectCreateDto(projectCreateDto)) {
      throw new ProjectValidationException("All fields must be filled");
    }

    // Validate the project name
    String name = projectCreateDto.projectName();
    if (name.contains(" ")) {
      throw new ProjectValidationException("Project name cannot contain whitespaces");
    }
    if (projectRepository.existsByProjectName(name)) {
      throw new ProjectValidationException("Project with name " + name + " already exists");
    }

    // Validate the deadline date and set creation date
    ZonedDateTime deadlineDate = ZonedDateTime.parse(projectCreateDto.deadlineDate(),
        DateTimeFormatter.ISO_DATE_TIME);
    ZonedDateTime creationDate = ZonedDateTime.now();
    if (deadlineDate.isBefore(creationDate)) {
      throw new ProjectValidationException("Deadline date cannot be before the creation date");
    }

    ZonedDateTime maxDeadline = ZonedDateTime.now()
        .plusYears(ProjectConstants.MAX_PROJECT_DEADLINE_YEARS);
    if (deadlineDate.isAfter(maxDeadline)) {
      throw new ProjectValidationException("Deadline date cannot be after 2030-12-31");
    }

    // Validate the owner
    String ownerId = getLoggedInUserId();
    if (ownerId == null || ownerId.isEmpty()) {
      throw new ProjectValidationException("Owner cannot be null or empty");
    }

    User owner = usersRepository.findByUserId(ownerId);
    if (owner == null) {
      throw new ProjectValidationException("User with ID " + ownerId + " does not exist");
    }

    // Owner must not be part of users
    List<String> userEmails = projectCreateDto.userEmails();
    if (userEmails.contains(owner.getEmail())) {
      throw new ProjectValidationException("Owner must not be part of the list of users");
    }

    // Validate the user emails and retrieve userIds
    List<String> userIds = new ArrayList<>();
    for (String email : userEmails) {
      if (email == null) {
        throw new ProjectValidationException(
            "There is an empty email in the list. Please remove it.");
      }

      User user = usersRepository.findByEmail(email);
      if (user == null) {
        throw new ProjectValidationException("User with email " + email + " does not exist");
      }
      if (userEmails.indexOf(email) != userEmails.lastIndexOf(email)) {
        throw new ProjectValidationException(
            "User with email " + email + " is mentioned more than once");
      }
      userIds.add(user.getUserId());
    }

    // Team validation removed. The system will automatically create one team and more can be added.

    // Artifact validation removed. The project will start with no artifacts and will be added later.

    // TODO: move this for when adding users to project
    // Send email to the users
    for (String email : userEmails) {
      User user = usersRepository.findByEmail(email);
      String emailMessage = String.format(
          """
              Dear %s,
              
              You have been invited to join the project: %s.
              
              Project Description: %s
              
              We look forward to your valuable contributions.
              
              Best regards,
              The Team
              """,
          user.getUsername(), name, projectCreateDto.projectDescription()
      );
      new GmailService().sendMail("Project Invitation", email, emailMessage);
    }

    return new CompletedProjectCreationDto(
        name,
        projectCreateDto.projectDescription(),
        creationDate.toInstant().toEpochMilli(),
        deadlineDate.toInstant().toEpochMilli(),
        ownerId,
        userIds);
  }
}




