package avengers.client.controller;

import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;
import avengers.service.CommandResult;

/** Controller responsible for handling interactions within the game. */
public class InteractionController implements CommandController {
  @Override
  public boolean supports(Verb verb) {
    return verb == Verb.INTERACT;
  }

  @Override
  public CommandResult handle(CommandToken cmd, GameContext ctx) {
    // Implementation for handling interaction commands goes here
    return CommandResult.success("Interaction handled.");
  }
}
