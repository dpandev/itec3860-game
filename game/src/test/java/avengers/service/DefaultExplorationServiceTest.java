package avengers.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import avengers.domain.model.*;
import avengers.domain.utils.GameContext;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultExplorationServiceTest {

  private DefaultExplorationService service;
  private InteractionService mockInteractionService;
  private GameContext ctx;
  private World world;
  private Player player;

  @BeforeEach
  void setUp() {
    mockInteractionService = mock(InteractionService.class);
    service = new DefaultExplorationService(mockInteractionService);

    // Create test world with rooms
    Room room1 =
        new Room(
            "RM-01",
            "Guild Hall",
            "A bustling headquarters.",
            Map.of("NORTH", "RM-02", "EAST", "RM-03"),
            List.of(),
            List.of("IT-01"),
            List.of());

    Room room2 =
        new Room(
            "RM-02",
            "Dungeon Entrance",
            "Dark and foreboding.",
            Map.of("SOUTH", "RM-01"),
            List.of("MON-01"),
            List.of(),
            List.of());

    Room room3 =
        new Room(
            "RM-03",
            "Puzzle Room",
            "A mysterious chamber.",
            Map.of("WEST", "RM-01"),
            List.of(),
            List.of(),
            List.of("PUZ-01"));

    Monster monster = new Monster("Iron Wolf", 100, 25, 10);
    Item item = new Item("IT-01", "Health Potion", "Restores HP", "Consumable", "+20 HP", "");
    Puzzle puzzle =
        new Puzzle(
            "PUZ-01",
            "Stone Riddle",
            "What walks on four legs?",
            "man",
            "Door unlocked",
            "Wrong!",
            "solve",
            3);

    world =
        new World(
            Map.of("RM-01", room1, "RM-02", room2, "RM-03", room3),
            Map.of("IT-01", item),
            Map.of("PUZ-01", puzzle),
            Map.of("MON-01", monster),
            "RM-01");

    player = new Player("TestPlayer", "RM-01");
    ctx = new GameContext(world, player);
  }

  @Test
  void testDescribeCurrentRoom_ValidRoom() {
    String description = service.describeCurrentRoom(ctx);

    assertNotNull(description);
    assertTrue(description.contains("Guild Hall"));
    assertTrue(description.contains("A bustling headquarters"));
    assertTrue(description.contains("north"));
    assertTrue(description.contains("east"));
    assertTrue(description.contains("Health Potion"));
  }

  @Test
  void testDescribeCurrentRoom_InvalidRoom() {
    player.setRoomId("INVALID");

    String description = service.describeCurrentRoom(ctx);

    assertTrue(description.contains("Error"));
    assertTrue(description.contains("not found"));
  }

  @Test
  void testExplore_SameAsDescribe() {
    String explore = service.explore(ctx);
    String describe = service.describeCurrentRoom(ctx);

    assertEquals(explore, describe);
  }

  @Test
  void testMove_ValidDirection() {
    ExplorationService.MoveResult result = service.move(ctx, "north");

    assertTrue(result.isSuccess());
    assertEquals("RM-02", player.getRoomId());
    assertTrue(result.getMessage().contains("Dungeon Entrance"));
    assertTrue(player.getRoomsVisited().contains("RM-02"));
  }

  @Test
  void testMove_DirectionAlias() {
    ExplorationService.MoveResult result = service.move(ctx, "n");

    assertTrue(result.isSuccess());
    assertEquals("RM-02", player.getRoomId());
  }

  @Test
  void testMove_InvalidDirection() {
    ExplorationService.MoveResult result = service.move(ctx, "west");

    assertFalse(result.isSuccess());
    assertTrue(result.getMessage().contains("cannot go"));
    assertEquals("RM-01", player.getRoomId());
  }

  @Test
  void testMove_NullDirection() {
    ExplorationService.MoveResult result = service.move(ctx, null);

    assertFalse(result.isSuccess());
    assertTrue(result.getMessage().contains("Which direction"));
  }

  @Test
  void testMove_BlankDirection() {
    ExplorationService.MoveResult result = service.move(ctx, "   ");

    assertFalse(result.isSuccess());
    assertTrue(result.getMessage().contains("Which direction"));
  }

  @Test
  void testMove_ShowsMonsters() {
    ExplorationService.MoveResult result = service.move(ctx, "north");

    assertTrue(result.isSuccess());
    assertTrue(result.getMessage().contains("Iron Wolf"));
  }

  @Test
  void testMove_PuzzlePresentation() {
    when(mockInteractionService.presentPuzzle(any(), eq("PUZ-01")))
        .thenReturn("\nPuzzle presented!");

    ExplorationService.MoveResult result = service.move(ctx, "east");

    assertTrue(result.isSuccess());
    assertTrue(result.isPuzzlePresented());
    assertTrue(result.getMessage().contains("Puzzle presented"));
    verify(mockInteractionService).presentPuzzle(ctx, "PUZ-01");
  }

  @Test
  void testMove_PuzzleNotPresentedIfSolved() {
    // Solve the puzzle first
    Puzzle puzzle = world.findPuzzle("PUZ-01").get();
    puzzle.setSolved();

    ExplorationService.MoveResult result = service.move(ctx, "east");

    assertTrue(result.isSuccess());
    assertFalse(result.isPuzzlePresented());
    verify(mockInteractionService, never()).presentPuzzle(any(), any());
  }

  @Test
  void testMove_ResetsPuzzleWhenLeaving() {
    // Move to puzzle room
    service.move(ctx, "east");
    ctx.setAwaitingPuzzleAnswer(true);

    // Move away
    ExplorationService.MoveResult result = service.move(ctx, "west");

    assertTrue(result.isSuccess());
    assertFalse(ctx.isAwaitingPuzzleAnswer());
    // Puzzle should be reset
    Puzzle puzzle = world.findPuzzle("PUZ-01").get();
    assertEquals(3, puzzle.getAttemptsRemaining());
  }

  @Test
  void testMove_TracksVisitedRooms() {
    assertTrue(player.getRoomsVisited().isEmpty());

    service.move(ctx, "north");
    assertEquals(1, player.getRoomsVisited().size());
    assertTrue(player.getRoomsVisited().contains("RM-02"));

    service.move(ctx, "south");
    service.move(ctx, "east");
    assertEquals(2, player.getRoomsVisited().size());
  }

  @Test
  void testMove_NoExitInDirection() {
    ExplorationService.MoveResult result = service.move(ctx, "south");

    assertFalse(result.isSuccess());
    assertTrue(result.getMessage().toLowerCase().contains("cannot go"));
    assertEquals("RM-01", player.getRoomId());
  }

  @Test
  void testMove_DestinationNotFound() {
    // Create room with invalid exit
    Room badRoom =
        new Room(
            "BAD", "Bad Room", "Test", Map.of("NORTH", "NOWHERE"), List.of(), List.of(), List.of());

    World badWorld = new World(Map.of("BAD", badRoom), Map.of(), Map.of(), Map.of(), "BAD");
    Player badPlayer = new Player("Test", "BAD");
    GameContext badCtx = new GameContext(badWorld, badPlayer);

    DefaultExplorationService badService = new DefaultExplorationService(null);
    ExplorationService.MoveResult result = badService.move(badCtx, "north");

    assertFalse(result.isSuccess());
    assertTrue(result.getMessage().contains("Destination room not found"));
  }

  @Test
  void testDescribeRoom_ShowsDeadMonsters() {
    Monster deadMonster = new Monster("Dead Wolf", 100, 10, 5);
    deadMonster.takeDamage(100);

    Room roomWithDead =
        new Room("TEST", "Test Room", "Test", Map.of(), List.of("MON-DEAD"), List.of(), List.of());

    World testWorld =
        new World(
            Map.of("TEST", roomWithDead),
            Map.of(),
            Map.of(),
            Map.of("MON-DEAD", deadMonster),
            "TEST");

    Player testPlayer = new Player("Test", "TEST");
    GameContext testCtx = new GameContext(testWorld, testPlayer);

    DefaultExplorationService testService = new DefaultExplorationService(null);
    String description = testService.describeCurrentRoom(testCtx);

    // Dead monsters should not appear
    assertFalse(description.contains("Dead Wolf"));
  }

  @Test
  void testDescribeRoom_NoExits() {
    Room noExitRoom =
        new Room("TRAP", "Dead End", "No way out", Map.of(), List.of(), List.of(), List.of());

    World testWorld = new World(Map.of("TRAP", noExitRoom), Map.of(), Map.of(), Map.of(), "TRAP");
    Player testPlayer = new Player("Test", "TRAP");
    GameContext testCtx = new GameContext(testWorld, testPlayer);

    DefaultExplorationService testService = new DefaultExplorationService(null);
    String description = testService.describeCurrentRoom(testCtx);

    assertTrue(description.contains("No obvious exits"));
  }

  @Test
  void testMove_CaseInsensitiveDirection() {
    ExplorationService.MoveResult result1 = service.move(ctx, "NORTH");
    player.setRoomId("RM-01"); // reset

    ExplorationService.MoveResult result2 = service.move(ctx, "North");
    player.setRoomId("RM-01"); // reset

    ExplorationService.MoveResult result3 = service.move(ctx, "NoRtH");

    assertTrue(result1.isSuccess());
    assertTrue(result2.isSuccess());
    assertTrue(result3.isSuccess());
  }
}
