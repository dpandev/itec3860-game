package avengers.client.controller;

import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;
import avengers.service.InteractionService;

/** Controller responsible for handling interactions within the game. */
public class InteractionController implements CommandController {

  private final InteractionService interactionService;

  /**
   * Constructs an InteractionController with the specified InteractionService.
   *
   * @param interactionService the interaction service to use
   */
  public InteractionController(InteractionService interactionService) {
    this.interactionService = interactionService;
  }

  @Override
  public boolean supports(Verb verb) {
    return verb == Verb.SOLVE || verb == Verb.HINT;
  }

  @Override
  public CommandResult handle(CommandToken cmd, GameContext ctx) {
    if (cmd == null) {
      return CommandResult.fail("Invalid command.");
    }

    return switch (cmd.verb()) {
      case SOLVE -> handleSolve(cmd, ctx);
      case HINT -> interactionService.getHint(ctx);
      default -> CommandResult.fail("Unsupported interaction command: " + cmd.verb());
    };
  }

  /**
   * Handles the SOLVE command for puzzle solving.
   *
   * @param cmd the command token
   * @param ctx the game context
   * @return the result of the solve operation
   */
  private CommandResult handleSolve(CommandToken cmd, GameContext ctx) {
    if (!ctx.isAwaitingPuzzleAnswer()) {
      return CommandResult.fail("There is no active puzzle to solve.");
    }

    String answer = cmd.target();
    if (answer == null || answer.isBlank()) {
      return CommandResult.fail("You must provide an answer. Usage: solve <answer>");
    }

    return interactionService.solvePuzzle(ctx, answer);
  }
}
