package avengers.client.controller;

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
  void testSupportsPickupVerb() {
    assertTrue(controller.supports(Verb.PICKUP));
  }

  @Test
  void testSupportsDropVerb() {
    assertTrue(controller.supports(Verb.DROP));
  }

  @Test
  void testSupportsInspectVerb() {
    assertTrue(controller.supports(Verb.INSPECT));
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
  void testHandleReturnsSuccess() {
    CommandToken cmd = new CommandToken(Verb.INVENTORY, null, List.of(), "inventory");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success());
  }

  @Test
  void testHandleInventoryReturnsCorrectMessage() {
    CommandToken cmd = new CommandToken(Verb.INVENTORY, null, List.of(), "inventory");

    CommandResult result = controller.handle(cmd, context);

    assertTrue(result.message().contains("INVENTORY"));
    assertTrue(result.message().contains("Your inventory is empty"));
  }

  @Test
  void testHandlePickupWithoutArgs() {
    CommandToken cmd = new CommandToken(Verb.PICKUP, null, List.of(), "pickup");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertFalse(result.success());
    assertTrue(result.message().contains("What do you want to pick up?"));
  }

  @Test
  void testHandleDropWithoutArgs() {
    CommandToken cmd = new CommandToken(Verb.DROP, null, List.of(), "drop");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertFalse(result.success());
    assertTrue(result.message().contains("What do you want to drop?"));
  }

  @Test
  void testHandleInspectWithoutArgs() {
    CommandToken cmd = new CommandToken(Verb.INSPECT, null, List.of(), "inspect");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertFalse(result.success());
    assertTrue(result.message().contains("What do you want to inspect?"));
  }
}
