package it.unisannio.studenti.qualitag.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * Represents an artifact in the system.
 */
@Data
@Document(collection = "artifact")
public class Artifact {

  @MongoId
  @Field(targetType = FieldType.OBJECT_ID)
  private String artifactId;

  @Field(name = "artifactName")
  private String artifactName;

  @Field(name = "description")
  private String description;

  @Field(name = "projectId")
  private String projectId;

  @Field(name = "teamId")
  private String teamId;

  @Field(name = "filePath")
  private String filePath;

  @Field(name = "artifactTags")
  private List<String> tags;

  @Field(name = "isTaggingOpen")
  private boolean isTaggingOpen;


  /**
   * Default constructor for Artifact.
   */
  public Artifact() {
    this.tags = new ArrayList<>();
  }

  /**
   * Constructor for Artifact, without description.
   *
   * @param artifactName The name of the artifact
   * @param projectId The id of the project the artifact belongs to
   * @param teamId The id of the team the artifact belongs to
   * @param filePath The path of the file of the artifact in the system
   */
  public Artifact(String artifactName, String projectId, String teamId, String filePath) {
    this.artifactName = artifactName;
    this.projectId = projectId;
    this.teamId = teamId;
    this.filePath = filePath;
    this.tags = new ArrayList<>();
    this.isTaggingOpen = true;
  }

  /**
   * Constructor for Artifact, without description.
   *
   * @param artifactName The name of the artifact
   * @param projectId The id of the project the artifact belongs to
   * @param teamId The id of the team the artifact belongs to
   * @param filePath The path of the file of the artifact in the system
   * @param isTaggingOpen The tagging status of the artifact
   */
  public Artifact(String artifactName, String projectId, String teamId, String filePath,
      Boolean isTaggingOpen) {
    this.artifactName = artifactName;
    this.projectId = projectId;
    this.teamId = teamId;
    this.filePath = filePath;
    this.tags = new ArrayList<>();
    this.isTaggingOpen = isTaggingOpen;
  }

  /**
   * Constructor for Artifact, with description.
   *
   * @param artifactName The name of the artifact
   * @param description The description of the artifact
   * @param projectId The id of the project the artifact belongs to
   * @param teamId The id of the team the artifact belongs to
   * @param filePath The path of the file of the artifact in the system
   */
  public Artifact(String artifactName, String description, String projectId, String teamId,
      String filePath) {
    this.artifactName = artifactName;
    this.description = description;
    this.projectId = projectId;
    this.teamId = teamId;
    this.filePath = filePath;
    this.tags = new ArrayList<>();
    this.isTaggingOpen = true;
  }

  // EQUALS AND HASHCODE

  /**
   * Checks if two artifacts are equal.
   *
   * @param o the object to compare
   * @return true if the artifacts are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Artifact artifact = (Artifact) o;
    return Objects.equals(artifactId, artifact.artifactId);
  }

  /**
   * Generates the hashcode of the artifact.
   *
   * @return the hashcode of the artifact
   */
  @Override
  public int hashCode() {
    return Objects.hash(artifactId);
  }

  /**
   * Generates the string representation of the artifact.
   *
   * @return the string representation of the artifact
   */
  @Override
  public String toString() {
    return "Artifact{" + "artifactId='" + artifactId + '\'' + ", artifactName='" + artifactName
        + '\'' + ", description='" + description + '\'' + ", projectId='" + projectId + '\''
        + ", teamId='" + teamId + '\'' + ", filePath='" + filePath + '\'' + ", tags=" + tags
        + ", isTaggingOpen=" + isTaggingOpen + '}';
  }

}
