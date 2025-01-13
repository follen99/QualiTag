package it.unisannio.studenti.qualitag.service;

import it.unisannio.studenti.qualitag.constants.TeamConstants;
import it.unisannio.studenti.qualitag.dto.team.CompletedTeamCreateDto;
import it.unisannio.studenti.qualitag.dto.team.TeamCreateDto;
import it.unisannio.studenti.qualitag.exception.TeamValidationException;
import it.unisannio.studenti.qualitag.mapper.TeamMapper;
import it.unisannio.studenti.qualitag.model.Project;
import it.unisannio.studenti.qualitag.model.Team;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.ProjectRepository;
import it.unisannio.studenti.qualitag.repository.TeamRepository;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import it.unisannio.studenti.qualitag.security.model.CustomUserDetails;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service class for managing teams.
 */
@Service
@RequiredArgsConstructor
public class TeamService {

  private final ProjectRepository projectRepository;
  private final TeamRepository teamRepository;
  private final UserRepository userRepository;

  private final ArtifactService artifactService;

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private final Validator validator = factory.getValidator();

  private boolean validateTeamCreateDto(TeamCreateDto dto) {
    Set<ConstraintViolation<TeamCreateDto>> violations = validator.validate(dto);
    return violations.isEmpty();
  }

  /*
   * TODO:
   * -  Add possibility of team switching if a user is already in another team
   * -  If a user is not present in the project, add it or return an error
   */
  /**
   * Adds a new team.
   *
   * @param teamCreateDto The team data transfer object.
   * @return The response entity.
   */
  public ResponseEntity<?> addTeam(TeamCreateDto teamCreateDto) {
    Map<String, Object> response = new HashMap<>();

    // Team validation
    try {
      // Validate DTO
      CompletedTeamCreateDto correctTeamDto = validateTeam(teamCreateDto);

      // Create team
      Team team = TeamMapper.toEntity(correctTeamDto);

      // Save team and add it to users
      this.teamRepository.save(team);
      this.addTeamToUsers(team);

      // Add team to project
      Project project = projectRepository.findProjectByProjectId(teamCreateDto.projectId());
      project.getTeamIds().add(team.getTeamId());
      projectRepository.save(project);

      response.put("msg", "Team added successfully");
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (TeamValidationException e) {
      response.put("msg", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
  }

  /**
   * Validates a team.
   *
   * @param teamCreateDto The team data transfer object.
   * @return The validated team data transfer object.
   */
  private CompletedTeamCreateDto validateTeam(TeamCreateDto teamCreateDto) {
    if (!validateTeamCreateDto(teamCreateDto)) {
      throw new TeamValidationException("Invalid team data");
    }

    // Retrieve and validate project
    Project project = projectRepository.findProjectByProjectId(teamCreateDto.projectId());
    if (project == null) {
      throw new TeamValidationException("Project not found");
    }

    // Check that team is being created by project owner
    User owner = userRepository.findByUserId(getLoggedInUserId());
    if (!project.getOwnerId().equals(owner.getUserId())) {
      throw new TeamValidationException("Only the project owner can create a team");
    }

    // Validate team name length
    String name = teamCreateDto.teamName();
    if (name.length() > TeamConstants.MAX_TEAM_NAME_LENGTH) {
      throw new TeamValidationException("Team name is too long");
    }
    if (name.length() < TeamConstants.MIN_TEAM_NAME_LENGTH) {
      throw new TeamValidationException("Team name is too short");
    }

    // Remove duplicates from the list of user emails
    List<String> userEmails = teamCreateDto.userEmails();
    userEmails = userEmails.stream().distinct().collect(Collectors.toList());

    // Owner email must not be in the list
    if (userEmails.contains(owner.getEmail())) {
      throw new TeamValidationException("Owner email must not be in the list");
    }

    // Validate number of users
    if (userEmails.size() < TeamConstants.MIN_TEAM_USERS) {
      throw new TeamValidationException(
          "A team must have at least " + TeamConstants.MIN_TEAM_USERS + " users");
    }
    if (userEmails.size() > TeamConstants.MAX_TEAM_USERS) {
      throw new TeamValidationException(
          "A team cannot have more than " + TeamConstants.MAX_TEAM_USERS + " users");
    }

    // Validate emails and convert them to user IDs
    List<String> userIds = new ArrayList<>();
    for (String email : userEmails) {
      // Check for empty email
      if (email == null || email.trim().isEmpty()) {
        throw new TeamValidationException("There is an empty email in the list. Remove it.");
      }

      // Retrieve user by email
      User user = userRepository.findByEmail(email);
      if (user == null) {
        throw new TeamValidationException("User with email " + email + " does not exist");
      }

      // Add user ID to list
      userIds.add(user.getUserId());
    }

    // Validate and correct team description
    String description = teamCreateDto.teamDescription();
    if (description == null) {
      description = "Hi, we are team " + name + "!";
    } else {
      description = description.trim();

      if (description.length() > TeamConstants.MAX_TEAM_DESCRIPTION_LENGTH) {
        throw new TeamValidationException("Team description is too long. Max is "
            + TeamConstants.MAX_TEAM_DESCRIPTION_LENGTH + " characters including whitespaces.");
      }
    }

    return new CompletedTeamCreateDto(name, project.getProjectId(), System.currentTimeMillis(),
        description, userIds);
  }

  /**
   * Adds a team to a user.
   *
   * @param team The team entity.
   * @throws TeamValidationException If the team is null or the user does not exist.
   */
  private void addTeamToUsers(Team team) throws TeamValidationException {
    // Validate team
    if (team == null) {
      throw new TeamValidationException("Team cannot be null");
    }

    // Validate users and add them to the team
    List<String> users = team.getUserIds();
    for (String userId : users) {
      if (userId == null || userId.isEmpty()) {
        throw new TeamValidationException("User ID is null or empty");
      }
      if (!userRepository.existsById(userId)) {
        throw new TeamValidationException("User with ID " + userId + " does not exist");
      }

      // Check if user is part of the project
      Project project = projectRepository.findProjectByProjectId(team.getProjectId());
      if (!project.getUserIds().contains(userId)) {
        throw new TeamValidationException("User with ID " + userId + " is not part of the project");
      }

      // TODO: If user is already in another team, switch it

      User currentUser = userRepository.findById(userId).orElse(null);
      assert currentUser != null;
      List<String> currentUserTeams = new ArrayList<>(currentUser.getTeamIds());

      if (currentUserTeams.contains(team.getTeamId())) {
        throw new TeamValidationException("User with ID " + userId + " is already in team "
            + team.getTeamName() + ". Same user cannot be in multiple teams.");
      }
      currentUserTeams.add(team.getTeamId());
      currentUser.setTeamIds(currentUserTeams);

      userRepository.save(currentUser);
    }
  }

  /**
   * Gets teams by project ID.
   *
   * @param projectId The project ID.
   * @return The response entity.
   */
  public ResponseEntity<?> getTeamsByProject(String projectId) {
    if (projectId == null || projectId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Project ID is null or empty");
    }
    if (!teamRepository.existsByProjectId(projectId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("No teams found for project ID " + projectId);
    }
    return ResponseEntity.status(HttpStatus.OK)
        .body(teamRepository.findTeamsByProjectId(projectId));
  }

  /**
   * Deletes a team by its ID.
   *
   * @param teamId The team ID.
   * @return The response entity.
   */
  public ResponseEntity<?> deleteTeam(String teamId) {
    Map<String, Object> response = new HashMap<>();

    // Validate team ID
    if (teamId == null || teamId.isEmpty()) {
      response.put("msg", "Team ID is null or empty");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Retrieve the team to delete
    Team team = teamRepository.findById(teamId).orElse(null);
    if (team == null) {
      response.put("msg", "Team not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Retrieve the project to remove the team from
    Project project = projectRepository.findProjectByProjectId(team.getProjectId());
    if (project == null) {
      response.put("msg", "Project not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Check if the team is the only team in the project
    if (project.getTeamIds().size() == 1) {
      response.put("msg", "Cannot delete the only team in the project");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Remove team from project
    project.getTeamIds().remove(teamId);
    projectRepository.save(project);

    // Remove team from users
    List<String> userIds = team.getUserIds();
    for (String userId : userIds) {
      User user = userRepository.findById(userId).orElse(null);
      if (user == null) {
        response.put("msg", "User with ID " + userId + " not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }
      user.getTeamIds().remove(teamId);
      userRepository.save(user);
    }

    // Delete all the artifacts of the team
    List<String> artifactIds = team.getArtifactIds();
    for (String artifactId : artifactIds) {
      artifactService.deleteArtifact(artifactId);
    }

    // Delete team from repository
    teamRepository.deleteById(teamId);
    if (teamRepository.existsById(teamId)) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Team not deleted");
    }

    return ResponseEntity.status(HttpStatus.OK).body("Team deleted successfully.");
  }

  /**
   * Gets teams by user ID.
   *
   * @param userId The user ID.
   * @return The response entity.
   */
  public ResponseEntity<?> getTeamsByUser(String userId) {
    if (userId == null || userId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User ID is null or empty");
    }
    if (!teamRepository.existsByUserIdsContaining(userId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("No teams found for user ID " + userId);
    }
    return ResponseEntity.status(HttpStatus.OK)
        .body(teamRepository.findByUserIdsContaining(userId));
  }

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
}
