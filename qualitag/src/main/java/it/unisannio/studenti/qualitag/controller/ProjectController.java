package it.unisannio.studenti.qualitag.controller;

import it.unisannio.studenti.qualitag.dto.project.ProjectCreateDto;
import it.unisannio.studenti.qualitag.service.ProjectService;
import java.util.List;
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
@RequestMapping("/api/v1/project")
public class ProjectController {

  private final ProjectService projectService;

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
  @PostMapping()
  public ResponseEntity<?> createProject(@RequestBody ProjectCreateDto projectCreateDto) {
    return projectService.createProject(projectCreateDto);
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

  /**
   * Gets projects by their IDs.
   *
   * @param projectIds The list of project IDs.
   * @return The response entity with the list of project DTOs.
   */
  @PostMapping("/get-by-ids")
  public ResponseEntity<?> getProjectsByIds(@RequestBody List<String> projectIds) {
    if (projectIds == null || projectIds.isEmpty()) {
      return ResponseEntity.badRequest().body("Project IDs cannot be null or empty");
    }
    return projectService.getProjectsByIds(projectIds);
  }

  /**
   * Retrieve a comprehensive DTO regarding the status of the whole project.
   *
   * @param projectId The ID of the project
   * @return The DTO
   */
  @GetMapping("/{projectId}/status/whole")
  public ResponseEntity<?> getHumanReadableProjectStatus(@PathVariable String projectId) {
    return projectService.getHumanReadableProjectStatus(projectId);
  }

  /**
   * Gets a project by its id.
   *
   * @param projectId the id of the project to find
   * @return the response entity
   */
  @GetMapping("/{projectId}")
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

  /**
   * Gets all the teams of a project.
   *
   * @param projectId the id of the project to find the teams of
   * @return the response entity
   */
  @GetMapping("/{projectId}/teams")
  public ResponseEntity<?> getProjectTeams(@PathVariable String projectId) {
    return projectService.getProjectsTeams(projectId);
  }

  /**
   * Updates a project.
   *
   * @param projectId        the id of the project to update
   * @param projectCreateDto the updated project
   * @return the response entity
   */
  @PutMapping("/{projectId}")
  public ResponseEntity<?> updateProject(@PathVariable String projectId,
      @RequestBody ProjectCreateDto projectCreateDto) {
    return projectService.updateProject(projectCreateDto, projectId);
  }

  /**
   * Deletes a project from the repository.
   *
   * @param projectId the id of the project to delete
   */
  @DeleteMapping("/{projectId}")
  public ResponseEntity<?> deleteProject(@PathVariable String projectId) {
    System.out.println("deleting: " + projectId);
    return projectService.deleteProject(projectId);
  }
}
