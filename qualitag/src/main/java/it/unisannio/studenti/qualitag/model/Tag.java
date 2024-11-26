package it.unisannio.studenti.qualitag.model;

import java.util.Objects;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document
public class Tag {

  @MongoId
  @Field(targetType = FieldType.OBJECT_ID)
  private String tagId;
  private String createdBy;
  private String tagValue;
  private String colorHex;

  /**
   * Default constructor.
   */
  public Tag() {
  }


  /**
   * Constructor with a specific tag color (hex).
   *
   * @param tagValue     the value of the tag
   * @param createdBy       the ID of the user
   * @param tag_color_hex the color of the tag
   */
  public Tag(String tagValue, String createdBy, String tag_color_hex) {
    this.tagValue = tagValue;
    this.createdBy = createdBy;
    this.colorHex = tag_color_hex;
  }

  // GETTERS AND SETTERS

  public String getTagId() {
    return tagId;
  }

  public void setTagId(String tagId) {
    this.tagId = tagId;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
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
    if (o == null || getClass() != o.getClass()) return false;
    Tag tag = (Tag) o;
    return Objects.equals(tagId, tag.tagId) && Objects.equals(createdBy, tag.createdBy) && Objects.equals(tagValue, tag.tagValue) && Objects.equals(colorHex, tag.colorHex);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tagId, createdBy, tagValue, colorHex);
  }


  // TO STRING
  @Override
  public String toString() {
    return "Tag{" +
            "tagId='" + tagId + '\'' +
            ", createdBy='" + createdBy + '\'' +
            ", tagValue='" + tagValue + '\'' +
            ", colorHex='" + colorHex + '\'' +
            '}';
  }
}
