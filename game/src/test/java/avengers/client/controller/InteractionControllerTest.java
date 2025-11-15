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

class InteractionControllerTest {

  private InteractionController controller;
  private GameContext context;

  @BeforeEach
  void setUp() {
    controller = new InteractionController();
    context = new GameContext();
  }

  @Test
  void testControllerCreation() {
    assertNotNull(controller);
  }

  @Test
  void testSupportsActivateVerb() {
    assertTrue(controller.supports(Verb.ACTIVATE));
  }

  @Test
  void testDoesNotSupportGoVerb() {
    assertFalse(controller.supports(Verb.GO));
  }

  @Test
  void testDoesNotSupportInventoryVerb() {
    assertFalse(controller.supports(Verb.INVENTORY));
  }

  @Test
  void testDoesNotSupportHelpVerb() {
    assertFalse(controller.supports(Verb.HELP));
  }

  @Test
  void testHandleReturnsSuccess() {
    CommandToken cmd = new CommandToken("activate", "object");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success());
  }

  @Test
  void testHandleReturnsCorrectMessage() {
    CommandToken cmd = new CommandToken("activate", "door");

    CommandResult result = controller.handle(cmd, context);

    assertEquals("Interaction handled.", result.message());
  }

  @Test
  void testHandleWithNullCommand() {
    CommandResult result = controller.handle(null, context);

    assertNotNull(result);
  }
}
