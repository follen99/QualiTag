package it.unisannio.studenti.qualitag.repository;

import it.unisannio.studenti.qualitag.model.Artifact;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ArtifactRepository extends MongoRepository<Artifact, String> {

     Artifact findArtifactByArtifactId(String artifactId);

     List<Artifact> findArtifactsByUserId(String UserId);

}
