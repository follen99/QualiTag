package it.unisannio.studenti.qualitag.dto.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserRegistrationDtoTest {

  @Test
  public void testValidEmail() {
    UserRegistrationDto dto = new UserRegistrationDto("user", "user@example.com", "password123", "John", "Doe");
    assertTrue(dto.isValidEmail());
  }

  @Test
  public void testInvalidEmail() {
    UserRegistrationDto dto = new UserRegistrationDto("user", "userexample.com", "password123", "John", "Doe");
    assertFalse(dto.isValidEmail());
  }

  @Test
  public void testValidPassword() {
    UserRegistrationDto dto = new UserRegistrationDto("user", "user@example.com", "password123", "John", "Doe");
    assertTrue(dto.isValidPassword());
  }

  @Test
  public void testInvalidPassword() {
    UserRegistrationDto dto = new UserRegistrationDto("user", "user@example.com", "pass", "John", "Doe");
    assertFalse(dto.isValidPassword());
  }
}