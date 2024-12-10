package it.unisannio.studenti.qualitag.repository;

import it.unisannio.studenti.qualitag.model.Project;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for the Project model.
 */
public interface ProjectRepository extends MongoRepository<Project, String> {

  /**
   * Finds a project by its id.
   *
   * @param projectId The id of the project to find
   * @return The project with the given id
   */
  Project findProjectByProjectId(String projectId);

  /**
   * Checks if a project with the given owner id exists.
   *
   * @param ownerId The id of the owner of the project to check
   * @return True if a project with the given id exists, false otherwise
   */
  boolean existsByOwnerId(String ownerId);

  /**
   * Finds all projects with the given owner id.
   *
   * @param ownerId The id of the owner of the projects to find
   * @return A list of projects with the given owner id
   */
  List<Project> findProjectsByOwnerId(String ownerId);

  /**
   * Checks if a project with the given name exists.
   *
   * @param name The name of the project to check
   * @return True if a project with the given name exists, false otherwise
   */
  boolean existsByProjectName(String name);
}
