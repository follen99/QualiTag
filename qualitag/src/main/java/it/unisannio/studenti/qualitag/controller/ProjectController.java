package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.dto.project.ProjectCreateDto;
import it.unisannio.studenti.qualitag.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {
  private final ProjectService projectService;

  //POST
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
   * @param projectCreateDto the project to add to the repository
   *
   */
  @PostMapping("/add")
  public ResponseEntity<?> createProject(@RequestBody ProjectCreateDto projectCreateDto) {
    return projectService.createProject(projectCreateDto);
  }

  //GET
  /**
   * Gets all the projects
   * @return the response entity
   */
  @GetMapping("/get")
  public ResponseEntity<?> getAllProjects() {
    return projectService.getAllProjects();
  }

  /**
   * Gets all the projects created by a user with id ownerId
   * @return the response entity
   */

  @GetMapping ("/get/{ownerId}")
  public ResponseEntity<?> getProjectsByOwnerId(@PathVariable String ownerId) {
    return projectService.getProjecstByOwner(ownerId);
  }

  //DELETE
  /**
   * Deletes a project from the repository
   * @param projectId the id of the project to delete
   */
  @DeleteMapping("/delete/{projectId}")
  public ResponseEntity<?> deleteProject(@PathVariable String projectId) {
    return projectService.deleteProject(projectId);
  }

}
