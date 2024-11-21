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

  @PostMapping("/addProject")
  public void addProject(@RequestBody Project project) {
    System.out.println(project.toString());
    projectRepository.save(project);
  }

  @GetMapping("/getAllProjects")
  public void getAllProjects() {
    projectRepository.findAll();
  }

  @GetMapping("/getProjectById/{id}")
  public Project getProjectById(@PathVariable String id) {
    return projectRepository.findProjectByProjectId(id);
  }

  @GetMapping ("/getProjectsByUserId/{userId}")
  public List<Project> getProjectsByUserId(@PathVariable String userId) {
    return projectRepository.findProjectsByUserId(userId);
  }

  @DeleteMapping("/deleteProject/{id}")
  public void deleteProject(@PathVariable String id) {
    projectRepository.deleteById(id);
  }

  @DeleteMapping("/deleteProjects")
  public void deleteProjects(@RequestBody List<String> ids) {
    ids.forEach(id -> projectRepository.deleteById(id));
  }

}
