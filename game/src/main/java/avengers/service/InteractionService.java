package avengers.service;

import avengers.domain.utils.CommandResult;
import avengers.domain.utils.GameContext;

/** Service interface for handling interactions with puzzles and other interactive elements. */
public interface InteractionService {

  /**
   * Checks if the current room has a puzzle and presents it if in LOCKED state.
   *
   * @param ctx the game context
   * @return CommandResult with puzzle presentation, or null if no puzzle to present
   */
  CommandResult checkAndPresentPuzzle(GameContext ctx);

  /**
   * Attempts to solve the currently active puzzle with the given answer.
   *
   * @param ctx the game context
   * @param answer the player's answer
   * @return CommandResult indicating success or failure
   */
  CommandResult solvePuzzle(GameContext ctx, String answer);

  /**
   * Provides a hint for the currently active puzzle.
   *
   * @param ctx the game context
   * @return CommandResult with hint information
   */
  CommandResult getHint(GameContext ctx);
}
