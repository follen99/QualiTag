package it.unisannio.studenti.qualitag.service;

import it.unisannio.studenti.qualitag.dto.project.ProjectCreateDto;
import it.unisannio.studenti.qualitag.exception.ProjectValidationException;
import it.unisannio.studenti.qualitag.exception.TeamValidationException;
import it.unisannio.studenti.qualitag.mapper.ProjectMapper;
import it.unisannio.studenti.qualitag.model.Project;
import it.unisannio.studenti.qualitag.repository.ArtifactRepository;
import it.unisannio.studenti.qualitag.repository.ProjectRepository;
import it.unisannio.studenti.qualitag.repository.TeamRepository;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * the ProjectService class is a service class that provides methods to manage the project entity
 */
@Service
public class ProjectService {

  private static final int LOG_ROUNDS = 12;

  private final ProjectRepository projectRepository;
  private final ProjectMapper projectMapper;
  private final UserRepository userRepository;
  private final TeamRepository teamRepository;
  private final ArtifactRepository artifactRepository;


  /**
   * Constructs a new ProjectService
   *
   * @param projectRepository the project repository
   * @param userRepository    the user repository
   */
  public ProjectService(ProjectRepository projectRepository,
      UserRepository userRepository, TeamRepository teamRepository,
      ArtifactRepository artifactRepository) {
    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
    this.teamRepository = teamRepository;
    this.artifactRepository = artifactRepository;
    this.projectMapper = new ProjectMapper(this);
  }

