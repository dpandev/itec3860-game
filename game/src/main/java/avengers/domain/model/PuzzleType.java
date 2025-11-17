package avengers.domain.model;

/**
 * Represents different types of puzzles that can be encountered in the game.
 *
 * <p>Each puzzle type corresponds to different interaction patterns and solution mechanisms.
 */
public enum PuzzleType {
  /** Puzzles that require answering questions or solving word-based challenges. */
  RIDDLE,

  /** Puzzles involving finding the correct combination of inputs or sequences. */
  COMBINATION,

  /** Puzzles requiring actions to be performed in a specific order. */
  SEQUENCE,

  /** Puzzles involving entering or deciphering codes. */
  CODE,

  /** Puzzles based on knowledge or lore questions. */
  TRIVIA
}
