package it.unisannio.studenti.qualitag.model;

import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Tag {

  @Id
//  @SequenceGenerator(name = "TAG_SEQUENCE", sequenceName = "TAG_SEQUENCE_ID", initialValue = 1, allocationSize = 1)
//  @GeneratedValue(strategy = GenerationType.AUTO, generator = "TAG_SEQUENCE")
  private String tagId;
  private String projectId;
  private String userId;
  private String tagValue;
  private String colorHex;

  /**
   * Default constructor.
   */
  public Tag() {
  }


  /**
   * Constructor with default tag color choosen randomly.
   *
   * @param tagValue  the value of the tag
   * @param projectId the ID of the project
   * @param userId    the ID of the user
   */
  public Tag(String tagValue, String projectId, String userId) {
    this.tagValue = tagValue;
    this.projectId = projectId;
    this.userId = userId;
    this.colorHex = this.chooseColor();  // Choose a random color from the default colors
  }

  /**
   * Constructor with a specific tag color (hex).
   *
   * @param tagValue     the value of the tag
   * @param projectId    the ID of the project
   * @param userId       the ID of the user
   * @param tag_color_hex the color of the tag
   */
  public Tag(String tagValue, String projectId, String userId, String tag_color_hex) {
    this.tagValue = tagValue;
    this.projectId = projectId;
    this.userId = userId;
    this.colorHex = tag_color_hex;
  }

  private String chooseColor() {
    DefaultColor[] colors = DefaultColor.values();
    int randomIndex = (int) (Math.random() * colors.length);
    return colors[randomIndex].getRgb();
  }

  // GETTERS AND SETTERS

  public String getTagId() {
    return tagId;
  }

  public void setTagId(String tagId) {
    this.tagId = tagId;
  }

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getTagValue() {
    return tagValue;
  }

  public void setTagValue(String tagValue) {
    this.tagValue = tagValue;
  }

  public String getColorHex() {
    return colorHex;
  }

  public void setColorHex(String colorHex) {
    this.colorHex = colorHex;
  }


  // EQUALS AND HASHCODE

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Tag tag = (Tag) o;
    return Objects.equals(getTagId(), tag.getTagId()) && Objects.equals(
        getProjectId(), tag.getProjectId()) && Objects.equals(getUserId(),
        tag.getUserId()) && Objects.equals(getTagValue(), tag.getTagValue())
        && Objects.equals(colorHex, tag.colorHex);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTagId(), getProjectId(), getUserId(), getTagValue(), colorHex);
  }

  // TO STRING


  @Override
  public String toString() {
    return "Tag{" +
        "tag_id='" + tagId + '\'' +
        ", project_id='" + projectId + '\'' +
        ", user_id='" + userId + '\'' +
        ", tag_value='" + tagValue + '\'' +
        ", color_hex='" + colorHex + '\'' +
        '}';
  }
}
