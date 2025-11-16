package avengers.service;

import avengers.domain.model.Item;
import avengers.domain.model.Monster;
import avengers.domain.model.Player;
import avengers.domain.model.Puzzle;
import avengers.domain.model.Room;
import avengers.domain.model.World;
import avengers.domain.utils.GameContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/** Default implementation of ExplorationService. */
public class DefaultExplorationService implements ExplorationService {
  //

  private static final Map<String, String> DIRECTION_ALIASES = new HashMap<>();

  static {
    DIRECTION_ALIASES.put("n", "NORTH");
    DIRECTION_ALIASES.put("north", "NORTH");
    DIRECTION_ALIASES.put("s", "SOUTH");
    DIRECTION_ALIASES.put("south", "SOUTH");
    DIRECTION_ALIASES.put("e", "EAST");
    DIRECTION_ALIASES.put("east", "EAST");
    DIRECTION_ALIASES.put("w", "WEST");
    DIRECTION_ALIASES.put("west", "WEST");
    DIRECTION_ALIASES.put("up", "UP");
    DIRECTION_ALIASES.put("u", "UP");
    DIRECTION_ALIASES.put("down", "DOWN");
    DIRECTION_ALIASES.put("d", "DOWN");
  }

  private final InteractionService interactionService;

  /** Constructs DefaultExplorationService with interaction service for puzzles. */
  public DefaultExplorationService(InteractionService interactionService) {
    this.interactionService = interactionService;
  }

  @Override
  public String describeCurrentRoom(GameContext ctx) {
    Player player = ctx.player();
    World world = ctx.world();

    Optional<Room> roomOpt = world.findRoom(player.getRoomId());
    if (roomOpt.isEmpty()) {
      return "Error: Current room not found. Please contact support.";
    }

    return formatRoomDescription(roomOpt.get(), world);
  }

  @Override
  public String explore(GameContext ctx) {
    return describeCurrentRoom(ctx);
  }

  @Override
  public MoveResult move(GameContext ctx, String direction) {
    if (direction == null || direction.isBlank()) {
      return MoveResult.failure("Which direction? Try: north, south, east, west");
    }

    // Normalize direction using aliases
    String normalizedDir =
        DIRECTION_ALIASES.getOrDefault(direction.toLowerCase().trim(), direction.toUpperCase());

    Player player = ctx.player();
    World world = ctx.world();

    // Get current room
    Optional<Room> currentRoomOpt = world.findRoom(player.getRoomId());
    if (currentRoomOpt.isEmpty()) {
      return MoveResult.failure("Error: Current room not found. Unable to move.");
    }

    Room currentRoom = currentRoomOpt.get();

    // Check if exit exists
    if (!currentRoom.hasExit(normalizedDir)) {
      return MoveResult.failure("You cannot go " + direction.toLowerCase() + " from here.");
    }

    String nextRoomId = currentRoom.getExit(normalizedDir);

    // Verify destination exists
    Optional<Room> nextRoomOpt = world.findRoom(nextRoomId);
    if (nextRoomOpt.isEmpty()) {
      return MoveResult.failure("Error: Destination room not found. Unable to move.");
    }

    Room nextRoom = nextRoomOpt.get();

    // Reset puzzle state if leaving a room with in-progress puzzle
    if (ctx.isAwaitingPuzzleAnswer()) {
      resetPuzzlesInRoom(currentRoom, world);
      ctx.setAwaitingPuzzleAnswer(false);
    }

    // Move player
    player.setRoomId(nextRoomId);

    // Track visited rooms
    if (!player.getRoomsVisited().contains(nextRoomId)) {
      player.addRoomToRoomsVisited(nextRoomId);
    }

    // Build movement message
    String roomDesc = formatRoomDescription(nextRoom, world);

    // Check for puzzles and present if needed
    boolean puzzlePresented = false;
    if (nextRoom.hasPuzzles() && interactionService != null) {
      for (String puzzleId : nextRoom.getPuzzleIds()) {
        Optional<Puzzle> puzzleOpt = world.findPuzzle(puzzleId);
        if (puzzleOpt.isPresent()) {
          Puzzle puzzle = puzzleOpt.get();
          // Only present if not already solved
          if (!puzzle.isSolved()) {
            String puzzlePrompt = interactionService.presentPuzzle(ctx, puzzleId);
            roomDesc = roomDesc + "\n" + puzzlePrompt;
            puzzlePresented = true;
            break; // Only present one puzzle at a time
          }
        }
      }
    }

    return MoveResult.success(roomDesc, puzzlePresented);
  }

  /**
   * Formats a complete room description.
   *
   * @param room the room to describe
   * @param world the game world
   * @return formatted description
   */
  private String formatRoomDescription(Room room, World world) {
    StringBuilder sb = new StringBuilder();

    // Room header
    sb.append("\n=== ").append(room.getName()).append(" ===\n");
    sb.append(room.getDescription()).append("\n");

    // Exits
    if (!room.getExits().isEmpty()) {
      sb.append("\nExits: ");
      sb.append(
          room.getExits().keySet().stream()
              .map(String::toLowerCase)
              .sorted()
              .collect(Collectors.joining(", ")));
      sb.append("\n");
    } else {
      sb.append("\nNo obvious exits.\n");
    }

    // Items
    if (room.hasItems()) {
      List<String> visibleItems =
          room.getItemIds().stream()
              .map(id -> world.findItem(id).map(Item::getName).orElse(null))
              .filter(name -> name != null)
              .collect(Collectors.toList());

      if (!visibleItems.isEmpty()) {
        sb.append("\nItems: ");
        sb.append(String.join(", ", visibleItems));
        sb.append("\n");
      }
    }

    // Monsters (only alive ones)
    if (room.hasMonsters()) {
      List<String> aliveMonsters =
          room.getMonsterIds().stream()
              .map(
                  id ->
                      world
                          .findMonster(id)
                          .filter(Monster::isAlive)
                          .map(Monster::getName)
                          .orElse(null))
              .filter(name -> name != null)
              .collect(Collectors.toList());

      if (!aliveMonsters.isEmpty()) {
        sb.append("\n⚠ Monsters: ");
        sb.append(String.join(", ", aliveMonsters));
        sb.append("\n");
      }
    }

    return sb.toString();
  }

  /**
   * Resets all puzzles in a room to LOCKED state.
   *
   * @param room the room containing puzzles
   * @param world the game world
   */
  private void resetPuzzlesInRoom(Room room, World world) {
    if (room.hasPuzzles()) {
      for (String puzzleId : room.getPuzzleIds()) {
        world.findPuzzle(puzzleId).ifPresent(Puzzle::reset);
      }
    }
  }
}
