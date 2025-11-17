package avengers.service;

import avengers.domain.model.Player;
import avengers.domain.model.Puzzle;
import avengers.domain.model.Room;
import avengers.domain.model.World;
import avengers.domain.utils.CommandResult;
import avengers.domain.utils.GameContext;
import java.util.ArrayList;
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

    // Special handling for PUZ-10 (Final Gate Seal) - requires all 4 sigils
    if (puzzle.getId().equals("PUZ-10")) {
      // Check if player has all required sigils
      List<String> requiredSigils = List.of("IT-06", "IT-07", "IT-08", "IT-09");
      List<String> missingSigils = new ArrayList<>();

      for (String sigilId : requiredSigils) {
        if (!ctx.player().hasItemInInventory(sigilId)) {
          Optional<avengers.domain.model.Item> itemOpt = world.findItem(sigilId);
          if (itemOpt.isPresent()) {
            missingSigils.add(itemOpt.get().getName());
          } else {
            missingSigils.add(sigilId);
          }
        }
      }

      if (!missingSigils.isEmpty()) {
        StringBuilder message = new StringBuilder();
        message.append("You cannot solve this puzzle yet. Missing sigils:\n");
        for (String sigil : missingSigils) {
          message.append("  - ").append(sigil).append("\n");
        }
        message.append("\nDefeat the elemental bosses to obtain all sigils.");
        return CommandResult.fail(message.toString());
      }
    }

    // Check answer (case-insensitive)
    boolean isCorrect = checkAnswer(answer, puzzle);

    if (isCorrect) {
      // Special handling for PUZ-10: Remove sigils from inventory
      if (puzzle.getId().equals("PUZ-10")) {
        List<String> sigils = List.of("IT-06", "IT-07", "IT-08", "IT-09");
        for (String sigilId : sigils) {
          ctx.player().removeItemFromInventory(sigilId);
        }
      }

      puzzlePhases.put(activePuzzleId, PuzzlePhase.SOLVED);
      ctx.player().getPuzzlesSolved().add(activePuzzleId);
      ctx.setAwaitingPuzzleAnswer(false);
      activePuzzleId = null;

      // Handle reward if present
      StringBuilder rewardMessage = new StringBuilder();
      if (puzzle.getReward() != null && !puzzle.getReward().isBlank()) {
        String reward = puzzle.getReward();

        // Extract and add items to inventory
        // Reward format: "IT-XX IT-YY" or single "IT-XX"
        String[] parts = reward.split("\\s+");
        boolean itemsAdded = false;

        for (String part : parts) {
          if (part.matches("IT-\\d+")) {
            // This is an item ID, try to find and add it
            Optional<avengers.domain.model.Item> itemOpt = world.findItem(part);
            if (itemOpt.isPresent()) {
              avengers.domain.model.Item item = itemOpt.get();
              ctx.player().addItemToInventory(item.getId());
              if (!itemsAdded) {
                rewardMessage.append("\n\nRewards obtained:");
                itemsAdded = true;
              }
              rewardMessage.append("\n  - ").append(item.getName());
            }
          }
        }

        // If no items were extracted, show the reward text as-is
        if (!itemsAdded && !reward.isEmpty()) {
          rewardMessage.append("\n\nReward: ").append(reward);
        }
      }

      return CommandResult.success(
          "Correct! You have solved the puzzle: " + puzzle.getName() + rewardMessage.toString());
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

    // Return hint - show the puzzle's solution string
    String hint = "Hint: ";
    if (puzzle.getSolution() != null && !puzzle.getSolution().isBlank()) {
      hint += puzzle.getSolution();
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

  /**
   * Check if the given answer is correct for the puzzle.
   *
   * @param answer the user's answer
   * @param puzzle the puzzle to check against
   * @return true if the answer is correct
   */
  private boolean checkAnswer(String answer, Puzzle puzzle) {
    // Delegate to the Puzzle class which encapsulates its own validation logic
    return puzzle.isValidAnswer(answer);
  }
}
