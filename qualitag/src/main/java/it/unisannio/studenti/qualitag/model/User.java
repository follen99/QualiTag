package it.unisannio.studenti.qualitag.model;

import java.util.List;

/**
 * Represents the User in the system.
 */
public class User {
  // ************ ATTRIBUTES ************
  /**
   * Attributes regarding primary and external keys.
   */
  private String userId;
  private List<String> projectIds;
  private List<String> teamIds;
  private List<String> tagIds;

  /**
   * Attributes regarding log-in, authentication and roles.
   */
  private String username;
  private String email;
  private String password;
  private List<String> roles;

  /**
   * Attributes regarding useful data about the real identity of the user, to improve readability
   * and user interface and create an actual profile for the user.
   */
  private String name;
  private String surname;

  // ************ CONSTRUCTORS ************
  /**
   * Base constructor to create a user.
   *
   * @param userId The ID of the user, given by the platform.
   * @param username The username of the user on the platform. Must be unique.
   * @param email The email of the user on the platform. Must be unique.
   * @param password The password of the user on the platform.
   * @param name The name of the user on the platform.
   * @param surname The surname of the user on the platform.
   */
  public User(String userId, String username, String password, String email, String name,
      String surname) {
    this.userId = userId;
    this.username = username;
    this.email = email;
    this.password = password;
    this.name = name;
    this.surname = surname;
  }

  // ************ METHODS ************
}
