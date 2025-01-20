package it.unisannio.studenti.qualitag.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TeamTest {

  private Team team;
  private Team equalTeam;
  private Team differentTeam;

  /**
   * Sets up a team for the tests.
   */
  @BeforeEach
  public void setUp() {
    team = new Team("TeamName", "projectId", 123456789L, "Description",
        new ArrayList<>(Arrays.asList("user1", "user2")));
    team.setTeamId("teamId1");
    List<String> artifactIds = new ArrayList<>(Arrays.asList("artifactId1", "artifactId2"));
    team.setArtifactIds(artifactIds);

    equalTeam = new Team("TeamName", "projectId", 123456789L, "Description",
        new ArrayList<>(Arrays.asList("user1", "user2")));
    equalTeam.setTeamId("teamId1");
    equalTeam.setArtifactIds(artifactIds);

    differentTeam = new Team("DifferentTeamName", "differentProjectId", 987654321L,
        "DifferentDescription", new ArrayList<>(Arrays.asList("user3", "user4")));
    differentTeam.setTeamId("teamId2");
    List<String> artifactIds2 = new ArrayList<>(Arrays.asList("artifactId3", "artifactId4"));
    differentTeam.setArtifactIds(artifactIds2);
  }

  /**
   * Test the default constructor.
   */
  @Test
  void testDefaultConstructor() {
    Team defaultTeam = new Team();
    assertNull(defaultTeam.getTeamId());
    assertNull(defaultTeam.getProjectId());
    assertNotNull(defaultTeam.getUserIds());
    assertTrue(defaultTeam.getUserIds().isEmpty());
    assertNotNull(defaultTeam.getArtifactIds());
    assertTrue(defaultTeam.getArtifactIds().isEmpty());
    assertNull(defaultTeam.getTeamName());
    assertNull(defaultTeam.getCreationTimeStamp());
    assertNull(defaultTeam.getTeamDescription());
  }

  /**
   * Test the constructor with parameters.
   */
  @Test
  void testConstructorWithParameters() {
    List<String> users = Arrays.asList("user1", "user2");
    Team teamWithParams = new Team("TeamName", "projectId", 123456789L, "Description", users);
    assertNull(teamWithParams.getTeamId());
    assertEquals("projectId", teamWithParams.getProjectId());
    assertEquals("TeamName", teamWithParams.getTeamName());
    assertEquals(123456789L, teamWithParams.getCreationTimeStamp());
    assertEquals("Description", teamWithParams.getTeamDescription());
    assertEquals(users, teamWithParams.getUserIds());
    assertTrue(teamWithParams.getArtifactIds().isEmpty());
  }

  /**
   * Test the getTeamId method.
   */
  @Test
  void testGetTeamId() {
    assertEquals("teamId1", team.getTeamId());
  }

  /**
   * Test the setTeamId method.
   */
  @Test
  void testGetProjectId() {
    assertEquals("projectId", team.getProjectId());
  }

  /**
   * Test the setProjectId method.
   */
  @Test
  void testSetProjectId() {
    team.setProjectId("newProjectId");
    assertEquals("newProjectId", team.getProjectId());
  }

  /**
   * Test the getUserIds method.
   */
  @Test
  void testGetUserIds() {
    assertEquals(Arrays.asList("user1", "user2"), team.getUserIds());
  }

  /**
   * Test the setUserIds method.
   */
  @Test
  void testSetUserIds() {
    List<String> users = Arrays.asList("user3", "user4");
    team.setUserIds(users);
    assertEquals(users, team.getUserIds());
  }

  /**
   * Test the getArtifactIds method.
   */
  @Test
  void testGetArtifactIds() {
    assertEquals(Arrays.asList("artifactId1", "artifactId2"), team.getArtifactIds());
  }

  /**
   * Test the setArtifactIds method.
   */
  @Test
  void testSetArtifactIds() {
    List<String> artifacts = Arrays.asList("artifactId3", "artifactId4");
    team.setArtifactIds(artifacts);
    assertEquals(artifacts, team.getArtifactIds());
  }

  /**
   * Test the setTeamId method.
   */
  @Test
  void testGetTeamName() {
    assertEquals("TeamName", team.getTeamName());
  }

  /**
   * Test the setTeamName method.
   */
  @Test
  void testSetTeamName() {
    team.setTeamName("newTeamName");
    assertEquals("newTeamName", team.getTeamName());
  }

  /**
   * Test the getCreationTimeStamp method.
   */
  @Test
  void testGetCreationTimeStamp() {
    assertEquals(123456789L, team.getCreationTimeStamp());
  }

  /**
   * Test the setCreationTimeStamp method.
   */
  @Test
  void testSetCreationTimeStamp() {
    team.setCreationTimeStamp(987654321L);
    assertEquals(987654321L, team.getCreationTimeStamp());
  }

  /**
   * Test the getTeamDescription method.
   */
  @Test
  void testGetTeamDescription() {
    assertEquals("Description", team.getTeamDescription());
  }

  /**
   * Test the setTeamDescription method.
   */
  @Test
  void testSetTeamDescription() {
    team.setTeamDescription("newDescription");
    assertEquals("newDescription", team.getTeamDescription());
  }

  /**
   * Test the addUser method.
   */
  @Test
  void testAddUser() {
    team.getUserIds().add("user3");
    assertTrue(team.getUserIds().contains("user3"));
  }

  /**
   * Test the removeUser method.
   */
  @Test
  void testRemoveUser() {
    team.getUserIds().remove("user1");
    assertNotEquals(Arrays.asList("user1", "user2"), team.getUserIds());
  }

  /**
   * Test the addArtifactId method.
   */
  @Test
  void testAddArtifactId() {
    team.getArtifactIds().add("artifactId3");
    assertTrue(team.getArtifactIds().contains("artifactId3"));
  }

  /**
   * Test the removeArtifactId method.
   */
  @Test
  void testRemoveArtifactId() {
    team.getArtifactIds().remove("artifactId1");
    assertNotEquals(Arrays.asList("artifactId1", "artifactId2"), team.getArtifactIds());
  }

  /**
   * Test the equals' method.
   */
  @Test
  void testEquals() {
    assertEquals(team, equalTeam);
  }

  /**
   * Test the equals method with different objects.
   */
  @Test
  void testNotEquals() {
    assertNotEquals(team, differentTeam);
  }

  /**
   * Test the hashCode method.
   */
  @Test
  void testHashCode() {
    assertEquals(team.hashCode(), equalTeam.hashCode());
  }

  /**
   * Test the toString method.
   */
  @Test
  void testToString() {
    String expected = "Team{team_id='teamId1', project_id='projectId', users=[user1, user2], team_name='TeamName', creation_date=123456789, team_description='Description'}";
    assertEquals(expected, team.toString());
  }

  /**
   * Test equals with different cases.
   */
  @Test
  void testEqualsWithNull() {
    assertNotEquals(team, null);
  }

  /**
   * Test equals with different class.
   */
  @Test
  void testEqualsWithDifferentClass() {
    assertNotEquals(team, "I am a string");
  }

  /**
   * Test equals with the same object.
   */
  @Test
  void testEqualsWithSameObject() {
    assertEquals(team, team);
  }

  /**
   * Test equals with different teamId.
   */
  @Test
  void testEqualsWithDifferentTeamId() {
    Team team2 = new Team("TeamName", "projectId", 123456789L, "Description",
        new ArrayList<>(Arrays.asList("user1", "user2")));
    team2.setTeamId("teamId2");
    assertNotEquals(team, team2);
  }

  /**
   * Test equals with different projectId.
   */
  @Test
  void testEqualsWithDifferentProjectId() {
    Team team2 = new Team("TeamName", "differentProjectId", 123456789L, "Description",
        new ArrayList<>(Arrays.asList("user1", "user2")));
    team2.setTeamId("teamId1");
    assertNotEquals(team, team2);
  }

  /**
   * Test equals with different users.
   */
  @Test
  void testEqualsWithDifferentUsers() {
    Team team2 = new Team("TeamName", "projectId", 123456789L, "Description",
        new ArrayList<>(Arrays.asList("user3", "user4")));
    team2.setTeamId("teamId1");
    assertNotEquals(team, team2);
  }

  /**
   * Test equals with different teamName.
   */
  @Test
  void testEqualsWithDifferentTeamName() {
    Team team2 = new Team("DifferentTeamName", "projectId", 123456789L, "Description",
        new ArrayList<>(Arrays.asList("user1", "user2")));
    team2.setTeamId("teamId1");
    assertNotEquals(team, team2);
  }

  /**
   * Test equals with different creationTimeStamp.
   */
  @Test
  void testEqualsWithDifferentCreationTimeStamp() {
    Team team2 = new Team("TeamName", "projectId", 987654321L, "Description",
        new ArrayList<>(Arrays.asList("user1", "user2")));
    team2.setTeamId("teamId1");
    assertNotEquals(team, team2);
  }

  /**
   * Test equals with different teamDescription.
   */
  @Test
  void testEqualsWithDifferentTeamDescription() {
    Team team2 = new Team("TeamName", "projectId", 123456789L, "DifferentDescription",
        new ArrayList<>(Arrays.asList("user1", "user2")));
    team2.setTeamId("teamId1");
    assertNotEquals(team, team2);
  }
}