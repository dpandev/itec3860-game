package avengers.domain.model;

/**
 * Represents the four cardinal directions for room navigation.
 *
 * <p>Provides utility methods for converting from string representations commonly found in JSON
 * data or user input.
 */
public enum Direction {
  NORTH,
  SOUTH,
  EAST,
  WEST;

  /**
   * Converts a string representation to a Direction enum value.
   *
   * <p>Supports both full names (NORTH, SOUTH, EAST, WEST) and single-letter abbreviations (N, S,
   * E, W). Case-insensitive.
   *
   * @param s the string to convert
   * @return the corresponding Direction
   * @throws IllegalArgumentException if the string doesn't match any known direction
   */
  public static Direction fromString(String s) {
    if (s == null || s.isBlank()) {
      throw new IllegalArgumentException("Direction string cannot be null or blank");
    }

    return switch (s.toUpperCase().trim()) {
      case "N", "NORTH" -> NORTH;
      case "S", "SOUTH" -> SOUTH;
      case "E", "EAST" -> EAST;
      case "W", "WEST" -> WEST;
      default -> throw new IllegalArgumentException("Unknown direction: " + s);
    };
  }
}
