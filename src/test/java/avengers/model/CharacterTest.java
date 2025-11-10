package avengers.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Test class for Character validation logic. Tests through Player subclass. */
class CharacterTest {
  @Test
  void testValidCharacterCreation() {
    Player player = new Player("player1", "TestPlayer", "A test player", 100, 100, 10, 5);
    assertNotNull(player);
    assertEquals("player1", player.getId());
    assertEquals("TestPlayer", player.getName());
    assertEquals("A test player", player.getDescription());
    assertEquals(100, player.getHp());
    assertEquals(100, player.getMaxHp());
    assertEquals(10, player.getBaseDamage());
    assertEquals(5, player.getDefense());
  }

  @Test
  void testNullIdThrowsException() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Player(null, "name", "description", 100, 100, 10, 5));
    assertEquals("ID cannot be null or empty", exception.getMessage());
  }

  @Test
  void testEmptyIdThrowsException() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Player("", "name", "description", 100, 100, 10, 5));
    assertEquals("ID cannot be null or empty", exception.getMessage());
  }

  @Test
  void testWhitespaceIdThrowsException() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Player("   ", "name", "description", 100, 100, 10, 5));
    assertEquals("ID cannot be null or empty", exception.getMessage());
  }

  @Test
  void testNullNameThrowsException() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Player("player1", null, "description", 100, 100, 10, 5));
    assertEquals("Name cannot be null or empty", exception.getMessage());
  }

  @Test
  void testEmptyNameThrowsException() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Player("player1", "", "description", 100, 100, 10, 5));
    assertEquals("Name cannot be null or empty", exception.getMessage());
  }

  @Test
  void testWhitespaceNameThrowsException() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Player("player1", "   ", "description", 100, 100, 10, 5));
    assertEquals("Name cannot be null or empty", exception.getMessage());
  }

  @Test
  void testNegativeMaxHpThrowsException() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Player("player1", "name", "description", 50, -100, 10, 5));
    assertEquals("Maximum HP must be positive", exception.getMessage());
  }

  @Test
  void testZeroMaxHpThrowsException() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Player("player1", "name", "description", 0, 0, 10, 5));
    assertEquals("Maximum HP must be positive", exception.getMessage());
  }

  @Test
  void testNegativeHpThrowsException() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Player("player1", "name", "description", -10, 100, 10, 5));
    assertEquals("HP cannot be negative", exception.getMessage());
  }

  @Test
  void testHpGreaterThanMaxHpThrowsException() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Player("player1", "name", "description", 150, 100, 10, 5));
    assertEquals("HP cannot be greater than maximum HP", exception.getMessage());
  }

  @Test
  void testNegativeBaseDamageThrowsException() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Player("player1", "name", "description", 100, 100, -10, 5));
    assertEquals("Base damage cannot be negative", exception.getMessage());
  }

  @Test
  void testNegativeDefenseThrowsException() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Player("player1", "name", "description", 100, 100, 10, -5));
    assertEquals("Defense cannot be negative", exception.getMessage());
  }

  @Test
  void testZeroValuesAreValid() {
    // Zero HP is valid (character can start dead)
    Player deadPlayer = new Player("player1", "Dead", "description", 0, 100, 0, 0);
    assertNotNull(deadPlayer);
    assertEquals(0, deadPlayer.getHp());
    assertEquals(0, deadPlayer.getBaseDamage());
    assertEquals(0, deadPlayer.getDefense());
  }

  @Test
  void testHpEqualToMaxHpIsValid() {
    Player player = new Player("player1", "name", "description", 100, 100, 10, 5);
    assertNotNull(player);
    assertEquals(100, player.getHp());
    assertEquals(100, player.getMaxHp());
  }
}
