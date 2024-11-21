package it.unisannio.studenti.qualitag.repository;

import it.unisannio.studenti.qualitag.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProjectRepository extends MongoRepository<Project, String> {
    List<Project> findProjectsByUserId(String userId);

    Project findProjectByProjectId(String projectId);
}
