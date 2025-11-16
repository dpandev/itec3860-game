package avengers.domain.model;

/**
 * Represents an immutable puzzle in the game world.
 *
 * <p>Puzzles are challenges that players must solve to progress through the game. Each puzzle has a
 * specific solution, reward for success, and consequence for failure.
 */
public final class Puzzle {
  private final String id;
  private final String name;
  private final String description;
  private final String solution;
  private final String reward;
  private final String failureConsequence;
  private final String commandUsed;
  private final int numberOfAttempts;
  private final PuzzleType type;

  /**
   * Constructs a new Puzzle with the specified properties.
   *
   * @param id the unique identifier for this puzzle (e.g., "PUZ-01")
   * @param name the display name of the puzzle
   * @param description the description of the puzzle challenge
   * @param solution the solution or hint for solving the puzzle
   * @param reward the reward for successfully solving the puzzle
   * @param failureConsequence what happens when the puzzle is failed
   * @param commandUsed the command format used to interact with this puzzle
   * @param numberOfAttempts the number of attempts allowed (0 for unlimited)
   * @param type the type of puzzle this represents
   * @throws IllegalArgumentException if any required field is null or blank
   */
  public Puzzle(
      String id,
      String name,
      String description,
      String solution,
      String reward,
      String failureConsequence,
      String commandUsed,
      int numberOfAttempts,
      PuzzleType type) {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("Puzzle id cannot be null or blank");
    }
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Puzzle name cannot be null or blank");
    }
    if (description == null || description.isBlank()) {
      throw new IllegalArgumentException("Puzzle description cannot be null or blank");
    }
    if (name.length() > 100) {
      throw new IllegalArgumentException("Puzzle name cannot exceed 100 characters");
    }
    if (description.length() > 500) {
      throw new IllegalArgumentException("Puzzle description cannot exceed 500 characters");
    }
    if (numberOfAttempts < 0) {
      throw new IllegalArgumentException("Number of attempts cannot be negative");
    }

    this.id = id;
    this.name = name;
    this.description = description;
    this.solution = solution != null ? solution : "";
    this.reward = reward != null ? reward : "";
    this.failureConsequence = failureConsequence != null ? failureConsequence : "";
    this.commandUsed = commandUsed != null ? commandUsed : "";
    this.numberOfAttempts = numberOfAttempts;
    this.type = type != null ? type : PuzzleType.RIDDLE;
  }

  /**
   * Gets the unique identifier of this puzzle.
   *
   * @return the puzzle ID
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the display name of this puzzle.
   *
   * @return the puzzle name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the description of this puzzle.
   *
   * @return the puzzle description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the solution or hint for this puzzle.
   *
   * @return the puzzle solution
   */
  public String getSolution() {
    return solution;
  }

  /**
   * Gets the reward for successfully solving this puzzle.
   *
   * @return the puzzle reward
   */
  public String getReward() {
    return reward;
  }

  /**
   * Gets the consequence for failing this puzzle.
   *
   * @return the failure consequence
   */
  public String getFailureConsequence() {
    return failureConsequence;
  }

  /**
   * Gets the command format used to interact with this puzzle.
   *
   * @return the command used
   */
  public String getCommandUsed() {
    return commandUsed;
  }

  /**
   * Gets the number of attempts allowed for this puzzle.
   *
   * @return the number of attempts (0 for unlimited)
   */
  public int getNumberOfAttempts() {
    return numberOfAttempts;
  }

  /**
   * Gets the type of this puzzle.
   *
   * @return the puzzle type
   */
  public PuzzleType getType() {
    return type;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Puzzle puzzle = (Puzzle) obj;
    return id.equals(puzzle.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return String.format("Puzzle{id='%s', name='%s', type=%s}", id, name, type);
  }
}
