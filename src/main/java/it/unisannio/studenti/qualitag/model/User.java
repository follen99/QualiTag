package it.unisannio.studenti.qualitag.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * Represents the User in the system.
 */
@Document(collection = "user")
public class User {

  // Attributes
  @MongoId
  @Field(targetType = FieldType.OBJECT_ID)
  private String userId;

  @Field(name = "username")
  @Indexed(unique = true)
  private String username;

  @Field(name = "email")
  @Indexed(unique = true)
  private String email;

  @Field(name = "passwordHash")
  private String passwordHash;

  @Field(name = "name")
  private String name;

  @Field(name = "surname")
  private String surname;

  @Field(name = "projectIds")
  private List<String> projectIds;

  @Field(name = "teamIds")
  private List<String> teamIds;

  @Field(name = "tagIds")
  private List<String> tagIds;

  @Field(name = "roles")
  private List<String> roles;

  // Constructors

  /**
   * Constructs a new User with no parameters.
   */
  public User() {
    this.projectIds = new ArrayList<>();
    this.teamIds = new ArrayList<>();
    this.tagIds = new ArrayList<>();
  }

  /**
   * Constructs a new User.
   *
   * @param username     The username of the user
   * @param email        The email address of the user
   * @param passwordHash The hashed password of the user
   * @param name         The first name of the user
   * @param surname      The last name of the user
   */
  public User(String username, String email, String passwordHash, String name,
      String surname) {
    this.username = username;
    this.email = email;
    this.passwordHash = passwordHash;
    this.name = name;
    this.surname = surname;

    this.projectIds = new ArrayList<>();
    this.teamIds = new ArrayList<>();
    this.tagIds = new ArrayList<>();
  }

  // Methods
  public String getUserId() {
    return userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public List<String> getProjectIds() {
    return Collections.unmodifiableList(projectIds);
  }

  public void setProjectIds(List<String> projectIds) {
    this.projectIds = projectIds;
  }

  /**
   * Adds a project ID to the user.
   *
   * @param projectId the project ID
   */
  public void addProjectId(String projectId) {
    projectIds.add(projectId);
  }

  /**
   * Removes a project ID from the user.
   *
   * @param projectId the project ID
   */
  public void removeProjectId(String projectId) {
    projectIds.remove(projectId);
  }

  public List<String> getTeamIds() {
    return Collections.unmodifiableList(teamIds);
  }

  public void setTeamIds(List<String> teamIds) {
    this.teamIds = teamIds;
  }

  /**
   * Adds a team ID to the user.
   *
   * @param teamId the team ID
   */
  public void addTeamId(String teamId) {
    teamIds.add(teamId);
  }

  /**
   * Removes a team ID from the user.
   *
   * @param teamId the team ID
   */
  public void removeTeamId(String teamId) {
    teamIds.remove(teamId);
  }

  public List<String> getTagIds() {
    return Collections.unmodifiableList(tagIds);
  }

  public void setTagIds(List<String> tagIds) {
    this.tagIds = tagIds;
  }

  /**
   * Adds a tag ID to the user.
   *
   * @param tagId the tag ID
   */
  public void addTagId(String tagId) {
    tagIds.add(tagId);
  }

  /**
   * Removes a tag ID from the user.
   *
   * @param tagId the tag ID
   */
  public void removeTagId(String tagId) {
    tagIds.remove(tagId);
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(userId, user.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId);
  }

  @Override
  public String toString() {
    return "User{"
        + "userId='" + userId + '\''
        + ", username='" + username + '\''
        + ", email='" + email + '\''
        + ", name='" + name + '\''
        + ", surname='" + surname + '\''
        + ", projectIds=" + projectIds
        + ", teamIds=" + teamIds
        + ", tagIds=" + tagIds
        + ", roles=" + roles
        + '}';
  }
}