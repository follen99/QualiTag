package it.unisannio.studenti.qualitag.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests the class Role.
 */
public class RoleTest {

  /**
   * Test the values of the Role enum.
   */
  @Test
  public void testRoleValues() {
    assertEquals("OWNER", Role.OWNER.toString());
    assertEquals("MEMBER", Role.MEMBER.toString());
  }

}
