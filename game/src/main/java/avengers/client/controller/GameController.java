package avengers.client.controller;

import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.VerbCategory;
import avengers.service.CommandResult;
import avengers.service.GameService;
import java.util.Map;

/**
 * Example controller class. Replace with actual controllers (GameController, MenuController, etc.)
 */
public class GameController {
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
    this.controllersByCategory = controllersByCategory;
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
    return systemController.handle(cmd, ctx);
  }

  /** Example method to demonstrate functionality. */
  public void handleInput(String input) {
    System.out.println("Controller handling: " + input);
  }
}
