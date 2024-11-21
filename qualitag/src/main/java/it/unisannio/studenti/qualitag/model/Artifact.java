package it.unisannio.studenti.qualitag.model;

import java.util.ArrayList;
import java.util.Objects;
import org.springframework.data.mongodb.core.mapping.*;

public class Artifact {

  @MongoId
  @Field(targetType = FieldType.OBJECT_ID)
  private String artifactId;

  @Field(name="artifactTags")
  private ArrayList<Tag> tags;

  @Field(name="ArtifactContent")
  private String content;

  /**
   * Default constructor for Artifact
   */
  public Artifact() {
    this.tags = new ArrayList<Tag>();
  }

  /**
   * Constructor for Artifact
   *
   * @param artifactId the id of the artifact
   * @param content     the content of the artifact
   */

  public Artifact(String artifactId, String content) {
    this.artifactId = artifactId;
    this.tags = new ArrayList<Tag>();
    this.content = content;
  }

  /**
   * Adds a tag to the artifact
   *
   * @param tag the tag to add
   */
  public void addTag(Tag tag) {
    this.tags.add(tag);
  }

  /**
   * Removes a tag from the artifact
   *
   * @param tag the tag to remove
   */
  public void removeTag(Tag tag) {
    this.tags.remove(tag);
  }

  /**
   * Checks if the artifact has a specific tag
   *
   * @param tag the tag to check
   * @return true if the artifact has the tag, false otherwise
   */
  public boolean isTagInArtifact(Tag tag) {
    return this.tags.contains(tag);
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
   * Sets the id of the artifact
   *
   * @param artifactId the id of the artifact
   */
  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  /**
   * Gets the tags of the artifact
   *
   * @return the tags of the artifact
   */
  public ArrayList<Tag> getTags() {
    return tags;
  }

  /**
   * Sets the tags of the artifact
   *
   * @param tags the tags of the artifact
   */
  public void setTags(ArrayList<Tag> tags) {
    this.tags = tags;
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
    return Objects.equals(artifactId, artifact.getArtifactId()) &&
        Objects.equals(tags, artifact.getTags()) &&
        Objects.equals(content, artifact.getContent());
  }

  /**
   * Generates the hashcode of the artifact
   *
   * @return the hashcode of the artifact
   */
  @Override
  public int hashCode() {
    return Objects.hash(getArtifactId(), getTags(), getContent());
  }

  //TO STRING

  /**
   * Generates the string representation of the artifact
   *
   * @return the string representation of the artifact
   */
  public String toString() {
    return "Artifact{" +
        "artifact_id='" + artifactId + '\'' +
        ", tags=" + tags +
        ", content='" + content + '\'' +
        '}';
  }
}
