package avengers.client.controller;

import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.service.CommandResult;

/** Controller to handle combat-related commands. */
public class CombatController implements CommandController {
  @Override
  public CommandResult handle(CommandToken cmd, GameContext ctx) {
    // Implementation for combat commands goes here
    return CommandResult.fail("CombatController not yet implemented.");
  }
}
