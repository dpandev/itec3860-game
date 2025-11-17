package avengers.client.runtime;

import avengers.client.command.CommandParser;
import avengers.client.controller.GameController;
import avengers.client.view.ConsoleView;
import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.service.SaveService;

/** Main game loop for handling user input and game progression. */
public final class GameLoop {
  private static final long AUTO_SAVE_INTERVAL_MS = 3 * 60 * 1000; // 3 minutes in milliseconds

  private final ConsoleView view;
  private final CommandParser parser;
  private final GameController gameController;
  private final GameContext ctx;
  private final SaveService saveService;
  private long lastAutoSaveTime;

  /** Constructor for GameLoop. */
  public GameLoop(
      ConsoleView view,
      CommandParser parser,
      GameController gameController,
      GameContext ctx,
      SaveService saveService) {
    this.view = view;
    this.parser = parser;
    this.gameController = gameController;
    this.ctx = ctx;
    this.saveService = saveService;
    this.lastAutoSaveTime = System.currentTimeMillis();
  }

  /** Starts the main game loop. */
  public void start() {
    // Welcome message already shown in main menu and ClientApp
    // No need to show it again here

    while (true) {
      // Check if auto-save is needed (every 3 minutes)
      checkAndPerformAutoSave();

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

  /** Checks if 3 minutes have passed since the last auto-save and performs auto-save if needed. */
  private void checkAndPerformAutoSave() {
    long currentTime = System.currentTimeMillis();
    long timeSinceLastSave = currentTime - lastAutoSaveTime;

    if (timeSinceLastSave >= AUTO_SAVE_INTERVAL_MS) {
      try {
        saveService.saveData(ctx);
        view.println(
            "[Auto-saved at "
                + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date())
                + "]");
        lastAutoSaveTime = currentTime;
      } catch (Exception e) {
        // Silent failure for auto-save - don't interrupt gameplay
        view.println("[Auto-save failed - use 'save' command manually]");
        lastAutoSaveTime = currentTime; // Reset timer to avoid spam
      }
    }
  }
}
