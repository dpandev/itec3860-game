package avengers.client.controller;

import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;
import avengers.service.CombatService;

/** Controller to handle combat-related commands. */
public class CombatController implements CommandController {
  private final CombatService combatService;
  private final avengers.service.InteractionService interactionService;

  /**
   * Constructs a CombatController with the specified CombatService.
   *
   * @param combatService the combat service to use
   * @param interactionService the interaction service for puzzle handling
   */
  public CombatController(
      CombatService combatService, avengers.service.InteractionService interactionService) {
    this.combatService = combatService;
    this.interactionService = interactionService;
  }

  @Override
  public boolean supports(Verb verb) {
    return verb == Verb.ATTACK || verb == Verb.DEFEND || verb == Verb.IGNORE || verb == Verb.SUMMON;
  }

  @Override
  public CommandResult handle(CommandToken cmd, GameContext ctx) {
    if (cmd == null) {
      return CommandResult.fail("Invalid command.");
    }

    // If puzzle is active and command is IGNORE, delegate to InteractionService
    if (ctx.isAwaitingPuzzleAnswer() && cmd.verb() == Verb.IGNORE) {
      return interactionService.ignorePuzzle(ctx);
    }

    // Block other combat actions if player is currently solving a puzzle
    if (ctx.isAwaitingPuzzleAnswer()) {
      return CommandResult.fail(
          "You cannot engage in combat while solving a puzzle! Type 'ignore' to bypass the puzzle.");
    }

    return switch (cmd.verb()) {
      case ATTACK -> handleAttack(cmd, ctx);
      case DEFEND -> combatService.defend(ctx);
      case IGNORE -> handleIgnore(cmd, ctx);
      case SUMMON -> combatService.summonAllies(ctx);
      default -> CommandResult.fail("Unsupported combat command: " + cmd.verb());
    };
  }

  /**
   * Handles the ATTACK command.
   *
   * @param cmd the command token
   * @param ctx the game context
   * @return the result of the attack operation
   */
  private CommandResult handleAttack(CommandToken cmd, GameContext ctx) {
    // If already in combat, continue attacking
    if (ctx.isInCombat()) {
      return combatService.playerAttack(ctx);
    }

    // Otherwise, start combat with the specified monster
    String monsterName = cmd.target();
    if (monsterName == null || monsterName.isBlank()) {
      return CommandResult.fail(
          "Which creature do you want to attack? Usage: attack <monster name>");
    }

    // Start combat
    CommandResult startResult = combatService.startCombat(ctx, monsterName);
    if (!startResult.success()) {
      return startResult;
    }

    // Immediately perform first attack
    CommandResult attackResult = combatService.playerAttack(ctx);

    return CommandResult.success(startResult.message() + "\n\n" + attackResult.message());
  }

  /**
   * Handles the IGNORE command.
   *
   * @param cmd the command token
   * @param ctx the game context
   * @return the result of the ignore operation
   */
  private CommandResult handleIgnore(CommandToken cmd, GameContext ctx) {
    String monsterName = cmd.target();
    if (monsterName == null || monsterName.isBlank()) {
      return CommandResult.fail(
          "Which creature do you want to ignore? Usage: ignore <monster name>");
    }

    return combatService.ignoreMonster(ctx, monsterName);
  }
}
