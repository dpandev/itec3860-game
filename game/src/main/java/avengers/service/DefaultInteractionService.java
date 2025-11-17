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

          // Check if puzzle requires specific items to be accessible
          if (!hasRequiredItemsForPuzzle(player, puzzleId)) {
            continue; // Skip this puzzle, check next one
          }

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

  /**
   * Checks if the player has the required items to access a specific puzzle. Some puzzles are gated
   * behind specific items that must be in the player's inventory.
   *
   * @param player the player
   * @param puzzleId the puzzle ID to check
   * @return true if the player has the required items (or no items required), false otherwise
   */
  private boolean hasRequiredItemsForPuzzle(Player player, String puzzleId) {
    // Define puzzle-item requirements
    // PUZ-11 (Architect's Test) requires IT-14 (Architect's Core)
    if (puzzleId.equals("PUZ-11")) {
      return player.hasItemInInventory("IT-14");
    }

    // Add more puzzle requirements here as needed
    // Example:
    // if (puzzleId.equals("PUZ-XX")) {
    //   return player.hasItemInInventory("IT-XX");
    // }

    // By default, no item requirement
    return true;
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
      // Special handling for PUZ-12: Track destiny choice before marking as solved
      if (puzzle.getId().equals("PUZ-12")) {
        String lowerAnswer = answer.trim().toLowerCase();
        if (lowerAnswer.contains("embrace")) {
          ctx.player().setDestinyChoice(Player.DestinyChoice.SHADOW_MONARCH);
        } else if (lowerAnswer.contains("resist")) {
          ctx.player().setDestinyChoice(Player.DestinyChoice.HUNTER_KING);
        }
        // If neither, the answer was just "puzzle" or "solve puzzle" - treat as indecision
        else {
          return handlePuz12Indecision(ctx, puzzle);
        }
      }

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

      // Special handling for PUZ-08: Grant shadow allies
      if (puzzle.getId().equals("PUZ-08")) {
        int numberOfShadows = 3; // Grant 3 shadow allies
        for (int i = 0; i < numberOfShadows; i++) {
          ctx.player().addAlly("SHADOW-" + (i + 1));
        }
        rewardMessage.append("\n\n=== SHADOW ARMY SUMMONED ===\n");
        rewardMessage.append(numberOfShadows).append(" shadows rise from the fallen souls!\n");
        rewardMessage.append("They pledge their loyalty to you.\n\n");
        rewardMessage.append("You can now use the 'summon' command during combat\n");
        rewardMessage.append("to call upon your Shadow Army to attack enemies!");
      } else if (puzzle.getReward() != null && !puzzle.getReward().isBlank()) {
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
          generateSuccessMessage(puzzle, ctx.player(), rewardMessage.toString()));
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

  @Override
  public CommandResult ignorePuzzle(GameContext ctx) {
    if (!ctx.isAwaitingPuzzleAnswer() || activePuzzleId == null) {
      return CommandResult.fail("There is no active puzzle to ignore.");
    }

    World world = ctx.world();
    Optional<Puzzle> puzzleOpt = world.findPuzzle(activePuzzleId);

    String puzzleName = puzzleOpt.map(Puzzle::getName).orElse("the puzzle");

    // Reset puzzle state to LOCKED so it can be attempted again
    puzzlePhases.put(activePuzzleId, PuzzlePhase.LOCKED);

    // Clear active puzzle and awaiting flag
    activePuzzleId = null;
    ctx.setAwaitingPuzzleAnswer(false);

    return CommandResult.success(
        "You decide to ignore "
            + puzzleName
            + " for now.\n"
            + "You can attempt it again if you return to this room.");
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
   * Handles indecision for PUZ-12 (Shadow Monarch's Choice). When player uses generic "solve
   * puzzle" instead of making a specific choice, they are expelled without progression.
   *
   * @param ctx the game context
   * @param puzzle the PUZ-12 puzzle
   * @return CommandResult indicating expulsion due to indecision
   */
  private CommandResult handlePuz12Indecision(GameContext ctx, Puzzle puzzle) {
    // Reset puzzle to allow retry
    puzzlePhases.put(activePuzzleId, PuzzlePhase.LOCKED);

    // Clear Shadow Army allies as per failure consequence
    ctx.player().getAllies().clear();

    // Clear active puzzle state
    activePuzzleId = null;
    ctx.setAwaitingPuzzleAnswer(false);

    return CommandResult.fail(
        "The shadows recoil at your indecision!\n\n"
            + "You must make a CHOICE - will you 'embrace shadows' or 'resist shadows'?\n"
            + "Your hesitation has consequences:\n"
            + "  - All Shadow Army allies have abandoned you\n"
            + "  - You are expelled from the throne chamber\n\n"
            + "Return when you are ready to decide your destiny.");
  }

  /**
   * Generates custom success message for puzzle completion, with special handling for PUZ-12.
   *
   * @param puzzle the solved puzzle
   * @param player the player who solved it
   * @param rewardMessage additional reward message
   * @return formatted success message
   */
  private String generateSuccessMessage(Puzzle puzzle, Player player, String rewardMessage) {
    StringBuilder message = new StringBuilder();

    // Special success message for PUZ-12 based on choice
    if (puzzle.getId().equals("PUZ-12")) {
      message.append("=== DESTINY CHOSEN ===\n\n");

      if (player.getDestinyChoice() == Player.DestinyChoice.SHADOW_MONARCH) {
        message.append("You embrace the shadows, accepting the power and burden of dominion.\n\n");
        message.append(
            "The throne pulses with dark energy as the shadows bow to their new master.\n");
        message.append("Your path: SHADOW MONARCH ENDING\n\n");
        message.append("Powers unlocked:\n");
        message.append("  - Shadow Army size increased by 20%\n");
        message.append("  - Shadow Monarch's Cloak obtained\n");
        message.append("  - Dominion over the shadow realm established\n");
      } else if (player.getDestinyChoice() == Player.DestinyChoice.HUNTER_KING) {
        message.append(
            "You resist the shadows, choosing your own path as humanity's protector.\n\n");
        message.append(
            "The shadows dissipate, leaving you standing alone in the light of your choice.\n");
        message.append("Your path: HUNTER KING ENDING\n\n");
        message.append("Powers retained:\n");
        message.append("  - Independence from shadow influence\n");
        message.append("  - Humanity's champion title\n");
        message.append("  - Freedom to forge your own destiny\n");
      }

      message.append("\nYour choice will determine the final ending of your journey.");
    } else {
      // Default success message for other puzzles
      message.append("Correct! You have solved the puzzle: ").append(puzzle.getName());
    }

    message.append(rewardMessage);
    return message.toString();
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
