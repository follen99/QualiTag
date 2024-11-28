package it.unisannio.studenti.qualitag.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

  private User user;

  @BeforeEach
  public void setUp() {
    user = new User("username", "user@example.com", "hashedPassword123", "John", "Doe");
  }

  @Test
  public void testGetUserId() {
    assertNull(user.getUserId()); // Initially, userId should be null
  }

  @Test
  public void testGetUsername() {
    assertEquals("username", user.getUsername());
  }

  @Test
  public void testSetUsername() {
    user.setUsername("newUsername");
    assertEquals("newUsername", user.getUsername());
  }

  @Test
  public void testGetEmail() {
    assertEquals("user@example.com", user.getEmail());
  }

  @Test
  public void testSetEmail() {
    user.setEmail("new@example.com");
    assertEquals("new@example.com", user.getEmail());
  }

  @Test
  public void testGetPasswordHash() {
    assertEquals("hashedPassword123", user.getPasswordHash());
  }

  @Test
  public void testSetPasswordHash() {
    user.setPasswordHash("newHashedPassword");
    assertEquals("newHashedPassword", user.getPasswordHash());
  }

  @Test
  public void testGetName() {
    assertEquals("John", user.getName());
  }

  @Test
  public void testSetName() {
    user.setName("Jane");
    assertEquals("Jane", user.getName());
  }

  @Test
  public void testGetSurname() {
    assertEquals("Doe", user.getSurname());
  }

  @Test
  public void testSetSurname() {
    user.setSurname("Smith");
    assertEquals("Smith", user.getSurname());
  }

//  @Test
//  public void testAddAndRemoveProjectId() {
//    user.addProjectId("project1");
//    assertTrue(user.getProjectIds().contains("project1"));
//    user.removeProjectId("project1");
//    assertFalse(user.getProjectIds().contains("project1"));
//  }
//
//  @Test
//  public void testAddAndRemoveTeamId() {
//    user.addTeamId("team1");
//    assertTrue(user.getTeamIds().contains("team1"));
//    user.removeTeamId("team1");
//    assertFalse(user.getTeamIds().contains("team1"));
//  }
//
//  @Test
//  public void testAddAndRemoveTagId() {
//    user.addTagId("tag1");
//    assertTrue(user.getTagIds().contains("tag1"));
//    user.removeTagId("tag1");
//    assertFalse(user.getTagIds().contains("tag1"));
//  }

  @Test
  public void testEqualsAndHashCode() {
    User anotherUser = new User("username", "user@example.com", "hashedPassword123", "John", "Doe");
    assertEquals(user, anotherUser);
    assertEquals(user.hashCode(), anotherUser.hashCode());
  }

  @Test
  public void testToString() {
    String expected = "User{userId='null', username='username', email='user@example.com', name='John', surname='Doe', projectIds=[], teamIds=[], tagIds=[], roles=null}";
    assertEquals(expected, user.toString());
  }
}