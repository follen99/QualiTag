package it.unisannio.studenti.qualitag.view;

import it.unisannio.studenti.qualitag.dto.artifact.WholeArtifactDto;
import it.unisannio.studenti.qualitag.dto.project.ProjectInfoDto;
import it.unisannio.studenti.qualitag.dto.team.WholeTeamHeavyDto;
import it.unisannio.studenti.qualitag.dto.user.UserShortResponseDto;
import it.unisannio.studenti.qualitag.mapper.ArtifactMapper;
import it.unisannio.studenti.qualitag.mapper.UserMapper;
import it.unisannio.studenti.qualitag.model.Artifact;
import it.unisannio.studenti.qualitag.model.Project;
import it.unisannio.studenti.qualitag.model.Team;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.ArtifactRepository;
import it.unisannio.studenti.qualitag.repository.ProjectRepository;
import it.unisannio.studenti.qualitag.repository.TeamRepository;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the user views.
 */
@Controller
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamViewController {

  private final TeamRepository teamRepository;
  private final UserRepository userRepository;
  private final ArtifactRepository artifactRepository;
  private final ProjectRepository projectRepository;

  /**
   * Returns the create team view.
   *
   * @return the create team view
   */
  @GetMapping("/{teamid}/create")
  public String createTeam(@PathVariable("teamid") String teamId) {
    return "team/create_team";
  }

  /**
   * Returns the team details view.
   *
   * @return the team details view
   */
  @GetMapping("/{teamid}/details")
  public String getTeamDetails(@PathVariable("teamid") String teamId,
      Model model) {
    WholeTeamHeavyDto responseTeam = null;
    try {
      Team team = teamRepository.findById(teamId)
          .orElseThrow(() -> new NoSuchElementException("Team not found"));

      Project project = projectRepository.findById(team.getProjectId())
          .orElseThrow(() -> new NoSuchElementException("Project not found"));

      List<UserShortResponseDto> users = new ArrayList<>();
      List<WholeArtifactDto> artifacts = new ArrayList<>();

      for (String userId : team.getUserIds()) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found"));
        users.add(UserMapper.toUserShortResponseDto(user));
      }

      for (String artifactId : team.getArtifactIds()) {
        Artifact artifact = artifactRepository.findById(artifactId)
            .orElseThrow(() -> new NoSuchElementException("Artifact not found"));
        artifacts.add(ArtifactMapper.toWholeArtifactDto(artifact));
      }

      responseTeam = new WholeTeamHeavyDto(
          teamId,
          new ProjectInfoDto(project.getProjectName(),
              project.getProjectDescription(),
              project.getProjectStatus().name(),
              project.getProjectId()),
          users,
          artifacts,
          team.getTeamName(),
          team.getCreationTimeStamp(),
          team.getTeamDescription()
      );
    } catch (NoSuchElementException e) {
      // TODO: handle exception
    }

    System.out.println("whole team: " + responseTeam);
    model.addAttribute("team", responseTeam);
    return "team/team_details";
  }
}
