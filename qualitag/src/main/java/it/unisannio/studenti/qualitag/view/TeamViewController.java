package it.unisannio.studenti.qualitag.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for the user views.
 */
@Controller
@RequestMapping("/team")
public class TeamViewController {

  /**
   * Returns the create team view.
   *
   * @return the create team view
   */
  @GetMapping("/{teamid}/create")
  public String createTeam(@PathVariable("teamid") String teamId) {
    return "team/create_team";
  }
}
