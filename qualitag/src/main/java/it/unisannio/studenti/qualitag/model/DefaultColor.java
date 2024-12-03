package it.unisannio.studenti.qualitag.model;

public enum DefaultColor {
  PASTEL_GRAY_DARK("#a6aebf"), PASTEL_GRAY_LIGHT("#c5d3e8"), PASTEL_GREEN(
      "#d0e8c5"), PASTEL_YELLOW_LIGHT("#fff8de"), PASTEL_BLUE_NAVY("#295f98");

  private final String rgb;

  DefaultColor(String rgb) {
    this.rgb = rgb;
  }

  public String getRgb() {
    return rgb;
  }
}
