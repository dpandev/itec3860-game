package avengers.client.controller;

import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;
import avengers.domain.utils.VerbCategory;
import avengers.service.GameService;
import java.util.EnumMap;
import java.util.Map;

/**
 * Example controller class. Replace with actual controllers (GameController, MenuController, etc.)
 */
public final class GameController {
  private final GameService gameService;
  private final Map<VerbCategory, CommandController> controllersByCategory;
  private final CommandController systemController;

  /**
   * Constructor for GameController.
   *
   * @param controllersByCategory Map of VerbCategory to CommandController
   * @param systemController CommandController for system commands
   */
  public GameController(
      Map<VerbCategory, CommandController> controllersByCategory,
      CommandController systemController) {
    this.gameService = new GameService();
    // make immutable copy of controllers map
    this.controllersByCategory = new EnumMap<>(VerbCategory.class);
    this.controllersByCategory.putAll(controllersByCategory);
    this.systemController = systemController;
  }

  /**
   * Handle a command token within the given game context.
   *
   * @param cmd CommandToken to handle
   * @param ctx GameContext for the command
   * @return CommandResult of handling the command
   */
  public CommandResult handle(CommandToken cmd, GameContext ctx) {
    final Verb verb = (cmd == null) ? Verb.UNKNOWN : cmd.verb();
    // route to appropriate controller based on verb category
    VerbCategory vc = VerbCategory.of(verb);
    CommandController controller = controllersByCategory.get(vc);

    if (controller != null && controller.supports(verb)) {
      return controller.handle(cmd, ctx);
    } else if (systemController != null && systemController.supports(verb)) {
      return systemController.handle(cmd, ctx);
    } else {
      return CommandResult.fail("No controller found for verb: " + verb);
    }
  }
}
