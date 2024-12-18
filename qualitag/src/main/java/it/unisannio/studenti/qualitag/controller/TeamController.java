package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.dto.team.TeamCreateDto;
import it.unisannio.studenti.qualitag.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing team-related operations.
 */
@RestController
@RequestMapping("/api/v1/team")
public class TeamController {

  private final TeamService teamService;

  /**
   * Constructor for the TeamController.
   *
   * @param teamService the service for managing team-related operations
   */
  public TeamController(TeamService teamService) {
    this.teamService = teamService;
  }

  /**
   * Adds a team to the database.
   *
   * @param teamCreateDto the DTO containing the team's information
   * @return a ResponseEntity containing the result of the operation
   */
  @PostMapping("/add")
  public ResponseEntity<?> addTeam(@RequestBody TeamCreateDto teamCreateDto) {
    return this.teamService.addTeam(teamCreateDto);
  }

  /**
   * Retrieves all teams from the database.
   *
   * @return a ResponseEntity containing the result of the operation
   */
  @GetMapping("/get/all")
  public ResponseEntity<?> getAllTeams() {
    return this.teamService.getAllTeams();
  }

  /**
   * Gets teams by project ID.
   *
   * @param projectId The project ID.
   * @return The response entity.
   */
  @GetMapping("/get/byproject/{projectId}")
  public ResponseEntity<?> getTeamsByProjectId(@PathVariable String projectId) {
    return this.teamService.getTeamsByProject(projectId);
  }

  /**
   * Gets teams by user ID.
   *
   * @param userId The user ID.
   * @return The response entity.
   */
  @GetMapping("/get/byuser/{userId}")
  public ResponseEntity<?> getTeamsByUserId(@PathVariable String userId) {
    return this.teamService.getTeamsByUser(userId);
  }

  /**
   * Deletes a team by its ID.
   *
   * @param teamId The team ID.
   * @return The response entity.
   */
  @DeleteMapping("/delete/{teamId}")
  public ResponseEntity<?> deleteTeam(@PathVariable String teamId) {
    return this.teamService.deleteTeam(teamId);
  }
}
