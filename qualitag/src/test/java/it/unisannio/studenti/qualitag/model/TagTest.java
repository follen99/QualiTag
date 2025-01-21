package it.unisannio.studenti.qualitag.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TagTest {

  private Tag tag;
  private Tag equalTag;
  private Tag differentTag;

  /**
   * Sets up a tag for the test.
   */
  @BeforeEach
  public void setUp() {
    tag = new Tag("value", "user1", "#FFFFFF");
    tag.setTagId("tagId1");
    List<String> artifactIds = new ArrayList<>(Arrays.asList("artifactId1", "artifactId2"));
    tag.setArtifactIds(artifactIds);
    equalTag = new Tag("value", "user1", "#FFFFFF");
    equalTag.setTagId("tagId1");
    equalTag.setArtifactIds(artifactIds);
    differentTag = new Tag("different", "user2", "#000000");
    differentTag.setTagId("tagId2");
    List<String> artifactIds2 = new ArrayList<>(Arrays.asList("artifactId3", "artifactId4"));
    differentTag.setArtifactIds(artifactIds2);
  }

  /**
   * Test the default constructor.
   */
  @Test
  void testDefaultConstructor() {
    Tag defaultTag = new Tag();
    assertNull(defaultTag.getTagId());
    assertNull(defaultTag.getCreatedBy());
    assertNull(defaultTag.getTagValue());
    assertNull(defaultTag.getColorHex());
    assertTrue(defaultTag.getArtifactIds().isEmpty());
  }

  /**
   * Test the constructor with parameters.
   */
  @Test
  void testConstructorWithParameters() {
    Tag tagWithParameter = new Tag("value", "user1", "#FFFFFF");
    assertNull(tagWithParameter.getTagId());
    assertEquals("VALUE", tagWithParameter.getTagValue());
    assertEquals("user1", tagWithParameter.getCreatedBy());
    assertEquals("#FFFFFF", tagWithParameter.getColorHex());
    assertTrue(tagWithParameter.getArtifactIds().isEmpty());
  }

  /**
   * Test the getTagValue method.
   */
  @Test
  void testGetTagValue() {
    assertEquals("VALUE", tag.getTagValue());
  }

  /**
   * Test the setTagValue method.
   */
  @Test
  void testSetTagValue() {
    tag.setTagValue("different");
    assertEquals("DIFFERENT", tag.getTagValue());
  }


  /**
   * Test the getTagId method.
   */
  @Test
  void testGetTagId() {
    assertEquals("tagId1", tag.getTagId());
  }

  /**
   * Test the setTagId method.
   */
  @Test
  void testGetCreatedBy() {
    assertEquals("user1", tag.getCreatedBy());
  }

  /**
   * Test the setCreatedBy method.
   */
  @Test
  void testSetCreatedBy() {
    tag.setCreatedBy("user2");
    assertEquals("user2", tag.getCreatedBy());
  }

  /**
   * Test the getColorHex method.
   */
  @Test
  void testGetColorHex() {
    assertEquals("#FFFFFF", tag.getColorHex());
  }

  /**
   * Test the setColorHex method.
   */
  @Test
  void testSetColorHex() {
    tag.setColorHex("#000000");
    assertEquals("#000000", tag.getColorHex());
  }

  /**
   * Test the getArtifactIds method.
   */
  @Test
  void testGetArtifactIds() {
    List<String> artifactIds = new ArrayList<>(Arrays.asList("artifactId1", "artifactId2"));
    assertEquals(artifactIds, tag.getArtifactIds());
  }

  /**
   * Test the setArtifactIds method.
   */
  @Test
  void testSetArtifactIds() {
    List<String> artifactIds = new ArrayList<>(Arrays.asList("artifactId3", "artifactId4"));
    tag.setArtifactIds(artifactIds);
    assertEquals(artifactIds, tag.getArtifactIds());
  }

  /**
   * Test the addArtifactId method.
   */
  @Test
  void testAddArtifactId() {
    tag.getArtifactIds().add("artifactId3");
    assertTrue(tag.getArtifactIds().contains("artifactId3"));
  }

  /**
   * Test the removeArtifactId method.
   */
  @Test
  void testRemoveArtifactId() {
    tag.getArtifactIds().remove("artifactId1");
    assertNotEquals(Arrays.asList("artifactId1", "artifactId2"), tag.getArtifactIds());
  }

  /**
   * Test if a newly created tag has an empty list of artifactIds.
   */
  @Test
  void testEmptyArtifactIdsList() {
    Tag newTag = new Tag();
    assertTrue(newTag.getArtifactIds().isEmpty());
  }

  /**
   * Test if the tag value is always uppercase.
   */
  @Test
  void testCaseInsensitiveTagValueComparison() {
    Tag tag2 = new Tag("VALUE", "user1", "#FFFFFF");
    tag2.setTagId("tagId1");
    assertEquals(tag, tag2);
  }

  /**
   * Test if two tags are equal.
   */
  @Test
  void testEquals() {
    assertEquals(tag, equalTag);
  }

  /**
   * test if two different tags are not equal.
   */
  @Test
  void testNotEquals() {
    assertNotEquals(tag, differentTag);
  }

  /**
   * Test if two equal tags have the same hashcode.
   */
  @Test
  void testHashCode() {
    assertEquals(tag, equalTag);
    assertEquals(tag.hashCode(), equalTag.hashCode());
  }

  /**
   * Test the toString method.
   */
  @Test
  void testToString() {
    String expected =
        "Tag{tagId='tagId1', createdBy='user1', tagValue='VALUE', colorHex='#FFFFFF', artifactIds=["
            +
            "artifactId1, artifactId2]}";
    assertEquals(expected, tag.toString());
  }

  /**
   * Test if two tags are different if one of them is null.
   */
  @Test
  void testEqualsWithNull() {
    Tag tag2 = null;
    assertNotEquals(tag, tag2);
  }

  /**
   * Test if a tag is equal to an object of a different class.
   */
  @Test
  void testEqualsWithDifferentClass() {
    String differentClassObject = "I am a string";
    assertNotEquals(tag, differentClassObject);
  }

  /**
   * test if two tags with different tagId are different TagId is the only field compares by the
   * equal method of the Tag class.
   */
  @Test
  void testEqualsWithDifferentTagId() {
    Tag tag2 = new Tag("value", "user1", "#FFFFFF");
    tag2.setTagId("tagId2");
    List<String> artifactIds = Arrays.asList("artifactId1", "artifactId2");
    tag2.setArtifactIds(artifactIds);

    assertNotEquals(tag, tag2);
  }

}