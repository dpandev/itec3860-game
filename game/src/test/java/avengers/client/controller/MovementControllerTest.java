package avengers.client.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import avengers.domain.model.Player;
import avengers.domain.model.World;
import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;
import avengers.service.DefaultExplorationService;
import avengers.service.DefaultInteractionService;
import avengers.service.ExplorationService;
import avengers.service.InteractionService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MovementControllerTest {

  private MovementController controller;
  private GameContext context;
  private ExplorationService explorationService;

  @BeforeEach
  void setUp() {
    // Create services
    InteractionService interactionService = new DefaultInteractionService();
    explorationService = new DefaultExplorationService(interactionService);

    // Create controller with service
    controller = new MovementController(explorationService);

    // Create test world and context
    World world = new World(Map.of(), Map.of(), Map.of(), Map.of(), "room1");
    Player player = new Player("TestPlayer", "room1");
    context = new GameContext(world, player);
  }

  @Test
  void testControllerCreation() {
    assertNotNull(controller);
  }

  @Test
  void testHandleReturnsNull() {
    CommandToken cmd = new CommandToken(Verb.GO, "north", List.of("north"), "go north");

    CommandResult result = controller.handle(cmd, context);

    // Now returns a proper result instead of null
    assertNotNull(result);
  }

  @Test
  void testHandleWithDifferentDirections() {
    CommandToken cmdNorth = new CommandToken(Verb.GO, "north", List.of("north"), "go north");
    CommandToken cmdSouth = new CommandToken(Verb.GO, "south", List.of("south"), "go south");
    CommandToken cmdEast = new CommandToken(Verb.GO, "east", List.of("east"), "go east");
    CommandToken cmdWest = new CommandToken(Verb.GO, "west", List.of("west"), "go west");

    // All should return proper results (failures since test world has no exits)
    assertNotNull(controller.handle(cmdNorth, context));
    assertNotNull(controller.handle(cmdSouth, context));
    assertNotNull(controller.handle(cmdEast, context));
    assertNotNull(controller.handle(cmdWest, context));
  }

  @Test
  void testHandleWithNullCommand() {
    CommandResult result = controller.handle(null, context);

    // Should handle gracefully
    assertNotNull(result);
  }

  @Test
  void testHandleWithNullContext() {
    CommandToken cmd = new CommandToken(Verb.GO, "up", List.of("up"), "go up");

    CommandResult result = controller.handle(cmd, null);

    // Should handle gracefully
    assertNotNull(result);
  }
}
