package it.unisannio.studenti.qualitag.model;

/**
 * Represents a project in the system
 * <p>
 * Created by Raffaele Izzo on 14/11/2024
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Date;
import java.util.Objects;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.*;
@Data
public class Project {

  @MongoId
  @Field(targetType = FieldType.OBJECT_ID)
  private String projectId;

  @Field(name="projectName")
  private String projectName;

  @Field(name="projectDescription")
  private String projectDescription;

  @Field(name="projectDeadline")
  private Long projectDeadline;

  @Field(name="projectCreationDate")
  private Long projectCreationDate;

  @Field(name="projectUsers")
  private List<String> userIds;

  @Field(name="projectTeams")
  private List<String> teamIds;

  @Field(name="projectArtifacts")
  private List<String> artifactIds;

  @Field(name="projectOwner")
  private String ownerId;

  /**
   * Default constructor for Project
   *
   */
  public Project() {
    this.userIds = new ArrayList<>();
    this.teamIds = new ArrayList<>();
    this.artifactIds = new ArrayList<>();
  }

  /**
   * Constructor for Project with Parameters
   *
   * @param projectName The project's name
   * @param projectDescription The project's description
   * @param projectDeadline The project's deadline
   */
  public Project(String projectName, String projectDescription,
      Long projectDeadline) {
    this.projectName = projectName;
    this.projectDeadline = projectDeadline;
    this.projectCreationDate = System.currentTimeMillis();
    this.projectDescription = projectDescription;

    this.userIds = new ArrayList<>();
    this.teamIds = new ArrayList<>();
    this.artifactIds = new ArrayList<>();
  }

  /**
   * Constructor for Project with one user
   *
   * @param projectName The project's name
   * @param projectDeadline The project's deadline
   * @param projectDescription The project's description
   * @param userId The userId of the user that is part of the project
   */
  public Project(String projectName, String projectDescription,
      Long projectDeadline, String userId) {
    this.projectName = projectName;
    this.projectDescription = projectDescription;
    this.projectCreationDate = System.currentTimeMillis();
    this.projectDeadline = projectDeadline;

    this.userIds = new ArrayList<>();
    userIds.add(userId);
    this.teamIds = new ArrayList<>();
    this.artifactIds = new ArrayList<>();
  }

  /**
   * Constructor for Project with a list of users, teams and artifacts as well as the ownerId
   *
   * @param projectName The project's name
   * @param projectDescription The project's description
   * @param projectDeadline The project's deadline
   * @param userIds The ids of the users in the project
   * @param teamIds The ids of the teams in the project
   * @param artifactIds The ids of the artifacts in the project
   * @param ownerId The id of the owner of the project
   */
  public Project (String projectName, String projectDescription,
      Long projectDeadline,List<String> userIds,
      List<String> teamIds, List<String> artifactIds
      , String ownerId){
    this.projectName = projectName;
    this.projectDescription = projectDescription;
    this.projectCreationDate = System.currentTimeMillis();
    this.projectDeadline = projectDeadline;

    this.userIds = userIds;
    this.teamIds = teamIds;
    this.artifactIds = artifactIds;
    this.ownerId = ownerId;
  }

  /**
   * Constructor for Project with a list of users, teams and artifacts as well as the ownerId
   *
   * @param projectName The project's name
   * @param projectDescription The project's description
   * @param projectDeadline The project's deadline
   * @param userIds The ids of the users in the project
   * @param teamIds The ids of the teams in the project
   * @param artifactIds The ids of the artifacts in the project
   * @param ownerId The id of the owner of the project
   */
  public Project (String projectName, String projectDescription,
      Long projectDeadline, Long projectCreationDate, List<String> userIds,
      List<String> teamIds, List<String> artifactIds
      , String ownerId){
    this.projectName = projectName;
    this.projectDescription = projectDescription;
    this.projectCreationDate = projectCreationDate;
    this.projectDeadline = projectDeadline;

    this.userIds = userIds;
    this.teamIds = teamIds;
    this.artifactIds = artifactIds;
    this.ownerId = ownerId;
  }

  /**
   * Adds one user to the project
   *
   * @param userId The id of the user to add to the project
   */
  public void addUserId(String userId) {
    userIds.add(userId);
  }

  /**
   * Checks if a user is in the project
   *
   * @param userId the id of the user to check
   * @return true if the user is in the project, false otherwise
   */
  public boolean isUserIdInProject(String userId) {
    return userIds.contains(userId);
  }

  /**
   * Removes one user from the project
   *
   * @param userId the id of the user to remove from the project
   */
  public void removeUserId(String userId) {
    userIds.remove(userId);
  }

  /**
   * Adds one team to the project
   *
   * @param teamId The id of the team to add to the project
   */
  public void addTeamId(String teamId) {
    teamIds.add(teamId);
  }

  /**
   * Checks if a team is in the project
   *
   * @param teamId The id of the team to check
   * @return true if the team is in the project, false otherwise
   */
  public boolean isTeamIDInProject(String teamId) {
    return teamIds.contains(teamId);
  }

  /**
   * Removes one team from the project
   *
   * @param teamId The id of the team to remove from the project
   */
  public void removeTeamId(String teamId) {
    teamIds.remove(teamId);
  }

  /**
   * Adds one artifact to the project
   * @param artifactId the id of the artifact to add to the project
   */
  public void addArtifactId(String artifactId) {
    artifactIds.add(artifactId);
  }

  /**
   * Checks if an artifact is in the project
   * @param artifactId the id of the artifact to check
   * @return true if the artifact is in the project, false otherwise
   */
  public boolean isArtifactIdInProject(String artifactId) {
    return artifactIds.contains(artifactId);
  }

  /**
   * Removes one artifact from the project
   * @param artifactId the id of the artifact to remove from the project
   */
  public void removeArtifactId(String artifactId) {
    artifactIds.remove(artifactId);
  }


  /**
   * Gets the users
   *
   * @return userIds The id of the users in the project
   */
  public List<String> getUserIds() {
    return Collections.unmodifiableList(userIds);
  }

  /**
   * Gets the teams
   *
   * @return teams The ids of the teams in the project
   */
  public List<String> getTeamIds() {
    return Collections.unmodifiableList(teamIds);
  }


  //EQUALS AND HASHCODE

  /**
   * Checks if two projects are equal
   *
   * @param o The object to compare
   * @return true if the projects are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
      if (this == o) {
          return true;
      }
      if (o == null || getClass() != o.getClass()) {
          return false;
      }
    Project project = (Project) o;
    return Objects.equals(getProjectId(), project.getProjectId());
  }

  /**
   * Generates the hash code for the project
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(getProjectId());
  }

  //TO STRING

  /**
   * Gets the project as a string
   *
   * @return the project as a string
   */
  @Override
  public String toString() {
    return "Project{"
        + "projectId='" + projectId + '\''
        + ", projectName='" + projectName + '\''
        + ", projectDescription='" + projectDescription + '\''
        + ", projectCreationDate=" + projectCreationDate
        + ", projectDeadline=" + projectDeadline
        + ", usersIds=" + userIds
        + ", teamsIds=" + teamIds
        + ", artifactsIds=" + artifactIds
        + ", ownerId=" + ownerId
        + '}';
  }
}
