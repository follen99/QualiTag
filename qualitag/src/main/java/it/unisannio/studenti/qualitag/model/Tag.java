package it.unisannio.studenti.qualitag.model;

import java.util.Objects;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * Represents a tag in the system.
 */
@Data
@Document
public class Tag {

  @MongoId
  @Field(targetType = FieldType.OBJECT_ID)
  private String tagId;
  private String createdBy;   // user username (not user ID)
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
   * @param tagValue    the value of the tag
   * @param createdBy   the ID of the user
   * @param tagColorHex the color of the tag
   */
  public Tag(String tagValue, String createdBy, String tagColorHex) {
    this.tagValue = tagValue.toUpperCase();   // tag values are always uppercase
    this.createdBy = createdBy;
    this.colorHex = tagColorHex;
  }

  /**
   * Constructor with a specific tag color (hex).
   *
   * @param tagValue the value of the tag
   */
  public void setTagValue(String tagValue) {
    this.tagValue = tagValue.toUpperCase();
  }

  // EQUALS AND HASHCODE

  /**
   * Compares this tag to another object. Ignores tagValue case.
   *
   * <p>Example: equals(new Tag("tag1", "user1", "color1"), new Tag("TAG1", "user1", "color1"))
   * returns true.
   *
   * @param o The object to compare to.
   * @return True if the objects are equal, false otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Tag tag = (Tag) o;
    return Objects.equals(tagId, tag.tagId) && Objects.equals(createdBy, tag.createdBy)
        && Objects.equals(tagValue.toUpperCase(), tag.tagValue.toUpperCase()) && Objects.equals(
        colorHex, tag.colorHex);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tagId, createdBy, tagValue, colorHex);
  }


  // TO STRING
  @Override
  public String toString() {
    return "Tag{" + "tagId='" + tagId + '\'' + ", createdBy='" + createdBy + '\'' + ", tagValue='"
        + tagValue.toUpperCase() + '\'' + ", colorHex='" + colorHex + '\'' + '}';
  }
}
