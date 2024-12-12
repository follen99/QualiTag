package it.unisannio.studenti.qualitag.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * Represents a project in the system. Created by Raffaele Izzo on 14/11/2024
 */
@Data
@NoArgsConstructor
@Document(collection = "project")
public class Project {

  @MongoId
  @Field(targetType = FieldType.OBJECT_ID)
  private String projectId;

  @Field(name = "projectName")
  private String projectName;

  @Field(name = "projectDescription")
  private String projectDescription;

  @Field(name = "projectCreationDate")
  private Long projectCreationDate;

  @Field(name = "projectDeadline")
  private Long projectDeadline;

  @Field(name = "projectOwner")
  private String ownerId;

  @Field(name = "projectUsers")
  private List<String> users;

  @Field(name = "projectTeams")
  private List<String> teams;

  @Field(name = "projectArtifacts")
  private List<String> artifacts;

  /**
   * Constructor for Project with no users, teams or artifacts.
   *
   * @param projectName        The project's name
   * @param projectDescription The project's description
   * @param projectDeadline    The project's deadline
   */
  public Project(String projectName, String projectDescription,
      Long projectCreationDate, Long projectDeadline, String ownerId, List<String> users) {
    this.projectName = projectName;
    this.projectDescription = projectDescription;
    this.projectCreationDate = projectCreationDate;
    this.projectDeadline = projectDeadline;
    this.ownerId = ownerId;

    this.users = users;
    this.teams = new ArrayList<>();
    this.artifacts = new ArrayList<>();
  }

  // TODO: Constructor with all args is never used, probably should be removed

  /**
   * Constructor for Project with a list of users, teams and artifacts as well as the ownerId.
   *
   * @param projectName        The project's name
   * @param projectDescription The project's description
   * @param projectDeadline    The project's deadline
   * @param ownerId            The id of the owner of the project
   * @param users              The ids of the users in the project
   * @param teams              The ids of the teams in the project
   * @param artifacts          The ids of the artifacts in the project
   */
  public Project(String projectName, String projectDescription,
      Long projectCreationDate, Long projectDeadline, String ownerId,
      List<String> users, List<String> teams, List<String> artifacts) {
    this.projectName = projectName;
    this.projectDescription = projectDescription;
    this.projectCreationDate = projectCreationDate;
    this.projectDeadline = projectDeadline;
    this.ownerId = ownerId;

    this.users = users;
    this.teams = teams;
    this.artifacts = artifacts;
  }

  // EQUALS AND HASHCODE

  /**
   * Checks if two projects are equal.
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
    return Objects.equals(projectId, project.projectId);
  }

  /**
   * Generates the hash code for the project.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(projectId);
  }

  // TO STRING

  /**
   * Gets the project as a string.
   *
   * @return the project as a string
   */
  @Override
  public String toString() {
    return "Project{"
        + "projectId='" + projectId
        + ", projectName='" + projectName
        + ", projectDescription='" + projectDescription
        + ", projectCreationDate=" + projectCreationDate
        + ", projectDeadline=" + projectDeadline
        + ", ownerId=" + ownerId
        + ", usersIds=" + users
        + ", teamsIds=" + teams
        + ", artifactsIds=" + artifacts
        + '}';
  }
}
