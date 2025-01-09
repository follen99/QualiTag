package it.unisannio.studenti.qualitag.service;

import it.unisannio.studenti.qualitag.constants.ProjectConstants;
import it.unisannio.studenti.qualitag.dto.project.CompletedProjectCreationDto;
import it.unisannio.studenti.qualitag.dto.project.ProjectCreateDto;
import it.unisannio.studenti.qualitag.dto.team.TeamCreateDto;
import it.unisannio.studenti.qualitag.exception.ProjectValidationException;
import it.unisannio.studenti.qualitag.mapper.ProjectMapper;
import it.unisannio.studenti.qualitag.model.Artifact;
import it.unisannio.studenti.qualitag.model.Project;
import it.unisannio.studenti.qualitag.model.ProjectStatus;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
  private final TeamService teamService;
  private final UserRepository userRepository;

  /**
   * Constructs a new ProjectService.
   *
   * @param projectRepository the project repository
   * @param usersRepository   the user repository
   */
  public ProjectService(ProjectRepository projectRepository, UserRepository usersRepository,
      TeamRepository teamsRepository, ArtifactRepository artifactsRepository,
      TeamService teamService, UserRepository userRepository) {
    this.projectRepository = projectRepository;
    this.usersRepository = usersRepository;
    this.teamsRepository = teamsRepository;
    this.artifactsRepository = artifactsRepository;
    this.projectMapper = new ProjectMapper(this);
    this.teamService = teamService;
    this.userRepository = userRepository;
  }

  // POST

  /**
   * Creates a new project.
   *
   * @param projectCreateDto the DTO used to create a project
   * @return the response entity
   */
  @Transactional
  public ResponseEntity<?> createProject(ProjectCreateDto projectCreateDto) {
    Map<String, Object> response = new HashMap<>();

    //Project validation
    try {
      CompletedProjectCreationDto correctProjectDto = validateProject(projectCreateDto);

      Project project = projectMapper.toEntity(correctProjectDto);

      // Save project to get ID
      this.projectRepository.save(project);

      // Create a default team for the project
      TeamCreateDto teamCreateDto = new TeamCreateDto("Default team",
          "Default team for project " + project.getProjectName(), project.getUsers());
      ResponseEntity<?> teamResponse = teamService.addTeam(teamCreateDto, project.getProjectId());
      if (teamResponse.getStatusCode() != HttpStatus.CREATED) {
        // If there's a problem, rollback the project creation
        this.deleteProject(project.getProjectId());
        return teamResponse;
      }

      // Add the project to the users
      try {
        this.addProjectsToUsers(project);
      } catch (ProjectValidationException e) {
        // If there's a problem, rollback the project creation
        this.deleteProject(project.getProjectId());

        response.put("msg", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }

      response.put("msg", "Project created successfully");
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (ProjectValidationException e) {
      response.put("msg", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // TODO: Add roles to users
  private void addProjectsToUsers(Project project) throws Exception {
    User owner = usersRepository.findByUserId(project.getOwnerId());
    if (owner == null) {
      throw new ProjectValidationException("User with ID " + project.getOwnerId() + " not found");
    }

    // Add project to owner
    owner.getProjectIds().add(project.getProjectId());
    userRepository.save(owner);

    // Add project to users
    List<String> userList = project.getUsers();
    for (String userId : userList) {
      User user = usersRepository.findByUserId(userId);

      // Debugging
      // System.out.println("User: " + user);
      // System.out.println("User ID: " + userId);

      // TODO add a list of users that were not found, then throw an exception returning the list
      if (user == null) {
        throw new ProjectValidationException("User with ID " + userId + " not found");
      }

      // Add the project to the user
      user.getProjectIds().add(project.getProjectId());
      userRepository.save(user);

      // Send email to user
      String emailMessage = String.format(
          """
              Dear %s,
              
              You have been invited to join the project: %s.
              
              Project Description: %s
              
              We look forward to your valuable contributions.
              
              Best regards,
              %s
              """,
          user.getUsername(), project.getProjectName(), project.getProjectDescription(),
          owner.getName() + " " + owner.getSurname()
      );
      new GmailService().sendMail("Project Invitation", user.getEmail(), emailMessage);
    }
  }

  // TODO: Probably have to rewrite this method
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

    if (project.getProjectStatus() == ProjectStatus.CLOSED) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Project is closed. Cannot add artifact");
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

  /**
   * Close a project.
   *
   * @param projectId the id of the project to close
   */
  public ResponseEntity<?> closeProject(String projectId) {
    Map<String, Object> response = new HashMap<>();

    if (projectId == null || projectId.isEmpty()) {
      response.put("msg", "Project ID cannot be null or empty");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    Project project = projectRepository.findProjectByProjectId(projectId);
    if (project == null) {
      response.put("msg", "Project not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    if (project.getProjectStatus() == ProjectStatus.CLOSED) {
      response.put("msg", "Project is already closed");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    String currentUserId = getLoggedInUserId();
    if (!project.getOwnerId().equals(currentUserId)) {
      response.put("msg", "Only the project owner can close the project");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    project.setProjectStatus(ProjectStatus.CLOSED);
    projectRepository.save(project);

    response.put("msg", "Project closed successfully");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // GET

  // TODO: Add some protection or delete the method
  /**
   * Return all the projects in the database.
   *
   * @return the response entity
   */
  public ResponseEntity<?> getAllProjects() {
    return ResponseEntity.status(HttpStatus.OK).body(projectRepository.findAll());
  }

  // TODO: It might be possible to implement this method using the role of the user
  /**
   * Returns all the projects created by a specific owner.
   *
   * @param ownerId The id of the owner
   * @return the response entity
   */
  public ResponseEntity<?> getProjectsByOwner(String ownerId) {
    Map<String, Object> response = new HashMap<>();

    // Check if there's a problem with the owner ID
    if (ownerId == null || ownerId.isEmpty()) {
      response.put("msg", "Owner ID cannot be null or empty");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    if (!usersRepository.existsById(ownerId)) {
      response.put("msg", "Owner not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Retrieve the projects of the owner. If none, return message
    List<Project> projects = projectRepository.findProjectsByOwnerId(ownerId);
    if (projects == null || projects.isEmpty()) {
      response.put("msg", "No projects found for the owner");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    response.put("msg", "Projects found successfully");
    response.put("projects", projects);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * Searches for a project with a specific id.
   *
   * @param projectId the id of the project to search
   * @return the response entity
   */
  public ResponseEntity<?> getProjectById(String projectId) {
    Map<String, Object> response = new HashMap<>();

    // Check if there's a problem with the project ID
    if (projectId == null || projectId.isEmpty()) {
      response.put("msg", "Project ID cannot be null or empty");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    if (!projectRepository.existsById(projectId)) {
      response.put("msg", "Project not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Retrieve the project and return it
    response.put("msg", "Project found successfully");
    response.put("project", projectRepository.findProjectByProjectId(projectId));
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // TODO: Probably move this method to tag service and fix response to be a map
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

  // TODO: Probably move this method to artifact service and fix response to be a map
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
    Map<String, Object> response = new HashMap<>();

    // Check if there's a problem with the project ID
    if (projectId == null || projectId.isEmpty()) {
      response.put("msg", "Project ID cannot be null or empty");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    if (!projectRepository.existsById(projectId)) {
      response.put("msg", "Project not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    Project projectToDelete = projectRepository.findProjectByProjectId(projectId);

    // Check if the logged user is the owner of the project
    String currentUserId = getLoggedInUserId();
    if (!currentUserId.equals(projectToDelete.getOwnerId())) {
      response.put("msg", "Only the owner can delete the project!");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // TODO: Eventually delete the roles of the users
    // Delete links to the project from owner
    User owner = usersRepository.findByUserId(currentUserId);
    owner.getProjectIds().remove(projectId);

    // Delete links to the project from users
    List<String> userIds = projectToDelete.getUsers();
    for (String userId : userIds) {
      User user = usersRepository.findByUserId(userId);
      user.getProjectIds().remove(projectId);
    }

    // TODO: Delete tags using the proper service

    // Delete teams using the proper service
    List<String> teamIds = projectToDelete.getTeams();
    for (String teamId : teamIds) {
      teamsRepository.deleteById(teamId);
    }

    // Delete artifacts using the proper service
    List<String> artifactIds = projectToDelete.getArtifacts();
    for (String artifactId : artifactIds) {
      artifactsRepository.deleteById(artifactId);
    }

    projectRepository.deleteById(projectId);
    // FIXME: Is this check necessary?
    if (projectRepository.existsById(projectId)) {
      response.put("msg", "Project not deleted");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    response.put("msg", "Project deleted successfully");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // UPDATE

  // TODO: Implement this method
  /**
   * Modifies an existing projects.
   *
   * @param projectModifyDto the DTO used to modify the project
   * @param projectId        the id of the project to modify
   * @return the response entity
   */
  public ResponseEntity<?> updateProject(ProjectCreateDto projectModifyDto, String projectId) {
    /*
    //id check
    if (projectId == null || projectId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Project id is null or empty");
    }

    //project validation
    try {
      ProjectCreateDto correctProjectDto = validateProject(projectModifyDto);

      Project project = projectRepository.findProjectByProjectId(projectId);
      if (project == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
      }

      project.setProjectName(correctProjectDto.projectName());
      project.setProjectDescription(correctProjectDto.projectDescription());
      project.setProjectDeadline(correctProjectDto.deadlineDate());
      project.setUsers(correctProjectDto.users());
      project.setTeams(correctProjectDto.teams());
      project.setArtifacts(correctProjectDto.artifacts());
      //TODO checks if other parameters should be changed

      this.projectRepository.save(project);

      return ResponseEntity.status(HttpStatus.OK).body("Project updated successfully");

    } catch (ProjectValidationException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

     */

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
  private CompletedProjectCreationDto validateProject(ProjectCreateDto projectCreateDto) {
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
      throw new ProjectValidationException(
          "Deadline date cannot be after " + ProjectConstants.MAX_PROJECT_DEADLINE_YEARS
              + " years from now");
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

    // Team validation removed. One team is created by default.

    // Artifact validation removed. No artifacts when creating a project.

    return new CompletedProjectCreationDto(
        name,
        projectCreateDto.projectDescription(),
        creationDate.toInstant().toEpochMilli(),
        deadlineDate.toInstant().toEpochMilli(),
        ownerId,
        userIds);
  }
}