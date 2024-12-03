package it.unisannio.studenti.qualitag.repository;

import it.unisannio.studenti.qualitag.model.Artifact;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ArtifactRepository extends MongoRepository<Artifact, String> {

  /**
   * Finds an artifact by its id
   *
   * @param artifactId the id of the artifact to find
   * @return the artifact with the given id
   */
  Artifact findArtifactByArtifactId(String artifactId);


}
