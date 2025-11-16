package avengers.domain.model;

/**
 * Represents the six directions for room navigation including vertical movement.
 *
 * <p>Provides utility methods for converting from string representations commonly found in JSON
 * data or user input.
 */
public enum Direction {
  NORTH,
  SOUTH,
  EAST,
  WEST,
  UP,
  DOWN;

  /**
   * Converts a string representation to a Direction enum value.
   *
   * <p>Supports both full names (NORTH, SOUTH, UP, DOWN, etc.) and single-letter abbreviations (N,
   * S, U, D, etc.). Case-insensitive.
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
      case "U", "UP" -> UP;
      case "D", "DOWN" -> DOWN;
      default -> throw new IllegalArgumentException("Unknown direction: " + s);
    };
  }
}
