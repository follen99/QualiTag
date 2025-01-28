package it.unisannio.studenti.qualitag.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for the Artifact class.
 */
public class ArtifactTest {

  private Artifact artifact;
  private Artifact equalArtifact;
  private Artifact differentArtifact;


  /**
   * Sets up an artifact for the tests.
   */
  @BeforeEach
  public void setUp() {
    artifact = new Artifact("artifactName", "description", "projectId",
        "teamId", "filePath");
    equalArtifact = new Artifact("artifactName", "description", "projectId",
        "teamId", "filePath");
    differentArtifact = new Artifact("differentArtifactName", "differentDescription",
        "differentProjectId",
        "differentTeamId", "differentFilePath");
    differentArtifact.setArtifactId("differentArtifactId");
  }

  /**
   * Test the default constructor.
   */
  @Test
  public void testDefaultConstructor() {
    Artifact defaultArtifact = new Artifact();
    assertNull(defaultArtifact.getArtifactId());
    assertNull(defaultArtifact.getArtifactName());
    assertNull(defaultArtifact.getDescription());
    assertNull(defaultArtifact.getProjectId());
    assertNull(defaultArtifact.getTeamId());
    assertNull(defaultArtifact.getFilePath());
    assertTrue(defaultArtifact.getTags().isEmpty());
  }

  /**
   * Test the constructor without description.
   */
  @Test
  public void testConstructorWithoutDescription() {
    Artifact artifactWithoutDescription = new Artifact("artifactName", "projectId", "teamId",
        "filePath");
    assertEquals("artifactName", artifactWithoutDescription.getArtifactName());
    assertNull(artifactWithoutDescription.getDescription());
    assertEquals("projectId", artifactWithoutDescription.getProjectId());
    assertEquals("teamId", artifactWithoutDescription.getTeamId());
    assertEquals("filePath", artifactWithoutDescription.getFilePath());
    assertTrue(artifactWithoutDescription.getTags().isEmpty());
  }


  /**
   * Test the getArtifactId method.
   */
  @Test
  public void testGetArtifactId() {
    assertNull(artifact.getArtifactId());
  }


  /**
   * Test the getArtifactName method.
   */
  @Test
  public void testGetArtifactName() {
    assertEquals("artifactName", artifact.getArtifactName());
  }

  /**
   * Test the setArtifactName method.
   */
  @Test
  public void testSetArtifactName() {
    artifact.setArtifactName("newArtifactName");
    assertEquals("newArtifactName", artifact.getArtifactName());
  }

  /**
   * Test the getDescription method.
   */
  @Test
  public void testGetDescription() {
    assertEquals("description", artifact.getDescription());
  }

  /**
   * Test the setDescription method.
   */
  @Test
  public void testSetDescription() {
    artifact.setDescription("newDescription");
    assertEquals("newDescription", artifact.getDescription());
  }

  /**
   * Test the getProjectId method.
   */
  @Test
  public void testGetProjectId() {
    assertEquals("projectId", artifact.getProjectId());
  }

  /**
   * Test the setProjectId method.
   */
  @Test
  public void testSetProjectId() {
    artifact.setProjectId("newProjectId");
    assertEquals("newProjectId", artifact.getProjectId());
  }

  /**
   * Test the getTeamId method.
   */
  @Test
  public void testGetTeamId() {
    assertEquals("teamId", artifact.getTeamId());
  }

  /**
   * Test the setTeamId method.
   */
  @Test
  public void testSetTeamId() {
    artifact.setTeamId("newTeamId");
    assertEquals("newTeamId", artifact.getTeamId());
  }

  /**
   * Test the getFilePath method.
   */
  @Test
  public void testGetFilePath() {
    assertEquals("filePath", artifact.getFilePath());
  }

  /**
   * Test the setFilePath method.
   */
  @Test
  public void testSetFilePath() {
    artifact.setFilePath("newFilePath");
    assertEquals("newFilePath", artifact.getFilePath());
  }

  /**
   * Test the getTags method.
   */
  @Test
  public void testGetTags() {
    assertTrue(artifact.getTags().isEmpty());
  }

  /**
   * Test the addTag method.
   */
  @Test
  public void testAddTag() {
    artifact.getTags().add("tag1");
    assertTrue(artifact.getTags().contains("tag1"));
  }

  /**
   * Test the removeTag method.
   */
  @Test
  public void testRemoveTag() {
    artifact.getTags().add("tag1");
    artifact.getTags().remove("tag1");
    assertFalse(artifact.getTags().contains("tag1"));
  }

  /**
   * Test the equals' method.
   */
  @Test
  public void testEquals() {
    assertEquals(artifact, equalArtifact);
  }

  /**
   * Test the equals method with null.
   */
  @Test
  public void testEqualsWithNull() {
    assertFalse(artifact.equals(null));
  }

  /**
   * Test the equals method with a different object.
   */
  @Test
  public void testNotEquals() {
    assertFalse(artifact.equals(differentArtifact));
  }

  /**
   * Test the hashCode method.
   */
  @Test
  public void testHashCode() {
    assertEquals(artifact.hashCode(), equalArtifact.hashCode());
  }

  /**
   * Test the hashcode method with a different object.
   */
  @Test
  public void testNotEqualsWithTaggingOpen() {
    artifact.setTaggingOpen(true);
    differentArtifact.setTaggingOpen(false);
    assertNotEquals(artifact, differentArtifact);
  }

  /**
   * Test the hashcode method with a different object.
   */
  @Test
  public void testIsTaggingOpen() {
    artifact.setTaggingOpen(true);
    assertTrue(artifact.isTaggingOpen());
  }

  /**
   * Test the hashcode method with a different object.
   */
  @Test
  public void testSetTaggingOpen() {
    artifact.setTaggingOpen(true);
    assertTrue(artifact.isTaggingOpen());
    artifact.setTaggingOpen(false);
    assertFalse(artifact.isTaggingOpen());
  }

  /**
   * Test the toString method.
   */
  @Test
  public void testToStringWithTaggingOpen() {
    artifact.setTaggingOpen(true);
    String expected = "Artifact{artifactId='null', "
        + "artifactName='artifactName', description='description', "
        + "projectId='projectId', teamId='teamId', filePath='filePath', tags=[], isTaggingOpen=true}";
    assertEquals(expected, artifact.toString());
  }


  /**
   * Test equals with different cases.
   */
  @Test
  public void testEqualsWithDifferentCase() {
    Artifact sameArtifact = new Artifact("artifactName", "description", "projectId", "teamId",
        "filePath");
    sameArtifact.setArtifactId(artifact.getArtifactId());
    assertEquals(sameArtifact, artifact);
  }

  /**
   * Test equals with different cases.
   */
  @Test
  public void testEqualsWithTaggingOpen() {
    artifact.setTaggingOpen(true);
    equalArtifact.setTaggingOpen(true);
    assertEquals(artifact, equalArtifact);
  }

  /**
   * Test hashcode with different cases.
   */
  @Test
  public void testHashCodeWithDifferentCase() {
    Artifact sameArtifact = new Artifact("artifactName", "description", "projectId", "teamId",
        "filePath");
    sameArtifact.setArtifactId(artifact.getArtifactId());
    assertEquals(sameArtifact.hashCode(), artifact.hashCode());
  }

  /**
   * Test equals with different class.
   */
  @Test
  public void testEqualsWithDifferentClass() {
    assertNotEquals("Not an artifact", artifact);
  }


}

