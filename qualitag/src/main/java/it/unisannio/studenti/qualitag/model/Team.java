package it.unisannio.studenti.qualitag.model;

import it.unisannio.studenti.qualitag.dto.team.WholeTeamDto;
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
@Data
@Document(collection = "team")
public class Team {

  // Attributes
  @MongoId
  @Field(targetType = FieldType.OBJECT_ID)
  private String teamId;

  @Field(name = "projectId")
  private String projectId;

  @Field(name = "userIds")
  private List<String> userIds;

  @Field(name = "artifactIds")
  private List<String> artifactIds;

  @Field(name = "teamName")
  private String teamName;

  @Field(name = "creationDate")
  private Long creationTimeStamp;

  @Field(name = "teamDescription")
  private String teamDescription;

  /**
   * Default constructor for Team.
   */
  public Team() {
    this.userIds = new ArrayList<>();
    this.artifactIds = new ArrayList<>();
  }

  /**
   * Constructs a Team with the specified details.
   *
   * @param teamName the name of the team
   * @param creationTimeStamp the creation date of the team
   * @param teamDescription the description of the team
   */
  public Team(String teamName, String projectId, Long creationTimeStamp, String teamDescription,
      List<String> userIds) {
    this.teamName = teamName;
    this.projectId = projectId;
    this.creationTimeStamp = creationTimeStamp;
    this.teamDescription = teamDescription;
    this.userIds = userIds;

    this.artifactIds = new ArrayList<>();
  }

  // GETTERS AND SETTERS
  /**
   * Adds a user to the team.
   *
   * @param user the user to add
   */
  public void addUser(User user) {
    String userId = user.getUserId();
    this.userIds.add(userId);
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
    return Objects.equals(getTeamId(), team.getTeamId())
        && Objects.equals(getProjectId(), team.getProjectId())
        && Objects.equals(getUserIds(), team.getUserIds())
        && Objects.equals(getTeamName(), team.getTeamName())
        && Objects.equals(getCreationTimeStamp(), team.getCreationTimeStamp())
        && Objects.equals(getTeamDescription(), team.getTeamDescription());
  }

  /**
   * Generates a hash code for the team.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(getTeamId(), getProjectId(), getUserIds(), getTeamName(),
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
        + userIds + ", team_name='" + teamName + '\'' + ", creation_date=" + creationTimeStamp
        + ", team_description='" + teamDescription + '\'' + '}';
  }

  // TODO: Add javadoc
  public WholeTeamDto toWholeTeamDto() {
    return new WholeTeamDto(
            this.teamId, 
            this.projectId, 
            this.userIds, 
            this.teamName,
            this.creationTimeStamp, 
            this.teamDescription);
  }
}
