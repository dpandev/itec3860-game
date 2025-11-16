package avengers.client.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;
import avengers.service.MapService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MapControllerTest {

  private MapController mapController;
  private MapService mockMapService;
  private GameContext mockGameContext;

  @BeforeEach
  void setUp() {
    mockMapService = mock(MapService.class);
    mockGameContext = mock(GameContext.class);
    mapController = new MapController(mockMapService);
  }

  @Test
  void testSupportsMapVerb() {
    assertTrue(mapController.supports(Verb.MAP));
  }

  @Test
  void testDoesNotSupportOtherVerbs() {
    assertFalse(mapController.supports(Verb.GO));
    assertFalse(mapController.supports(Verb.INVENTORY));
    assertFalse(mapController.supports(Verb.HELP));
  }

  @Test
  void testHandleMapCommandWithoutArguments() {
    CommandToken mapCommand = new CommandToken(Verb.MAP, null, List.of(), "map");
    String expectedMapOutput = "=== MAP ===\n@ Current Room\n";

    when(mockMapService.showMap(mockGameContext)).thenReturn(expectedMapOutput);

    CommandResult result = mapController.handle(mapCommand, mockGameContext);

    assertTrue(result.success());
    assertEquals(expectedMapOutput, result.message());
    assertFalse(result.shouldExit());
    verify(mockMapService).showMap(mockGameContext);
  }

  @Test
  void testHandleMapCommandWithFullArgument() {
    CommandToken mapCommand = new CommandToken(Verb.MAP, "full", List.of("full"), "map full");
    String expectedFullMapOutput = "=== FULL MAP ===\n@ # # Current and visited rooms\n";

    when(mockMapService.showFullMap(mockGameContext)).thenReturn(expectedFullMapOutput);

    CommandResult result = mapController.handle(mapCommand, mockGameContext);

    assertTrue(result.success());
    assertEquals(expectedFullMapOutput, result.message());
    assertFalse(result.shouldExit());
    verify(mockMapService).showFullMap(mockGameContext);
  }

  @Test
  void testHandleMapCommandWithFullArgumentCaseInsensitive() {
    CommandToken mapCommand = new CommandToken(Verb.MAP, "FULL", List.of("FULL"), "map FULL");
    String expectedFullMapOutput = "=== FULL MAP ===\n";

    when(mockMapService.showFullMap(mockGameContext)).thenReturn(expectedFullMapOutput);

    CommandResult result = mapController.handle(mapCommand, mockGameContext);

    assertTrue(result.success());
    verify(mockMapService).showFullMap(mockGameContext);
  }

  @Test
  void testHandleMapCommandWithOtherArguments() {
    CommandToken mapCommand = new CommandToken(Verb.MAP, "nearby", List.of("nearby"), "map nearby");
    String expectedMapOutput = "=== MAP ===\n";

    when(mockMapService.showMap(mockGameContext)).thenReturn(expectedMapOutput);

    CommandResult result = mapController.handle(mapCommand, mockGameContext);

    assertTrue(result.success());
    verify(mockMapService).showMap(mockGameContext); // Should default to regular map
  }

  @Test
  void testHandleUnsupportedVerb() {
    CommandToken goCommand = new CommandToken(Verb.GO, "north", List.of("north"), "go north");

    CommandResult result = mapController.handle(goCommand, mockGameContext);

    assertFalse(result.success());
    assertTrue(result.message().contains("Map controller does not support verb"));
    assertFalse(result.shouldExit());
  }

  @Test
  void testHandleMapServiceException() {
    CommandToken mapCommand = new CommandToken(Verb.MAP, null, List.of(), "map");

    when(mockMapService.showMap(mockGameContext))
        .thenThrow(new RuntimeException("Map generation failed"));

    CommandResult result = mapController.handle(mapCommand, mockGameContext);

    assertFalse(result.success());
    assertTrue(result.message().contains("Failed to generate map"));
    assertTrue(result.message().contains("Map generation failed"));
    assertFalse(result.shouldExit());
  }

  @Test
  void testHandleFullMapServiceException() {
    CommandToken mapCommand = new CommandToken(Verb.MAP, "full", List.of("full"), "map full");

    when(mockMapService.showFullMap(mockGameContext))
        .thenThrow(new RuntimeException("Full map generation failed"));

    CommandResult result = mapController.handle(mapCommand, mockGameContext);

    assertFalse(result.success());
    assertTrue(result.message().contains("Failed to generate map"));
    assertTrue(result.message().contains("Full map generation failed"));
    assertFalse(result.shouldExit());
  }

  @Test
  void testConstructorWithMapService() {
    MapService testMapService = mock(MapService.class);
    MapController testController = new MapController(testMapService);

    assertNotNull(testController);
    assertTrue(testController.supports(Verb.MAP));
  }

  @Test
  void testHandleMapCommandWithEmptyArgsList() {
    CommandToken mapCommand = new CommandToken(Verb.MAP, null, List.of(), "map");
    String expectedMapOutput = "=== MAP ===\n";

    when(mockMapService.showMap(mockGameContext)).thenReturn(expectedMapOutput);

    CommandResult result = mapController.handle(mapCommand, mockGameContext);

    assertTrue(result.success());
    verify(mockMapService).showMap(mockGameContext);
  }

  @Test
  void testHandleMapCommandWithMultipleArguments() {
    CommandToken mapCommand =
        new CommandToken(Verb.MAP, "full", List.of("full", "detailed"), "map full detailed");
    String expectedFullMapOutput = "=== FULL MAP ===\n";

    when(mockMapService.showFullMap(mockGameContext)).thenReturn(expectedFullMapOutput);

    CommandResult result = mapController.handle(mapCommand, mockGameContext);

    assertTrue(result.success());
    verify(mockMapService).showFullMap(mockGameContext); // Should use full map for first arg "full"
  }
}
