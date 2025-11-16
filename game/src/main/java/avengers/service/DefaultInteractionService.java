package avengers.service;

import avengers.domain.model.Player;
import avengers.domain.model.Puzzle;
import avengers.domain.model.World;
import avengers.domain.utils.GameContext;
import java.util.Optional;

/** Default implementation of InteractionService. */
public class DefaultInteractionService implements InteractionService {

  @Override
  public String presentPuzzle(GameContext ctx, String puzzleId) {
    World world = ctx.world();
    Optional<Puzzle> puzzleOpt = world.findPuzzle(puzzleId);

    if (puzzleOpt.isEmpty()) {
      return ""; // Silently skip missing puzzles
    }

    Puzzle puzzle = puzzleOpt.get();

    // Don't present if already solved
    if (puzzle.isSolved()) {
      return "";
    }

    // Don't present if locked out
    if (puzzle.isAttemptsExhausted()) {
      return "\n🔒 The puzzle is locked. You've exhausted all attempts.\n";
    }

    // Set context flag
    ctx.setAwaitingPuzzleAnswer(true);

    // Build puzzle prompt
    StringBuilder sb = new StringBuilder();
    sb.append("\n🔍 === PUZZLE: ").append(puzzle.getName()).append(" ===\n");
    sb.append(puzzle.getDescription()).append("\n");

    if (!puzzle.hasUnlimitedAttempts()) {
      sb.append("\nAttempts remaining: ").append(puzzle.getAttemptsRemaining()).append("\n");
    }

    sb.append("\nUse 'solve <answer>' to attempt a solution.\n");

    return sb.toString();
  }

  @Override
  public SolveResult solve(GameContext ctx, String puzzleId, String answer) {
    World world = ctx.world();
    Optional<Puzzle> puzzleOpt = world.findPuzzle(puzzleId);

    if (puzzleOpt.isEmpty()) {
      return SolveResult.incorrect("Puzzle not found.", false);
    }

    Puzzle puzzle = puzzleOpt.get();

    // Check if already solved
    if (puzzle.isSolved()) {
      return SolveResult.incorrect("You've already solved this puzzle.", false);
    }

    // Check if locked out
    if (puzzle.isAttemptsExhausted()) {
      return SolveResult.incorrect("This puzzle is locked. You've used all attempts.", true);
    }

    // Use an attempt
    if (!puzzle.useAttempt()) {
      return SolveResult.incorrect("No attempts remaining.", true);
    }

    // Check solution
    if (puzzle.checkSolution(answer)) {
      puzzle.setSolved();
      ctx.setAwaitingPuzzleAnswer(false);

      // Add puzzle to solved list
      Player player = ctx.player();
      if (!player.getPuzzlesSolved().contains(puzzleId)) {
        player.getPuzzlesSolved().add(puzzleId);
      }

      StringBuilder sb = new StringBuilder();
      sb.append("\n✓ Correct! ").append(puzzle.getName()).append(" solved!\n");

      // Handle reward
      String reward = puzzle.getReward();
      if (reward != null && !reward.isBlank()) {
        sb.append("\n").append(reward).append("\n");

        // Check if reward is an item (format: contains "item" or item ID)
        if (reward.toLowerCase().contains("item") || reward.matches(".*IT-\\d+.*")) {
          // Extract item ID if present
          if (reward.matches(".*IT-\\d+.*")) {
            String itemId = reward.replaceAll(".*?(IT-\\d+).*", "$1");
            if (world.findItem(itemId).isPresent()) {
              player.addItemToInventory(itemId);
              sb.append("(Item added to inventory)\n");
            }
          }
        }
      }

      return SolveResult.correct(sb.toString());
    } else {
      // Wrong answer
      StringBuilder sb = new StringBuilder();
      sb.append("\n✗ Incorrect answer.\n");

      if (puzzle.isAttemptsExhausted()) {
        sb.append("\n").append(puzzle.getFailureConsequence()).append("\n");
        sb.append("The puzzle is now locked.\n");
        ctx.setAwaitingPuzzleAnswer(false);
        return SolveResult.incorrect(sb.toString(), true);
      } else {
        if (!puzzle.hasUnlimitedAttempts()) {
          sb.append("Attempts remaining: ").append(puzzle.getAttemptsRemaining()).append("\n");
        }
        return SolveResult.incorrect(sb.toString(), false);
      }
    }
  }
}
