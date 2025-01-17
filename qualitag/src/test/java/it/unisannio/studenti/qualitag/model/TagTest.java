package it.unisannio.studenti.qualitag.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class TagTest {

  // @Test
  // void testDefaultConstructor() {
  //   Tag tag = new Tag();
  //   assertNull(tag.getTagId());
  //   assertNull(tag.getCreatedBy());
  //   assertNull(tag.getTagValue());
  //   assertNull(tag.getColorHex());
  // }

  // @Test
  // void testConstructorWithParameters() {
  //   Tag tag = new Tag("value", "user1", "#FFFFFF");
  //   assertNull(tag.getTagId());
  //   assertEquals("VALUE", tag.getTagValue());
  //   assertEquals("user1", tag.getCreatedBy());
  //   assertEquals("#FFFFFF", tag.getColorHex());
  // }

  // @Test
  // void testSettersAndGetters() {
  //   Tag tag = new Tag();
  //   tag.setTagId("1");
  //   tag.setCreatedBy("user1");
  //   tag.setTagValue("value");
  //   tag.setColorHex("#FFFFFF");

  //   assertEquals("1", tag.getTagId());
  //   assertEquals("VALUE", tag.getTagValue());
  //   assertEquals("user1", tag.getCreatedBy());
  //   assertEquals("#FFFFFF", tag.getColorHex());
  // }

  // @Test
  // void testEquals() {
  //   Tag tag1 = new Tag("value", "user1", "#FFFFFF");
  //   tag1.setTagId("1");
  //   Tag tag2 = new Tag("VALUE", "user1", "#FFFFFF");
  //   tag2.setTagId("1");

  //   assertEquals(tag1, tag2);
  // }

  // @Test
  // void testNotEquals() {
  //   Tag tag1 = new Tag("value", "user1", "#FFFFFF");
  //   tag1.setTagId("1");
  //   Tag tag2 = new Tag("different", "user2", "#000000");
  //   tag2.setTagId("2");

  //   assertNotEquals(tag1, tag2);
  // }

  // @Test
  // void testHashCode() {
  //   Tag tag1 = new Tag("value", "user1", "#FFFFFF");
  //   tag1.setTagId("1");
  //   Tag tag2 = new Tag("VALUE", "user1", "#FFFFFF");
  //   tag2.setTagId("1");

  //   assertEquals(tag1.hashCode(), tag2.hashCode());
  // }

  // @Test
  // void testToString() {
  //   Tag tag = new Tag("value", "user1", "#FFFFFF");
  //   tag.setTagId("1");
  //   String expected = "Tag{tagId='1', createdBy='user1', tagValue='VALUE', colorHex='#FFFFFF'}";
  //   assertEquals(expected, tag.toString());
  // }

  // @Test
  // void testEqualsWithDifferentCase() {
  //   Tag tag1 = new Tag("value", "user1", "#FFFFFF");
  //   tag1.setTagId("1");
  //   Tag tag2 = new Tag("VALUE", "user1", "#FFFFFF");
  //   tag2.setTagId("1");

  //   assertEquals(tag1, tag2);
  // }

  // @Test
  // void testHashCodeWithDifferentCase() {
  //   Tag tag1 = new Tag("value", "user1", "#FFFFFF");
  //   tag1.setTagId("1");
  //   Tag tag2 = new Tag("VALUE", "user1", "#FFFFFF");
  //   tag2.setTagId("1");

  //   assertEquals(tag1.hashCode(), tag2.hashCode());
  // }

  // @Test
  // void testEqualsWithNull() {
  //   Tag tag1 = new Tag("value", "user1", "#FFFFFF");
  //   tag1.setTagId("1");
  //   Tag tag2 = null;

  //   assertNotEquals(tag1, tag2);
  // }

  // @Test
  // void testEqualsWithDifferentClass() {
  //   Tag tag1 = new Tag("value", "user1", "#FFFFFF");
  //   tag1.setTagId("1");
  //   String differentClassObject = "I am a string";

  //   assertNotEquals(tag1, differentClassObject);
  // }

  // @Test
  // void testEqualsWithDifferentTagId() {
  //   Tag tag1 = new Tag("value", "user1", "#FFFFFF");
  //   tag1.setTagId("1");
  //   Tag tag2 = new Tag("value", "user1", "#FFFFFF");
  //   tag2.setTagId("2");

  //   assertNotEquals(tag1, tag2);
  // }

  // @Test
  // void testEqualsWithDifferentCreatedBy() {
  //   Tag tag1 = new Tag("value", "user1", "#FFFFFF");
  //   tag1.setTagId("1");
  //   Tag tag2 = new Tag("value", "user2", "#FFFFFF");
  //   tag2.setTagId("1");

  //   assertNotEquals(tag1, tag2);
  // }

  // @Test
  // void testEqualsWithDifferentColorHex() {
  //   Tag tag1 = new Tag("value", "user1", "#FFFFFF");
  //   tag1.setTagId("1");
  //   Tag tag2 = new Tag("value", "user1", "#000000");
  //   tag2.setTagId("1");

  //   assertNotEquals(tag1, tag2);
  // }

}