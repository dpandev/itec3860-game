package avengers.client.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.VerbCategory;
import avengers.service.CommandResult;
import java.util.HashMap;
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
  void testHandleInput() {
    assertDoesNotThrow(() -> controller.handleInput("move north"));
  }

  @Test
  void testHandleCommandToken() {
    CommandToken cmd = new CommandToken("help", null);

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
  }

  @Test
  void testHandleWithNullCommand() {
    CommandResult result = controller.handle(null, context);

    assertNotNull(result);
  }

  @Test
  void testHandleWithNullContext() {
    CommandToken cmd = new CommandToken("test", "command");

    CommandResult result = controller.handle(cmd, null);

    assertNotNull(result);
  }

  @Test
  void testHandleInputWithEmptyString() {
    assertDoesNotThrow(() -> controller.handleInput(""));
  }

  @Test
  void testHandleInputWithNull() {
    assertDoesNotThrow(() -> controller.handleInput(null));
  }
}
