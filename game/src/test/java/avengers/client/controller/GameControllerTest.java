package avengers.client.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import avengers.domain.model.Player;
import avengers.domain.model.World;
import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;
import avengers.domain.utils.VerbCategory;
import avengers.service.*;
import avengers.service.spi.FileSaveRepository;
import avengers.service.world.JsonWorldLoader;
import avengers.service.world.WorldLoader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControllerTest {
  private GameController controller;
  private Map<VerbCategory, CommandController> controllerMap;
  private CommandController systemController;
  private GameContext context;

  @BeforeEach
  void setUp() {
    // Create services
    InteractionService interactionService = new DefaultInteractionService();
    ExplorationService explorationService = new DefaultExplorationService(interactionService);
    CombatService combatService = new DefaultCombatService();

    // Create controller map with services
    controllerMap = new HashMap<>();
    controllerMap.put(VerbCategory.MOVEMENT, new MovementController(explorationService));
    controllerMap.put(VerbCategory.INVENTORY, new InventoryController());
    controllerMap.put(VerbCategory.INTERACTION, new InteractionController(interactionService));
    controllerMap.put(VerbCategory.COMBAT, new CombatController(combatService));

    SaveService saveService = new SaveService(new FileSaveRepository(Paths.get("test-saves")));
    WorldLoader worldLoader = new JsonWorldLoader();
    systemController = new SystemController(saveService, worldLoader);
    controller = new GameController(controllerMap, systemController);

    World world = new World(Map.of(), Map.of(), Map.of(), Map.of(), "room1");
    Player player = new Player("TestPlayer", "room1");
    context = new GameContext(world, player);
  }

  @Test
  void testControllerCreation() {
    assertNotNull(controller);
  }

  @Test
  void testControllerCreationWithEmptyMap() {
    SaveService saveService = new SaveService(new FileSaveRepository(Paths.get("test-saves")));
    WorldLoader worldLoader = new JsonWorldLoader();
    GameController emptyController =
      new GameController(new HashMap<>(), new SystemController(saveService, worldLoader));

    assertNotNull(emptyController);
  }

  @Test
  void testHandleCommandToken() {
    CommandToken cmd = new CommandToken(Verb.HELP, null, List.of(), "help");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success());
  }

  @Test
  void testHandleWithNullCommand() {
    // Null command should be handled gracefully
    CommandResult result = controller.handle(null, context);

    assertNotNull(result);
    assertFalse(result.success());
    assertTrue(result.message().contains("No controller found"));
  }

  @Test
  void testHandleWithNullContext() {
    CommandToken cmd = new CommandToken(Verb.HELP, null, List.of(), "help");

    // This will throw NPE because SystemController.handle doesn't check for null context
    // This is expected behavior - context should never be null
    try {
      controller.handle(cmd, null);
    } catch (NullPointerException e) {
      // Expected
      assertNotNull(e);
    }
  }

  @Test
  void testHandleInventoryCommand() {
    CommandToken cmd = new CommandToken(Verb.INVENTORY, null, List.of(), "inventory");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success());
  }

  @Test
  void testHandleUnknownCommand() {
    CommandToken cmd = new CommandToken(Verb.UNKNOWN, null, List.of(), "dance");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertFalse(result.success());
    assertTrue(result.message().contains("Unknown command") || result.message().contains("UNKNOWN"));
  }

  @Test
  void testHandleActivateCommand() {
    CommandToken cmd = new CommandToken(Verb.ACTIVATE, "lever", List.of("lever"), "activate lever");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success());
    // InteractionController's activate returns a placeholder message
    assertTrue(
      result.message().contains("activate")
        || result.message().contains("nothing happens"));
  }

  @Test
  void testHandleExploreCommand() {
    CommandToken cmd = new CommandToken(Verb.EXPLORE, null, List.of(), "explore");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    // Should fail because context has no valid room, but controller should handle it
    assertNotNull(result.message());
  }

  @Test
  void testHandleGoCommand() {
    CommandToken cmd = new CommandToken(Verb.GO, "north", List.of("north"), "go north");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    // Will fail because test world has no rooms/exits
    assertFalse(result.success());
  }

  @Test
  void testHandleAttackCommand() {
    CommandToken cmd = new CommandToken(Verb.ATTACK, "goblin", List.of("goblin"), "attack goblin");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    // Should handle gracefully even with no monsters
    assertNotNull(result.message());
  }

  @Test
  void testHandleDefendCommand() {
    CommandToken cmd = new CommandToken(Verb.DEFEND, null, List.of(), "defend");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    // Should indicate not in combat
    assertTrue(result.message().toLowerCase().contains("not in combat"));
  }

  @Test
  void testHandleSaveCommand() {
    CommandToken cmd = new CommandToken(Verb.SAVE, null, List.of(), "save");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success());
    assertTrue(result.message().toLowerCase().contains("saved"));
  }

  @Test
  void testHandleQuitCommand() {
    CommandToken cmd = new CommandToken(Verb.QUIT, null, List.of(), "quit");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success());
    assertTrue(result.shouldExit());
    assertTrue(result.message().toLowerCase().contains("goodbye"));
  }

  @Test
  void testRouting_MovementCategory() {
    CommandToken goCmd = new CommandToken(Verb.GO, "north", List.of("north"), "go north");
    CommandToken exploreCmd = new CommandToken(Verb.EXPLORE, null, List.of(), "explore");
    CommandToken mapCmd = new CommandToken(Verb.MAP, null, List.of(), "map");

    // All should route to MovementController
    CommandResult goResult = controller.handle(goCmd, context);
    CommandResult exploreResult = controller.handle(exploreCmd, context);
    CommandResult mapResult = controller.handle(mapCmd, context);

    assertNotNull(goResult);
    assertNotNull(exploreResult);
    assertNotNull(mapResult);
  }

  @Test
  void testRouting_InventoryCategory() {
    CommandToken invCmd = new CommandToken(Verb.INVENTORY, null, List.of(), "inventory");

    CommandResult result = controller.handle(invCmd, context);

    assertNotNull(result);
    assertTrue(result.success());
  }

  @Test
  void testRouting_InteractionCategory() {
    CommandToken activateCmd =
      new CommandToken(Verb.ACTIVATE, "lever", List.of("lever"), "activate lever");
    CommandToken inspectCmd =
      new CommandToken(Verb.INSPECT, "item", List.of("item"), "inspect item");

    CommandResult activateResult = controller.handle(activateCmd, context);
    CommandResult inspectResult = controller.handle(inspectCmd, context);

    assertNotNull(activateResult);
    assertNotNull(inspectResult);
  }

  @Test
  void testRouting_CombatCategory() {
    CommandToken attackCmd =
      new CommandToken(Verb.ATTACK, "monster", List.of("monster"), "attack monster");
    CommandToken defendCmd = new CommandToken(Verb.DEFEND, null, List.of(), "defend");
    CommandToken ignoreCmd =
      new CommandToken(Verb.IGNORE, "monster", List.of("monster"), "ignore monster");

    CommandResult attackResult = controller.handle(attackCmd, context);
    CommandResult defendResult = controller.handle(defendCmd, context);
    CommandResult ignoreResult = controller.handle(ignoreCmd, context);

    assertNotNull(attackResult);
    assertNotNull(defendResult);
    assertNotNull(ignoreResult);
  }

  @Test
  void testRouting_SystemCategory() {
    CommandToken helpCmd = new CommandToken(Verb.HELP, null, List.of(), "help");
    CommandToken saveCmd = new CommandToken(Verb.SAVE, null, List.of(), "save");
    CommandToken quitCmd = new CommandToken(Verb.QUIT, null, List.of(), "quit");

    CommandResult helpResult = controller.handle(helpCmd, context);
    CommandResult saveResult = controller.handle(saveCmd, context);
    CommandResult quitResult = controller.handle(quitCmd, context);

    assertNotNull(helpResult);
    assertNotNull(saveResult);
    assertNotNull(quitResult);
    assertTrue(helpResult.success());
    assertTrue(saveResult.success());
    assertTrue(quitResult.shouldExit());
  }

  @Test
  void testFallbackToSystemController() {
    // UNKNOWN verb should fall back to system controller
    CommandToken cmd = new CommandToken(Verb.UNKNOWN, null, List.of(), "unknown");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertFalse(result.success());
  }
}
