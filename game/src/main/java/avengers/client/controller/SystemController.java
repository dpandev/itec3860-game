package avengers.client.controller;

import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.service.CommandResult;

/** Controller to handle system-related commands such as HELP and QUIT. */
public class SystemController implements CommandController {
  @Override
  public CommandResult handle(CommandToken cmd, GameContext ctx) {
    // Implementation for handling system commands
    return CommandResult.fail("SystemController not yet implemented.");
  }
}
