package avengers.client.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SystemControllerTest {

  private SystemController controller;
  private GameContext context;

  @BeforeEach
  void setUp() {
    controller = new SystemController();
    context = new GameContext();
  }

  @Test
  void testControllerCreation() {
    assertNotNull(controller);
  }

  @Test
  void testHandleReturnsResult() {
    CommandToken cmd = new CommandToken(Verb.HELP, null, List.of(), "help");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
  }

  @Test
  void testHandleReturnsNotImplementedMessage() {
    CommandToken cmd = new CommandToken(Verb.QUIT, null, List.of(), "quit");

    CommandResult result = controller.handle(cmd, context);

    assertFalse(result.success());
    assertEquals("SystemController not yet implemented.", result.message());
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
}
