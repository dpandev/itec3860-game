package avengers.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** Unit tests for the Direction enum. */
class DirectionTest {

  @Test
  void fromString_validDirections_returnsCorrectEnum() {
    assertEquals(Direction.NORTH, Direction.fromString("NORTH"));
    assertEquals(Direction.SOUTH, Direction.fromString("SOUTH"));
    assertEquals(Direction.EAST, Direction.fromString("EAST"));
    assertEquals(Direction.WEST, Direction.fromString("WEST"));
    assertEquals(Direction.UP, Direction.fromString("UP"));
    assertEquals(Direction.DOWN, Direction.fromString("DOWN"));
  }

  @Test
  void fromString_validAbbreviations_returnsCorrectEnum() {
    assertEquals(Direction.NORTH, Direction.fromString("N"));
    assertEquals(Direction.SOUTH, Direction.fromString("S"));
    assertEquals(Direction.EAST, Direction.fromString("E"));
    assertEquals(Direction.WEST, Direction.fromString("W"));
    assertEquals(Direction.UP, Direction.fromString("U"));
    assertEquals(Direction.DOWN, Direction.fromString("D"));
  }

  @Test
  void fromString_caseInsensitive_returnsCorrectEnum() {
    assertEquals(Direction.NORTH, Direction.fromString("north"));
    assertEquals(Direction.SOUTH, Direction.fromString("South"));
    assertEquals(Direction.EAST, Direction.fromString("EAST"));
    assertEquals(Direction.WEST, Direction.fromString("w"));
  }

  @Test
  void fromString_withWhitespace_returnsCorrectEnum() {
    assertEquals(Direction.NORTH, Direction.fromString(" NORTH "));
    assertEquals(Direction.SOUTH, Direction.fromString("\tS\t"));
  }

  @Test
  void fromString_nullInput_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> Direction.fromString(null));
  }

  @Test
  void fromString_blankInput_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> Direction.fromString(""));
    assertThrows(IllegalArgumentException.class, () -> Direction.fromString("   "));
  }

  @Test
  void fromString_invalidDirection_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> Direction.fromString("INVALID"));
    assertThrows(IllegalArgumentException.class, () -> Direction.fromString("NORTHEAST"));
    assertThrows(IllegalArgumentException.class, () -> Direction.fromString("LEFT"));
  }
}
