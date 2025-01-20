package it.unisannio.studenti.qualitag.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class tests the User class.
 */
public class UserTest {

  private User user;
  private User equalUser;
  private User differentUser;

  /**
   * This method is executed before each test method is executed and creates a user for tests.
   */
  @BeforeEach
  public void setUp() {
    user = new User("username", "user@example.com", "hashedPassword123", "John", "Doe");
    user.setUserId("userId1");
    equalUser = new User("username", "user@example.com", "hashedPassword123", "John", "Doe");
    equalUser.setUserId("userId1");
    differentUser = new User("differentUsername", "user2@example.com", "hashedPassword456", "Jane",
        "Smith");
    differentUser.setUserId("userId2");
  }

  /**
   * Test if the id of a newly created user is null
   */
  @Test
  public void testGetInitialUserId() {
    User newUser = new User("username", "user@example.com", "hashedPassword123", "John", "Doe");
    assertNull(newUser.getUserId()); // Initially, userId should be null
  }

  /**
   * Test the get method for the userId
   */
  @Test
  public void testGetUserId() {
    assertEquals("userId1", user.getUserId());
  }

  /**
   * Test the get method for the username
   */
  @Test
  public void testGetUsername() {
    assertEquals("username", user.getUsername());
  }

  /**
   * Test the set method for the username
   */
  @Test
  public void testSetUsername() {
    user.setUsername("newUsername");
    assertEquals("newUsername", user.getUsername());
  }

  /**
   * Test the get method for the email
   */
  @Test
  public void testGetEmail() {
    assertEquals("user@example.com", user.getEmail());
  }

  /**
   * Test the set method for the email
   */
  @Test
  public void testSetEmail() {
    user.setEmail("new@example.com");
    assertEquals("new@example.com", user.getEmail());
  }

  /**
   * Test the get method for the passwordHash
   */
  @Test
  public void testGetPasswordHash() {
    assertEquals("hashedPassword123", user.getPasswordHash());
  }

  /**
   * Test the set method for the passwordHash
   */
  @Test
  public void testSetPasswordHash() {
    user.setPasswordHash("newHashedPassword");
    assertEquals("newHashedPassword", user.getPasswordHash());
  }

  /**
   * Test the get method for the name
   */
  @Test
  public void testGetName() {
    assertEquals("John", user.getName());
  }

  /**
   * Test the set method for the name
   */
  @Test
  public void testSetName() {
    user.setName("Jane");
    assertEquals("Jane", user.getName());
  }

  /**
   * Test the get method for the surname
   */
  @Test
  public void testGetSurname() {
    assertEquals("Doe", user.getSurname());
  }

  /**
   * Test the set method for the surname
   */
  @Test
  public void testSetSurname() {
    user.setSurname("Smith");
    assertEquals("Smith", user.getSurname());
  }

  /**
   * Test the equals method
   */
  @Test
  public void testEquals() {
    assertEquals(user, equalUser);
  }

  /**
   * Test the equals method with different objects.
   */
  @Test
  public void testNotEquals() {
    assertNotEquals(user, differentUser);
  }

  /**
   * Test the equals method with a different object.
   */
  @Test
  public void testNotEqualsDifferentObject() {
    assertNotEquals(user, "I am a string");
  }

  /**
   * Test if the hashcode is the same for equal users.
   */
  @Test
  public void testHashCode() {
    assertEquals(user.hashCode(), equalUser.hashCode());
  }

  /**
   * Test if the hashcode is different for different users.
   */
  @Test
  public void testNotHashCode() {
    assertNotEquals(user.hashCode(), differentUser.hashCode());
  }

}