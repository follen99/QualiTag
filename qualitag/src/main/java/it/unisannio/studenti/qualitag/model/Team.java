package it.unisannio.studenti.qualitag.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * Represents a team in the system.
 */
@Document
@Data
public class Team {

  @MongoId
  @Field(targetType = FieldType.OBJECT_ID)
  private String teamId;
  private String projectId;
  private List<String> users;
  private String teamName;
  private Long creationTimeStamp;
  private String teamDescription;

  /**
   * Default constructor for Team.
   */
  public Team() {
    this.users = new ArrayList<>();
  }

  /**
   * Constructs a Team with the specified details.
   *
   * @param teamName          the name of the team
   * @param creationTimeStamp the creation date of the team
   * @param teamDescription   the description of the team
   */
  public Team(String teamName, Long creationTimeStamp, String teamDescription, List<String> users) {
    this.teamName = teamName;
    this.creationTimeStamp = creationTimeStamp;
    this.teamDescription = teamDescription;
    this.users = users;
  }

  // GETTERS AND SETTERS
  /**
   * Adds a user to the team.
   *
   * @param user the user to add
   */
  public void addUser(User user) {
    String userId = user.getUserId();
    this.users.add(userId);
  }

  // EQUALS AND HASHCODE

  /**
   * Checks if the team is equal to another object.
   *
   * @param o the object to compare
   * @return true if the team is equal to the object, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Team team = (Team) o;
    return Objects.equals(getTeamId(), team.getTeamId()) && Objects.equals(getProjectId(),
        team.getProjectId()) && Objects.equals(getUsers(), team.getUsers()) && Objects.equals(
        getTeamName(), team.getTeamName()) && Objects.equals(getCreationTimeStamp(),
        team.getCreationTimeStamp()) && Objects.equals(getTeamDescription(),
        team.getTeamDescription());
  }

  /**
   * Generates a hash code for the team.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(getTeamId(), getProjectId(), getUsers(), getTeamName(),
        getCreationTimeStamp(), getTeamDescription());
  }

  // TO STRING

  /**
   * Gets the team as a string.
   *
   * @return the team as a string
   */
  @Override
  public String toString() {
    return "Team{" + "team_id='" + teamId + '\'' + ", project_id='" + projectId + '\'' + ", users="
        + users + ", team_name='" + teamName + '\'' + ", creation_date=" + creationTimeStamp
        + ", team_description='" + teamDescription + '\'' + '}';
  }
}
