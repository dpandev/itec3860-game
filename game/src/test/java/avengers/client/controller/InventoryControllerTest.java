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
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventoryControllerTest {

  private InventoryController controller;
  private GameContext context;

  @BeforeEach
  void setUp() {
    controller = new InventoryController();
    World world = new World(Map.of(), Map.of(), Map.of(), Map.of(), "room1");
    Player player = new Player("TestPlayer", "room1");
    context = new GameContext(world, player);
  }

  @Test
  void testControllerCreation() {
    assertNotNull(controller);
  }

  @Test
  void testSupportsInventoryVerb() {
    assertTrue(controller.supports(Verb.INVENTORY));
  }

  @Test
  void testDoesNotSupportGoVerb() {
    assertFalse(controller.supports(Verb.GO));
  }

  @Test
  void testDoesNotSupportActivateVerb() {
    assertFalse(controller.supports(Verb.ACTIVATE));
  }

  @Test
  void testDoesNotSupportHelpVerb() {
    assertFalse(controller.supports(Verb.HELP));
  }

  @Test
  void testHandleReturnsSuccess() {
    CommandToken cmd = new CommandToken(Verb.INVENTORY, null, List.of(), "inventory");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success());
  }

  @Test
  void testHandleReturnsCorrectMessage() {
    CommandToken cmd = new CommandToken(Verb.INVENTORY, null, List.of(), "inventory");

    CommandResult result = controller.handle(cmd, context);

    assertEquals("Inventory displayed.", result.message());
  }

  @Test
  void testHandleWithNullCommand() {
    CommandResult result = controller.handle(null, context);

    assertNotNull(result);
  }
}
