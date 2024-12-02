package it.unisannio.studenti.qualitag.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultColorTest {

  @Test
  public void testGetRgb() {
    DefaultColor color = DefaultColor.PASTEL_GRAY_DARK;
    String rgb = color.getRgb();
    assertEquals("#a6aebf", rgb);
  }
}