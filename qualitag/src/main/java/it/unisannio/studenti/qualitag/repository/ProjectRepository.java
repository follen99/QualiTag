package it.unisannio.studenti.qualitag.repository;

import it.unisannio.studenti.qualitag.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProjectRepository extends MongoRepository<Project, String> {


    /**
     * Finds a project by its id
     * @param projectId The id of the project to find
     * @return The project with the given id
     */
    Project findProjectByProjectId(String projectId);

    //checks if a project created by a user with id ownerId exists
    boolean existsByOwnerId(String ownerId);

    //finds all the projects created by a user with id ownerId
    List<Project> findProjectsByOwnerId(String ownerId);
}
