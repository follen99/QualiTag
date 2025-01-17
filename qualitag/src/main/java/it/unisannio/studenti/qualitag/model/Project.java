package it.unisannio.studenti.qualitag.model;

import it.unisannio.studenti.qualitag.dto.project.ProjectInfoDto;
import it.unisannio.studenti.qualitag.dto.project.WholeProjectDto;
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

  @Field(name = "ownerId")
  private String ownerId;

  @Field(name = "projectStatus")
  private ProjectStatus projectStatus;

  @Field(name = "usersIds")
  private List<String> userIds;

  @Field(name = "teamsIds")
  private List<String> teamIds;

  @Field(name = "artifactsIds")
  private List<String> artifactIds;

  /**
   * Constructor for Project with no users, teams or artifacts.
   *
   * @param projectName The project's name
   * @param projectDescription The project's description
   * @param projectDeadline The project's deadline
   */
  public Project(String projectName, String projectDescription, Long projectCreationDate,
      Long projectDeadline, String ownerId, List<String> userIds) {
    this.projectName = projectName;
    this.projectDescription = projectDescription;
    this.projectCreationDate = projectCreationDate;
    this.projectDeadline = projectDeadline;
    this.ownerId = ownerId;
    this.projectStatus = ProjectStatus.OPEN;

    this.userIds = userIds;
    this.teamIds = new ArrayList<>();
    this.artifactIds = new ArrayList<>();
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

  /**
   * Converts this Project entity to a ResponseProjectDto.
   *
   * @return the ResponseProjectDto
   */
  public WholeProjectDto toResponseProjectDto() {
    if (this.projectStatus == null) {
      this.projectStatus = ProjectStatus.NO_INFO;
    }
    if (this.projectDescription == null) {
      this.projectDescription = "";
    }
    if (this.projectName == null) {
      this.projectName = "";
    }

    return new WholeProjectDto(
            this.projectName, 
            this.projectDescription, 
            this.projectCreationDate,
            this.projectDeadline, 
            this.ownerId, 
            this.projectStatus.name(), 
            this.userIds, 
            this.artifactIds,
            this.teamIds);
  }

  /**
   * Converts this Project entity to a ProjectInfoDto.
   *
   * @return the ProjectInfoDto
   */
  public ProjectInfoDto toProjectInfoDto() {
    if (this.projectStatus == null) {
      this.projectStatus = ProjectStatus.NO_INFO;
    }
    if (this.projectDescription == null) {
      this.projectDescription = "";
    }
    if (this.projectName == null) {
      this.projectName = "";
    }

    return new ProjectInfoDto(this.projectName, this.projectDescription, this.projectStatus.name(),
        this.projectId);
  }

  /**
   * Gets the project as a string.
   *
   * @return the project as a string
   */
  @Override
  public String toString() {
    return "Project{" + "projectId='" + projectId + ", projectName='" + projectName
        + ", projectDescription='" + projectDescription + ", projectCreationDate="
        + projectCreationDate + ", projectDeadline=" + projectDeadline + ", ownerId=" + ownerId
        + ", usersIds=" + userIds + ", teamsIds=" + teamIds + ", artifactsIds=" + artifactIds + '}';
  }
}
