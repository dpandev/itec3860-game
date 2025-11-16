package avengers.service;

import static org.junit.jupiter.api.Assertions.*;

import avengers.domain.model.*;
import avengers.domain.utils.GameContext;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultInteractionServiceTest {

  private DefaultInteractionService service;
  private GameContext ctx;
  private World world;
  private Player player;

  @BeforeEach
  void setUp() {
    service = new DefaultInteractionService();

    Puzzle puzzle1 =
        new Puzzle(
            "PUZ-01",
            "Simple Riddle",
            "What is 2+2?",
            "4",
            "You got it!",
            "Wrong answer!",
            "solve",
            3);

    Puzzle puzzle2 =
        new Puzzle(
            "PUZ-02",
            "One Attempt",
            "Guess the number",
            "42",
            "Correct!",
            "Locked forever!",
            "solve",
            1);

    Puzzle puzzle3 =
        new Puzzle(
            "PUZ-03", "Unlimited", "Keep trying", "secret", "Well done!", "Try again", "solve", -1);

    Item rewardItem = new Item("IT-99", "Magic Key", "Opens doors", "Key Item", "", "Special");

    Puzzle puzzleWithReward =
        new Puzzle(
            "PUZ-04",
            "Reward Puzzle",
            "Win a prize",
            "win",
            "You found IT-99!",
            "No reward",
            "solve",
            1);

    world =
        new World(
            Map.of(),
            Map.of("IT-99", rewardItem),
            Map.of(
                "PUZ-01", puzzle1,
                "PUZ-02", puzzle2,
                "PUZ-03", puzzle3,
                "PUZ-04", puzzleWithReward),
            Map.of(),
            "RM-01");

    player = new Player("TestPlayer", "RM-01");
    ctx = new GameContext(world, player);
  }

  @Test
  void testPresentPuzzle_ValidPuzzle() {
    String prompt = service.presentPuzzle(ctx, "PUZ-01");

    assertNotNull(prompt);
    assertTrue(prompt.contains("Simple Riddle"));
    assertTrue(prompt.contains("What is 2+2?"));
    assertTrue(prompt.contains("Attempts remaining: 3"));
    assertTrue(prompt.contains("solve <answer>"));
    assertTrue(ctx.isAwaitingPuzzleAnswer());
  }

  @Test
  void testPresentPuzzle_UnlimitedAttempts() {
    String prompt = service.presentPuzzle(ctx, "PUZ-03");

    assertNotNull(prompt);
    assertFalse(prompt.contains("Attempts remaining"));
  }

  @Test
  void testPresentPuzzle_AlreadySolved() {
    Puzzle puzzle = world.findPuzzle("PUZ-01").get();
    puzzle.setSolved();

    String prompt = service.presentPuzzle(ctx, "PUZ-01");

    assertEquals("", prompt);
    assertFalse(ctx.isAwaitingPuzzleAnswer());
  }

  @Test
  void testPresentPuzzle_LockedOut() {
    Puzzle puzzle = world.findPuzzle("PUZ-02").get();
    puzzle.useAttempt(); // Exhaust the single attempt

    String prompt = service.presentPuzzle(ctx, "PUZ-02");

    assertTrue(prompt.contains("locked"));
    assertFalse(ctx.isAwaitingPuzzleAnswer());
  }

  @Test
  void testPresentPuzzle_InvalidPuzzleId() {
    String prompt = service.presentPuzzle(ctx, "INVALID");

    assertEquals("", prompt);
  }

  @Test
  void testSolve_CorrectAnswer() {
    InteractionService.SolveResult result = service.solve(ctx, "PUZ-01", "4");

    assertTrue(result.isCorrect());
    assertTrue(result.getMessage().contains("Correct"));
    assertFalse(ctx.isAwaitingPuzzleAnswer());

    Puzzle puzzle = world.findPuzzle("PUZ-01").get();
    assertTrue(puzzle.isSolved());
    assertTrue(player.getPuzzlesSolved().contains("PUZ-01"));
  }

  @Test
  void testSolve_CorrectAnswerCaseInsensitive() {
    InteractionService.SolveResult result = service.solve(ctx, "PUZ-03", "SECRET");

    assertTrue(result.isCorrect());
  }

  @Test
  void testSolve_WrongAnswer() {
    InteractionService.SolveResult result = service.solve(ctx, "PUZ-01", "5");

    assertFalse(result.isCorrect());
    assertTrue(result.getMessage().contains("Incorrect"));
    assertTrue(result.getMessage().contains("Attempts remaining: 2"));

    Puzzle puzzle = world.findPuzzle("PUZ-01").get();
    assertFalse(puzzle.isSolved());
    assertEquals(2, puzzle.getAttemptsRemaining());
  }

  @Test
  void testSolve_LastAttemptFails() {
    Puzzle puzzle = world.findPuzzle("PUZ-02").get();

    InteractionService.SolveResult result = service.solve(ctx, "PUZ-02", "wrong");

    assertFalse(result.isCorrect());
    assertTrue(result.isLocked());
    assertTrue(result.getMessage().contains("Locked forever!"));
    assertTrue(result.getMessage().contains("locked"));
    assertTrue(puzzle.isAttemptsExhausted());
    assertFalse(ctx.isAwaitingPuzzleAnswer());
  }

  @Test
  void testSolve_AlreadySolved() {
    Puzzle puzzle = world.findPuzzle("PUZ-01").get();
    puzzle.setSolved();

    InteractionService.SolveResult result = service.solve(ctx, "PUZ-01", "4");

    assertFalse(result.isCorrect());
    assertTrue(result.getMessage().contains("already solved"));
  }

  @Test
  void testSolve_AlreadyLocked() {
    Puzzle puzzle = world.findPuzzle("PUZ-02").get();
    puzzle.useAttempt();

    InteractionService.SolveResult result = service.solve(ctx, "PUZ-02", "42");

    assertFalse(result.isCorrect());
    assertTrue(result.isLocked());
    assertTrue(result.getMessage().contains("locked"));
  }

  @Test
  void testSolve_InvalidPuzzleId() {
    InteractionService.SolveResult result = service.solve(ctx, "INVALID", "answer");

    assertFalse(result.isCorrect());
    assertTrue(result.getMessage().contains("not found"));
  }

  @Test
  void testSolve_WithReward() {
    InteractionService.SolveResult result = service.solve(ctx, "PUZ-04", "win");

    assertTrue(result.isCorrect());
    assertTrue(result.getMessage().contains("IT-99"));
    assertTrue(player.getInventoryItemIds().contains("IT-99"));
  }

  @Test
  void testSolve_MultipleWrongAttempts() {
    service.solve(ctx, "PUZ-01", "wrong1");
    service.solve(ctx, "PUZ-01", "wrong2");
    InteractionService.SolveResult result = service.solve(ctx, "PUZ-01", "wrong3");

    assertFalse(result.isCorrect());
    assertTrue(result.isLocked());

    Puzzle puzzle = world.findPuzzle("PUZ-01").get();
    assertTrue(puzzle.isAttemptsExhausted());
  }

  @Test
  void testSolve_UnlimitedAttemptsNeverLocks() {
    for (int i = 0; i < 100; i++) {
      InteractionService.SolveResult result = service.solve(ctx, "PUZ-03", "wrong");
      assertFalse(result.isCorrect());
      assertFalse(result.isLocked());
    }

    Puzzle puzzle = world.findPuzzle("PUZ-03").get();
    assertFalse(puzzle.isAttemptsExhausted());
  }

  @Test
  void testSolve_NullAnswer() {
    InteractionService.SolveResult result = service.solve(ctx, "PUZ-01", null);

    assertFalse(result.isCorrect());
    assertEquals(2, world.findPuzzle("PUZ-01").get().getAttemptsRemaining());
  }

  @Test
  void testSolve_BlankAnswer() {
    InteractionService.SolveResult result = service.solve(ctx, "PUZ-01", "   ");

    assertFalse(result.isCorrect());
  }

  @Test
  void testSolve_AnswerWithSpaces() {
    Puzzle spacePuzzle =
        new Puzzle("PUZ-05", "Space Answer", "What?", "the answer", "Good!", "Nope", "solve", 1);

    World testWorld =
        new World(Map.of(), Map.of(), Map.of("PUZ-05", spacePuzzle), Map.of(), "RM-01");
    GameContext testCtx = new GameContext(testWorld, player);

    InteractionService.SolveResult result1 = service.solve(testCtx, "PUZ-05", "the answer");
    assertTrue(result1.isCorrect());
  }
}
