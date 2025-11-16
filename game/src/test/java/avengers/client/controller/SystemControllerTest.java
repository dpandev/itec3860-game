package avengers.client.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import avengers.domain.model.Player;
import avengers.domain.model.World;
import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;
import avengers.service.DefaultExplorationService;
import avengers.service.ExplorationService;
import avengers.service.SaveService;
import avengers.service.spi.FileSaveRepository;
import avengers.service.world.JsonWorldLoader;
import avengers.service.world.WorldLoader;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SystemControllerTest {

  private SystemController controller;
  private GameContext context;

  @BeforeEach
  void setUp() {
    SaveService saveService = new SaveService(new FileSaveRepository(Paths.get("test-saves")));
    WorldLoader worldLoader = new JsonWorldLoader();
    ExplorationService explorationService = new DefaultExplorationService();
    controller = new SystemController(saveService, worldLoader, explorationService);
    World world = new World(Map.of(), Map.of(), Map.of(), Map.of(), "room1");
    Player player = new Player("TestPlayer", "room1");
    context = new GameContext(world, player);
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
  void testHandleQuitCommand() {
    CommandToken cmd = new CommandToken(Verb.QUIT, null, List.of(), "quit");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.shouldExit());
    assertEquals("Game saved. Goodbye!", result.message());
  }

  @Test
  void testHandleWithNullCommand() { // SystemController doesn't handle null, so it throws
    // NullPointerException
    assertThrows(NullPointerException.class, () -> controller.handle(null, context));
  }

  @Test
  void testHandleWithNullContext() {
    CommandToken cmd = new CommandToken(Verb.HELP, null, List.of(), "help");

    CommandResult result = controller.handle(cmd, null);

    assertNotNull(result);
  }
}
