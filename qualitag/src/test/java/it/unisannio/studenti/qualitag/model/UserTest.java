package it.unisannio.studenti.qualitag.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class tests the User class.
 */
public class UserTest {

  private User user;

  /**
   * This method is executed before each test method is executed and creates a user for tests.
   */
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

  @Test
  public void testEqualsAndHashCode() {
    User anotherUser = new User("username", "user@example.com", "hashedPassword123", "John", "Doe");
    assertEquals(user, anotherUser);
    assertEquals(user.hashCode(), anotherUser.hashCode());
  }
}