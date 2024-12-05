package it.unisannio.studenti.qualitag.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class TeamTest {

  @Test
  void testDefaultConstructor() {
    Team team = new Team();
    assertNull(team.getTeamId());
    assertNull(team.getProjectId());
    assertNotNull(team.getUsers());
    assertTrue(team.getUsers().isEmpty());
    assertNull(team.getTeamName());
    assertNull(team.getCreationTimeStamp());
    assertNull(team.getTeamDescription());
  }

  @Test
  void testConstructorWithParameters() {
    List<String> users = Arrays.asList("user1", "user2");
    Team team = new Team("TeamName", 123456789L, "Description", users);
    assertNull(team.getTeamId());
    assertNull(team.getProjectId());
    assertEquals("TeamName", team.getTeamName());
    assertEquals(123456789L, team.getCreationTimeStamp());
    assertEquals("Description", team.getTeamDescription());
    assertEquals(users, team.getUsers());
  }

  @Test
  void testSettersAndGetters() {
    Team team = new Team();
    team.setTeamId("1");
    team.setProjectId("project1");
    team.setTeamName("TeamName");
    team.setCreationTimeStamp(123456789L);
    team.setTeamDescription("Description");
    List<String> users = new ArrayList<>(Arrays.asList("user1", "user2"));
    team.setUsers(users);

    assertEquals("1", team.getTeamId());
    assertEquals("project1", team.getProjectId());
    assertEquals("TeamName", team.getTeamName());
    assertEquals(123456789L, team.getCreationTimeStamp());
    assertEquals("Description", team.getTeamDescription());
    assertEquals(users, team.getUsers());
  }

  @Test
  void testAddUser() {
    Team team = new Team();
    User user = new User();
    user.setUserId("user1");
    team.addUser(user);
    assertEquals(1, team.getUsers().size());
    assertEquals("user1", team.getUsers().getFirst());
  }

  @Test
  void testEquals() {
    List<String> users = Arrays.asList("user1", "user2");
    Team team1 = new Team("TeamName", 123456789L, "Description", users);
    team1.setTeamId("1");
    team1.setProjectId("project1");

    Team team2 = new Team("TeamName", 123456789L, "Description", users);
    team2.setTeamId("1");
    team2.setProjectId("project1");

    assertEquals(team1, team2);
  }

  @Test
  void testNotEquals() {
    List<String> users1 = Arrays.asList("user1", "user2");
    List<String> users2 = Arrays.asList("user3", "user4");
    Team team1 = new Team("TeamName1", 123456789L, "Description1", users1);
    team1.setTeamId("1");
    team1.setProjectId("project1");

    Team team2 = new Team("TeamName2", 987654321L, "Description2", users2);
    team2.setTeamId("2");
    team2.setProjectId("project2");

    assertNotEquals(team1, team2);
  }

  @Test
  void testHashCode() {
    List<String> users = Arrays.asList("user1", "user2");
    Team team1 = new Team("TeamName", 123456789L, "Description", users);
    team1.setTeamId("1");
    team1.setProjectId("project1");

    Team team2 = new Team("TeamName", 123456789L, "Description", users);
    team2.setTeamId("1");
    team2.setProjectId("project1");

    assertEquals(team1.hashCode(), team2.hashCode());
  }

  @Test
  void testToString() {
    List<String> users = Arrays.asList("user1", "user2");
    Team team = new Team("TeamName", 123456789L, "Description", users);
    team.setTeamId("1");
    team.setProjectId("project1");
    String expected = "Team{team_id='1', project_id='project1', users=[user1, user2], team_name='TeamName', creation_date=123456789, team_description='Description'}";
    assertEquals(expected, team.toString());
  }

  @Test
  void testEqualsWithNull() {
    Team team1 = new Team();
    assertNotEquals(team1, null);
  }

  @Test
  void testEqualsWithDifferentClass() {
    Team team1 = new Team();
    String notATeam = "Not a Team";
    assertNotEquals(team1, notATeam);
  }

  @Test
  void testEqualsWithSameObject() {
    Team team1 = new Team();
    assertEquals(team1, team1);
  }

  @Test
  void testEqualsWithDifferentTeamId() {
    Team team1 = new Team();
    team1.setTeamId("1");
    Team team2 = new Team();
    team2.setTeamId("2");
    assertNotEquals(team1, team2);
  }

  @Test
  void testEqualsWithDifferentProjectId() {
    Team team1 = new Team();
    team1.setProjectId("project1");
    Team team2 = new Team();
    team2.setProjectId("project2");
    assertNotEquals(team1, team2);
  }

  @Test
  void testEqualsWithDifferentUsers() {
    Team team1 = new Team();
    team1.setUsers(Arrays.asList("user1", "user2"));
    Team team2 = new Team();
    team2.setUsers(Arrays.asList("user3", "user4"));
    assertNotEquals(team1, team2);
  }

  @Test
  void testEqualsWithDifferentTeamName() {
    Team team1 = new Team();
    team1.setTeamName("Team1");
    Team team2 = new Team();
    team2.setTeamName("Team2");
    assertNotEquals(team1, team2);
  }

  @Test
  void testEqualsWithDifferentCreationTimeStamp() {
    Team team1 = new Team();
    team1.setCreationTimeStamp(123456789L);
    Team team2 = new Team();
    team2.setCreationTimeStamp(987654321L);
    assertNotEquals(team1, team2);
  }

  @Test
  void testEqualsWithDifferentTeamDescription() {
    Team team1 = new Team();
    team1.setTeamDescription("Description1");
    Team team2 = new Team();
    team2.setTeamDescription("Description2");
    assertNotEquals(team1, team2);
  }
}