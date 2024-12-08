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
   *
   * @param projectCreateDto the project to add to the repository
   */
  @PostMapping("/add")
  public ResponseEntity<?> createProject(@RequestBody ProjectCreateDto projectCreateDto) {
    return projectService.createProject(projectCreateDto);
  }

  /**
   * Adds an artifact to a project
   * @param projectId the id of the project
   * @param artifactId the id of the artifact
   * @return the response entity
   */
  @PostMapping("/add/{projectId}/artifact/{artifactId}")
  public ResponseEntity<?> addArtifact(@PathVariable String projectId, @PathVariable String artifactId) {
    return projectService.addArtifact(projectId, artifactId);
  }

  //GET
  /**
   * Gets all the projects
   *
   * @return the response entity
   */
  @GetMapping("/get")
  public ResponseEntity<?> getAllProjects() {
    return projectService.getAllProjects();
  }

  /**
   * Gets all the projects created by a user with a given id
   *
   * @param ownerId the id of the user to find the projects of
   * @return the response entity
   */

  @GetMapping("/get/{ownerId}/status")
  public ResponseEntity<?> getProjectsByOwnerId(@PathVariable String ownerId) {
    return projectService.getProjectsByOwner(ownerId);
  }

  /**
   * Gets a project by its id
   *
   * @param projectId the id of the project to find
   * @return the response entity
   */
  @GetMapping("/{projectId}/status")
  public ResponseEntity<?> getProjectByProjectId(@PathVariable String projectId) {
    return projectService.getProjectById(projectId);
  }

  /**
   * Gets all the tags of the artifacts of a project
   *
   * @param projectId the id of the project to find the tags of
   * @return the response entity
   */
  @GetMapping("/{projectId}/tags")
  public ResponseEntity<?> getProjectTags(@PathVariable String projectId) {
    return projectService.getProjectsTags(projectId);
  }

  @GetMapping("/{projectId}/artifacts")
  public ResponseEntity<?> getProjectArtifacts(@PathVariable String projectId) {
    return projectService.getProjectsArtifacts(projectId);
  }

  //PUT
  /**
   * Updates a project
   * @param projectId the id of the project to update
   * @param projectCreateDto the updated project
   * @return the response entity
   */
  @PutMapping("/update/{projectId}")
  public ResponseEntity<?> updateProject(@PathVariable String projectId, @RequestBody ProjectCreateDto projectCreateDto) {
    return projectService.updateProject(projectCreateDto, projectId);
  }

  //DELETE

  /**
   * Deletes a project from the repository
   *
   * @param projectId the id of the project to delete
   */
  @DeleteMapping("/delete/{projectId}")
  public ResponseEntity<?> deleteProject(@PathVariable String projectId) {
    return projectService.deleteProject(projectId);
  }

}

