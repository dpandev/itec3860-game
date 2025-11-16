package avengers.client.controller;

import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;
import avengers.service.ExplorationService;

/** Controller to handle movement-related commands such as GO, EXPLORE, and MAP. */
public class MovementController implements CommandController {

  private final ExplorationService explorationService;

  /**
   * Constructs MovementController with exploration service.
   *
   * @param explorationService the service for exploration
   */
  public MovementController(ExplorationService explorationService) {
    this.explorationService = explorationService;
  }

  @Override
  public boolean supports(Verb verb) {
    return verb == Verb.GO || verb == Verb.EXPLORE || verb == Verb.MAP;
  }

  @Override
  public CommandResult handle(CommandToken cmd, GameContext ctx) {
    if (cmd == null || ctx == null) {
      return CommandResult.fail("Invalid command or context.");
    }

    return switch (cmd.verb()) {
      case GO -> handleGo(cmd, ctx);
      case EXPLORE -> handleExplore(ctx);
      case MAP -> handleMap(ctx);
      default -> CommandResult.fail("Movement command not supported: " + cmd.verb());
    };
  }

  /**
   * Handles GO command.
   *
   * @param cmd command token
   * @param ctx game context
   * @return command result
   */
  private CommandResult handleGo(CommandToken cmd, GameContext ctx) {
    String direction = cmd.target();
    ExplorationService.MoveResult result = explorationService.move(ctx, direction);

    if (result.isSuccess()) {
      return CommandResult.success(result.getMessage());
    } else {
      return CommandResult.fail(result.getMessage());
    }
  }

  /**
   * Handles EXPLORE command.
   *
   * @param ctx game context
   * @return command result
   */
  private CommandResult handleExplore(GameContext ctx) {
    String description = explorationService.explore(ctx);
    return CommandResult.success(description);
  }

  /**
   * Handles MAP command.
   *
   * @param ctx game context
   * @return command result
   */
  private CommandResult handleMap(GameContext ctx) {
    String roomId = ctx.player().getRoomId();
    var world = ctx.world();
    var roomOpt = world.findRoom(roomId);

    if (roomOpt.isEmpty()) {
      return CommandResult.fail("Error: Current room not found.");
    }

    var room = roomOpt.get();
    var exits = room.getExits();

    StringBuilder map = new StringBuilder();
    map.append("\n=== MAP ===\n");
    map.append("Current Location: ").append(room.getName()).append("\n\n");

    if (exits.isEmpty()) {
      map.append("No exits from this location.\n");
    } else {
      map.append("Connected Rooms:\n");
      for (var entry : exits.entrySet()) {
        String direction = entry.getKey().toLowerCase();
        String destRoomId = entry.getValue();
        var destRoom = world.findRoom(destRoomId);
        String destName = destRoom.isPresent() ? destRoom.get().getName() : "Unknown";
        String visitedMarker =
            ctx.player().getRoomsVisited().contains(destRoomId) ? " [Visited]" : " [Unexplored]";
        map.append("  ")
            .append(direction.toUpperCase())
            .append(": ")
            .append(destName)
            .append(visitedMarker)
            .append("\n");
      }
    }

    map.append("\nRooms visited: ").append(ctx.player().getRoomsVisited().size()).append("\n");

    return CommandResult.success(map.toString());
  }
}
