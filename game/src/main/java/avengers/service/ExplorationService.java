package avengers.service;

import avengers.domain.utils.GameContext;

/** Service for exploring rooms and moving through the game world. */
public interface ExplorationService {
  //

  /**
   * Describes the current room the player is in.
   *
   * @param ctx the game context
   * @return formatted description of the current room
   */
  String describeCurrentRoom(GameContext ctx);

  /**
   * Explores the current room, showing detailed information.
   *
   * @param ctx the game context
   * @return formatted exploration result
   */
  String explore(GameContext ctx);

  /**
   * Moves the player in the specified direction.
   *
   * @param ctx the game context
   * @param direction the direction to move (supports aliases like n/s/e/w)
   * @return result of the movement attempt
   */
  MoveResult move(GameContext ctx, String direction);

  /** Result of a movement attempt. */
  class MoveResult {
    private final boolean success;
    private final String message;
    private final boolean puzzlePresented;

    public MoveResult(boolean success, String message, boolean puzzlePresented) {
      this.success = success;
      this.message = message;
      this.puzzlePresented = puzzlePresented;
    }

    public boolean isSuccess() {
      return success;
    }

    public String getMessage() {
      return message;
    }

    public boolean isPuzzlePresented() {
      return puzzlePresented;
    }

    public static MoveResult success(String message, boolean puzzlePresented) {
      return new MoveResult(true, message, puzzlePresented);
    }

    public static MoveResult failure(String message) {
      return new MoveResult(false, message, false);
    }
  }
}
