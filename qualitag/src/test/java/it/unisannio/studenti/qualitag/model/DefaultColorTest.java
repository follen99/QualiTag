package it.unisannio.studenti.qualitag.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DefaultColorTest {

  @Test
  public void testGetRgb() {
    assertEquals("#a6aebf", DefaultColor.PASTEL_GRAY_DARK.getRgb());
    assertEquals("#c5d3e8", DefaultColor.PASTEL_GRAY_LIGHT.getRgb());
    assertEquals("#d0e8c5", DefaultColor.PASTEL_GREEN.getRgb());
    assertEquals("#fff8de", DefaultColor.PASTEL_YELLOW_LIGHT.getRgb());
    assertEquals("#295f98", DefaultColor.PASTEL_BLUE_NAVY.getRgb());
  }

}