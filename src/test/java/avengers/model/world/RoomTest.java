package avengers.model.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import avengers.model.Monster;
import avengers.model.item.Item;
import avengers.model.item.ItemType;
import avengers.model.puzzle.Puzzle;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test class for Room. */
class RoomTest {
  private EnumMap<Direction, String> exits;
  private List<Item> items;
  private List<Monster> monsters;
  private List<Puzzle> puzzles;

  @BeforeEach
  void setUp() {
    exits = new EnumMap<>(Direction.class);
    exits.put(Direction.N, "RM-02");
    exits.put(Direction.E, "RM-03");

    items = new ArrayList<>();
    monsters = new ArrayList<>();
    puzzles = new ArrayList<>();
  }

  @Test
  void testValidRoomCreation() {
    Room room =
        new Room(
            "RM-01",
            "Test Room",
            "A test room description",
            exits,
            items,
            monsters,
            puzzles,
            false);

    assertNotNull(room);
    assertEquals("RM-01", room.getId());
    assertEquals("Test Room", room.getName());
    assertEquals("A test room description", room.getDescription());
    assertFalse(room.isSafeZone());
    assertFalse(room.isVisited());
    assertEquals(2, room.getExitCount());
  }

  @Test
  void testNullIdThrowsException() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new Room(null, "Test Room", "Description", exits, items, monsters, puzzles, false));
    assertEquals("Room ID cannot be null or empty", exception.getMessage());
  }

  @Test
  void testEmptyIdThrowsException() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Room("", "Test Room", "Description", exits, items, monsters, puzzles, false));
    assertEquals("Room ID cannot be null or empty", exception.getMessage());
  }

  @Test
  void testWhitespaceIdThrowsException() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new Room(
                    "   ", "Test Room", "Description", exits, items, monsters, puzzles, false));
    assertEquals("Room ID cannot be null or empty", exception.getMessage());
  }

  @Test
  void testNullNameThrowsException() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Room("RM-01", null, "Description", exits, items, monsters, puzzles, false));
    assertEquals("Room name cannot be null or empty", exception.getMessage());
  }

  @Test
  void testEmptyNameThrowsException() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Room("RM-01", "", "Description", exits, items, monsters, puzzles, false));
    assertEquals("Room name cannot be null or empty", exception.getMessage());
  }

  @Test
  void testNullDescriptionThrowsException() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Room("RM-01", "Test Room", null, exits, items, monsters, puzzles, false));
    assertEquals("Room description cannot be null or empty", exception.getMessage());
  }

  @Test
  void testEmptyDescriptionThrowsException() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Room("RM-01", "Test Room", "", exits, items, monsters, puzzles, false));
    assertEquals("Room description cannot be null or empty", exception.getMessage());
  }

  @Test
  void testNullExitsThrowsException() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new Room(
                    "RM-01", "Test Room", "Description", null, items, monsters, puzzles, false));
    assertEquals("Exits cannot be null", exception.getMessage());
  }

  @Test
  void testSafeZoneWithoutMonstersIsValid() {
    Room room = new Room("RM-01", "Test Room", "Description", exits, items, null, puzzles, true);

    assertTrue(room.isSafeZone());
    assertFalse(room.hasMonsters());
  }

  @Test
  void testExitReturnsCorrectRoomId() {
    Room room =
        new Room("RM-01", "Test Room", "Description", exits, items, monsters, puzzles, false);

    Optional<String> northExit = room.exit(Direction.N);
    assertTrue(northExit.isPresent());
    assertEquals("RM-02", northExit.get());

    Optional<String> eastExit = room.exit(Direction.E);
    assertTrue(eastExit.isPresent());
    assertEquals("RM-03", eastExit.get());
  }

  @Test
  void testExitReturnsEmptyForNonExistentDirection() {
    Room room =
        new Room("RM-01", "Test Room", "Description", exits, items, monsters, puzzles, false);

    Optional<String> southExit = room.exit(Direction.S);
    assertFalse(southExit.isPresent());
  }

  @Test
  void testHasExitReturnsTrueForExistingDirection() {
    Room room =
        new Room("RM-01", "Test Room", "Description", exits, items, monsters, puzzles, false);

    assertTrue(room.hasExit(Direction.N));
    assertTrue(room.hasExit(Direction.E));
  }

  @Test
  void testHasExitReturnsFalseForNonExistentDirection() {
    Room room =
        new Room("RM-01", "Test Room", "Description", exits, items, monsters, puzzles, false);

    assertFalse(room.hasExit(Direction.S));
    assertFalse(room.hasExit(Direction.W));
  }

  @Test
  void testAddItem() {
    Room room =
        new Room("RM-01", "Test Room", "Description", exits, items, monsters, puzzles, false);

    assertFalse(room.hasItems());

    Item item =
        new Item(
            "IT-01",
            ItemType.CONSUMABLE,
            "Potion",
            "A healing potion",
            false,
            false,
            null,
            new ArrayList<>(),
            false);
    room.addItem(item);

    assertTrue(room.hasItems());
    assertEquals(1, room.getItems().size());
  }

  @Test
  void testAddNullItemThrowsException() {
    Room room =
        new Room("RM-01", "Test Room", "Description", exits, items, monsters, puzzles, false);

    Exception exception = assertThrows(IllegalArgumentException.class, () -> room.addItem(null));
    assertEquals("Item cannot be null", exception.getMessage());
  }

  @Test
  void testRemoveItem() {
    Item item =
        new Item(
            "IT-01",
            ItemType.CONSUMABLE,
            "Potion",
            "A healing potion",
            false,
            false,
            null,
            new ArrayList<>(),
            false);
    items.add(item);

    Room room =
        new Room("RM-01", "Test Room", "Description", exits, items, monsters, puzzles, false);

    assertTrue(room.hasItems());
    room.removeItem("IT-01");
    assertFalse(room.hasItems());
  }

  @Test
  void testRemoveNullItemIdThrowsException() {
    Room room =
        new Room("RM-01", "Test Room", "Description", exits, items, monsters, puzzles, false);

    Exception exception = assertThrows(IllegalArgumentException.class, () -> room.removeItem(null));
    assertEquals("Item ID cannot be null or empty", exception.getMessage());
  }

  @Test
  void testRemoveEmptyItemIdThrowsException() {
    Room room =
        new Room("RM-01", "Test Room", "Description", exits, items, monsters, puzzles, false);

    Exception exception = assertThrows(IllegalArgumentException.class, () -> room.removeItem(""));
    assertEquals("Item ID cannot be null or empty", exception.getMessage());
  }

  @Test
  void testAddPuzzle() {
    Room room =
        new Room("RM-01", "Test Room", "Description", exits, items, monsters, puzzles, false);

    assertFalse(room.hasPuzzles());

    Puzzle puzzle = new Puzzle();
    room.addPuzzle(puzzle);

    assertTrue(room.hasPuzzles());
    assertEquals(1, room.getPuzzles().size());
  }

  @Test
  void testAddNullPuzzleThrowsException() {
    Room room =
        new Room("RM-01", "Test Room", "Description", exits, items, monsters, puzzles, false);

    Exception exception = assertThrows(IllegalArgumentException.class, () -> room.addPuzzle(null));
    assertEquals("Puzzle cannot be null", exception.getMessage());
  }

  @Test
  void testRemovePuzzle() {
    Puzzle puzzle = new Puzzle();
    puzzles.add(puzzle);

    Room room =
        new Room("RM-01", "Test Room", "Description", exits, items, monsters, puzzles, false);

    assertTrue(room.hasPuzzles());
    room.removePuzzle(puzzle);
    assertFalse(room.hasPuzzles());
  }

  @Test
  void testRemoveNullPuzzleThrowsException() {
    Room room =
        new Room("RM-01", "Test Room", "Description", exits, items, monsters, puzzles, false);

    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> room.removePuzzle(null));
    assertEquals("Puzzle cannot be null", exception.getMessage());
  }

  @Test
  void testSetVisited() {
    Room room =
        new Room("RM-01", "Test Room", "Description", exits, items, monsters, puzzles, false);

    assertFalse(room.isVisited());
    room.setVisited(true);
    assertTrue(room.isVisited());
    room.setVisited(false);
    assertFalse(room.isVisited());
  }

  @Test
  void testHasNoMonsters() {
    Room room = new Room("RM-01", "Test Room", "Description", exits, items, null, puzzles, false);

    assertFalse(room.hasMonsters());
  }

  @Test
  void testHasExits() {
    Room room =
        new Room("RM-01", "Test Room", "Description", exits, items, monsters, puzzles, false);

    assertTrue(room.hasExits());
    assertEquals(2, room.getExitCount());
  }

  @Test
  void testHasNoExits() {
    EnumMap<Direction, String> emptyExits = new EnumMap<>(Direction.class);
    Room room =
        new Room("RM-01", "Test Room", "Description", emptyExits, items, monsters, puzzles, false);

    assertFalse(room.hasExits());
    assertEquals(0, room.getExitCount());
  }

  @Test
  void testGetExitsReturnsDefensiveCopy() {
    Room room =
        new Room("RM-01", "Test Room", "Description", exits, items, monsters, puzzles, false);

    EnumMap<Direction, String> retrievedExits = room.getExits();
    retrievedExits.put(Direction.S, "RM-99");

    // Original room exits should remain unchanged
    assertFalse(room.hasExit(Direction.S));
    assertEquals(2, room.getExitCount());
  }

  @Test
  void testGetItemsReturnsImmutableCopy() {
    Item item =
        new Item(
            "IT-01",
            ItemType.CONSUMABLE,
            "Potion",
            "A healing potion",
            false,
            false,
            null,
            new ArrayList<>(),
            false);
    items.add(item);

    Room room =
        new Room("RM-01", "Test Room", "Description", exits, items, monsters, puzzles, false);

    List<Item> retrievedItems = room.getItems();

    // Should throw UnsupportedOperationException when trying to modify
    assertThrows(UnsupportedOperationException.class, () -> retrievedItems.add(item));
  }

  @Test
  void testGetPuzzlesReturnsImmutableCopy() {
    Puzzle puzzle = new Puzzle();
    puzzles.add(puzzle);

    Room room =
        new Room("RM-01", "Test Room", "Description", exits, items, monsters, puzzles, false);

    List<Puzzle> retrievedPuzzles = room.getPuzzles();

    // Should throw UnsupportedOperationException when trying to modify
    assertThrows(UnsupportedOperationException.class, () -> retrievedPuzzles.add(puzzle));
  }

  @Test
  void testNullListsAreHandledCorrectly() {
    Room room = new Room("RM-01", "Test Room", "Description", exits, null, null, null, false);

    assertNotNull(room.getItems());
    assertNotNull(room.getMonsters());
    assertNotNull(room.getPuzzles());
    assertEquals(0, room.getItems().size());
    assertEquals(0, room.getMonsters().size());
    assertEquals(0, room.getPuzzles().size());
  }

  @Test
  void testMultiplePuzzlesInRoom() {
    Puzzle puzzle1 = new Puzzle();
    Puzzle puzzle2 = new Puzzle();
    puzzles.add(puzzle1);
    puzzles.add(puzzle2);

    Room room =
        new Room("RM-01", "Test Room", "Description", exits, items, monsters, puzzles, false);

    assertTrue(room.hasPuzzles());
    assertEquals(2, room.getPuzzles().size());
  }
}
