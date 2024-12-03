package it.unisannio.studenti.qualitag.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DefaultColorTest {

  @Test
  public void testGetRgb() {
    DefaultColor color = DefaultColor.PASTEL_GRAY_DARK;
    String rgb = color.getRgb();
    assertEquals("#a6aebf", rgb);
  }
}