  /**
   * Creates a new project
   *
   * @param projectCreateDto the project creation data
   * @return the response entity with the result of the project creation
   */
  public ResponseEntity<?> createProject(ProjectCreateDto projectCreateDto) {
    //Project validation
    try {
      ProjectCreateDto correctProjectDto = validateProject(projectCreateDto);


      Project project = projectMapper.toEntity(correctProjectDto);
      this.projectRepository.save(project);
      return ResponseEntity.status(HttpStatus.CREATED).body("Project created successfully");
    } catch (ProjectValidationException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  //GET
  public ResponseEntity<?> getAllProjects() {
    return ResponseEntity.status(HttpStatus.OK).body(projectRepository.findAll());
  }

  //TODO it should probably return a list, assuming a user can create
  public ResponseEntity<?> getProjectsByOwner(String ownerId) {
    if (ownerId == null || ownerId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Owner ID cannot be null or empty");
    }
    if (!userRepository.existsById(ownerId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Owner not found");
    }
    if (!projectRepository.existsByOwnerId(ownerId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Project with owner " + ownerId + " not found");
    }
    return ResponseEntity.status(HttpStatus.OK)
        .body(projectRepository.findProjectsByOwnerId(ownerId));
  }

  public ResponseEntity<?> getProjectById(String projectId) {
    if (projectId == null || projectId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Project ID cannot be null or empty");
    }
    if (!projectRepository.existsById(projectId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
    }
    return ResponseEntity.status(HttpStatus.OK).body(projectRepository.findProjectByProjectId(projectId));
  }

  //DELETE
  public ResponseEntity<?> deleteProject(String projectId) {
    if (projectId == null || projectId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Project ID cannot be null or empty");
    }
    if (!projectRepository.existsById(projectId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
    }
    projectRepository.deleteById(projectId);
    if (projectRepository.existsById(projectId)) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Project not deleted");
    }

    return ResponseEntity.status(HttpStatus.OK).body("Project deleted successfully");
  }

  //UPDATE
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
      project.setUserIds(correctProjectDto.userIds());
      project.setTeamIds(correctProjectDto.teamIds());
      project.setArtifactIds(correctProjectDto.artifactIds());
      //TODO checks if other parameters should be changed

      this.projectRepository.save(project);

      return ResponseEntity.status(HttpStatus.OK).body("Project updated successfully");

    } catch (ProjectValidationException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }


  /**
   * Validates a project.
   *
   * @param projectCreateDto the project to validate
   * @return the validated project
   */
  private ProjectCreateDto validateProject(ProjectCreateDto projectCreateDto) {
    if (projectCreateDto == null) {
      throw new ProjectValidationException("Project cannot be null");
    }
    System.out.println("DTO IS " + projectCreateDto);

    String name = projectCreateDto.projectName();
    String description = projectCreateDto.projectDescription();
//    Date creationDate = projectCreateDto.creationDate();
//    Date deadlineDate = projectCreateDto.deadlineDate();
    Long creationDate = projectCreateDto.creationDate();
    Long deadlineDate = projectCreateDto.deadlineDate();
    List<String> users = projectCreateDto.userIds();
    List<String> teams = projectCreateDto.teamIds();
    List<String> artifacts = projectCreateDto.artifactIds();
    String owner = projectCreateDto.ownerId();

    LocalDate localDate = LocalDate.of(2030, 12, 31);
    Date maxDeadline = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

    //Validate the project name
    if (name == null || name.isEmpty()) {
      throw new ProjectValidationException("Project name cannot be null or empty");
    }
    if (name.contains(" ")) {
      throw new ProjectValidationException("Project name cannot contain whitespaces");
    }

    //Validate the project description
    if (description == null || description.isEmpty()) {
      throw new ProjectValidationException("Project description cannot be null or empty");
    }

    //Validate the creation date
    if (creationDate == null) {
      throw new ProjectValidationException("Creation date cannot be null");
    }
    if (creationDate < 0) {
      throw new ProjectValidationException("Creation date cannot be negative");
    }
    if (creationDate > System.currentTimeMillis()) {
      throw new ProjectValidationException("Creation date cannot be in the future");
    }
    if (creationDate > deadlineDate) {
      throw new ProjectValidationException("Creation date cannot be after the deadline date");
    }




    //Validate the users
    if (users == null || users.isEmpty()) {
      throw new ProjectValidationException("Users cannot be null or empty");
    }

    for (String currentUserId : users) {
      if (currentUserId == null || currentUserId.trim().isEmpty()) {
        throw new TeamValidationException("There is an empty user in the list. Remove it.");
      }
      currentUserId = currentUserId.trim(); // Remove leading and trailing whitespaces
      if (!userRepository.existsById(currentUserId)) {
        throw new TeamValidationException("User with ID " + currentUserId + " does not exist");
      }
    }

    //TODO check other constraints with users

    //Validate the teams
    if (teams == null || teams.isEmpty()) {
      throw new ProjectValidationException("Teams cannot be null or empty");
    }

    for (String currentTeamId : teams) {
      if (currentTeamId == null || currentTeamId.trim().isEmpty()) {
        throw new TeamValidationException("There is an empty team in the list. Remove it.");
      }
      currentTeamId = currentTeamId.trim(); // Remove leading and trailing whitespaces
      if (!teamRepository.existsById(currentTeamId)) {
        throw new TeamValidationException("Team with ID " + currentTeamId + " does not exist");
      }
    }

    //TODO check other constraints with teams

    //check the artifacts
    if (artifacts == null || artifacts.isEmpty()) {
      throw new ProjectValidationException("Artifacts cannot be null or empty");
    }

    for (String currentArtifactId : artifacts) {
      if (currentArtifactId == null || currentArtifactId.trim().isEmpty()) {
        throw new TeamValidationException("There is an empty artifact in the list. Remove it.");
      }
      currentArtifactId = currentArtifactId.trim(); // Remove leading and trailing whitespaces

      // POSSIBLE BUG
      boolean exists = false;
      try {
        exists = artifactRepository.existsById(currentArtifactId);
      } catch (Exception e) {
        throw new ProjectValidationException("Artifact with ID " + currentArtifactId + " does not exist");
      }
      if (!exists) {
        throw new ProjectValidationException("Artifact with ID " + currentArtifactId + " does not exist");
      }

    }

    //TODO check other constraints with artifacts

    //Validate the owner
    if (owner == null || owner.isEmpty()) {
      throw new ProjectValidationException("Owner cannot be null or empty");
    }
    if (!users.contains(owner)) {
      throw new ProjectValidationException("Owner must be a user in the project");
    }


    return new ProjectCreateDto(name, description, creationDate, deadlineDate,
        users, teams, artifacts, owner);
  }
}


