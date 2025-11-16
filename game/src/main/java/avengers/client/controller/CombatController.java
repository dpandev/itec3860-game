package avengers.client.controller;

import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;
import avengers.service.CombatService;

/** Controller to handle combat-related commands. */
public class CombatController implements CommandController {
  private final CombatService combatService;

  /**
   * Constructs a CombatController with the specified CombatService.
   *
   * @param combatService the combat service to use
   */
  public CombatController(CombatService combatService) {
    this.combatService = combatService;
  }

  @Override
  public boolean supports(Verb verb) {
    return verb == Verb.ATTACK || verb == Verb.DEFEND || verb == Verb.IGNORE;
  }

  @Override
  public CommandResult handle(CommandToken cmd, GameContext ctx) {
    if (cmd == null) {
      return CommandResult.fail("Invalid command.");
    }

    return switch (cmd.verb()) {
      case ATTACK -> handleAttack(cmd, ctx);
      case DEFEND -> combatService.defend(ctx);
      case IGNORE -> handleIgnore(cmd, ctx);
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
