package it.unisannio.studenti.qualitag.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * Represents a team in the system.
 */
@Document
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
   * @param projectId       the ID of the project
   * @param teamName        the name of the team
   * @param creationTimeStamp    the creation date of the team
   * @param teamDescription the description of the team
   */
  public Team(String projectId,
      String teamName,
      Long creationTimeStamp,
      String teamDescription,
      List<String> users) {
    this.projectId = projectId;
    this.teamName = teamName;
    this.creationTimeStamp = creationTimeStamp;
    this.teamDescription = teamDescription;
    this.users = users;
  }

  // GETTERS AND SETTERS

  /**
   * Gets the team ID.
   *
   * @return the team ID
   */
  public String getTeamId() {
    return teamId;
  }

  /**
   * Sets the team ID.
   *
   * @param teamId the team ID to set
   */
  public void setTeamId(String teamId) {
    this.teamId = teamId;
  }

  /**
   * Gets the project ID.
   *
   * @return the project ID
   */
  public String getProjectId() {
    return projectId;
  }

  /**
   * Sets the project ID.
   *
   * @param projectId the project ID to set
   */
  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  /**
   * Gets the list of users in the team.
   *
   * @return the list of users
   */
  public List<String> getUsers() {
    return users;
  }

  /**
   * Sets the list of users in the team.
   *
   * @param users the list of users to set
   */
  public void setUsers(ArrayList<String> users) {
    this.users = users;
  }

  /**
   * Sets the list of users in the team.
   *
   * @param users the list of users to set
   */
  public void setUsers(List<String> users) {
    this.users = users;
  }

  /**
   * Adds a user to the team.
   *
   * @param user the user to add
   */
  public void addUser(User user) {
    String userId = user.getUserId();
    this.users.add(userId);
  }

  /**
   * Gets the team name.
   *
   * @return the team name
   */
  public String getTeamName() {
    return teamName;
  }

  /**
   * Sets the team name.
   *
   * @param teamName the team name to set
   */
  public void setTeamName(String teamName) {
    this.teamName = teamName;
  }

  /**
   * Gets the creation date of the team.
   *
   * @return the creation date
   */
  public Long getCreationTimeStamp() {
    return creationTimeStamp;
  }

  /**
   * Sets the creation date of the team.
   *
   * @param creationTimeStamp the creation date to set
   */
  public void setCreationTimeStamp(Long creationTimeStamp) {
    this.creationTimeStamp = creationTimeStamp;
  }

  /**
   * Gets the team description.
   *
   * @return the team description
   */
  public String getTeamDescription() {
    return teamDescription;
  }

  /**
   * Sets the team description.
   *
   * @param teamDescription the team description to set
   */
  public void setTeamDescription(String teamDescription) {
    this.teamDescription = teamDescription;
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
    return "Team{" +
        "team_id='" + teamId + '\'' +
        ", project_id='" + projectId + '\'' +
        ", users=" + users +
        ", team_name='" + teamName + '\'' +
        ", creation_date=" + creationTimeStamp +
        ", team_description='" + teamDescription + '\'' +
        '}';
  }
}
