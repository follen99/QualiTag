package it.unisannio.studenti.qualitag.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the User in the system.
 */
public class User {

  // ************ ATTRIBUTES ************
  private final String userId;
  private final String username;
  private final String email;
  private String passwordHash;
  private final String name;
  private final String surname;
  private final List<String> projectIds;
  private final List<String> teamIds;
  private final List<String> tagIds;
  private List<String> roles;

  // ************ CONSTRUCTORS ************

  /**
   * Constructs a new User.
   *
   * @param userId the user ID
   * @param username the username
   * @param email the email address
   * @param password the password
   * @param name the first name
   * @param surname the last name
   */
  public User(String userId, String username, String email, String password, String name,
      String surname) {
    this.userId = userId;
    this.username = username;
    this.email = email;
    this.passwordHash = hashPassword(password);
    this.name = name;
    this.surname = surname;
    this.projectIds = new ArrayList<>();
    this.teamIds = new ArrayList<>();
    this.tagIds = new ArrayList<>();
  }

  // ************ METHODS ************

  public String getUserId() {
    return userId;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getName() {
    return name;
  }

  public String getSurname() {
    return surname;
  }

  public List<String> getProjectIds() {
    return Collections.unmodifiableList(projectIds);
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

  public void setPassword(String password) {
    this.passwordHash = hashPassword(password);
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  private String hashPassword(String password) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] salt = new byte[16];
      SecureRandom sr = new SecureRandom();
      sr.nextBytes(salt);
      md.update(salt);
      byte[] hashedPassword = md.digest(password.getBytes());
      return Base64.getEncoder().encodeToString(hashedPassword);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error hashing password", e);
    }
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