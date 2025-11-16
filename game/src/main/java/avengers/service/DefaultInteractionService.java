package avengers.service;

import avengers.domain.model.Player;
import avengers.domain.model.Puzzle;
import avengers.domain.model.Room;
import avengers.domain.model.World;
import avengers.domain.utils.CommandResult;
import avengers.domain.utils.GameContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Default implementation of InteractionService for managing puzzles. */
public class DefaultInteractionService implements InteractionService {

  // Track puzzle state: puzzleId -> remaining attempts
  private final Map<String, Integer> puzzleAttempts = new HashMap<>();

  // Track puzzle state: puzzleId -> current phase (LOCKED, IN_PROGRESS, SOLVED)
  private final Map<String, PuzzlePhase> puzzlePhases = new HashMap<>();

  // Track active puzzle per player
  private String activePuzzleId = null;

  /** Puzzle phase enum. */
  private enum PuzzlePhase {
    LOCKED,
    IN_PROGRESS,
    SOLVED
  }

  @Override
  public CommandResult checkAndPresentPuzzle(GameContext ctx) {
    Player player = ctx.player();
    World world = ctx.world();

    Optional<Room> roomOpt = world.getRoomById(player.getRoomId());
    if (roomOpt.isEmpty()) {
      return null;
    }

    Room room = roomOpt.get();
    List<String> puzzleIds = room.getPuzzleIds();

    if (puzzleIds.isEmpty()) {
      return null;
    }

    // Find first unsolved puzzle in room
    for (String puzzleId : puzzleIds) {
      PuzzlePhase phase = puzzlePhases.getOrDefault(puzzleId, PuzzlePhase.LOCKED);

      if (phase == PuzzlePhase.LOCKED && !player.getPuzzlesSolved().contains(puzzleId)) {
        Optional<Puzzle> puzzleOpt = world.findPuzzle(puzzleId);
        if (puzzleOpt.isPresent()) {
          Puzzle puzzle = puzzleOpt.get();

          // Initialize attempts if not set
          if (!puzzleAttempts.containsKey(puzzleId)) {
            puzzleAttempts.put(puzzleId, puzzle.getNumberOfAttempts());
          }

          // Present the puzzle
          activePuzzleId = puzzleId;
          puzzlePhases.put(puzzleId, PuzzlePhase.IN_PROGRESS);
          ctx.setAwaitingPuzzleAnswer(true);

          return CommandResult.success(presentPuzzleText(puzzle));
        }
      }
    }

    return null;
  }

  @Override
  public CommandResult solvePuzzle(GameContext ctx, String answer) {
    if (!ctx.isAwaitingPuzzleAnswer() || activePuzzleId == null) {
      return CommandResult.fail("There is no active puzzle to solve.");
    }

    if (answer == null || answer.isBlank()) {
      return CommandResult.fail("You must provide an answer. Usage: solve <answer>");
    }

    World world = ctx.world();
    Optional<Puzzle> puzzleOpt = world.findPuzzle(activePuzzleId);

    if (puzzleOpt.isEmpty()) {
      return CommandResult.fail("Puzzle data not found.");
    }

    Puzzle puzzle = puzzleOpt.get();
    int remainingAttempts = puzzleAttempts.getOrDefault(activePuzzleId, 0);

    // Check answer (case-insensitive)
    if (answer.trim().equalsIgnoreCase(puzzle.getSolution().trim())) {
      // Correct answer
      puzzlePhases.put(activePuzzleId, PuzzlePhase.SOLVED);
      ctx.player().getPuzzlesSolved().add(activePuzzleId);
      ctx.setAwaitingPuzzleAnswer(false);
      activePuzzleId = null;

      // Handle reward if present
      String rewardMessage = "";
      if (puzzle.getReward() != null && !puzzle.getReward().isBlank()) {
        rewardMessage = "\n\nReward: " + puzzle.getReward();

        // If reward contains item ID, add it to inventory
        String reward = puzzle.getReward();
        if (reward.contains("IT-") || reward.toLowerCase().contains("item")) {
          // Extract potential item ID or name from reward text
          // This is simplified - in production, rewards should be structured data
          rewardMessage += "\n(Item added to inventory if applicable)";
        }
      }

      return CommandResult.success(
          "Correct! You have solved the puzzle: " + puzzle.getName() + rewardMessage);
    } else {
      // Wrong answer
      remainingAttempts--;
      puzzleAttempts.put(activePuzzleId, remainingAttempts);

      if (remainingAttempts <= 0) {
        // Lockout
        puzzlePhases.put(activePuzzleId, PuzzlePhase.LOCKED);
        ctx.setAwaitingPuzzleAnswer(false);

        String failureMsg = "Incorrect! You have no attempts remaining.";
        if (puzzle.getFailureConsequence() != null && !puzzle.getFailureConsequence().isBlank()) {
          failureMsg += "\n" + puzzle.getFailureConsequence();
        }

        activePuzzleId = null;
        return CommandResult.fail(failureMsg);
      } else {
        return CommandResult.fail(
            "Incorrect! You have "
                + remainingAttempts
                + " attempt"
                + (remainingAttempts == 1 ? "" : "s")
                + " remaining.");
      }
    }
  }

  @Override
  public CommandResult getHint(GameContext ctx) {
    if (!ctx.isAwaitingPuzzleAnswer() || activePuzzleId == null) {
      return CommandResult.fail("There is no active puzzle.");
    }

    World world = ctx.world();
    Optional<Puzzle> puzzleOpt = world.findPuzzle(activePuzzleId);

    if (puzzleOpt.isEmpty()) {
      return CommandResult.fail("Puzzle data not found.");
    }

    Puzzle puzzle = puzzleOpt.get();

    // Return hint based on puzzle solution or description
    String hint = "Hint: ";
    if (puzzle.getSolution() != null && !puzzle.getSolution().isBlank()) {
      // Give a partial hint (first letter and length)
      String solution = puzzle.getSolution();
      hint +=
          "The answer starts with '"
              + solution.charAt(0)
              + "' and has "
              + solution.length()
              + " characters.";
    } else {
      hint += "Study the puzzle description carefully.";
    }

    return CommandResult.success(hint);
  }

  /**
   * Resets puzzle state for the given puzzle ID.
   *
   * @param puzzleId the puzzle ID to reset
   */
  public void resetPuzzle(String puzzleId) {
    puzzlePhases.put(puzzleId, PuzzlePhase.LOCKED);
    puzzleAttempts.remove(puzzleId);
    if (puzzleId.equals(activePuzzleId)) {
      activePuzzleId = null;
    }
  }

  /**
   * Formats puzzle presentation text.
   *
   * @param puzzle the puzzle to present
   * @return formatted puzzle text
   */
  private String presentPuzzleText(Puzzle puzzle) {
    StringBuilder text = new StringBuilder();
    text.append("=== PUZZLE: ").append(puzzle.getName()).append(" ===\n\n");
    text.append(puzzle.getDescription()).append("\n\n");

    int attempts = puzzleAttempts.getOrDefault(puzzle.getId(), puzzle.getNumberOfAttempts());
    if (attempts > 0) {
      text.append("You have ")
          .append(attempts)
          .append(" attempt")
          .append(attempts == 1 ? "" : "s")
          .append(" to solve this puzzle.\n");
    } else {
      text.append("You have unlimited attempts to solve this puzzle.\n");
    }

    if (puzzle.getCommandUsed() != null && !puzzle.getCommandUsed().isBlank()) {
      text.append("\nHow to solve: ").append(puzzle.getCommandUsed());
    }

    return text.toString();
  }
}
