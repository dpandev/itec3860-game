package avengers.service;

import avengers.domain.utils.GameContext;

/** Service for handling puzzle interactions. */
public interface InteractionService {
  //

  /**
   * Presents a puzzle to the player.
   *
   * @param ctx the game context
   * @param puzzleId the ID of the puzzle to present
   * @return the puzzle prompt message
   */
  String presentPuzzle(GameContext ctx, String puzzleId);

  /**
   * Attempts to solve a puzzle with the given answer.
   *
   * @param ctx the game context
   * @param puzzleId the ID of the puzzle to solve
   * @param answer the player's answer
   * @return result of the solve attempt
   */
  SolveResult solve(GameContext ctx, String puzzleId, String answer);

  /** Result of a puzzle solve attempt. */
  class SolveResult {
    private final boolean correct;
    private final String message;
    private final boolean locked;

    public SolveResult(boolean correct, String message, boolean locked) {
      this.correct = correct;
      this.message = message;
      this.locked = locked;
    }

    public boolean isCorrect() {
      return correct;
    }

    public String getMessage() {
      return message;
    }

    public boolean isLocked() {
      return locked;
    }

    public static SolveResult correct(String message) {
      return new SolveResult(true, message, false);
    }

    public static SolveResult incorrect(String message, boolean locked) {
      return new SolveResult(false, message, locked);
    }
  }
}
