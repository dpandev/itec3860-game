package avengers.client.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;
import avengers.service.CommandResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventoryControllerTest {

  private InventoryController controller;
  private GameContext context;

  @BeforeEach
  void setUp() {
    controller = new InventoryController();
    context = new GameContext();
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
    CommandToken cmd = new CommandToken("inventory", null);

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success());
  }

  @Test
  void testHandleReturnsCorrectMessage() {
    CommandToken cmd = new CommandToken("inventory", "");

    CommandResult result = controller.handle(cmd, context);

    assertEquals("Inventory displayed.", result.message());
  }

  @Test
  void testHandleWithNullCommand() {
    CommandResult result = controller.handle(null, context);

    assertNotNull(result);
  }
}
