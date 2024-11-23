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
import org.springframework.data.mongodb.core.mapping.*;

public class Project {

  @MongoId
  @Field(targetType = FieldType.OBJECT_ID)
  private String projectId;

  @Field(name="projectName")
  private String projectName;

  @Field(name="projectDescription")
  private String projectDescription;

  @Field(name="projectDeadline")
  private Date projectDeadline;

  @Field(name="projectCreationDate")
  private Date projectCreationDate;

  @Field(name="projectUsers")
  private List<String> userIds;

  @Field(name="projectTeams")
  private List<String> teamIds;

  /**
   * Default constructor for Project
   *
   */
  public Project() {
    this.userIds = new ArrayList<>();
    this.teamIds = new ArrayList<>();
  }

  /**
   * Constructor for Project with Parameters
   *
   * @param projectName The project's name
   * @param projectDescription The project's description
   * @param projectDeadline The project's deadline
   */
  public Project(String projectName, String projectDescription,
      Date projectDeadline) {
    this.projectName = projectName;
    this.projectDeadline = projectDeadline;
    this.projectCreationDate = new Date();
    this.projectDescription = projectDescription;

    this.userIds = new ArrayList<>();
    this.teamIds = new ArrayList<>();
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
      Date projectDeadline, String userId) {
    this.projectName = projectName;
    this.projectDescription = projectDescription;
    this.projectCreationDate = new Date();
    this.projectDeadline = projectDeadline;

    this.userIds = new ArrayList<>();
    userIds.add(userId);
    this.teamIds = new ArrayList<>();
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


  //GETTERS AND SETTERS
  /**
   * Gets the project_name
   *
   * @return project_name The project's name
   */
  public String getProjectId() {
    return projectId;
  }

  /**
   * Gets the project_name
   *
   * @return project_name The project's name
   */
  public String getProjectName() {
    return projectName;
  }

  /**
   * Sets the project_name
   *
   * @param projectName The project's name
   */
  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  /**
   * Gets the project_description
   *
   * @return project_description The project's description
   */
  public String getProjectDescription() {
    return projectDescription;
  }

  /**
   * Sets the project_description
   *
   * @param projectDescription The project's description
   */
  public void setProjectDescription(String projectDescription) {
    this.projectDescription = projectDescription;
  }

  /**
   * Gets the project_deadline
   *
   * @return project_deadline The project's deadline
   */
  public Date getProjectDeadline() {
    return projectDeadline;
  }

  /**
   * Sets the project_deadline
   *
   * @param projectDeadline The project's deadline
   */
  public void setProjectDeadline(Date projectDeadline) {
    this.projectDeadline = projectDeadline;
  }

  /**
   * Gets the project_creation_date
   *
   * @return project_creation_date The project's creation date
   */
  public Date getProjectCreationDate() {
    return projectCreationDate;
  }

  /**
   * Sets the project_creation_date
   *
   * @param projectCreationDate The project's creation date
   */
  public void setProjectCreationDate(Date projectCreationDate) {
    this.projectCreationDate = projectCreationDate;
  } //is this really necessary?


  /**
   * Gets the users
   *
   * @return userIds The id of the users in the project
   */
  public List<String> getUserIds() {
    return Collections.unmodifiableList(userIds);
  }

  /**
   * Sets the users
   *
   * @param userIds The ids of the users in the project
   */
  public void setUserIds(List<String> userIds) {
    this.userIds = userIds;
  }

  /**
   * Gets the teams
   *
   * @return teams The ids of the teams in the project
   */
  public List<String> getTeamIds() {
    return Collections.unmodifiableList(teamIds);
  }

  /**
   * Sets the teams
   *
   * @param teamIds The ids of the teams in the project
   */
  public void setTeamIds(List<String> teamIds) {
    this.teamIds = teamIds;
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
        + '}';
  }
}
