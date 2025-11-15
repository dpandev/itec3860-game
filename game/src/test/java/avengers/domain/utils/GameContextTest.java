package avengers.domain.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import avengers.domain.model.Player;
import avengers.domain.model.World;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameContextTest {
  private World testWorld;
  private Player testPlayer;

  @BeforeEach
  void setUp() {
    testWorld = new World(Map.of(), Map.of(), Map.of(), Map.of(), "room1");
    testPlayer = new Player("TestPlayer", "room1");
  }

  @Test
  void testGameContextCreationWithValidParameters() {
    GameContext context = new GameContext(testWorld, testPlayer);
    assertNotNull(context);
    assertEquals(testWorld, context.world());
    assertEquals(testPlayer, context.player());
  }

  @Test
  void testGameContextCreationWithNullWorldThrowsException() {
    assertThrows(
        NullPointerException.class,
        () -> new GameContext(null, testPlayer),
        "world must not be null");
  }

  @Test
  void testGameContextCreationWithNullPlayerThrowsException() {
    assertThrows(
        NullPointerException.class,
        () -> new GameContext(testWorld, null),
        "player must not be null");
  }

  @Test
  void testGameContextCreationWithNullBothThrowsException() {
    assertThrows(NullPointerException.class, () -> new GameContext(null, null));
  }

  @Test
  void testWorldAccessor() {
    GameContext context = new GameContext(testWorld, testPlayer);
    assertEquals(testWorld, context.world());
  }

  @Test
  void testPlayerAccessor() {
    GameContext context = new GameContext(testWorld, testPlayer);
    assertEquals(testPlayer, context.player());
  }

  @Test
  void testAwaitingPuzzleAnswerDefaultsToFalse() {
    GameContext context = new GameContext(testWorld, testPlayer);
    assertFalse(context.isAwaitingPuzzleAnswer());
  }

  @Test
  void testSetAwaitingPuzzleAnswerToTrue() {
    GameContext context = new GameContext(testWorld, testPlayer);
    context.setAwaitingPuzzleAnswer(true);
    assertTrue(context.isAwaitingPuzzleAnswer());
  }

  @Test
  void testSetAwaitingPuzzleAnswerToFalse() {
    GameContext context = new GameContext(testWorld, testPlayer);
    context.setAwaitingPuzzleAnswer(true);
    context.setAwaitingPuzzleAnswer(false);
    assertFalse(context.isAwaitingPuzzleAnswer());
  }

  @Test
  void testInCombatDefaultsToFalse() {
    GameContext context = new GameContext(testWorld, testPlayer);
    assertFalse(context.isInCombat());
  }

  @Test
  void testSetInCombatToTrue() {
    GameContext context = new GameContext(testWorld, testPlayer);
    context.setInCombat(true);
    assertTrue(context.isInCombat());
  }

  @Test
  void testSetInCombatToFalse() {
    GameContext context = new GameContext(testWorld, testPlayer);
    context.setInCombat(true);
    context.setInCombat(false);
    assertFalse(context.isInCombat());
  }

  @Test
  void testCombatMonsterIdDefaultsToNull() {
    GameContext context = new GameContext(testWorld, testPlayer);
    assertNull(context.getCombatMonsterId());
  }

  @Test
  void testSetCombatMonsterId() {
    GameContext context = new GameContext(testWorld, testPlayer);
    context.setCombatMonsterId("monster1");
    assertEquals("monster1", context.getCombatMonsterId());
  }

  @Test
  void testSetCombatMonsterIdToNull() {
    GameContext context = new GameContext(testWorld, testPlayer);
    context.setCombatMonsterId("monster1");
    context.setCombatMonsterId(null);
    assertNull(context.getCombatMonsterId());
  }

  @Test
  void testStartCombat() {
    GameContext context = new GameContext(testWorld, testPlayer);
    context.startCombat("monster1");
    assertTrue(context.isInCombat());
    assertEquals("monster1", context.getCombatMonsterId());
  }

  @Test
  void testStartCombatWithDifferentMonster() {
    GameContext context = new GameContext(testWorld, testPlayer);
    context.startCombat("monster1");
    context.startCombat("monster2");
    assertTrue(context.isInCombat());
    assertEquals("monster2", context.getCombatMonsterId());
  }

  @Test
  void testEndCombat() {
    GameContext context = new GameContext(testWorld, testPlayer);
    context.startCombat("monster1");
    context.endCombat();
    assertFalse(context.isInCombat());
    assertNull(context.getCombatMonsterId());
  }

  @Test
  void testEndCombatWhenNotInCombat() {
    GameContext context = new GameContext(testWorld, testPlayer);
    context.endCombat();
    assertFalse(context.isInCombat());
    assertNull(context.getCombatMonsterId());
  }

  @Test
  void testResetGame() {
    GameContext context = new GameContext(testWorld, testPlayer);
    context.setAwaitingPuzzleAnswer(true);
    context.startCombat("monster1");
    World newWorld = new World(Map.of(), Map.of(), Map.of(), Map.of(), "room2");
    context.resetGame(newWorld, "NewPlayer");
    assertEquals(newWorld, context.world());
    assertEquals("NewPlayer", context.player().getName());
    assertEquals("room2", context.player().getRoomId());
    assertFalse(context.isAwaitingPuzzleAnswer());
    assertFalse(context.isInCombat());
    assertNull(context.getCombatMonsterId());
  }

  @Test
  void testResetGameWithNullWorldThrowsException() {
    GameContext context = new GameContext(testWorld, testPlayer);
    assertThrows(
        NullPointerException.class,
        () -> context.resetGame(null, "NewPlayer"),
        "world must not be null");
  }

  @Test
  void testResetGameClearsAllFlags() {
    GameContext context = new GameContext(testWorld, testPlayer);
    context.setAwaitingPuzzleAnswer(true);
    context.setInCombat(true);
    context.setCombatMonsterId("monster1");
    World newWorld = new World(Map.of(), Map.of(), Map.of(), Map.of(), "room3");
    context.resetGame(newWorld, "ResetPlayer");
    assertFalse(context.isAwaitingPuzzleAnswer());
    assertFalse(context.isInCombat());
    assertNull(context.getCombatMonsterId());
  }

  @Test
  void testMultipleStateChanges() {
    GameContext context = new GameContext(testWorld, testPlayer);
    context.setAwaitingPuzzleAnswer(true);
    assertTrue(context.isAwaitingPuzzleAnswer());
    context.startCombat("monster1");
    assertTrue(context.isInCombat());
    assertEquals("monster1", context.getCombatMonsterId());
    context.setAwaitingPuzzleAnswer(false);
    assertFalse(context.isAwaitingPuzzleAnswer());
    assertTrue(context.isInCombat());
    context.endCombat();
    assertFalse(context.isInCombat());
    assertNull(context.getCombatMonsterId());
  }
}
