package it.unisannio.studenti.qualitag.model;

/**
 * Represents a project in the system
 * <p>
 * Created by Raffaele Izzo on 14/11/2024
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Project {

  private String project_id;
  private String project_name;
  private ArrayList<User> users;
  private ArrayList<Team> teams;
  private Date project_deadline;
  private Date project_creation_date;
  private String project_description;


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
   * @param project_id The project's id
   * @param project_name The project's name
   * @param project_deadline The project's deadline
   * @param project_description The project's description
   */
  public Project(String project_id, String project_name, Date project_deadline,
      String project_description) {
    this.project_id = project_id;
    this.project_name = project_name;
    this.users = new ArrayList<User>();
    this.teams = new ArrayList<Team>();
    this.project_deadline = project_deadline;
    this.project_creation_date = new Date();
    this.project_description = project_description;
  }

  /**
   * Constructor for Project with one user
   *
   * @param project_id The project's id
   * @param project_name The project's name
   * @param user The user that is part of the project
   * @param project_deadline The project's deadline
   * @param project_description The project's description
   */
  public Project(String project_id, String project_name, User user, Date project_deadline,
      String project_description) {
    this.project_id = project_id;
    this.project_name = project_name;
    this.users = new ArrayList<User>();
    users.add(user);
    this.teams = new ArrayList<Team>();
    this.project_deadline = project_deadline;
    this.project_creation_date = new Date();
    this.project_description = project_description;
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
  public String getProject_id() {
    return project_id;
  }

  /**
   * Sets the project_id
   *
   * @param project_id The project's id
   */
  public void setProject_id(String project_id) {
    this.project_id = project_id;
  }

  /**
   * Gets the project_name
   *
   * @return project_name The project's name
   */
  public String getProject_name() {
    return project_name;
  }

  /**
   * Sets the project_name
   *
   * @param project_name The project's name
   */
  public void setProject_name(String project_name) {
    this.project_name = project_name;
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
  public Date getProject_deadline() {
    return project_deadline;
  }

  /**
   * Sets the project_deadline
   *
   * @param project_deadline The project's deadline
   */
  public void setProject_deadline(Date project_deadline) {
    this.project_deadline = project_deadline;
  }

  /**
   * Gets the project_creation_date
   *
   * @return project_creation_date The project's creation date
   */
  public Date getProject_creation_date() {
    return project_creation_date;
  }

  /**
   * Sets the project_creation_date
   *
   * @param project_creation_date The project's creation date
   */
  public void setProject_creation_date(Date project_creation_date) {
    this.project_creation_date = project_creation_date;
  } //is this really necessary?

  /**
   * Gets the project_description
   *
   * @return project_description The project's description
   */
  public String getProject_description() {
    return project_description;
  }

  /**
   * Sets the project_description
   *
   * @param project_description The project's description
   */
  public void setProject_description(String project_description) {
    this.project_description = project_description;
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
    return Objects.equals(getProject_id(), project.getProject_id()) &&
        Objects.equals(getProject_name(), project.getProject_name()) &&
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
    return Objects.hash(getProject_id(), getProject_name(), getUsers(), getTeams(),
        getProject_deadline(), getProject_creation_date(), getProject_description());
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
        "project_id='" + project_id + '\'' +
        ", project_name='" + project_name + '\'' +
        ", users=" + users +
        ", teams=" + teams +
        ", project_deadline=" + project_deadline +
        ", project_creation_date=" + project_creation_date +
        ", project_description='" + project_description + '\'' +
        '}';
  }
}
