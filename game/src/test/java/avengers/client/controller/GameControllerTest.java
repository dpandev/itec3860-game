package avengers.client.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import avengers.domain.model.Player;
import avengers.domain.model.World;
import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;
import avengers.domain.utils.VerbCategory;
import avengers.service.DefaultExplorationService;
import avengers.service.ExplorationService;
import avengers.service.SaveService;
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
    controllerMap = new HashMap<>();
    controllerMap.put(VerbCategory.MOVEMENT, new MovementController());
    controllerMap.put(VerbCategory.INVENTORY, new InventoryController());
    controllerMap.put(VerbCategory.INTERACTION, new InteractionController());
    controllerMap.put(VerbCategory.COMBAT, new CombatController());

    SaveService saveService = new SaveService(new FileSaveRepository(Paths.get("test-saves")));
    WorldLoader worldLoader = new JsonWorldLoader();
    ExplorationService explorationService = new DefaultExplorationService();
    systemController = new SystemController(saveService, worldLoader, explorationService);
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
    ExplorationService explorationService = new DefaultExplorationService();
    GameController emptyController =
        new GameController(
            new HashMap<>(), new SystemController(saveService, worldLoader, explorationService));

    assertNotNull(emptyController);
  }

  @Test
  void testHandleCommandToken() {
    CommandToken cmd = new CommandToken(Verb.HELP, null, List.of(), "help");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
  }

  @Test
  void testHandleWithNullCommand() {
    // GameController converts null to UNKNOWN and routes to SystemController
    // SystemController doesn't handle null, so it throws NPE
    assertThrows(NullPointerException.class, () -> controller.handle(null, context));
  }

  @Test
  void testHandleWithNullContext() {
    CommandToken cmd = new CommandToken(Verb.HELP, null, List.of(), "help");

    CommandResult result = controller.handle(cmd, null);

    assertNotNull(result);
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
  }

  @Test
  void testHandleActivateCommand() {
    CommandToken cmd = new CommandToken(Verb.ACTIVATE, "lever", List.of("lever"), "activate lever");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success());
    assertEquals("Interaction handled.", result.message());
  }
}
