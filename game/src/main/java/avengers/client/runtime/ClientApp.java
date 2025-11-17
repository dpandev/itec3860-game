package avengers.client.runtime;

import avengers.client.command.CommandParser;
import avengers.client.command.InputCommandParser;
import avengers.client.controller.*;
import avengers.client.view.ConsoleView;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.VerbCategory;
import avengers.service.*;
import avengers.service.spi.FileSaveRepository;
import avengers.service.world.JsonWorldLoader;
import avengers.service.world.WorldLoader;
import java.nio.file.Path;
import java.util.Map;

/** Client application entry point. */
public class ClientApp {
  /** Main method to start the client application. */
  public static void main(String[] args) {
    ConsoleView view = new ConsoleView();
    CommandParser parser = new InputCommandParser();

    // Init world loader and save service for main menu
    WorldLoader loader = new JsonWorldLoader();
    String savesPath = System.getProperty("saves.dir", "saves");
    var saveDirectory = Path.of(savesPath).toAbsolutePath();
    SaveService saveService = new SaveService(new FileSaveRepository(saveDirectory));

    // Show main menu and get game context (new or loaded game)
    MainMenu mainMenu = new MainMenu(view, loader, saveService);
    GameContext ctx = mainMenu.show();

    // If user chose to exit, terminate
    if (ctx == null) {
      return;
    }

    // init services
    InteractionService interactionService = new DefaultInteractionService();
    ExplorationService explorationService = new DefaultExplorationService(interactionService);
    CombatService combatService = new DefaultCombatService();
    MapService mapService = new DefaultMapService();

    // init controllers
    CommandController movementController = new MovementController(explorationService);
    CommandController inventoryController = new InventoryController();
    CommandController interactionController = new InteractionController(interactionService);
    CommandController combatController = new CombatController(combatService, interactionService);
    CommandController mapController = new MapController(mapService);
    CommandController systemController =
        new SystemController(saveService, loader, explorationService);

    // init game controller (main controller)
    GameController gameController =
        new GameController(
            Map.of(
                VerbCategory.MOVEMENT, movementController,
                VerbCategory.INVENTORY, inventoryController,
                VerbCategory.INTERACTION, interactionController,
                VerbCategory.COMBAT, combatController,
                VerbCategory.MAP, mapController,
                VerbCategory.SYSTEM, systemController),
            systemController // fallback
            );

    // Show initial room description
    view.println("════════════════════════════════════════════════════════════\n");
    var initialExplore = explorationService.explore(ctx);
    view.println(initialExplore.message());
    view.println("\n════════════════════════════════════════════════════════════");
    view.println("Type 'help' for commands, 'save' to save, 'quit' to exit.");
    view.println("════════════════════════════════════════════════════════════\n");

    // init and start game loop with auto-save support
    GameLoop gameLoop = new GameLoop(view, parser, gameController, ctx, saveService);
    gameLoop.start();
  }
}
