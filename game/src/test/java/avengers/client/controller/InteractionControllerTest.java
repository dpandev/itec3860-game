package avengers.client.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import avengers.domain.model.*;
import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;
import avengers.service.InteractionService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InteractionControllerTest {

  private InteractionController controller;
  private InteractionService mockInteractionService;
  private GameContext ctx;
  private World world;
  private Player player;
  private Room room;

  @BeforeEach
  void setUp() {
    mockInteractionService = mock(InteractionService.class);
    controller = new InteractionController(mockInteractionService);

    // Create test world with puzzle room
    Puzzle puzzle =
        new Puzzle(
            "PUZ-01",
            "Stone Riddle",
            "What walks on four legs in the morning?",
            "man",
            "Correct! The door opens.",
            "Wrong! Try again.",
            "solve",
            3);

    Item sword = new Item("IT-01", "Steel Sword", "A sharp blade", "Weapon", "+30 Damage", "");
    Monster goblin = new Monster("Goblin", 50, 15, 5);

    room =
        new Room(
            "RM-01",
            "Puzzle Chamber",
            "A mysterious room",
            Map.of(),
            List.of("MON-01"),
            List.of("IT-01"),
            List.of("PUZ-01"));

    world =
        new World(
            Map.of("RM-01", room),
            Map.of("IT-01", sword),
            Map.of("PUZ-01", puzzle),
            Map.of("MON-01", goblin),
            "RM-01");

    player = new Player("TestPlayer", "RM-01");
    ctx = new GameContext(world, player);
  }

  @Test
  void testControllerCreation() {
    assertNotNull(controller);
  }

  @Test
  void testSupportsVerb_Solve() {
    assertTrue(controller.supports(Verb.SOLVE));
  }

  @Test
  void testSupportsVerb_Hint() {
    assertTrue(controller.supports(Verb.HINT));
  }

  @Test
  void testSupportsVerb_Inspect() {
    assertTrue(controller.supports(Verb.INSPECT));
  }

  @Test
  void testSupportsVerb_Activate() {
    assertTrue(controller.supports(Verb.ACTIVATE));
  }

  @Test
  void testSupportsVerb_DoesNotSupportGo() {
    assertFalse(controller.supports(Verb.GO));
  }

  @Test
  void testSupportsVerb_DoesNotSupportAttack() {
    assertFalse(controller.supports(Verb.ATTACK));
  }

  @Test
  void testHandle_NullCommand() {
    CommandResult result = controller.handle(null, ctx);

    assertFalse(result.success());
    assertTrue(result.message().contains("Invalid"));
  }

  @Test
  void testHandle_NullContext() {
    CommandToken cmd = new CommandToken(Verb.SOLVE, "answer", List.of("answer"), "solve answer");

    CommandResult result = controller.handle(cmd, null);

    assertFalse(result.success());
    assertTrue(result.message().contains("Invalid"));
  }

  @Test
  void testHandle_UnsupportedVerb() {
    CommandToken cmd = new CommandToken(Verb.GO, "north", List.of("north"), "go north");

    CommandResult result = controller.handle(cmd, ctx);

    assertFalse(result.success());
    assertTrue(result.message().contains("not supported"));
  }

  // ========== SOLVE Command Tests ==========

  @Test
  void testSolve_WithCorrectAnswer() {
    ctx.setAwaitingPuzzleAnswer(true);

    when(mockInteractionService.solve(eq(ctx), eq("PUZ-01"), eq("man")))
        .thenReturn(InteractionService.SolveResult.correct("Correct! Puzzle solved."));

    CommandToken cmd = new CommandToken(Verb.SOLVE, "man", List.of("man"), "solve man");
    CommandResult result = controller.handle(cmd, ctx);

    assertTrue(result.success());
    assertTrue(result.message().contains("Correct"));
    verify(mockInteractionService).solve(ctx, "PUZ-01", "man");
  }

  @Test
  void testSolve_WithIncorrectAnswer() {
    ctx.setAwaitingPuzzleAnswer(true);

    when(mockInteractionService.solve(eq(ctx), eq("PUZ-01"), eq("dog")))
        .thenReturn(InteractionService.SolveResult.incorrect("Wrong answer.", false));

    CommandToken cmd = new CommandToken(Verb.SOLVE, "dog", List.of("dog"), "solve dog");
    CommandResult result = controller.handle(cmd, ctx);

    assertTrue(result.success());
    assertTrue(result.message().contains("Wrong"));
    verify(mockInteractionService).solve(ctx, "PUZ-01", "dog");
  }

  @Test
  void testSolve_NotAwaitingPuzzle() {
    ctx.setAwaitingPuzzleAnswer(false);

    CommandToken cmd = new CommandToken(Verb.SOLVE, "answer", List.of("answer"), "solve answer");
    CommandResult result = controller.handle(cmd, ctx);

    assertFalse(result.success());
    assertTrue(result.message().contains("no puzzle waiting"));
    verify(mockInteractionService, never()).solve(any(), any(), any());
  }

  @Test
  void testSolve_NoTargetProvided() {
    ctx.setAwaitingPuzzleAnswer(true);

    CommandToken cmd = new CommandToken(Verb.SOLVE, null, List.of(), "solve");
    CommandResult result = controller.handle(cmd, ctx);

    assertFalse(result.success());
    assertTrue(result.message().contains("What's your answer"));
    verify(mockInteractionService, never()).solve(any(), any(), any());
  }

  @Test
  void testSolve_BlankTarget() {
    ctx.setAwaitingPuzzleAnswer(true);

    CommandToken cmd = new CommandToken(Verb.SOLVE, "   ", List.of(), "solve   ");
    CommandResult result = controller.handle(cmd, ctx);

    assertFalse(result.success());
    assertTrue(result.message().contains("What's your answer"));
  }

  @Test
  void testSolve_CurrentRoomNotFound() {
    ctx.setAwaitingPuzzleAnswer(true);
    player.setRoomId("INVALID");

    CommandToken cmd = new CommandToken(Verb.SOLVE, "answer", List.of("answer"), "solve answer");
    CommandResult result = controller.handle(cmd, ctx);

    assertFalse(result.success());
    assertTrue(result.message().contains("room not found"));
  }

  @Test
  void testSolve_RoomWithNoPuzzles() {
    ctx.setAwaitingPuzzleAnswer(true);

    // Create room without puzzles
    Room noPuzzleRoom =
        new Room("RM-02", "Empty Room", "Nothing here", Map.of(), List.of(), List.of(), List.of());
    world.getRooms().put("RM-02", noPuzzleRoom);
    player.setRoomId("RM-02");

    CommandToken cmd = new CommandToken(Verb.SOLVE, "answer", List.of("answer"), "solve answer");
    CommandResult result = controller.handle(cmd, ctx);

    assertFalse(result.success());
    assertTrue(result.message().contains("No puzzle in this room"));
  }

  @Test
  void testSolve_AllPuzzlesSolved() {
    ctx.setAwaitingPuzzleAnswer(true);

    // Solve the puzzle
    Puzzle puzzle = world.findPuzzle("PUZ-01").get();
    puzzle.setSolved();

    CommandToken cmd = new CommandToken(Verb.SOLVE, "answer", List.of("answer"), "solve answer");
    CommandResult result = controller.handle(cmd, ctx);

    assertFalse(result.success());
    assertTrue(result.message().contains("No active puzzle"));
  }

  @Test
  void testSolve_MultiWordAnswer() {
    ctx.setAwaitingPuzzleAnswer(true);

    when(mockInteractionService.solve(eq(ctx), eq("PUZ-01"), eq("the answer is man")))
        .thenReturn(InteractionService.SolveResult.correct("Correct!"));

    CommandToken cmd =
        new CommandToken(
            Verb.SOLVE,
            "the answer is man",
            List.of("the", "answer", "is", "man"),
            "solve the answer is man");
    CommandResult result = controller.handle(cmd, ctx);

    assertTrue(result.success());
    verify(mockInteractionService).solve(ctx, "PUZ-01", "the answer is man");
  }

  // ========== HINT Command Tests ==========

  @Test
  void testHint_WithPuzzleInRoom() {
    CommandToken cmd = new CommandToken(Verb.HINT, null, List.of(), "hint");
    CommandResult result = controller.handle(cmd, ctx);

    assertTrue(result.success());
    assertTrue(result.message().contains("Hint"));
    assertTrue(result.message().contains("description"));
  }

  @Test
  void testHint_NoPuzzleInRoom() {
    Room noPuzzleRoom =
        new Room("RM-02", "Empty Room", "Nothing here", Map.of(), List.of(), List.of(), List.of());
    world.getRooms().put("RM-02", noPuzzleRoom);
    player.setRoomId("RM-02");

    CommandToken cmd = new CommandToken(Verb.HINT, null, List.of(), "hint");
    CommandResult result = controller.handle(cmd, ctx);

    assertFalse(result.success());
    assertTrue(result.message().contains("no puzzle here"));
  }

  @Test
  void testHint_AllPuzzlesSolved() {
    Puzzle puzzle = world.findPuzzle("PUZ-01").get();
    puzzle.setSolved();

    CommandToken cmd = new CommandToken(Verb.HINT, null, List.of(), "hint");
    CommandResult result = controller.handle(cmd, ctx);

    assertFalse(result.success());
    assertTrue(result.message().contains("All puzzles"));
    assertTrue(result.message().contains("solved"));
  }

  @Test
  void testHint_InvalidRoom() {
    player.setRoomId("INVALID");

    CommandToken cmd = new CommandToken(Verb.HINT, null, List.of(), "hint");
    CommandResult result = controller.handle(cmd, ctx);

    assertFalse(result.success());
    assertTrue(result.message().contains("room not found"));
  }

  // ========== INSPECT Command Tests ==========

  @Test
  void testInspect_ValidItem() {
    CommandToken cmd =
        new CommandToken(Verb.INSPECT, "steel sword", List.of("steel", "sword"), "inspect sword");
    CommandResult result = controller.handle(cmd, ctx);

    assertTrue(result.success());
    assertTrue(result.message().contains("Steel Sword"));
    assertTrue(result.message().contains("A sharp blade"));
    assertTrue(result.message().contains("Weapon"));
    assertTrue(result.message().contains("+30 Damage"));
  }

  @Test
  void testInspect_ValidMonster() {
    CommandToken cmd =
        new CommandToken(Verb.INSPECT, "goblin", List.of("goblin"), "inspect goblin");
    CommandResult result = controller.handle(cmd, ctx);

    assertTrue(result.success());
    assertTrue(result.message().contains("Goblin"));
    assertTrue(result.message().contains("HP:"));
    assertTrue(result.message().contains("Attack:"));
    assertTrue(result.message().contains("Defense:"));
  }

  @Test
  void testInspect_CaseInsensitiveItem() {
    CommandToken cmd =
        new CommandToken(Verb.INSPECT, "STEEL SWORD", List.of("STEEL", "SWORD"), "inspect sword");
    CommandResult result = controller.handle(cmd, ctx);

    assertTrue(result.success());
    assertTrue(result.message().contains("Steel Sword"));
  }

  @Test
  void testInspect_CaseInsensitiveMonster() {
    CommandToken cmd =
        new CommandToken(Verb.INSPECT, "GOBLIN", List.of("GOBLIN"), "inspect GOBLIN");
    CommandResult result = controller.handle(cmd, ctx);

    assertTrue(result.success());
    assertTrue(result.message().contains("Goblin"));
  }

  @Test
  void testInspect_NoTarget() {
    CommandToken cmd = new CommandToken(Verb.INSPECT, null, List.of(), "inspect");
    CommandResult result = controller.handle(cmd, ctx);

    assertFalse(result.success());
    assertTrue(result.message().contains("Inspect what"));
  }

  @Test
  void testInspect_BlankTarget() {
    CommandToken cmd = new CommandToken(Verb.INSPECT, "   ", List.of(), "inspect   ");
    CommandResult result = controller.handle(cmd, ctx);

    assertFalse(result.success());
    assertTrue(result.message().contains("Inspect what"));
  }

  @Test
  void testInspect_NotFound() {
    CommandToken cmd =
        new CommandToken(Verb.INSPECT, "dragon", List.of("dragon"), "inspect dragon");
    CommandResult result = controller.handle(cmd, ctx);

    assertFalse(result.success());
    assertTrue(result.message().contains("don't see"));
    assertTrue(result.message().contains("dragon"));
  }

  @Test
  void testInspect_ItemWithSpecialEffect() {
    Item magicItem =
        new Item(
            "IT-02", "Magic Wand", "A mystical wand", "Weapon", "+50 Damage", "Casts fireball");
    world.getItems().put("IT-02", magicItem);

    CommandToken cmd =
        new CommandToken(Verb.INSPECT, "magic wand", List.of("magic", "wand"), "inspect wand");
    CommandResult result = controller.handle(cmd, ctx);

    assertTrue(result.success());
    assertTrue(result.message().contains("Magic Wand"));
    assertTrue(result.message().contains("Special"));
    assertTrue(result.message().contains("Casts fireball"));
  }

  @Test
  void testInspect_ItemWithNoEffect() {
    Item simpleItem = new Item("IT-03", "Old Key", "Rusty key", "Key Item", "", "");
    world.getItems().put("IT-03", simpleItem);

    CommandToken cmd =
        new CommandToken(Verb.INSPECT, "old key", List.of("old", "key"), "inspect key");
    CommandResult result = controller.handle(cmd, ctx);

    assertTrue(result.success());
    assertTrue(result.message().contains("Old Key"));
    assertFalse(result.message().contains("Effect: \n"));
  }

  // ========== ACTIVATE Command Tests ==========

  @Test
  void testActivate_WithTarget() {
    CommandToken cmd = new CommandToken(Verb.ACTIVATE, "lever", List.of("lever"), "activate lever");
    CommandResult result = controller.handle(cmd, ctx);

    assertTrue(result.success());
    assertTrue(result.message().contains("activate"));
    assertTrue(result.message().contains("lever"));
    assertTrue(result.message().contains("nothing happens"));
  }

  @Test
  void testActivate_NoTarget() {
    CommandToken cmd = new CommandToken(Verb.ACTIVATE, null, List.of(), "activate");
    CommandResult result = controller.handle(cmd, ctx);

    assertFalse(result.success());
    assertTrue(result.message().contains("Activate what"));
  }

  @Test
  void testActivate_BlankTarget() {
    CommandToken cmd = new CommandToken(Verb.ACTIVATE, "   ", List.of(), "activate   ");
    CommandResult result = controller.handle(cmd, ctx);

    assertFalse(result.success());
    assertTrue(result.message().contains("Activate what"));
  }

  @Test
  void testActivate_MultiWordTarget() {
    CommandToken cmd =
        new CommandToken(
            Verb.ACTIVATE,
            "ancient stone door",
            List.of("ancient", "stone", "door"),
            "activate ancient stone door");
    CommandResult result = controller.handle(cmd, ctx);

    assertTrue(result.success());
    assertTrue(result.message().contains("ancient stone door"));
  }

  // ========== Integration Tests ==========

  @Test
  void testSolveFlow_CompleteSuccess() {
    // Setup: Enter room, puzzle presented
    ctx.setAwaitingPuzzleAnswer(true);

    // Mock correct answer
    when(mockInteractionService.solve(eq(ctx), eq("PUZ-01"), eq("man")))
        .thenReturn(InteractionService.SolveResult.correct("Success! Door opens."));

    // Solve the puzzle
    CommandToken cmd = new CommandToken(Verb.SOLVE, "man", List.of("man"), "solve man");
    CommandResult result = controller.handle(cmd, ctx);

    assertTrue(result.success());
    assertTrue(result.message().contains("Success"));
    verify(mockInteractionService).solve(ctx, "PUZ-01", "man");
  }

  @Test
  void testSolveFlow_MultipleAttempts() {
    ctx.setAwaitingPuzzleAnswer(true);

    // First attempt - wrong
    when(mockInteractionService.solve(eq(ctx), eq("PUZ-01"), eq("dog")))
        .thenReturn(InteractionService.SolveResult.incorrect("Wrong! 2 attempts left.", false));

    CommandToken cmd1 = new CommandToken(Verb.SOLVE, "dog", List.of("dog"), "solve dog");
    CommandResult result1 = controller.handle(cmd1, ctx);

    assertFalse(result1.message().contains("Correct"));

    // Second attempt - correct
    when(mockInteractionService.solve(eq(ctx), eq("PUZ-01"), eq("man")))
        .thenReturn(InteractionService.SolveResult.correct("Correct!"));

    CommandToken cmd2 = new CommandToken(Verb.SOLVE, "man", List.of("man"), "solve man");
    CommandResult result2 = controller.handle(cmd2, ctx);

    assertTrue(result2.success());
    assertTrue(result2.message().contains("Correct"));
  }

  @Test
  void testInspectFlow_ItemThenMonster() {
    // Inspect item
    CommandToken cmd1 =
        new CommandToken(Verb.INSPECT, "steel sword", List.of("steel", "sword"), "inspect sword");
    CommandResult result1 = controller.handle(cmd1, ctx);
    assertTrue(result1.success());
    assertTrue(result1.message().contains("Steel Sword"));

    // Inspect monster
    CommandToken cmd2 =
        new CommandToken(Verb.INSPECT, "goblin", List.of("goblin"), "inspect goblin");
    CommandResult result2 = controller.handle(cmd2, ctx);
    assertTrue(result2.success());
    assertTrue(result2.message().contains("Goblin"));
  }

  @Test
  void testHintThenSolve() {
    ctx.setAwaitingPuzzleAnswer(true);

    // Get hint first
    CommandToken hintCmd = new CommandToken(Verb.HINT, null, List.of(), "hint");
    CommandResult hintResult = controller.handle(hintCmd, ctx);
    assertTrue(hintResult.success());

    // Then solve
    when(mockInteractionService.solve(eq(ctx), eq("PUZ-01"), eq("man")))
        .thenReturn(InteractionService.SolveResult.correct("Correct!"));

    CommandToken solveCmd = new CommandToken(Verb.SOLVE, "man", List.of("man"), "solve man");
    CommandResult solveResult = controller.handle(solveCmd, ctx);
    assertTrue(solveResult.success());
  }

  @Test
  void testMultiplePuzzlesInRoom() {
    // Add second puzzle
    Puzzle puzzle2 =
        new Puzzle(
            "PUZ-02", "Number Puzzle", "What is 2+2?", "4", "Correct!", "Wrong!", "solve", 1);
    world.getPuzzles().put("PUZ-02", puzzle2);
    room.getPuzzleIds().add("PUZ-02");

    ctx.setAwaitingPuzzleAnswer(true);

    // Should use first unsolved puzzle
    when(mockInteractionService.solve(eq(ctx), eq("PUZ-01"), anyString()))
        .thenReturn(InteractionService.SolveResult.correct("First puzzle solved!"));

    CommandToken cmd = new CommandToken(Verb.SOLVE, "man", List.of("man"), "solve man");
    CommandResult result = controller.handle(cmd, ctx);

    assertTrue(result.success());
    verify(mockInteractionService).solve(eq(ctx), eq("PUZ-01"), anyString());
  }
}
