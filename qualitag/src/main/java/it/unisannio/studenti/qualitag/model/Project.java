package it.unisannio.studenti.qualitag.model;

/**
 * Represents a project in the system
 * <p>
 * Created by Raffaele Izzo on 14/11/2024
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import org.springframework.data.mongodb.core.mapping.*;

public class Project {

  @MongoId
  @Field(targetType = FieldType.OBJECT_ID)
  private String projectId;

  @Field(name="projectName")
  private String projectName;

  @Field(name="projectUsers")
  private ArrayList<User> users;

  @Field(name="projectTeams")
  private ArrayList<Team> teams;

  @Field(name="projectDeadline")
  private Date projectDeadline;

  @Field(name="projectCreationDate")
  private Date projectCreationDate;

  @Field(name="projectDescription")
  private String projectDescription;


  /**
   * Default constructor for Project
   *
   */
  public Project() {
    this.users = new ArrayList<User>();
    this.teams = new ArrayList<Team>();
  }

  /**
   * Constructor for Project with Parameters
   *
   * @param projectId The project's id
   * @param projectName The project's name
   * @param projectDeadline The project's deadline
   * @param projectDescription The project's description
   */
  public Project(String projectId, String projectName, Date projectDeadline,
      String projectDescription) {
    this.projectId = projectId;
    this.projectName = projectName;
    this.users = new ArrayList<User>();
    this.teams = new ArrayList<Team>();
    this.projectDeadline = projectDeadline;
    this.projectCreationDate = new Date();
    this.projectDescription = projectDescription;
  }

  /**
   * Constructor for Project with one user
   *
   * @param projectId The project's id
   * @param projectName The project's name
   * @param user The user that is part of the project
   * @param projectDeadline The project's deadline
   * @param projectDescription The project's description
   */
  public Project(String projectId, String projectName, User user, Date projectDeadline,
      String projectDescription) {
    this.projectId = projectId;
    this.projectName = projectName;
    this.users = new ArrayList<User>();
    users.add(user);
    this.teams = new ArrayList<Team>();
    this.projectDeadline = projectDeadline;
    this.projectCreationDate = new Date();
    this.projectDescription = projectDescription;
  }

  /**
   * Adds one user to the project
   *
   * @param user The user to add to the project
   */
  public void addUser(User user) {
    users.add(user);
  }

  /**
   * Checks if a user is in the project
   *
   * @param user The user to check
   * @return true if the user is in the project, false otherwise
   */
  public boolean isUserInProject(User user) {
    return users.contains(user);
  }

  /**
   * Removes one user from the project
   *
   * @param user The user to remove from the project
   */
  public void removeUser(User user) {
    users.remove(user);
  }

  /**
   * Adds one team to the project
   *
   * @param team The team to add to the project
   */
  public void addTeam(Team team) {
    teams.add(team);
  }

  /**
   * Checks if a team is in the project
   *
   * @param team The team to check
   * @return true if the team is in the project, false otherwise
   */
  public boolean isTeamInProject(Team team) {
    return teams.contains(team);
  }

  /**
   * Removes one team from the project
   *
   * @param team The team to remove from the project
   */
  public void removeTeam(Team team) {
    teams.remove(team);
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
   * Sets the project_id
   *
   * @param projectId The project's id
   */
  public void setProjectId(String projectId) {
    this.projectId = projectId;
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
   * Gets the users
   *
   * @return users The users in the project
   */
  public ArrayList<User> getUsers() {
    return users;
  }

  /**
   * Sets the users
   *
   * @param users The users in the project
   */
  public void setUsers(ArrayList<User> users) {
    this.users = users;
  }

  /**
   * Gets the teams
   *
   * @return teams The teams in the project
   */
  public ArrayList<Team> getTeams() {
    return teams;
  }

  /**
   * Sets the teams
   *
   * @param teams The teams in the project
   */
  public void setTeams(ArrayList<Team> teams) {
    this.teams = teams;
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
    return Objects.equals(getProjectId(), project.getProjectId()) &&
        Objects.equals(getProjectName(), project.getProjectName()) &&
        Objects.equals(getUsers(), project.getUsers()) &&
        Objects.equals(getTeams(), project.getTeams());
  }

  /**
   * Generates the hash code for the project
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(getProjectId(), getProjectName(), getUsers(), getTeams(),
        getProjectDeadline(), getProjectCreationDate(), getProjectDescription());
  }

  //TO STRING

  /**
   * Gets the project as a string
   *
   * @return the project as a string
   */
  @Override
  public String toString() {
    return "Project{" +
        "projectId='" + projectId + '\'' +
        ", projectName='" + projectName + '\'' +
        ", users=" + users +
        ", teams=" + teams +
        ", projectDeadline=" + projectDeadline +
        ", projectCreation_date=" + projectCreationDate +
        ", projectDescription='" + projectDescription + '\'' +
        '}';
  }
}
