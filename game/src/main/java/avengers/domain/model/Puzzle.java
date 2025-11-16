package avengers.domain.model;

/** Represents a puzzle in the game with solution, rewards, and failure consequences. */
public final class Puzzle {
  //
  private final String id;
  private final String name;
  private final String description;
  private final String solution;
  private final String reward;
  private final String failureConsequence;
  private final String commandUsed;
  private final int numberOfAttempts;
  private int attemptsRemaining;
  private boolean solved;

  /**
   * Constructs a Puzzle with the specified properties.
   *
   * @param id the unique identifier for the puzzle
   * @param name the name of the puzzle
   * @param description the description of the puzzle
   * @param solution the solution to the puzzle
   * @param reward the reward for solving the puzzle
   * @param failureConsequence what happens when the puzzle fails
   * @param commandUsed the command used to interact with the puzzle
   * @param numberOfAttempts the number of attempts allowed (-1 for unlimited)
   */
  public Puzzle(
      String id,
      String name,
      String description,
      String solution,
      String reward,
      String failureConsequence,
      String commandUsed,
      int numberOfAttempts) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.solution = solution;
    this.reward = reward;
    this.failureConsequence = failureConsequence;
    this.commandUsed = commandUsed;
    this.numberOfAttempts = numberOfAttempts;
    this.attemptsRemaining = numberOfAttempts;
    this.solved = false;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getSolution() {
    return solution;
  }

  public String getReward() {
    return reward;
  }

  public String getFailureConsequence() {
    return failureConsequence;
  }

  public String getCommandUsed() {
    return commandUsed;
  }

  public int getNumberOfAttempts() {
    return numberOfAttempts;
  }

  public int getAttemptsRemaining() {
    return attemptsRemaining;
  }

  public boolean isSolved() {
    return solved;
  }

  /** Marks the puzzle as solved. */
  public void setSolved() {
    this.solved = true;
  }

  /**
   * Checks if the puzzle allows unlimited attempts.
   *
   * @return true if unlimited attempts are allowed, false otherwise
   */
  public boolean hasUnlimitedAttempts() {
    return numberOfAttempts < 0;
  }

  /**
   * Decrements the attempts remaining and returns true if attempts are still available.
   *
   * @return true if attempts remain or unlimited attempts, false if no attempts left
   */
  public boolean useAttempt() {
    if (hasUnlimitedAttempts()) {
      return true;
    }
    if (attemptsRemaining > 0) {
      attemptsRemaining--;
      return true;
    }
    return false;
  }

  /**
   * Checks if attempts are exhausted.
   *
   * @return true if no attempts remain and not unlimited, false otherwise
   */
  public boolean isAttemptsExhausted() {
    return !hasUnlimitedAttempts() && attemptsRemaining <= 0;
  }

  /**
   * Checks if the given answer matches the solution (case-insensitive).
   *
   * @param answer the answer to check
   * @return true if the answer matches the solution, false otherwise
   */
  public boolean checkSolution(String answer) {
    if (answer == null || solution == null) {
      return false;
    }
    return answer.trim().equalsIgnoreCase(solution.trim());
  }

  /** Resets the puzzle to its initial state. */
  public void reset() {
    this.attemptsRemaining = numberOfAttempts;
    this.solved = false;
  }
}
