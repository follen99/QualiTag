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

  @Field(name = "ArtifactName")
  private String artifactName;

  @Field(name = "ArtifactContent")
  private String content;

  @Field(name = "artifactTags")
  private List<String> tags;

  /**
   * Default constructor for Artifact.
   */
  public Artifact() {
    this.tags = new ArrayList<>();
  }

  /**
   * Constructor for Artifact.
   *
   * @param artifactName the name of the artifact
   * @param content      the content of the artifact
   */
  public Artifact(String artifactName, String content) {
    this.artifactName = artifactName;
    this.content = content;
    this.tags = new ArrayList<>();
  }

  /**
   * Constructor for Artifact with tags.
   *
   * @param artifactName the name of the artifact
   * @param content      the content of the artifact
   * @param tags         the tags of the artifact
   */
  public Artifact(String artifactName, String content, List<String> tags) {
    this.artifactName = artifactName;
    this.content = content;
    this.tags = tags;
  }

  /**
   * Adds a tag to the artifact.
   *
   * @param tagId the id of the tag to add
   */
  public void addTagId(String tagId) {
    this.tags.add(tagId);
  }

  /**
   * Removes a tag from the artifact.
   *
   * @param tagId the id of the tag to remove
   */
  public void removeTagId(String tagId) {
    this.tags.remove(tagId);
  }

  /**
   * Checks if the artifact has a specific tag.
   *
   * @param tagId the id of the tag to check
   * @return true if the artifact has the tag, false otherwise
   */
  public boolean isTagIdInArtifact(String tagId) {
    return this.tags.contains(tagId);
  }

  //EQUALS AND HASHCODE

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
  public String toString() {
    return "Artifact{"
        + "artifactId='" + artifactId + '\''
        + ", artifactName='" + artifactName + '\''
        + ", content='" + content + '\''
        + ", tagIds=" + tags
        + '}';
  }
}
