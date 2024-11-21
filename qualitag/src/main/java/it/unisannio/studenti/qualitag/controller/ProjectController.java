package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.model.Project;
import it.unisannio.studenti.qualitag.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProjectController {
  @Autowired
  ProjectRepository projectRepository;

  /**
   * Adds a project to the repository
   * @param project the project to add to the repository
   */
  @PostMapping("/addProject")
  public void addProject(@RequestBody Project project) {
    System.out.println(project.toString());
    projectRepository.save(project);
  }

  /**
   *Returns all the project in the repository
   */
  @GetMapping("/getAllProjects")
  public void getAllProjects() {
    projectRepository.findAll();
  }

  /**
   * Returns a project with a specific id
   * @param id the id of the project to find
   * @return the project with the given id
   */
  @GetMapping("/getProjectById/{id}")
  public Project getProjectById(@PathVariable String id) {
    return projectRepository.findProjectByProjectId(id);
  }

  /**
   * Deletes a project with a specific id from the repository
   * @param id the id of the project to delete
   */
  @DeleteMapping("/deleteProject/{id}")
  public void deleteProject(@PathVariable String id) {
    projectRepository.deleteById(id);
  }

  /**
   * Deletes multiple projects from the repository
   * @param ids a list of the ids of the project to delete
   */
  @DeleteMapping("/deleteProjects")
  public void deleteProjects(@RequestBody List<String> ids) {
    ids.forEach(id -> projectRepository.deleteById(id));
  }

}
