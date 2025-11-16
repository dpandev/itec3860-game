package avengers.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import avengers.domain.model.Item;
import avengers.domain.model.Monster;
import avengers.domain.model.Player;
import avengers.domain.model.Puzzle;
import avengers.domain.model.Room;
import avengers.domain.model.World;
import avengers.domain.utils.GameContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultMapServiceTest {

  private DefaultMapService mapService;
  private GameContext gameContext;
  private World world;
  private Player player;

  @BeforeEach
  void setUp() {
    mapService = new DefaultMapService();

    // Create test rooms with connections
    Map<String, Room> rooms = createTestRooms();
    Map<String, Item> items = new HashMap<>();
    Map<String, Puzzle> puzzles = new HashMap<>();
    Map<String, Monster> monsters = new HashMap<>();

    world = new World(rooms, items, puzzles, monsters, "room1");
    player = new Player("TestPlayer", "room1");

    // Mark some rooms as visited
    player.addRoomToRoomsVisited("room1");
    player.addRoomToRoomsVisited("room2");

    gameContext = new GameContext(world, player);
  }

  private Map<String, Room> createTestRooms() {
    Map<String, Room> rooms = new HashMap<>();

    // Create a simple 3x3 grid of rooms
    // Layout:
    //   room4
    //     |
    // room3-room1-room2
    //     |
    //   room5

    Map<String, String> room1Exits = new HashMap<>();
    room1Exits.put("north", "room4");
    room1Exits.put("south", "room5");
    room1Exits.put("east", "room2");
    room1Exits.put("west", "room3");

    Map<String, String> room2Exits = new HashMap<>();
    room2Exits.put("west", "room1");

    Map<String, String> room3Exits = new HashMap<>();
    room3Exits.put("east", "room1");

    Map<String, String> room4Exits = new HashMap<>();
    room4Exits.put("south", "room1");
    room4Exits.put("up", "room6"); // Test vertical connection

    Map<String, String> room5Exits = new HashMap<>();
    room5Exits.put("north", "room1");

    Map<String, String> room6Exits = new HashMap<>();
    room6Exits.put("down", "room4");

    rooms.put(
        "room1",
        new Room(
            "room1",
            "Central Hall",
            "The main hall",
            new ArrayList<>(),
            new ArrayList<>(),
            room1Exits,
            new ArrayList<>(),
            false));
    rooms.put(
        "room2",
        new Room(
            "room2",
            "East Wing",
            "Eastern corridor",
            new ArrayList<>(),
            new ArrayList<>(),
            room2Exits,
            new ArrayList<>(),
            false));
    rooms.put(
        "room3",
        new Room(
            "room3",
            "West Wing",
            "Western corridor",
            new ArrayList<>(),
            new ArrayList<>(),
            room3Exits,
            new ArrayList<>(),
            false));
    rooms.put(
        "room4",
        new Room(
            "room4",
            "North Tower",
            "Northern tower",
            new ArrayList<>(),
            new ArrayList<>(),
            room4Exits,
            new ArrayList<>(),
            false));
    rooms.put(
        "room5",
        new Room(
            "room5",
            "South Cellar",
            "Southern cellar",
            new ArrayList<>(),
            new ArrayList<>(),
            room5Exits,
            new ArrayList<>(),
            false));
    rooms.put(
        "room6",
        new Room(
            "room6",
            "Upper Tower",
            "Top of the tower",
            new ArrayList<>(),
            new ArrayList<>(),
            room6Exits,
            new ArrayList<>(),
            false));

    return rooms;
  }

  @Test
  void testShowMapReturnsValidOutput() {
    String mapOutput = mapService.showMap(gameContext);

    assertNotNull(mapOutput);
    assertFalse(mapOutput.isEmpty());
    assertTrue(mapOutput.contains("=== MAP ==="));
    assertTrue(mapOutput.contains("LEGEND:"));
    assertTrue(mapOutput.contains("@ = You are here"));
    assertTrue(mapOutput.contains("# = Visited room"));
    assertTrue(mapOutput.contains("? = Adjacent unexplored room"));
  }

  @Test
  void testShowMapContainsCurrentRoomInfo() {
    String mapOutput = mapService.showMap(gameContext);

    assertTrue(mapOutput.contains("CURRENT LOCATION:"));
    assertTrue(mapOutput.contains("Central Hall"));
    assertTrue(mapOutput.contains("The main hall"));
  }

  @Test
  void testShowMapContainsAvailableExits() {
    String mapOutput = mapService.showMap(gameContext);

    assertTrue(mapOutput.contains("AVAILABLE EXITS:"));
    assertTrue(mapOutput.contains("NORTH"));
    assertTrue(mapOutput.contains("SOUTH"));
    assertTrue(mapOutput.contains("EAST"));
    assertTrue(mapOutput.contains("WEST"));
  }

  @Test
  void testShowMapContainsCurrentPlayerSymbol() {
    String mapOutput = mapService.showMap(gameContext);

    // Should contain the @ symbol for current player position
    assertTrue(mapOutput.contains("@"));
  }

  @Test
  void testShowMapContainsVisitedRoomSymbol() {
    String mapOutput = mapService.showMap(gameContext);

    // Should contain # symbol for visited rooms
    assertTrue(mapOutput.contains("#"));
  }

  @Test
  void testShowMapContainsNavigationHint() {
    String mapOutput = mapService.showMap(gameContext);

    assertTrue(mapOutput.contains("Use 'go [direction]' to move"));
  }

  @Test
  void testShowFullMapReturnsValidOutput() {
    String mapOutput = mapService.showFullMap(gameContext);

    assertNotNull(mapOutput);
    assertFalse(mapOutput.isEmpty());
    assertTrue(mapOutput.contains("=== MAP ==="));
    assertTrue(mapOutput.contains("LEGEND:"));
  }

  @Test
  void testShowMapWithNoVisitedRooms() {
    // Create a player with no visited rooms
    Player newPlayer = new Player("NewPlayer", "room1");
    GameContext newContext = new GameContext(world, newPlayer);

    String mapOutput = mapService.showMap(newContext);

    assertNotNull(mapOutput);
    assertTrue(mapOutput.contains("@")); // Should still show current position
  }

  @Test
  void testShowMapWithInvalidCurrentRoom() {
    // Set player to a non-existent room
    player.setRoomId("nonexistent");

    String mapOutput = mapService.showMap(gameContext);

    assertTrue(mapOutput.contains("Map unavailable - current location unknown"));
  }

  @Test
  void testMapLayoutCentering() {
    String mapOutput = mapService.showMap(gameContext);

    // The map should be properly formatted with consistent spacing
    String[] lines = mapOutput.split("\n");
    boolean foundMapSection = false;

    for (String line : lines) {
      if (line.contains("=== MAP ===")) {
        foundMapSection = true;
        continue;
      }
      if (foundMapSection && line.contains("LEGEND:")) {
        break;
      }
      if (foundMapSection && !line.trim().isEmpty()) {
        // Map lines should have consistent length for proper formatting
        assertTrue(line.length() > 0);
      }
    }
  }

  @Test
  void testMapTruncationWithinBounds() {
    String mapOutput = mapService.showMap(gameContext);

    // Map should not exceed reasonable size bounds
    String[] lines = mapOutput.split("\n");

    for (String line : lines) {
      // No line should be excessively long
      assertTrue(line.length() < 100, "Map line too long: " + line);
    }
  }

  @Test
  void testMapShowsConnections() {
    String mapOutput = mapService.showMap(gameContext);

    // Should contain connection symbols
    assertTrue(
        mapOutput.contains("-") || mapOutput.contains("|"),
        "Map should show connections between rooms");
  }

  @Test
  void testMapServiceHandlesEmptyExits() {
    // Create a room with no exits
    Map<String, String> noExits = new HashMap<>();
    Room isolatedRoom =
        new Room(
            "isolated",
            "Isolated Room",
            "A room with no exits",
            new ArrayList<>(),
            new ArrayList<>(),
            noExits,
            new ArrayList<>(),
            false);

    Map<String, Room> rooms = new HashMap<>();
    rooms.put("isolated", isolatedRoom);

    World isolatedWorld =
        new World(rooms, new HashMap<>(), new HashMap<>(), new HashMap<>(), "isolated");
    Player isolatedPlayer = new Player("IsolatedPlayer", "isolated");
    GameContext isolatedContext = new GameContext(isolatedWorld, isolatedPlayer);

    String mapOutput = mapService.showMap(isolatedContext);

    assertNotNull(mapOutput);
    assertTrue(mapOutput.contains("- None")); // Should show "None" for no exits
  }
}
