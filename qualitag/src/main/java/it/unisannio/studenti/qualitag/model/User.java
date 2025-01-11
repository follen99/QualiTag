package it.unisannio.studenti.qualitag.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Represents the User in the system.
 */
@Data
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

  @Field(name = "resetTokenExpiration")
  private LocalDateTime resetTokenExpiration;

  @Field(name = "projectIds")
  private List<String> projectIds;

  @Field(name = "teamIds")
  private List<String> teamIds;

  @Field(name = "tagIds")
  private List<String> tagIds;

  @Field(name = "roles")
  private Map<String, Role> projectRoles;

  // Constructors

  /**
   * Constructs a new User with no parameters.
   */
  public User() {
    this.resetTokenExpiration = null;

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

    this.resetTokenExpiration = null;

    this.projectIds = new ArrayList<>();
    this.teamIds = new ArrayList<>();
    this.tagIds = new ArrayList<>();

    this.projectRoles = new HashMap<>();
  }

  // Methods
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
        + ", roles=" + projectRoles
        + '}';
  }

  /**
   * Converts the user's project roles to Spring Security GrantedAuthority. Each role is prefixed
   * with the project ID to make it unique. Example: "project-1234_OWNER"
   *
   * @return Collection of GrantedAuthority representing the user's roles.
   */
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return projectRoles.entrySet().stream()
        .map(entry -> new SimpleGrantedAuthority(
            entry.getKey() + ":" + entry.getValue()))
        .collect(Collectors.toList());
  }

  /**
   * Returns a map of project IDs to roles as strings.
   *
   * @return Map of project IDs to roles.
   */
  public Map<String, String> getProjectRolesAsString() {
    return projectRoles.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().name()));
  }
}