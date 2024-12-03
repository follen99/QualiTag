package it.unisannio.studenti.qualitag.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.data.mongodb.core.mapping.*;

public class Artifact {

  @MongoId
  @Field(targetType = FieldType.OBJECT_ID)
  private String artifactId;

  @Field (name = "ArtifactName")
  private String artifactName;

  @Field(name="ArtifactContent")
  private String content;

  @Field(name="artifactTags")
  private List<String> tagIds;

  /**
   * Default constructor for Artifact
   */
  public Artifact() {
    this.tagIds = new ArrayList<>();
  }

  /**
   * Constructor for Artifact
   *
   * @param artifactName the name of the artifact
   * @param content     the content of the artifact
   */

  public Artifact(String artifactName, String content) {
    this.artifactName = artifactName;
    this.content = content;
    this.tagIds = new ArrayList<>();
  }

  /**
   * Constructor for Artifact with tags
   *
   * @param artifactName the name of the artifact
   * @param content     the content of the artifact
   * @param tagIds      the tags of the artifact
   */
  public Artifact(String artifactName, String content, List<String> tagIds) {
    this.artifactName = artifactName;
    this.content = content;
    this.tagIds = tagIds;
  }

  /**
   * Adds a tag to the artifact
   *
   * @param tagId the id of the tag to add
   */
  public void addTagId(String tagId) {
    this.tagIds.add(tagId);
  }

  /**
   * Removes a tag from the artifact
   *
   * @param tagId the id of the tag to remove
   */
  public void removeTagId(String tagId) {
    this.tagIds.remove(tagId);
  }

  /**
   * Checks if the artifact has a specific tag
   *
   * @param tagId the id of the tag to check
   * @return true if the artifact has the tag, false otherwise
   */
  public boolean isTagIdInArtifact(String tagId) {
    return this.tagIds.contains(tagId);
  }

  //GETTERS AND SETTERS

  /**
   * Gets the id of the artifact
   *
   * @return the id of the artifact
   */
  public String getArtifactId() {
    return artifactId;
  }

  /**
   * Gets the name of the artifact
   *
   * @return the name of the artifact
   */
  public String getArtifactName() {
    return artifactName;
  }

  /**
   * Sets the name of the artifact
   *
   * @param artifactName the name of the artifact
   */
  public void setArtifactName(String artifactName) {
    this.artifactName = artifactName;
  }

  /**
   * Gets the content of the artifact
   *
   * @return the content of the artifact
   */
  public String getContent() {
    return content;
  }

  /**
   * Sets the content of the artifact
   *
   * @param content the content of the artifact
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * Gets the tags of the artifact
   *
   * @return the tags of the artifact
   */
  public List<String> getTagIds() {
    return Collections.unmodifiableList(tagIds);
  }

  /**
   * Sets the tags of the artifact
   *
   * @param tagIds the tags of the artifact
   */
  public void setTagIds(List<String> tagIds) {
    this.tagIds = tagIds;
  }


  //EQUALS AND HASHCODE
  /**
   * Checks if two artifacts are equal
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
    return Objects.equals(artifactId, artifact.getArtifactId());
  }

  /**
   * Generates the hashcode of the artifact
   *
   * @return the hashcode of the artifact
   */
  @Override
  public int hashCode() {
    return Objects.hash(getArtifactId());
  }

  //TO STRING

  /**
   * Generates the string representation of the artifact
   *
   * @return the string representation of the artifact
   */
  public String toString() {
   return "Artifact{" +
       "artifactId='" + artifactId + '\'' +
       ", artifactName='" + artifactName + '\'' +
       ", content='" + content + '\'' +
       ", tagIds=" + tagIds +
       '}';
  }
}
