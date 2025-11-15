package avengers.domain.utils;

/**
 * Represents the result of executing a command in the Adventure Time game.
 *
 * @param success indicates if the command was executed successfully
 * @param message provides feedback or information about the command execution
 * @param shouldExit indicates if the game should exit after this command
 */
public record CommandResult(boolean success, String message, boolean shouldExit) {

  /**
   * Factory method to create a successful CommandResult with a message.
   *
   * @param message the success message, can be null
   * @return a CommandResult indicating success
   */
  public static CommandResult success(String message) {
    return new CommandResult(true, message == null ? "" : message, false);
  }

  /**
   * Factory method to create a failed CommandResult with a message.
   *
   * @param message the failure message, can be null
   * @return a CommandResult indicating failure
   */
  public static CommandResult fail(String message) {
    return new CommandResult(false, message == null ? "" : message, false);
  }

  /**
   * Factory method to create a successful CommandResult that signals the game to exit.
   *
   * @param message the exit message, can be null
   * @return a CommandResult indicating success and game exit
   */
  public static CommandResult exit(String message) {
    return new CommandResult(true, message == null ? "" : message, true);
  }
}
