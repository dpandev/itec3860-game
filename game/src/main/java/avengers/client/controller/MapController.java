package avengers.client.controller;

import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;
import avengers.service.MapService;

/**
 * Controller for handling map-related commands. Provides ASCII map display functionality showing
 * explored rooms and current position.
 */
public class MapController implements CommandController {

  private final MapService mapService;

  /**
   * Constructs a MapController with the specified MapService.
   *
   * @param mapService the service for generating map displays
   */
  public MapController(MapService mapService) {
    this.mapService = mapService;
  }

  @Override
  public boolean supports(Verb verb) {
    return verb == Verb.MAP;
  }

  @Override
  public CommandResult handle(CommandToken cmd, GameContext ctx) {
    if (!supports(cmd.verb())) {
      return CommandResult.fail("Map controller does not support verb: " + cmd.verb());
    }

    try {
      String mapDisplay;

      // Check if "full" argument is provided
      if (cmd.args() != null
          && !cmd.args().isEmpty()
          && "full".equalsIgnoreCase(cmd.args().get(0))) {
        mapDisplay = mapService.showFullMap(ctx);
      } else {
        mapDisplay = mapService.showMap(ctx);
      }

      return CommandResult.success(mapDisplay);

    } catch (Exception e) {
      return CommandResult.fail("Failed to generate map: " + e.getMessage());
    }
  }
}
