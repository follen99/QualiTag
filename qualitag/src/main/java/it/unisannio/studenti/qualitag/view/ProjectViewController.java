package it.unisannio.studenti.qualitag.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project")
public class ProjectViewController {

  @GetMapping("/{username}/projects")
  public String myProjects(@PathVariable("username") String username, Model model) {
    model.addAttribute("username", username);
    return "project/my_projects";
  }
}
