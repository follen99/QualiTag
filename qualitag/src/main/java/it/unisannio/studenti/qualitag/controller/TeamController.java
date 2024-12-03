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

@RestController
@RequestMapping("/api/v1/team")
public class TeamController {

  private final TeamService teamService;

  public TeamController(TeamService teamService) {
    this.teamService = teamService;
  }

  /**
   * ####################################################################### POST MAPPING
   * #######################################################################
   */

  @PostMapping("/add")
  public ResponseEntity<?> addTeam(@RequestBody TeamCreateDto teamCreateDto) {
    return this.teamService.addTeam(teamCreateDto);
  }

  /**
   * ####################################################################### GET MAPPING
   * #######################################################################
   */

  @GetMapping("/get/all")
  public ResponseEntity<?> getAllTeams() {
    return this.teamService.getAllTeams();
  }

  @GetMapping("/get/byproject/{projectId}")
  public ResponseEntity<?> getTeamsByProjectId(@PathVariable String projectId) {
    return this.teamService.getTeamsByProject(projectId);
  }

  @GetMapping("/get/byuser/{userId}")
  public ResponseEntity<?> getTeamsByUserId(@PathVariable String userId) {
    return this.teamService.getTeamsByUser(userId);
  }


  /**
   * ####################################################################### DELETE MAPPING
   * #######################################################################
   */

  @DeleteMapping("/delete/{teamId}")
  public ResponseEntity<?> deleteTeam(@PathVariable String teamId) {
    return this.teamService.deleteTeam(teamId);
  }


}
