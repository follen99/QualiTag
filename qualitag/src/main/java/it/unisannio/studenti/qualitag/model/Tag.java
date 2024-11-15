package it.unisannio.studenti.qualitag.model;

import java.util.Arrays;
import java.util.Objects;

public class Tag {
    private String tag_id;
    private String project_id;
    private String user_id;
    private String value;
    private int[] rgb = new int[3]; // Array to represent RGB values

    /**
     * Default constructor for Tag.
     */
    public Tag(String tag_id, String project_id, String user_id, String value, int[] tag_color) {
        this.tag_id = tag_id;           // Set the tag ID
        this.project_id = project_id;   // Set the project ID
        this.user_id = user_id;         // Set the user ID
        this.value = value;             // Set the value
        this.rgb = tag_color;           // Set the color
    }

    /**
     * Constructor with default tag color.
     *
     * @param tag_id the ID of the tag
     * @param project_id the ID of the project
     * @param user_id the ID of the user
     * @param value the value of the tag
     */
    public Tag(String tag_id, String project_id, String user_id, String value) {
        this.tag_id = tag_id;           // Set the tag ID
        this.project_id = project_id;   // Set the project ID
        this.user_id = user_id;         // Set the user ID
        this.value = value;             // Set the value
        this.rgb = this.chooseColor();  // Choose a random color from the default colors
    }

    private int[] chooseColor() {
        int random = (int) (Math.random() * DefaultColor.values().length);
        return DefaultColor.values()[random].getRgb();
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
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the tag.
     *
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the RGB color of the tag.
     *
     * @return the RGB color
     */
    public int[] getRgbAsIntArr() {
        return rgb;
    }

    /**
     * Gets the RGB color of the tag as a string.
     *
     * @return the RGB color as a string
     */
    public String getRgbAsHexString() {
        return String.format("#%02x%02x%02x", rgb[0], rgb[1], rgb[2]);
    }

    /**
     * Sets the RGB color of the tag.
     *
     * @param rgb the RGB color to set
     */
    public void setRgb(int[] rgb) {
        this.rgb = rgb;
    }

    /**
     * Sets the RGB color of the tag.
     *
     * @param r the red value
     * @param g the green value
     * @param b the blue value
     */
    public void setRgb(int r, int g, int b) {
        this.rgb[0] = r;
        this.rgb[1] = g;
        this.rgb[2] = b;
    }

    /**
     * Sets the RGB color of the tag.
     *
     * @param hex the hexadecimal color
     */
    public void setRgb(String hex) {
        this.rgb[0] = Integer.parseInt(hex.substring(1, 3), 16);
        this.rgb[1] = Integer.parseInt(hex.substring(3, 5), 16);
        this.rgb[2] = Integer.parseInt(hex.substring(5, 7), 16);
    }

    // EQUALS AND HASHCODE

    /**
     * Gets the tag as a string.
     *
     * @return the tag as a string
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(getTag_id(), tag.getTag_id()) && Objects.equals(getProject_id(), tag.getProject_id()) && Objects.equals(getUser_id(), tag.getUser_id()) && Objects.equals(getValue(), tag.getValue()) && Objects.deepEquals(getRgbAsIntArr(), tag.getRgbAsIntArr());
    }

    /**
     * Gets the hash code of the tag.
     *
     * @return the hash code of the tag
     */
    @Override
    public int hashCode() {
        return Objects.hash(getTag_id(), getProject_id(), getUser_id(), getValue(), Arrays.hashCode(getRgbAsIntArr()));
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
                ", value='" + value + '\'' +
                ", rgb=" + Arrays.toString(rgb) +
                '}';
    }
}
