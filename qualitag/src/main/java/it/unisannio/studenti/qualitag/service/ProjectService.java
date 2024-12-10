package it.unisannio.studenti.qualitag.service;

import it.unisannio.studenti.qualitag.dto.project.ProjectCreateDto;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
  private final WebInvocationPrivilegeEvaluator privilegeEvaluator;

  /**
   * Constructs a new ProjectService.
   *
   * @param projectRepository the project repository
   * @param usersRepository   the user repository
   */
  public ProjectService(ProjectRepository projectRepository, UserRepository usersRepository,
      TeamRepository teamsRepository, ArtifactRepository artifactsRepository,
      WebInvocationPrivilegeEvaluator privilegeEvaluator) {
    this.projectRepository = projectRepository;
    this.usersRepository = usersRepository;
    this.teamsRepository = teamsRepository;
    this.artifactsRepository = artifactsRepository;
    this.projectMapper = new ProjectMapper(this);
    this.privilegeEvaluator = privilegeEvaluator;
  }

  // POST

  /**
   * Creates a new project.
   *
   * @param projectCreateDto the DTO used to create a project
   * @return the response entity
   */
  public ResponseEntity<?> createProject(ProjectCreateDto projectCreateDto) {
    //Project validation
    try {
      ProjectCreateDto correctProjectDto = validateProject(projectCreateDto);

      Project project = projectMapper.toEntity(correctProjectDto);
      this.projectRepository.save(project);
      this.addProjectsToUsers(project);
      return ResponseEntity.status(HttpStatus.CREATED).body("Project created successfully");
    } catch (ProjectValidationException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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
    if (principal instanceof CustomUserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) principal;
      return userDetails.user().getUserId();
    }
    throw new IllegalStateException(
        "Unexpected authentication principal type: " + principal.getClass());
  }

  /**
   * Validates a project.
   *
   * @param projectCreateDto the dto used to create the project
   * @return the validated project
   */
  private ProjectCreateDto validateProject(ProjectCreateDto projectCreateDto) {
    if (projectCreateDto == null) {
      throw new ProjectValidationException("Project cannot be null");
    }

    //Validate the project name
    String name = projectCreateDto.projectName();
    if (name == null || name.isEmpty()) {
      throw new ProjectValidationException("Project name cannot be null or empty");
    }
    if (name.contains(" ")) {
      throw new ProjectValidationException("Project name cannot contain whitespaces");
    }
    if (projectRepository.existsByProjectName(name)) {
      throw new ProjectValidationException("Project with name " + name + " already exists");
    }

    //Validate the project description
    String description = projectCreateDto.projectDescription();
    if (description == null || description.isEmpty()) {
      throw new ProjectValidationException("Project description cannot be null or empty");
    }

    //Validate the deadline date
    Long deadlineDate = projectCreateDto.deadlineDate();
    Long creationDate = System.currentTimeMillis();

    LocalDate localDate = LocalDate.of(2030, 12, 31);
    Long maxDeadline = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    if (deadlineDate == null) {
      throw new ProjectValidationException("Deadline date cannot be null");
    }
    if (deadlineDate > creationDate) {
      throw new ProjectValidationException("Deadline date cannot be before the creation date");
    }
    if (deadlineDate > maxDeadline) {
      throw new ProjectValidationException("Deadline date cannot be after 2030-12-31");
    }

    //Validate the users
    List<String> users = projectCreateDto.users();
    if (users == null || users.isEmpty()) {
      throw new ProjectValidationException("Users cannot be null or empty");
    }

    for (String currentUserId : users) {
      if (currentUserId == null || currentUserId.trim().isEmpty()) {
        throw new TeamValidationException("There is an empty user in the list. Remove it.");
      }
      currentUserId = currentUserId.trim(); // Remove leading and trailing whitespaces
      try {
        if (!usersRepository.existsById(currentUserId)) {
          throw new TeamValidationException("User with ID " + currentUserId + " does not exist");
        }
      } catch (Exception e) {
        if (!usersRepository.existsByUsername(currentUserId)) {
          throw new TeamValidationException(
              "User with username " + currentUserId + " does not exist");
        }
      }

    }
    //TODO check other constraints with users

    //Validate the teams
    List<String> teams = projectCreateDto.teams();
    if (teams == null || teams.isEmpty()) {
      throw new ProjectValidationException("Teams cannot be null or empty");
    }

    for (String currentTeamId : teams) {
      if (currentTeamId == null || currentTeamId.trim().isEmpty()) {
        throw new TeamValidationException("There is an empty team in the list. Remove it.");
      }
      currentTeamId = currentTeamId.trim(); // Remove leading and trailing whitespaces
      if (!teamsRepository.existsById(currentTeamId)) {
        throw new TeamValidationException("Team with ID " + currentTeamId + " does not exist");
      }
    }

    //TODO check other constraints with teams
    //check the artifacts
    List<String> artifacts = projectCreateDto.artifacts();
    if (artifacts == null || artifacts.isEmpty()) {
      throw new ProjectValidationException("Artifacts cannot be null or empty");
    }

    for (String currentArtifactId : artifacts) {
      if (currentArtifactId == null || currentArtifactId.trim().isEmpty()) {
        throw new TeamValidationException("There is an empty artifact in the list. Remove it.");
      }
      currentArtifactId = currentArtifactId.trim(); // Remove leading and trailing whitespaces

      //POSSIBILE BUG
      boolean exist = false;
      try {
        exist = artifactsRepository.existsById(currentArtifactId);
      } catch (Exception e) {
        throw new ProjectValidationException(
            "Artifact with ID " + currentArtifactId + " does not exist");
      }
      if (!exist) {
        throw new ProjectValidationException(
            "Artifact with ID " + currentArtifactId + " does not exist");
      }
    }
    //TODO check other constraints with artifacts

    //Validate the owner
    String owner = getLoggedInUserId();
    if (owner == null || owner.isEmpty()) {
      throw new ProjectValidationException("Owner cannot be null or empty");
    }
    User ownerUser = usersRepository.findByUsername(owner);
    if (ownerUser == null) {
      ownerUser = usersRepository.findById(owner).orElse(null);
      if (ownerUser == null) {
        throw new ProjectValidationException("Owner with ID " + owner + " does not exist");
      }
    }

    if (!users.contains(ownerUser.getUserId()) && !users.contains(ownerUser.getUsername())) {
      throw new ProjectValidationException("Owner must be in the list of users");
    }

    return new ProjectCreateDto(name, description, creationDate, deadlineDate, users, teams,
        artifacts, owner);
  }
}




