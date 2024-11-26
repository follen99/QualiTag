package it.unisannio.studenti.qualitag.service;

import it.unisannio.studenti.qualitag.dto.team.TeamCreateDto;
import it.unisannio.studenti.qualitag.exception.TeamValidationException;
import it.unisannio.studenti.qualitag.mapper.TeamMapper;
import it.unisannio.studenti.qualitag.model.Team;
import it.unisannio.studenti.qualitag.repository.TeamRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TeamService {
  private final TeamRepository teamRepository;
  private final TeamMapper teamMapper;

  public TeamService(TeamRepository teamRepository, TeamMapper teamMapper) {
    this.teamRepository = teamRepository;
    this.teamMapper = teamMapper;
  }

  /**
   * #######################################################################
   *                                  POST
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

    if (name.length() > 50) {
      throw new TeamValidationException("Team name is too long");
    }
    if (name.length() < 3) {
      throw new TeamValidationException("Team name is too short");
    }

    // Validate users list
    if (users == null || users.isEmpty()) {
      throw new TeamValidationException("Users list cannot be empty");
    }

    // Validate project ID
    if (projectId == null || projectId.trim().isEmpty()) {
      throw new TeamValidationException("Project ID cannot be empty");
    }
    projectId = projectId.trim(); // Remove leading and trailing whitespaces
    // TODO CHECK IF PROJECT EXISTS

    // Validate creation date
    if (creationDate == null) {
      creationDate = System.currentTimeMillis();
    }else {
      if (creationDate <= 0) {
        throw new TeamValidationException("Invalid creation date");
      }

      if (creationDate > System.currentTimeMillis()) {
        throw new TeamValidationException("Creation date cannot be in the future");
      }
    }

    // Validate and correct team description
    if (description != null) {
      description = description.trim();
    } else {
      description = "Hi, we are team " + name + "!";
    }

    return new TeamCreateDto(name, projectId, creationDate, description, users);
  }
}
