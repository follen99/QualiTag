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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service class for managing teams.
 */
@Service
public class TeamService {

  private final ProjectRepository projectRepository;
  private final TeamRepository teamRepository;
  private final UserRepository userRepository;
  private final TeamMapper teamMapper;

  /**
   * Constructs a new TeamService.
   *
   * @param teamRepository The team repository.
   * @param userRepository The user repository.
   * @param teamMapper The team mapper.
   */
  public TeamService(ProjectRepository projectRepository, TeamRepository teamRepository,
      UserRepository userRepository, TeamMapper teamMapper) {
    this.projectRepository = projectRepository;
    this.teamRepository = teamRepository;
    this.userRepository = userRepository;
    this.teamMapper = teamMapper;
  }

  /**
   * Adds a new team.
   *
   * @param teamCreateDto The team data transfer object.
   * @return The response entity.
   */
  public ResponseEntity<?> addTeam(TeamCreateDto teamCreateDto, String projectId) {
    Map<String, Object> response = new HashMap<>();

    // Team validation
    try {
      // Validate project ID
      Project project = projectRepository.findProjectByProjectId(projectId);
      if (project == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
      }

      // Validate DTO
      CompletedTeamCreateDto correctTeamDto = validateTeam(teamCreateDto);

      // Create team
      Team team = teamMapper.toEntity(correctTeamDto);
      team.setProjectId(projectId);

      // Save team and add it to users
      this.teamRepository.save(team);
      this.addTeamToUser(team);

      // Add team to project
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
   * Gets all teams.
   *
   * @return The response entity.
   */
  public ResponseEntity<?> getAllTeams() {
    return ResponseEntity.status(HttpStatus.OK).body(teamRepository.findAll());
  }

  /**
   * Validates a team.
   *
   * @param teamCreateDto The team data transfer object.
   * @return The validated team data transfer object.
   */
  private CompletedTeamCreateDto validateTeam(TeamCreateDto teamCreateDto) {
    if (teamCreateDto == null) {
      throw new TeamValidationException("Team cannot be null");
    }

    // Validate and correct team name
    String name = teamCreateDto.teamName();
    if (name == null || name.trim().isEmpty()) {
      throw new TeamValidationException("Team name cannot be empty");
    }
    /*
     * if (name.contains(" ")) { throw new
     * TeamValidationException("Team name cannot contain whitespaces"); }
     */

    if (name.length() > TeamConstants.MAX_TEAM_NAME_LENGTH) {
      throw new TeamValidationException("Team name is too long");
    }
    if (name.length() < TeamConstants.MIN_TEAM_NAME_LENGTH) {
      throw new TeamValidationException("Team name is too short");
    }

    // Validate users list
    List<String> users = teamCreateDto.users();
    if (users == null || users.isEmpty()) {
      throw new TeamValidationException("Users list cannot be empty");
    }

    users = users.stream().distinct().collect(Collectors.toList()); // Remove duplicates

    /*
     * If MIN_TEAM_USERS == 1 this check is useless, we can use users.isEmpty() instead If
     * MIN_TEAM_USERS > 1 we need to check if the list has at least MIN_TEAM_USERS elements
     */

    /*
     * if (users.size() < TeamConstants.MIN_TEAM_USERS) { throw new TeamValidationException(
     * "A team must have at least " + TeamConstants.MIN_TEAM_USERS + ( TeamConstants.MIN_TEAM_USERS
     * > 1 ? " users" : " user")); } if (users.size() > TeamConstants.MAX_TEAM_USERS) { throw new
     * TeamValidationException( "A team cannot have more than " + TeamConstants.MAX_TEAM_USERS + (
     * TeamConstants.MAX_TEAM_USERS > 1 ? " users" : " user")); }
     */

    for (String currentUserId : users) {
      if (currentUserId == null || currentUserId.trim().isEmpty()) {
        throw new TeamValidationException("There is an empty user in the list. Remove it.");
      }
      currentUserId = currentUserId.trim(); // Remove leading and trailing whitespaces
      if (!userRepository.existsById(currentUserId)) {
        System.out.println("testing userid: " + currentUserId);
        throw new TeamValidationException("User with ID " + currentUserId + " does not exist");
      }

      if (teamRepository.existsByUserIdsContaining(currentUserId)) {
        throw new TeamValidationException("User with ID " + currentUserId
            + " is already in a team. Same user cannot be in multiple teams.");
      }
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

    return new CompletedTeamCreateDto(name, System.currentTimeMillis(), description, users);
  }

  /**
   * Adds a team to a user.
   *
   * @param team The team entity.
   * @throws TeamValidationException If the team is null or the user does not exist.
   */
  private void addTeamToUser(Team team) throws TeamValidationException {
    if (team == null) {
      throw new TeamValidationException("Team cannot be null");
    }
    List<String> users = team.getUserIds();
    for (String userId : users) {
      if (userId == null || userId.isEmpty()) {
        throw new TeamValidationException("User ID is null or empty");
      }
      if (!userRepository.existsById(userId)) {
        throw new TeamValidationException("User with ID " + userId + " does not exist");
      }

      /*
       * if (teamRepository.existsByUsersContaining(userId)) { throw new
       * TeamValidationException("User with ID " + userId +
       * " is already in a team. Same user cannot be in multiple teams."); }
       */

      if (teamRepository.existsByUserIdsContainingAndTeamIdNot(userId, team.getTeamId())) {
        throw new TeamValidationException("User with ID " + userId
            + " is already in a team. Same user cannot be in multiple teams.");
      }

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
    if (teamId == null || teamId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Team ID is null or empty");
    }
    if (!teamRepository.existsById(teamId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team not found");
    }
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
}
