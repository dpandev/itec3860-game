package avengers.client.runtime;

import avengers.client.command.CommandParser;
import avengers.client.controller.GameController;
import avengers.client.view.ConsoleView;
import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;

/** Main game loop for handling user input and game progression. */
public final class GameLoop {
  private final ConsoleView view;
  private final CommandParser parser;
  private final GameController gameController;
  private final GameContext ctx;

  /** Constructor for GameLoop. */
  public GameLoop(
      ConsoleView view, CommandParser parser, GameController gameController, GameContext ctx) {
    this.view = view;
    this.parser = parser;
    this.gameController = gameController;
    this.ctx = ctx;
  }

  /** Starts the main game loop. */
  public void start() {
    view.println("Welcome to Solo Leveling");
    view.println("Type 'help' for commands, 'quit' to exit.");
    view.println("");

    // show initial room desc
    // here

    while (true) {
      view.printf("> ");
      String line;
      try {
        line = view.readLine();
      } catch (Exception e) {
        view.println("Error reading input. Exiting.");
        return;
      }

      CommandToken cmd = parser.parse(line);
      CommandResult result = gameController.handle(cmd, ctx);
      if (result != null && !result.message().isBlank()) {
        view.println(result.message());
      }

      // check if the command is for game exit/quit
      if (result != null && result.shouldExit()) {
        return;
      }
    }
  }
}
