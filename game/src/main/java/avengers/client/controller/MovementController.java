package avengers.client.controller;

import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;
import avengers.service.ExplorationService;

/** Controller to handle movement-related commands such as GO, EXPLORE, and MAP. */
public class MovementController implements CommandController {

  private final ExplorationService explorationService;

  /**
   * Constructs a MovementController with the specified ExplorationService.
   *
   * @param explorationService the exploration service to use
   */
  public MovementController(ExplorationService explorationService) {
    this.explorationService = explorationService;
  }

  @Override
  public boolean supports(Verb verb) {
    return verb == Verb.GO || verb == Verb.EXPLORE;
  }

  @Override
  public CommandResult handle(CommandToken cmd, GameContext ctx) {
    if (cmd == null) {
      return CommandResult.fail("Invalid command.");
    }

    // Block movement if player is currently solving a puzzle
    if (ctx.isAwaitingPuzzleAnswer()) {
      return CommandResult.fail(
          "You cannot leave while solving a puzzle! Type 'ignore' to bypass the puzzle.");
    }

    return switch (cmd.verb()) {
      case GO -> handleGo(cmd, ctx);
      case EXPLORE -> explorationService.explore(ctx);
      default -> CommandResult.fail("Unsupported movement command: " + cmd.verb());
    };
  }

  /**
   * Handles the GO command for movement.
   *
   * @param cmd the command token
   * @param ctx the game context
   * @return the result of the movement operation
   */
  private CommandResult handleGo(CommandToken cmd, GameContext ctx) {
    String direction = cmd.target();

    if (direction == null || direction.isBlank()) {
      return CommandResult.fail(
          "Which direction do you want to go? Usage: go <direction>\n"
              + "Directions: north (n), south (s), east (e), west (w)");
    }

    return explorationService.move(ctx, direction);
  }
}
