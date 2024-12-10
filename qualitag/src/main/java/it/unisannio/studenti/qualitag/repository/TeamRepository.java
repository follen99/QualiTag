package it.unisannio.studenti.qualitag.repository;

import it.unisannio.studenti.qualitag.model.Team;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository interface for managing Team entities in MongoDB.
 */
public interface TeamRepository extends MongoRepository<Team, String> {

  /**
   * Finds teams by project ID.
   *
   * @param projectId The project ID.
   * @return A list of teams associated with the specified project ID.
   */
  List<Team> findTeamsByProjectId(String projectId);

  /**
   * Checks if a team exists by project ID.
   *
   * @param projectId The project ID.
   * @return true if a team exists with the specified project ID, false otherwise.
   */
  boolean existsByProjectId(String projectId);

  /**
   * Checks if a team exists by user ID.
   *
   * @param userId The user ID.
   * @return true if a team exists containing the specified user ID, false otherwise.
   */
  boolean existsByUsersContaining(String userId);

  /**
   * Finds teams by user ID.
   *
   * @param userId The user ID.
   * @return A list of teams containing the specified user ID.
   */
  List<Team> findByUsersContaining(String userId);

  /**
   * Checks if a team exists by user ID and team ID not matching.
   *
   * @param userId The user ID.
   * @param teamId The team ID to exclude.
   * @return true if a team exists containing the specified user ID and not matching the specified
   *      team ID, false otherwise.
   */
  boolean existsByUsersContainingAndTeamIdNot(String userId, String teamId);
}
