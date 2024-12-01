package it.unisannio.studenti.qualitag.service;

import it.unisannio.studenti.qualitag.constants.TeamConstants;
import it.unisannio.studenti.qualitag.dto.team.TeamCreateDto;
import it.unisannio.studenti.qualitag.exception.TeamValidationException;
import it.unisannio.studenti.qualitag.mapper.TeamMapper;
import it.unisannio.studenti.qualitag.model.Team;
import it.unisannio.studenti.qualitag.repository.TeamRepository;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

  private final TeamRepository teamRepository;
  private final UserRepository userRepository;
  private final TeamMapper teamMapper;

  public TeamService(TeamRepository teamRepository, UserRepository userRepository,
      TeamMapper teamMapper) {
    this.teamRepository = teamRepository;
    this.userRepository = userRepository;
    this.teamMapper = teamMapper;
  }

  /**
   * ####################################################################### POST
   * #######################################################################
   */

  public ResponseEntity<?> addTeam(TeamCreateDto teamCreateDto) {
    // team validation
    try {
      TeamCreateDto correctTeamDto = validateTeam(teamCreateDto);

      Team team = teamMapper.toEntity(correctTeamDto);
      this.teamRepository.save(team);
      return ResponseEntity.status(HttpStatus.CREATED).body("Team added successfully");
    } catch (TeamValidationException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  /**
   * ####################################################################### GET
   * #######################################################################
   */
  public ResponseEntity<?> getAllTeams() {
    return ResponseEntity.status(HttpStatus.OK).body(teamRepository.findAll());
  }

  /**
   * #######################################################################
   *                               VALIDATION
   * #######################################################################
   */

  /**
   * Validates a team.
   *
   * @param teamCreateDto
   * @return
   */
  private TeamCreateDto validateTeam(TeamCreateDto teamCreateDto) {
    if (teamCreateDto == null) {
      throw new TeamValidationException("Team cannot be null");
    }

    String name = teamCreateDto.teamName();
    String projectId = teamCreateDto.projectId();
    Long creationDate = teamCreateDto.creationDate();
    String description = teamCreateDto.teamDescription();
    List<String> users = teamCreateDto.users();

    // Validate and correct team name
    if (name == null || name.trim().isEmpty()) {
      throw new TeamValidationException("Team name cannot be empty");
    }
    if (name.contains(" ")) {
      throw new TeamValidationException("Team name cannot contain whitespaces");
    }

    if (name.length() > TeamConstants.MAX_TEAM_NAME_LENGTH) {
      throw new TeamValidationException("Team name is too long");
    }
    if (name.length() < TeamConstants.MIN_TEAM_NAME_LENGTH) {
      throw new TeamValidationException("Team name is too short");
    }

    // Validate users list
    if (users == null || users.isEmpty()) {
      throw new TeamValidationException("Users list cannot be empty");
    }

    users = users.stream().distinct().collect(Collectors.toList()); // Remove duplicates

    /**
     * If MIN_TEAM_USERS == 1 this check is useless, we can use users.isEmpty() instead
     * If MIN_TEAM_USERS > 1 we need to check if the list has at least MIN_TEAM_USERS elements
     */
    if (users.size() < TeamConstants.MIN_TEAM_USERS) {
      throw new TeamValidationException(
          "A team must have at least " + TeamConstants.MIN_TEAM_USERS + (
              TeamConstants.MIN_TEAM_USERS > 1 ? " users" : " user"));
    }
    if (users.size() > TeamConstants.MAX_TEAM_USERS) {
      throw new TeamValidationException(
          "A team cannot have more than " + TeamConstants.MAX_TEAM_USERS + (
              TeamConstants.MAX_TEAM_USERS > 1 ? " users" : " user"));
    }

    for (String currentUserId : users) {
      if (currentUserId == null || currentUserId.trim().isEmpty()) {
        throw new TeamValidationException("There is an empty user in the list. Remove it.");
      }
      currentUserId = currentUserId.trim(); // Remove leading and trailing whitespaces
      if (!userRepository.existsById(currentUserId)) {
        throw new TeamValidationException("User with ID " + currentUserId + " does not exist");
      }

      if (teamRepository.existsByUsersContaining(currentUserId)) {
        throw new TeamValidationException("User with ID " + currentUserId
            + " is already in a team. Same user cannot be in multiple teams.");
      }
    }
    // Validate project ID
    if (projectId == null || projectId.trim().isEmpty()) {
      throw new TeamValidationException("Project ID cannot be empty");
    }
    projectId = projectId.trim(); // Remove leading and trailing whitespaces

    // Validate creation date
    if (creationDate == null) {
      creationDate = System.currentTimeMillis();
    } else {
      if (creationDate <= 0) {
        throw new TeamValidationException("Invalid creation date");
      }

      if (creationDate > System.currentTimeMillis()) {
        throw new TeamValidationException("Creation date cannot be in the future");
      }
    }

    // Validate and correct team description
    if (description == null) {
      description = "Hi, we are team " + name + "!";
    } else {
      description = description.trim();

      if (description.length() > TeamConstants.MAX_TEAM_DESCRIPTION_LENGTH) {
        throw new TeamValidationException(
            "Team description is too long. Max is " + TeamConstants.MAX_TEAM_DESCRIPTION_LENGTH
                + " characters including whitespaces.");
      }
    }

    return new TeamCreateDto(name, projectId, creationDate, description, users);
  }

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

  public ResponseEntity<?> getTeamsByUser(String userId) {
    if (userId == null || userId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User ID is null or empty");
    }
    if (!teamRepository.existsByUsersContaining(userId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("No teams found for user ID " + userId);
    }
    return ResponseEntity.status(HttpStatus.OK).body(teamRepository.findByUsersContaining(userId));
  }
}
