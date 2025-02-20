package it.unisannio.studenti.qualitag.service;

import com.google.api.client.auth.oauth2.TokenResponseException;
import it.unisannio.studenti.qualitag.constants.ProjectConstants;
import it.unisannio.studenti.qualitag.dto.artifact.WholeArtifactDto;
import it.unisannio.studenti.qualitag.dto.project.CompletedProjectCreationDto;
import it.unisannio.studenti.qualitag.dto.project.CompletedProjectUpdateDto;
import it.unisannio.studenti.qualitag.dto.project.ProjectCreateDto;
import it.unisannio.studenti.qualitag.dto.project.ProjectInfoDto;
import it.unisannio.studenti.qualitag.dto.project.WholeProjectDto;
import it.unisannio.studenti.qualitag.dto.project.WholeProjectHeavyDto;
import it.unisannio.studenti.qualitag.dto.team.TeamCreateDto;
import it.unisannio.studenti.qualitag.dto.team.WholeTeamDto;
import it.unisannio.studenti.qualitag.dto.user.UserResponseDto;
import it.unisannio.studenti.qualitag.dto.user.UserShortResponseDto;
import it.unisannio.studenti.qualitag.exception.ProjectValidationException;
import it.unisannio.studenti.qualitag.mapper.ArtifactMapper;
import it.unisannio.studenti.qualitag.mapper.ProjectMapper;
import it.unisannio.studenti.qualitag.mapper.TeamMapper;
import it.unisannio.studenti.qualitag.mapper.UserMapper;
import it.unisannio.studenti.qualitag.model.Artifact;
import it.unisannio.studenti.qualitag.model.Project;
import it.unisannio.studenti.qualitag.model.ProjectStatus;
import it.unisannio.studenti.qualitag.model.Team;
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
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ProjectService {

  private final ArtifactService artifactService;
  private final TeamService teamService;

  private final ArtifactRepository artifactsRepository;
  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;
  private final TeamRepository teamsRepository;

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private final Validator validator = factory.getValidator();

  // POST

  /**
   * Creates a new project.
   *
   * @param projectCreateDto the DTO used to create a project
   * @return the response entity
   */
  @Transactional
  public ResponseEntity<?> createProject(ProjectCreateDto projectCreateDto) {
    System.out.println("progetto: " + projectCreateDto);
    Map<String, Object> response = new HashMap<>();

    // Project validation
    Project project = null;
    try {
      CompletedProjectCreationDto correctProjectDto = validateProject(projectCreateDto);

      project = ProjectMapper.toEntity(correctProjectDto);

      // Save project to get ID
      projectRepository.save(project);

      // Create a default team for the project
      TeamCreateDto teamCreateDto = new TeamCreateDto("Default team",
          "Default team for project " + project.getProjectName(), project.getProjectId(),
          projectCreateDto.userEmails());
      ResponseEntity<?> teamResponse = teamService.addTeam(teamCreateDto);
      if (teamResponse.getStatusCode() != HttpStatus.CREATED) {
        // If there's a problem, rollback the project creation
        this.deleteProject(project.getProjectId());
        return teamResponse;
      }

      // Add the project to the users
      try {
        // se il token mail non funziona, questo lancia l'eccezione
        this.addProjectToUsers(project);
      } catch (ProjectValidationException e) {
        // If there's a problem, rollback the project creation
        this.deleteProject(project.getProjectId());

        response.put("msg", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }

      response.put("msg", "Project created successfully.");
      response.put("projectId", project.getProjectId());
      return ResponseEntity.status(HttpStatus.CREATED).body(response);

    } catch (ProjectValidationException e) {
      response.put("msg", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    } catch (Exception e) {
      if (e instanceof TokenResponseException) {
        TokenResponseException tokenException = (TokenResponseException) e;
        if (tokenException.getStatusCode() == 400
            && tokenException.getContent().contains("invalid_grant")
            && tokenException.getContent().contains("Token has been expired or revoked.")) {
          // Specific behavior for this exception
          response.put("msg", """
              Internal server error: mailing service is not working.
              
              But do not worry, the project was created successfully.
              Please notify the users manually.""");

          response.put("createdAnyway", true);
          response.put("projectId", project.getProjectId());
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } else {
          // Other behavior for different exceptions
          System.out.println("Different TokenResponseException occurred.");
          response.put("msg", "Internal server error: " + e.getMessage());
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
      }
      throw new RuntimeException(e);
    }
  }

  // GET

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
      response.put("msg", "Owner ID cannot be null or empty.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    if (!userRepository.existsById(ownerId)) {
      response.put("msg", "Owner not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Retrieve the projects of the owner. If none, return message
    List<Project> projects = projectRepository.findProjectsByOwnerId(ownerId);
    if (projects == null || projects.isEmpty()) {
      response.put("msg", "No projects found for the owner.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    response.put("msg", "Projects found successfully.");
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
      response.put("msg", "Project ID cannot be null or empty.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Retrieve the project
    Project project = projectRepository.findProjectByProjectId(projectId);
    if (project == null) {
      response.put("msg", "Project not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    WholeProjectDto wholeProjectDto = project.toResponseProjectDto();

    return ResponseEntity.status(HttpStatus.OK).body(wholeProjectDto);
  }

  /**
   * Searches for the tags of the artifacts of a project.
   *
   * @param projectId the id of the project
   * @return the list of the tags associated to the artifacts of the project
   */
  public ResponseEntity<?> getProjectsTags(String projectId) {
    Map<String, Object> response = new HashMap<>();

    // Check if there's a problem with the project ID
    if (projectId == null || projectId.isEmpty()) {
      response.put("msg", "Project ID cannot be null or empty.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Retrieve the project
    Project project = projectRepository.findProjectByProjectId(projectId);
    if (project == null) {
      response.put("msg", "Project not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Retrieve the logged-in user's ID
    String currentUserId = getLoggedInUserId();
    if (!project.getOwnerId().equals(currentUserId)) {
      response.put("msg", "Only the project owner can see the tags!");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // Retrieve the project's artifacts
    List<String> artifactIds = project.getArtifactIds();
    if (artifactIds == null || artifactIds.isEmpty()) {
      response.put("msg", "No artifacts found for the project.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Retrive the tags of the artifacts
    List<String> tags = new ArrayList<>();
    for (String artifactId : artifactIds) {
      Artifact artifact = artifactsRepository.findArtifactByArtifactId(artifactId);
      if (artifact == null) {
        response.put("msg", "Artifact with ID " + artifactId + " not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }
      tags.addAll(artifact.getTags());
    }

    response.put("msg", "Tags found successfully.");
    response.put("tags", tags);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // TODO: Change to actually retrieve the files

  /**
   * Gets all the artifacts of a project.
   *
   * @param projectId the id of the project
   * @return the response entity
   */
  public ResponseEntity<?> getProjectsArtifacts(String projectId) {
    Map<String, Object> response = new HashMap<>();

    if (projectId == null || projectId.isEmpty()) {
      response.put("msg", "Project ID cannot be null or empty.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    Project project = projectRepository.findProjectByProjectId(projectId);
    if (project == null) {
      response.put("msg", "Project not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    List<String> artifactIds = project.getArtifactIds();
    if (artifactIds == null || artifactIds.isEmpty()) {
      response.put("msg", "No artifacts found for the project.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    List<Artifact> artifacts = new ArrayList<>();
    for (String artifactId : artifactIds) {
      Artifact artifact = artifactsRepository.findArtifactByArtifactId(artifactId);
      if (artifact == null) {
        response.put("msg", "Artifact with ID " + artifactId + " not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }
      artifacts.add(artifact);
    }

    response.put("msg", "Artifacts found successfully.");
    response.put("artifacts", artifacts);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * Retrieves a list of projects by their IDs.
   *
   * @param projectIds the list of project IDs to retrieve
   * @return the response entity containing the list of projects or an error message
   */
  public ResponseEntity<?> getProjectsByIds(List<String> projectIds) {
    if (projectIds == null || projectIds.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Project IDs cannot be null or empty.");
    }

    // new ArrayList to store the projects
    List<ProjectInfoDto> projects = new ArrayList<>();

    // for every id passed, check if the project exists and add it to the list
    for (String projectId : projectIds) {
      Project project = projectRepository.findProjectByProjectId(projectId);
      if (project == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Project with ID " + projectId + " not found.");
      }
      projects.add(project.toProjectInfoDto());
    }
    return ResponseEntity.status(HttpStatus.OK).body(projects);
  }

  /**
   * Retrieves a human-readable status of a project.
   *
   * @param projectId the ID of the project to retrieve the status for
   * @return the response entity containing the human-readable project status or an error message
   */
  public ResponseEntity<?> getHumanReadableProjectStatus(String projectId) {
    Map<String, Object> response = new HashMap<>();

    if (projectId == null || projectId.isEmpty()) {
      response.put("msg", "Project ID cannot be null or empty.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    if (!projectRepository.existsById(projectId)) {
      response.put("msg", "Project not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    Project project = projectRepository.findProjectByProjectId(projectId);
    if (project == null) {
      response.put("msg", "Project not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // users
    List<UserResponseDto> responseUserDtos = new ArrayList<>();
    for (String userId : project.getUserIds()) {
      User user = userRepository.findByUserId(userId);
      if (user == null) {
        response.put("msg", "User with ID " + userId + " not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }
      responseUserDtos.add(UserMapper.toUserResponseDto(user));
    }
    List<WholeTeamDto> wholeTeamDto = new ArrayList<>();
    for (String teamId : project.getTeamIds()) {
      Team team = teamsRepository.findTeamByTeamId(teamId);
      if (team == null) {
        response.put("msg", "Team with ID " + teamId + " not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }
      wholeTeamDto.add(TeamMapper.toWholeTeamDto(team));
    }

    // artifacts
    List<WholeArtifactDto> wholeArtifactDto = new ArrayList<>();
    for (String artifactId : project.getArtifactIds()) {
      Artifact artifact = artifactsRepository.findArtifactByArtifactId(artifactId);
      if (artifact == null) {
        response.put("msg", "Artifact with ID " + artifactId + " not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }
      wholeArtifactDto.add(ArtifactMapper.toWholeArtifactDto(artifact));
    }

    User owner = userRepository.findByUserId(project.getOwnerId());
    if (owner == null) {
      response.put("msg", "Owner not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    UserShortResponseDto ownerDto = UserMapper.toUserShortResponseDto(owner);

    return ResponseEntity.status(HttpStatus.OK).body(
        new WholeProjectHeavyDto(
            projectId,
            project.getProjectName(),
            project.getProjectDescription(),
            project.getProjectCreationDate(),
            project.getProjectDeadline(),
            ownerDto,
            project.getProjectStatus().name(),
            responseUserDtos,
            wholeArtifactDto,
            wholeTeamDto));
  }

  // PUT

  /**
   * Modifies an existing projects.
   *
   * @param projectModifyDto the DTO used to modify the project
   * @param projectId        the id of the project to modify
   * @return the response entity
   */
  public ResponseEntity<?> updateProject(ProjectCreateDto projectModifyDto, String projectId) {
    Map<String, Object> response = new HashMap<>();

    // Check if the project ID is null or empty
    if (projectId == null || projectId.isEmpty()) {
      response.put("msg", "Project ID cannot be null or empty.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Retrieve the project to modify
    Project project = projectRepository.findProjectByProjectId(projectId);
    if (project == null) {
      response.put("msg", "Project not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Check if the logged user is the owner of the project
    String currentUserId = getLoggedInUserId();
    if (!currentUserId.equals(project.getOwnerId())) {
      response.put("msg", "Only the owner can modify the project!");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // Project validation
    try {
      CompletedProjectUpdateDto correctProjectUpdateDto = validateUpdate(projectModifyDto);

      project.setProjectName(correctProjectUpdateDto.projectName());
      project.setProjectDescription(correctProjectUpdateDto.projectDescription());
      project.setProjectDeadline(correctProjectUpdateDto.projectDeadline());

      // Remove unneeded users
      List<String> usersToRemove = new ArrayList<>();

      // Collect users to remove
      for (String userId : project.getUserIds()) {
        if (!correctProjectUpdateDto.userIds().contains(userId)) {
          usersToRemove.add(userId);
        }
      }

      // Remove users separately
      for (String userId : usersToRemove) {
        User user = userRepository.findByUserId(userId);
        user.getProjectIds().remove(projectId);

        List<Team> teams = teamsRepository.findTeamsByProjectId(projectId);
        for (Team team : teams) {
          if (team.getUserIds().contains(userId)) {
            team.getUserIds().remove(userId);
            teamsRepository.save(team);
            user.getTeamIds().remove(team.getTeamId());
          }
        }

        userRepository.save(user);
        project.getUserIds().remove(userId); // Now it's safe to modify
      }

      // Add new users
      for (String userId : correctProjectUpdateDto.userIds()) {
        if (!project.getUserIds().contains(userId)) {
          User user = userRepository.findByUserId(userId);
          user.getProjectIds().add(projectId);
          userRepository.save(user);

          // Send email to user
          String emailMessage = String.format("""
                  Dear %s,
                  
                  You have been invited to join the project: %s.
                  
                  Project Description: %s
                  
                  We look forward to your valuable contributions.
                  
                  Best regards,
                  %s
                  """,
              user.getUsername(), project.getProjectName(), project.getProjectDescription(),
              userRepository.findByUserId(project.getOwnerId()).getName() + " "
                  + userRepository.findByUserId(project.getOwnerId()).getSurname());
          new GmailService().sendMail("Project Invitation", user.getEmail(), emailMessage);

          project.getUserIds().add(userId);
        }
      }

      projectRepository.save(project);

      response.put("msg", "Project updated successfully.");
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (ProjectValidationException e) {
      response.put("msg", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    } catch (Exception e) {
      e.printStackTrace();

      response.put("msg", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  /**
   * Adds a list of users to the project.
   *
   * @param projectId  the id of the project to add the user to
   * @param userEmails the list of emails of the users to add
   * @throws Exception if there's a problem with the user or the project
   */
  public void addUsersToProject(String projectId, List<String> userEmails) throws Exception {
    // Check if the project exists and retrieve it
    Project project = projectRepository.findProjectByProjectId(projectId);
    if (project == null) {
      throw new ProjectValidationException("Project with ID " + projectId + " not found.");
    }

    // Check that the logged-in user is the owner
    if (!getLoggedInUserId().equals(project.getOwnerId())) {
      throw new ProjectValidationException("Only the owner can add users to the project.");
    }

    // Validate emails
    String msg = this.checkEmails(userEmails);
    if (msg != null) {
      throw new ProjectValidationException(msg);
    }

    for (String email : userEmails) {
      User user = userRepository.findByEmail(email);
      if (!project.getUserIds().contains(user.getUserId())) {
        // Add the project to the user
        user.getProjectIds().add(project.getProjectId());
        userRepository.save(user);

        // Add the user to the project
        project.getUserIds().add(user.getUserId());
        projectRepository.save(project);

        // Send email to user
        String emailMessage = String.format("""
                Dear %s,
                
                You have been invited to join the project: %s.
                
                Project Description: %s
                
                We look forward to your valuable contributions.
                
                Best regards,
                %s
                """, user.getUsername(), project.getProjectName(), project.getProjectDescription(),
            userRepository.findByUserId(project.getOwnerId()).getName() + " "
                + userRepository.findByUserId(project.getOwnerId()).getSurname());
        new GmailService().sendMail("Project Invitation", user.getEmail(), emailMessage);
      }

      // If the user is already in the project, do nothing
    }
  }

  /**
   * Close a project.
   *
   * @param projectId the id of the project to close
   */
  public ResponseEntity<?> closeProject(String projectId) {
    Map<String, Object> response = new HashMap<>();

    if (projectId == null || projectId.isEmpty()) {
      response.put("msg", "Project ID cannot be null or empty.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    Project project = projectRepository.findProjectByProjectId(projectId);
    if (project == null) {
      response.put("msg", "Project not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    if (project.getProjectStatus() == ProjectStatus.CLOSED) {
      response.put("msg", "Project is already closed.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    String currentUserId = getLoggedInUserId();
    if (!project.getOwnerId().equals(currentUserId)) {
      response.put("msg", "Only the project owner can close the project.");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    project.setProjectStatus(ProjectStatus.CLOSED);
    projectRepository.save(project);

    response.put("msg", "Project closed successfully.");
    return ResponseEntity.status(HttpStatus.OK).body(response);
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

    // TODO: NON ELIMINA BENE I TEAMS!
    // Check if there's a problem with the project ID
    if (projectId == null || projectId.isEmpty()) {
      response.put("msg", "Project ID cannot be null or empty.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    Project projectToDelete = projectRepository.findProjectByProjectId(projectId);
    if (projectToDelete == null) {
      response.put("msg", "Project not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Check if the logged user is the owner of the project
    String currentUserId = getLoggedInUserId();
    if (!currentUserId.equals(projectToDelete.getOwnerId())) {
      response.put("msg", "Only the owner can delete the project!");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // TODO: Eventually delete the roles of the users
    // Delete links to the project from owner
    User owner = userRepository.findByUserId(currentUserId);
    owner.getProjectIds().remove(projectId);
    userRepository.save(owner);

    // Delete links to the project from users
    List<String> userIds = projectToDelete.getUserIds();
    for (String userId : userIds) {
      User user = userRepository.findByUserId(userId);
      user.getProjectIds().remove(projectId);
      userRepository.save(user);
    }

    // Delete teams using the proper service
    List<String> teamIds = projectToDelete.getTeamIds();
    for (String teamId : teamIds) {
      System.out.println("Deleting teamId: " + teamId);
      teamService.deleteTeam(teamId);
    }

    // Delete artifacts using the proper service
    List<String> artifactIds = projectToDelete.getArtifactIds();
    for (String artifactId : artifactIds) {
      artifactService.deleteArtifact(artifactId);
    }

    // Finally, delete the project
    projectRepository.deleteById(projectId);

    // checking if the project has been deleted correctly
    if (projectRepository.existsById(projectId)) {
      response.put("msg", "Project not deleted.");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    response.put("msg", "Project deleted successfully.");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // UTILITY METHODS

  // TODO: Add roles to users
  private void addProjectToUsers(Project project) throws Exception {
    User owner = userRepository.findByUserId(project.getOwnerId());
    if (owner == null) {
      throw new ProjectValidationException("User with ID " + project.getOwnerId() + " not found.");
    }

    // Add project to owner
    owner.getProjectIds().add(project.getProjectId());
    userRepository.save(owner);

    // Add project to users
    List<String> userList = project.getUserIds();
    for (String userId : userList) {
      User user = userRepository.findByUserId(userId);

      // Add the project to the user
      user.getProjectIds().add(project.getProjectId());
      userRepository.save(user);

      // Send email to user
      String emailMessage = String.format("""
              Dear %s,
              
              You have been invited to join the project: %s.
              
              Project Description: %s
              
              We look forward to your valuable contributions.
              
              Best regards,
              %s
              """, user.getUsername(), project.getProjectName(), project.getProjectDescription(),
          owner.getName() + " " + owner.getSurname());
      new GmailService().sendMail("Project Invitation", user.getEmail(), emailMessage);
    }
  }

  private String checkEmails(List<String> userEmails) {
    List<String> missingUserEmails = new ArrayList<>();
    for (String email : userEmails) {
      // Check if the email is null
      if (email == null) {
        return "There is an empty email in the list. Please remove it.";
      }

      // Check if the email is duplicated in the list
      if (userEmails.indexOf(email) != userEmails.lastIndexOf(email)) {
        return "User with email " + email + " is mentioned more than once.";
      }

      // Check if the email is registered. If not, add to a list of missing emails
      if (!userRepository.existsByEmail(email)) {
        missingUserEmails.add(email);
      }
    }

    return null;
  }

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

  private boolean isValidProjectCreateDto(ProjectCreateDto dto) {
    Set<ConstraintViolation<ProjectCreateDto>> violations = validator.validate(dto);

    return violations.isEmpty();
  }

  private CompletedProjectCreationDto validateProject(ProjectCreateDto projectCreateDto) {
    if (!isValidProjectCreateDto(projectCreateDto)) {
      throw new ProjectValidationException("All fields must be filled.");
    }

    // Validate the project name
    String name = projectCreateDto.projectName();
    if (projectRepository.existsByProjectName(name)) {
      throw new ProjectValidationException("Project with name '" + name + "' already exists.");
    }

    // Validate the deadline date and set creation date
    ZonedDateTime deadlineDate = ZonedDateTime.parse(projectCreateDto.deadlineDate(),
        DateTimeFormatter.ISO_DATE_TIME);
    ZonedDateTime creationDate = ZonedDateTime.now();
    if (deadlineDate.isBefore(creationDate)) {
      throw new ProjectValidationException("Deadline date cannot be before the creation date.");
    }

    ZonedDateTime maxDeadline = ZonedDateTime.now()
        .plusYears(ProjectConstants.MAX_PROJECT_DEADLINE_YEARS);
    if (deadlineDate.isAfter(maxDeadline)) {
      throw new ProjectValidationException(
          "Deadline date cannot be after " + ProjectConstants.MAX_PROJECT_DEADLINE_YEARS
              + " years from now.");
    }

    // Validate the owner
    String ownerId = getLoggedInUserId();
    if (ownerId == null || ownerId.isEmpty()) {
      throw new ProjectValidationException("Owner cannot be null or empty.");
    }

    User owner = userRepository.findByUserId(ownerId);
    if (owner == null) {
      throw new ProjectValidationException("User with ID " + ownerId + " does not exist.");
    }

    // Owner must not be part of users
    List<String> userEmails = projectCreateDto.userEmails();
    if (userEmails.contains(owner.getEmail())) {
      throw new ProjectValidationException("Owner must not be part of the list of users.");
    }

    // Validate the user emails
    String msg = this.checkEmails(userEmails);
    if (msg != null) {
      throw new ProjectValidationException(msg);
    }

    // Validate the user emails and retrieve userIds
    List<String> userIds = new ArrayList<>();
    for (String email : userEmails) {
      User user = userRepository.findByEmail(email);
      if (user == null) {
        throw new ProjectValidationException("User with email " + email + " does not exist.");
      }
      userIds.add(user.getUserId());
    }

    return new CompletedProjectCreationDto(name, projectCreateDto.projectDescription(),
        creationDate.toInstant().toEpochMilli(), deadlineDate.toInstant().toEpochMilli(), ownerId,
        userIds);
  }

  private CompletedProjectUpdateDto validateUpdate(ProjectCreateDto projectCreateDto) {
    if (!isValidProjectCreateDto(projectCreateDto)) {
      throw new ProjectValidationException("All fields must be filled.");
    }

    // Validate the project name
    String name = projectCreateDto.projectName();
    if (projectRepository.existsByProjectName(name)) {
      throw new ProjectValidationException("Project with name " + name + " already exists.");
    }

    // Validate the deadline date and set creation date
    ZonedDateTime deadlineDate = ZonedDateTime.parse(projectCreateDto.deadlineDate(),
        DateTimeFormatter.ISO_DATE_TIME);
    if (deadlineDate.isBefore(ZonedDateTime.now())) {
      throw new ProjectValidationException("Deadline date cannot be before the creation date.");
    }

    ZonedDateTime maxDeadline = ZonedDateTime.now()
        .plusYears(ProjectConstants.MAX_PROJECT_DEADLINE_YEARS);
    if (deadlineDate.isAfter(maxDeadline)) {
      throw new ProjectValidationException(
          "Deadline date cannot be after " + ProjectConstants.MAX_PROJECT_DEADLINE_YEARS
              + " years from now.");
    }

    // Owner must not be part of users
    String ownerId = getLoggedInUserId();
    User owner = userRepository.findByUserId(ownerId);
    List<String> userEmails = projectCreateDto.userEmails();
    if (userEmails.contains(owner.getEmail())) {
      throw new ProjectValidationException("Owner must not be part of the list of users.");
    }

    // Validate the user emails and eventually make a list of missing emails
    List<String> missingUserEmails = new ArrayList<>();
    for (String email : userEmails) {
      // Check if the email is null
      if (email == null) {
        throw new ProjectValidationException(
            "There is an empty email in the list. Please remove it.");
      }

      // Check if the email is duplicated in the list
      if (userEmails.indexOf(email) != userEmails.lastIndexOf(email)) {
        throw new ProjectValidationException(
            "User with email " + email + " is mentioned more than once.");
      }

      // Check if the email is registered. If not, add to a list of missing emails
      if (!userRepository.existsByEmail(email)) {
        missingUserEmails.add(email);
      }
    }
    if (!missingUserEmails.isEmpty()) {
      throw new ProjectValidationException(
          "The following emails are not registered: " + missingUserEmails);
    }

    // Validate the user emails and retrieve userIds
    List<String> userIds = new ArrayList<>();
    for (String email : userEmails) {
      User user = userRepository.findByEmail(email);
      if (user == null) {
        throw new ProjectValidationException("User with email " + email + " does not exist.");
      }
      userIds.add(user.getUserId());
    }

    return new CompletedProjectUpdateDto(name, projectCreateDto.projectDescription(),
        deadlineDate.toInstant().toEpochMilli(), userIds);
  }

  /**
   * Retrieves the teams of a project.
   *
   * @param projectId the id of the project
   * @return the response entity containing the list of teams or an error message
   */
  public ResponseEntity<?> getProjectsTeams(String projectId) {
    Map<String, Object> response = new HashMap<>();
    if (projectId == null || projectId.isEmpty()) {
      response.put("msg", "Project ID cannot be null or empty.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    Project project = projectRepository.findProjectByProjectId(projectId);

    if (project == null) {
      response.put("msg", "Project not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    List<String> teamIds = project.getTeamIds();
    List<WholeTeamDto> teams = new ArrayList<>();

    for (String teamId : teamIds) {
      Team team = teamsRepository.findTeamByTeamId(teamId);
      if (team == null) {
        response.put("msg", "Team with ID " + teamId + " not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }
      teams.add(TeamMapper.toWholeTeamDto(team));
    }

    response.put("msg", "Teams found successfully.");
    response.put("teams", teams);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
