package it.unisannio.studenti.qualitag.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.Objects;

@Document(collection = "tags")
public class Tag {

  @Id
//  @SequenceGenerator(name = "TAG_SEQUENCE", sequenceName = "TAG_SEQUENCE_ID", initialValue = 1, allocationSize = 1)
//  @GeneratedValue(strategy = GenerationType.AUTO, generator = "TAG_SEQUENCE")
  private String tag_id;
  private String project_id;
  private String user_id;
  private String tag_value;
  private String color_hex; // Array to represent RGB values

  /**
   * Constructor with default tag color choosen randomly.
   *
   * @param tag_value  the value of the tag
   * @param project_id the ID of the project
   * @param user_id    the ID of the user
   */
  public Tag(String tag_value, String project_id, String user_id) {
    this.tag_value = tag_value;
    this.project_id = project_id;
    this.user_id = user_id;
    this.color_hex = this.chooseColor();  // Choose a random color from the default colors
  }

  public Tag(String tag_value, String project_id, String user_id, String tag_color_hex) {
    this.tag_value = tag_value;
    this.project_id = project_id;
    this.user_id = user_id;
    this.color_hex = tag_color_hex;
  }

  /**
   * Default constructor for Tag.
   */
  public Tag() {

  }

  private String chooseColor() {
    DefaultColor[] colors = DefaultColor.values();
    int randomIndex = (int) (Math.random() * colors.length);
    return colors[randomIndex].getRgb();
  }

  // GETTERS AND SETTERS

  /**
   * Gets the tag ID.
   *
   * @return the tag ID
   */
  public String getTag_id() {
    return tag_id;
  }

  /**
   * Sets the tag ID.
   *
   * @param tag_id the tag ID to set
   */
  public void setTag_id(String tag_id) {
    this.tag_id = tag_id;
  }

  /**
   * Gets the project ID.
   *
   * @return the project ID
   */
  public String getProject_id() {
    return project_id;
  }

  /**
   * Sets the project ID.
   *
   * @param project_id the project ID to set
   */
  public void setProject_id(String project_id) {
    this.project_id = project_id;
  }

  /**
   * Gets the user ID.
   *
   * @return the user ID
   */
  public String getUser_id() {
    return user_id;
  }

  /**
   * Sets the user ID.
   *
   * @param user_id the user ID to set
   */
  public void setUser_id(String user_id) {
    this.user_id = user_id;
  }

  /**
   * Gets the value of the tag.
   *
   * @return the value of the tag
   */
  public String getTag_value() {
    return tag_value;
  }

  /**
   * Sets the value of the tag.
   *
   * @param value the value to set
   */
  public void setTag_value(String value) {
    this.tag_value = value;
  }

  /**
   * Gets the RGB color of the tag as a string.
   *
   * @return the RGB color as a string
   */
  public String getColorAsHex() {
    return this.color_hex;
  }

  /**
   * Gets the RGB color of the tag.
   *
   * @return the RGB color
   */
  public int[] getRgb() {
    int[] rgb = new int[3];
    rgb[0] = Integer.parseInt(color_hex.substring(1, 3), 16);
    rgb[1] = Integer.parseInt(color_hex.substring(3, 5), 16);
    rgb[2] = Integer.parseInt(color_hex.substring(5, 7), 16);
    return rgb;
  }

  /**
   * Sets the RGB color of the tag.
   *
   * @param rgb the RGB color to set
   */
  public void setRgb(int[] rgb) {
    this.color_hex = String.format("#%02x%02x%02x", rgb[0], rgb[1], rgb[2]);
  }

  /**
   * Sets the RGB color of the tag.
   *
   * @param r the red value
   * @param g the green value
   * @param b the blue value
   */
  public void setRgb(int r, int g, int b) {
    this.color_hex = String.format("#%02x%02x%02x", r, g, b);
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
    return Objects.equals(getTag_id(), tag.getTag_id()) && Objects.equals(
        getProject_id(), tag.getProject_id()) && Objects.equals(getUser_id(),
        tag.getUser_id()) && Objects.equals(getTag_value(), tag.getTag_value())
        && Objects.equals(color_hex, tag.color_hex);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTag_id(), getProject_id(), getUser_id(), getTag_value(), color_hex);
  }

  // TO STRING

  /**
   * Gets the tag as a string.
   *
   * @return the tag as a string
   */
  @Override
  public String toString() {
    return "Tag{" +
        "tag_id='" + tag_id + '\'' +
        ", project_id='" + project_id + '\'' +
        ", user_id='" + user_id + '\'' +
        ", value='" + tag_value + '\'' +
        ", rgb=" + Arrays.toString(this.getRgb()) +
        ", color_hex='" + color_hex + '\'' +
        '}';
  }
}
