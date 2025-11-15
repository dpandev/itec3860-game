package avengers.client.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;
import avengers.domain.utils.VerbCategory;
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

    systemController = new SystemController();
    controller = new GameController(controllerMap, systemController);
    context = new GameContext();
  }

  @Test
  void testControllerCreation() {
    assertNotNull(controller);
  }

  @Test
  void testControllerCreationWithEmptyMap() {
    GameController emptyController = new GameController(new HashMap<>(), new SystemController());

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
    CommandResult result = controller.handle(null, context);

    assertNotNull(result);
    assertFalse(result.success());
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
