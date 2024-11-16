package it.unisannio.studenti.qualitag.model;

public enum DefaultColor {
  PASTEL_GRAY_DARK(new int[]{166, 174, 191}),
  PASTEL_GRAY_LIGHT(new int[]{197, 211, 232}),
  PASTEL_GREEN(new int[]{208, 232, 197}),
  PASTEL_YELLOW_LIGHT(new int[]{255, 248, 222}),
  PASTEL_BLUE_NAVY(new int[]{41, 95, 152});

  private final int[] rgb;

  DefaultColor(int[] rgb) {
    this.rgb = rgb;
  }

  public int[] getRgb() {
    return rgb;
  }
}
