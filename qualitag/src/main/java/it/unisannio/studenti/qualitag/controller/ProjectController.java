package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.dto.project.ProjectCreateDto;
import it.unisannio.studenti.qualitag.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The ProjectController class is a REST controller that handles requests related to projects.
 */
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

  private final ProjectService projectService;

  // POST

  /**
   * Constructs a new ProjectController.
   *
   * @param projectService the project service
   */
  @Autowired
  public ProjectController(ProjectService projectService) {
    this.projectService = projectService;
  }

  /**
   * Adds a project to the repository.
   *
   * @param projectCreateDto the project to add to the repository
   */
  @PostMapping("/add")
  public ResponseEntity<?> createProject(@RequestBody ProjectCreateDto projectCreateDto) {
    return projectService.createProject(projectCreateDto);
  }

  /**
   * Adds an artifact to a project.
   *
   * @param projectId  the id of the project
   * @param artifactId the id of the artifact
   * @return the response entity
   */
  @PostMapping("/add/{projectId}/artifact/{artifactId}")
  public ResponseEntity<?> addArtifact(@PathVariable String projectId,
      @PathVariable String artifactId) {
    return projectService.addArtifact(projectId, artifactId);
  }

  /**
   * Close a project.
   *
   * @param projectId the id of the project to close
   * @return the response entity
   */
  @PostMapping("/{projectId}/close")
  public ResponseEntity<?> closeProject(@PathVariable String projectId) {
    return projectService.closeProject(projectId);
  }

  // GET
  /**
   * Gets all the projects.
   *
   * @return the response entity
   */
  @GetMapping("/get")
  public ResponseEntity<?> getAllProjects() {
    return projectService.getAllProjects();
  }

  /**
   * Gets all the projects created by a user with a given id.
   *
   * @param ownerId the id of the user to find the projects of
   * @return the response entity
   */

  @GetMapping("/get/{ownerId}/status")
  public ResponseEntity<?> getProjectsByOwnerId(@PathVariable String ownerId) {
    return projectService.getProjectsByOwner(ownerId);
  }

  /**
   * Gets a project by its id.
   *
   * @param projectId the id of the project to find
   * @return the response entity
   */
  @GetMapping("/{projectId}/status")
  public ResponseEntity<?> getProjectByProjectId(@PathVariable String projectId) {
    return projectService.getProjectById(projectId);
  }

  /**
   * Gets all the tags of the artifacts of a project.
   *
   * @param projectId the id of the project to find the tags of
   * @return the response entity
   */
  @GetMapping("/{projectId}/tags")
  public ResponseEntity<?> getProjectTags(@PathVariable String projectId) {
    return projectService.getProjectsTags(projectId);
  }

  /**
   * Gets all the artifacts of a project.
   *
   * @param projectId the id of the project to find the artifacts of
   * @return the response entity
   */
  @GetMapping("/{projectId}/artifacts")
  public ResponseEntity<?> getProjectArtifacts(@PathVariable String projectId) {
    return projectService.getProjectsArtifacts(projectId);
  }

  // PUT

  /**
   * Updates a project.
   *
   * @param projectId        the id of the project to update
   * @param projectCreateDto the updated project
   * @return the response entity
   */
  @PutMapping("/update/{projectId}")
  public ResponseEntity<?> updateProject(@PathVariable String projectId,
      @RequestBody ProjectCreateDto projectCreateDto) {
    return projectService.updateProject(projectCreateDto, projectId);
  }

  // DELETE

  /**
   * Deletes a project from the repository.
   *
   * @param projectId the id of the project to delete
   */
  @DeleteMapping("/delete/{projectId}")
  public ResponseEntity<?> deleteProject(@PathVariable String projectId) {
    return projectService.deleteProject(projectId);
  }
}