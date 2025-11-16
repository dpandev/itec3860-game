package avengers.client.runtime;

import avengers.client.command.CommandParser;
import avengers.client.command.InputCommandParser;
import avengers.client.controller.*;
import avengers.client.view.ConsoleView;
import avengers.domain.model.Player;
import avengers.domain.model.World;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.VerbCategory;
import avengers.service.DefaultExplorationService;
import avengers.service.ExplorationService;
import avengers.service.SaveService;
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

    WorldLoader loader = new JsonWorldLoader();
    World world = loader.load();
    Player player = new Player("Player", world.getStartRoomId());
    GameContext ctx = new GameContext(world, player);

    // init services here (when implemented)
    // here
    String savesPath = System.getProperty("saves.dir", "saves");
    var saveDirectory = Path.of(savesPath).toAbsolutePath();
    SaveService saveService = new SaveService(new FileSaveRepository(saveDirectory));
    InteractionService interactionService = new DefaultInteractionService();
    ExplorationService explorationService = new DefaultExplorationService(interactionService);
    CombatService combatService = new DefaultCombatService();

    // init controllers here
    CommandController movementController = new MovementController(explorationService);
    CommandController inventoryController = new InventoryController();
    CommandController interactionController = new InteractionController(interactionService);
    CommandController combatController = new CombatController(combatService);
    CommandController systemController = new SystemController(saveService, loader);

    // init game controller (main controller)
    GameController gameController =
        new GameController(
            Map.of(
                VerbCategory.MOVEMENT, movementController,
                VerbCategory.INVENTORY, inventoryController,
                VerbCategory.INTERACTION, interactionController,
                VerbCategory.COMBAT, combatController,
                VerbCategory.SYSTEM, systemController),
            systemController // fallback
            );

    // Show initial room description
    view.println("Welcome to Solo Leveling");
    view.println("Type 'help' for commands, 'quit' to exit.");
    view.println("");
    view.println(explorationService.describeCurrentRoom(ctx));

    // init and start game loop
    GameLoop gameLoop = new GameLoop(view, parser, gameController, ctx);
    gameLoop.start();
  }
}
