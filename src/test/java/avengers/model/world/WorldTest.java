package avengers.model.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test class for World. */
class WorldTest {
  private Map<String, Room> rooms;
  private Room room1;
  private Room room2;
  private Room room3;

  @BeforeEach
  void setUp() {
    EnumMap<Direction, String> exits1 = new EnumMap<>(Direction.class);
    exits1.put(Direction.N, "RM-02");
    room1 = new Room("RM-01", "Guild Hall", "Starting room", exits1, null, null, null, true);

    EnumMap<Direction, String> exits2 = new EnumMap<>(Direction.class);
    exits2.put(Direction.S, "RM-01");
    exits2.put(Direction.N, "RM-03");
    room2 = new Room("RM-02", "Dungeon", "A dark dungeon", exits2, null, null, null, false);

    EnumMap<Direction, String> exits3 = new EnumMap<>(Direction.class);
    exits3.put(Direction.S, "RM-02");
    room3 = new Room("RM-03", "Boss Room", "The final room", exits3, null, null, null, false);

    rooms = new HashMap<>();
    rooms.put("RM-01", room1);
    rooms.put("RM-02", room2);
    rooms.put("RM-03", room3);
  }

  @Test
  void testValidWorldCreation() {
    World world = new World(rooms);

    assertNotNull(world);
    assertEquals(3, world.getRoomCount());
  }

  @Test
  void testNullRoomsThrowsException() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> new World(null));
    assertEquals("World must have at least one room", exception.getMessage());
  }

  @Test
  void testEmptyRoomsThrowsException() {
    Map<String, Room> emptyRooms = new HashMap<>();

    Exception exception = assertThrows(IllegalArgumentException.class, () -> new World(emptyRooms));
    assertEquals("World must have at least one room", exception.getMessage());
  }

  @Test
  void testGetRoomReturnsCorrectRoom() {
    World world = new World(rooms);

    Room retrievedRoom = world.getRoom("RM-01");
    assertNotNull(retrievedRoom);
    assertEquals("RM-01", retrievedRoom.getId());
    assertEquals("Guild Hall", retrievedRoom.getName());
  }

  @Test
  void testGetRoomReturnsNullForNonExistentId() {
    World world = new World(rooms);

    Room retrievedRoom = world.getRoom("RM-99");
    assertNull(retrievedRoom);
  }

  @Test
  void testMarkVisited() {
    World world = new World(rooms);

    assertFalse(world.hasVisited("RM-01"));

    world.markVisited("RM-01");

    assertTrue(world.hasVisited("RM-01"));
  }

  @Test
  void testMarkMultipleRoomsVisited() {
    World world = new World(rooms);

    world.markVisited("RM-01");
    world.markVisited("RM-02");

    assertTrue(world.hasVisited("RM-01"));
    assertTrue(world.hasVisited("RM-02"));
    assertFalse(world.hasVisited("RM-03"));
  }

  @Test
  void testHasVisitedReturnsFalseForUnvisitedRoom() {
    World world = new World(rooms);

    assertFalse(world.hasVisited("RM-01"));
    assertFalse(world.hasVisited("RM-02"));
    assertFalse(world.hasVisited("RM-03"));
  }

  @Test
  void testGetVisitedReturnsEmptySetInitially() {
    World world = new World(rooms);

    Set<String> visited = world.getVisited();
    assertNotNull(visited);
    assertTrue(visited.isEmpty());
  }

  @Test
  void testGetVisitedReturnsCorrectSet() {
    World world = new World(rooms);

    world.markVisited("RM-01");
    world.markVisited("RM-02");

    Set<String> visited = world.getVisited();
    assertEquals(2, visited.size());
    assertTrue(visited.contains("RM-01"));
    assertTrue(visited.contains("RM-02"));
    assertFalse(visited.contains("RM-03"));
  }

  @Test
  void testGetVisitedReturnsImmutableCopy() {
    World world = new World(rooms);

    world.markVisited("RM-01");

    Set<String> visited = world.getVisited();

    // Should throw UnsupportedOperationException when trying to modify
    assertThrows(UnsupportedOperationException.class, () -> visited.add("RM-99"));
  }

  @Test
  void testGetRoomsReturnsAllRooms() {
    World world = new World(rooms);

    Map<String, Room> retrievedRooms = world.getRooms();
    assertEquals(3, retrievedRooms.size());
    assertTrue(retrievedRooms.containsKey("RM-01"));
    assertTrue(retrievedRooms.containsKey("RM-02"));
    assertTrue(retrievedRooms.containsKey("RM-03"));
  }

  @Test
  void testGetRoomsReturnsImmutableCopy() {
    World world = new World(rooms);

    Map<String, Room> retrievedRooms = world.getRooms();

    // Should throw UnsupportedOperationException when trying to modify
    assertThrows(UnsupportedOperationException.class, () -> retrievedRooms.put("RM-99", room1));
  }

  @Test
  void testGetRoomCount() {
    World world = new World(rooms);

    assertEquals(3, world.getRoomCount());
  }

  @Test
  void testWorldWithSingleRoom() {
    Map<String, Room> singleRoomMap = new HashMap<>();
    singleRoomMap.put("RM-01", room1);

    World world = new World(singleRoomMap);

    assertEquals(1, world.getRoomCount());
    assertNotNull(world.getRoom("RM-01"));
  }

  @Test
  void testMarkVisitedWithNonExistentRoomId() {
    World world = new World(rooms);

    // Should not throw exception, just adds to visited set
    world.markVisited("RM-99");

    assertTrue(world.hasVisited("RM-99"));
  }

  @Test
  void testMarkSameRoomVisitedMultipleTimes() {
    World world = new World(rooms);

    world.markVisited("RM-01");
    world.markVisited("RM-01");
    world.markVisited("RM-01");

    Set<String> visited = world.getVisited();
    assertEquals(1, visited.size());
    assertTrue(visited.contains("RM-01"));
  }

  @Test
  void testWorldPreservesRoomData() {
    World world = new World(rooms);

    Room retrievedRoom1 = world.getRoom("RM-01");
    Room retrievedRoom2 = world.getRoom("RM-02");
    Room retrievedRoom3 = world.getRoom("RM-03");

    assertEquals("Guild Hall", retrievedRoom1.getName());
    assertTrue(retrievedRoom1.isSafeZone());

    assertEquals("Dungeon", retrievedRoom2.getName());
    assertFalse(retrievedRoom2.isSafeZone());

    assertEquals("Boss Room", retrievedRoom3.getName());
    assertTrue(retrievedRoom3.hasExits());
    assertEquals(1, retrievedRoom3.getExitCount());
  }

  @Test
  void testWorldCreationDefensiveCopy() {
    World world = new World(rooms);

    // Modify the original map
    rooms.put("RM-99", room1);

    // World should not be affected
    assertEquals(3, world.getRoomCount());
    assertNull(world.getRoom("RM-99"));
  }

  @Test
  void testVisitedRoomsPersistAcrossMultipleCalls() {
    World world = new World(rooms);

    world.markVisited("RM-01");
    assertTrue(world.hasVisited("RM-01"));

    world.markVisited("RM-02");
    assertTrue(world.hasVisited("RM-01")); // Still visited
    assertTrue(world.hasVisited("RM-02"));

    world.markVisited("RM-03");
    assertTrue(world.hasVisited("RM-01")); // Still visited
    assertTrue(world.hasVisited("RM-02")); // Still visited
    assertTrue(world.hasVisited("RM-03"));

    assertEquals(3, world.getVisited().size());
  }
}
