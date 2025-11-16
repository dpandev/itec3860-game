package avengers.client.controller;

import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;

/** Controller for managing inventory-related commands in the Avengers game. */
public class InventoryController implements CommandController {
  //
  @Override
  public boolean supports(Verb verb) {
    return verb == Verb.INVENTORY;
  }

  @Override
  public CommandResult handle(CommandToken cmd, GameContext ctx) {
    return CommandResult.success("Inventory displayed.");
  }
}
