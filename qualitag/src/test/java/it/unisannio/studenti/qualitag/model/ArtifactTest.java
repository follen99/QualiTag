package it.unisannio.studenti.qualitag.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for the Artifact class.
 */
public class ArtifactTest {

  // private Artifact artifact;

  // /**
  //  * Set up an artifact object for the tests.
  //  */
  // @BeforeEach
  // public void setUp() {
  //   artifact = new Artifact("name", "content");
  // }

  // /**
  //  * Test the getArtifactId method.
  //  */
  // @Test
  // public void testGetArtifactId() {
  //   assertNull(artifact.getArtifactId()); //initially artifactId is null
  // }

  // /**
  //  * Test the getArtifactName method.
  //  */
  // @Test
  // public void testGetArtifactName() {
  //   assertEquals("name", artifact.getArtifactName());
  // }

  // /**
  //  * Test the setArtifactName method.
  //  */
  // @Test
  // public void testSetArtifactName() {
  //   artifact.setArtifactName("newName");
  //   assertEquals("newName", artifact.getArtifactName());
  // }

  // /**
  //  * Test the getContent method.
  //  */
  // @Test
  // public void testGetContent() {
  //   assertEquals("content", artifact.getContent());
  // }

  // /**
  //  * Test the setContent method.
  //  */
  // @Test
  // public void testSetContent() {
  //   artifact.setContent("newContent");
  //   assertEquals("newContent", artifact.getContent());
  // }

  // /**
  //  * Test the getTagIds method.
  //  */
  // @Test
  // public void addAndRemoveTagId() {
  //   artifact.addTagId("tagId");
  //   assertTrue(artifact.getTags().contains("tagId"));
  //   artifact.removeTagId("tagId");
  //   assertFalse(artifact.getTags().contains("tagId"));
  // }

  // /**
  //  * Test the equals and hashCode methods.
  //  */
  // @Test
  // public void testEqualsAndHashCode() {
  //   Artifact sameArtifact = new Artifact("name", "content");
  //   sameArtifact.addTagId("tagId");
  //   assertEquals(sameArtifact, artifact);
  //   assertEquals(sameArtifact.hashCode(), artifact.hashCode());
  // }

  // /**
  //  * Test the toString method.
  //  */
  // @Test
  // public void testToString() {
  //   String expected = "Artifact{artifactId='null', artifactName='name', "
  //       + "content='content', tagIds=[]}";
  //   assertEquals(expected, artifact.toString());
  // }
}
