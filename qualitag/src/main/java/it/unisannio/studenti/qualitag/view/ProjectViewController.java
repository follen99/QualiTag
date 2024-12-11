package it.unisannio.studenti.qualitag.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The ProjectViewController class is a controller
 * that handles requests related to the projects view.
 */
@Controller
@RequestMapping("/project")
public class ProjectViewController {

  /**
   * Returns the projects view.
   *
   * @param username The username of the user.
   * @param model    The model.
   * @return the projects view
   */
  @GetMapping("/{username}/projects")
  public String myProjects(@PathVariable("username") String username, Model model) {
    model.addAttribute("username", username);
    return "project/my_projects";
  }

  /**
   * Returns the project details view.
   *
   * @param projectId The id of the project.
   * @param model     The model.
   * @return the project details view
   */
  @GetMapping("/detail/{projectId}")
  public String project(@PathVariable("projectId") String projectId, Model model) {
    model.addAttribute("projectId", projectId);
    return "project/project_details";
  }
}
