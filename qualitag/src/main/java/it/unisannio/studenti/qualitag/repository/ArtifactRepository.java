package it.unisannio.studenti.qualitag.repository;

import it.unisannio.studenti.qualitag.model.Artifact;
import it.unisannio.studenti.qualitag.model.Tag;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for the Artifact entity. Provides methods to interact with the database.
 */
public interface ArtifactRepository extends MongoRepository<Artifact, String> {

  /**
   * Finds an artifact by its id.
   *
   * @param artifactId the id of the artifact to find
   * @return the artifact with the given id
   */
  Artifact findArtifactByArtifactId(String artifactId);

  /**
   * Deletes an artifact by its id.
   *
   * @param artifactId the id of the artifact to delete
   */
  void deleteArtifactByArtifactId(String artifactId);



}