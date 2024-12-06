package it.unisannio.studenti.qualitag.repository;

import it.unisannio.studenti.qualitag.model.Team;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TeamRepository extends MongoRepository<Team, String> {

  List<Team> findTeamsByProjectId(String projectId);

  boolean existsByProjectId(String projectId);

  boolean existsByUsersContaining(String userId);

  List<Team> findByUsersContaining(String userId);

  boolean existsByUsersContainingAndTeamIdNot(String userId, String teamId);
}
