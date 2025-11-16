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
   * Constructs CombatController with combat service.
   *
   * @param combatService the service for combat
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
    if (cmd == null || ctx == null) {
      return CommandResult.fail("Invalid command or context.");
    }

    return switch (cmd.verb()) {
      case ATTACK -> handleAttack(cmd, ctx);
      case DEFEND -> handleDefend(ctx);
      case IGNORE -> handleIgnore(cmd, ctx);
      default -> CommandResult.fail("Combat command not supported: " + cmd.verb());
    };
  }

  // line added for commit
  /**
   * Handles ATTACK command.
   *
   * @param cmd command token
   * @param ctx game context
   * @return command result
   */
  private CommandResult handleAttack(CommandToken cmd, GameContext ctx) {
    String monsterName = cmd.target();
    CombatService.CombatResult result = combatService.attack(ctx, monsterName);

    // Check if player died
    if (!result.isPlayerAlive()) {
      return CommandResult.exit(result.getMessage());
    }

    return CommandResult.success(result.getMessage());
  }

  /**
   * Handles DEFEND command.
   *
   * @param ctx game context
   * @return command result
   */
  private CommandResult handleDefend(GameContext ctx) {
    CombatService.CombatResult result = combatService.defend(ctx);

    // Check if player died
    if (!result.isPlayerAlive()) {
      return CommandResult.exit(result.getMessage());
    }

    return CommandResult.success(result.getMessage());
  }

  /**
   * Handles IGNORE command.
   *
   * @param cmd command token
   * @param ctx game context
   * @return command result
   */
  private CommandResult handleIgnore(CommandToken cmd, GameContext ctx) {
    String monsterName = cmd.target();
    String message = combatService.ignore(ctx, monsterName);
    return CommandResult.success(message);
  }
}
