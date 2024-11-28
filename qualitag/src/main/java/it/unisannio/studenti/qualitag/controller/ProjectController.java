package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.dto.ProjectCreationDto;
import it.unisannio.studenti.qualitag.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {
  private final ProjectService projectService;

  /**
   * Constructs a new ProjectController
   *
   * @param projectService the project service
   */
  @Autowired
  public ProjectController(ProjectService projectService) {
    this.projectService = projectService;
  }

  /**
   * Adds a project to the repository
   * @param projectCreationDto the project to add to the repository
   *
   */
  @PostMapping("/createNewProject")
  public ResponseEntity<?> createProject(@RequestBody ProjectCreationDto projectCreationDto) {
    return projectService.createProject(projectCreationDto);
  }

  /**
   * Gets all the projects
   * @return the response entity
   */
  @GetMapping("/get")
  public ResponseEntity<?> getAllProjects() {
    return projectService.getAllProjects();
  }

}
