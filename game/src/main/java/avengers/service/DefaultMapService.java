package avengers.service;

import avengers.domain.model.Room;
import avengers.domain.utils.GameContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Default implementation of MapService that generates ASCII maps of the game world. Uses BFS to
 * explore rooms and creates a dynamic grid layout centered on the player.
 */
public class DefaultMapService implements MapService {

  private static final int STANDARD_GRID_SIZE = 15; // For regular map
  private static final int FULL_GRID_SIZE = 31; // Larger grid for full map to show more rooms
  private static final String CURRENT_ROOM_SYMBOL = "[*]"; // Current location - highlighted
  private static final String VISITED_ROOM_SYMBOL = "[ ]"; // Explored rooms
  private static final String UNVISITED_ROOM_SYMBOL = "[?]"; // Adjacent unexplored
  private static final String EMPTY_SPACE = "   "; // 3 spaces for alignment
  private static final String HORIZONTAL_CONNECTION = "───"; // Box drawing characters
  private static final String VERTICAL_CONNECTION = " | "; // Vertical connection

  @Override
  public String showMap(GameContext ctx) {
    return generateMap(ctx, false);
  }

  @Override
  public String showFullMap(GameContext ctx) {
    return generateMap(ctx, true);
  }

  /**
   * Generates the ASCII map based on the specified mode.
   *
   * @param ctx the game context
   * @param showFullMap whether to show all visited rooms or just nearby rooms
   * @return formatted ASCII map with legend
   */
  private String generateMap(GameContext ctx, boolean showFullMap) {
    String currentRoomId = ctx.player().getRoomId();
    Room currentRoom = ctx.world().getRoomById(currentRoomId).orElse(null);

    if (currentRoom == null) {
      return "Map unavailable - current location unknown.";
    }

    // Use larger grid for full map
    int gridSize = showFullMap ? FULL_GRID_SIZE : STANDARD_GRID_SIZE;

    // Build room layout using BFS
    MapLayout layout = buildMapLayout(ctx, currentRoomId, showFullMap, gridSize);

    // Generate ASCII grid
    String[][] grid = createAsciiGrid(layout, gridSize);

    // Build the final output
    StringBuilder result = new StringBuilder();

    if (showFullMap) {
      result.append("╔═══════════════════════════════════════════════╗\n");
      result.append("║       COMPLETE EXPLORATION MAP (FULL)        ║\n");
      result.append("╚═══════════════════════════════════════════════╝\n\n");
    } else {
      result.append("╔═══════════════════════════════════════════════╗\n");
      result.append("║           EXPLORED TERRITORY MAP             ║\n");
      result.append("╚═══════════════════════════════════════════════╝\n\n");
    }

    // Add the ASCII grid
    for (String[] row : grid) {
      result.append("  "); // Left padding
      for (String cell : row) {
        result.append(cell);
      }
      result.append("\n");
    }

    result.append("\n");

    // Add room directory for full map
    if (showFullMap) {
      result.append(generateRoomDirectory(ctx, layout));
      result.append("\n");
    }

    result.append(generateLegend(ctx, currentRoom));

    return result.toString();
  }

  /**
   * Builds the map layout using BFS to explore connected rooms.
   *
   * @param ctx the game context
   * @param startRoomId the starting room ID
   * @param includeAllVisited whether to include all visited rooms
   * @param gridSize the size of the grid to use
   * @return MapLayout containing room positions and connections
   */
  private MapLayout buildMapLayout(
      GameContext ctx, String startRoomId, boolean includeAllVisited, int gridSize) {
    MapLayout layout = new MapLayout();
    Queue<String> queue = new LinkedList<>();
    Set<String> processed = new HashSet<>();

    // Start with current room at center
    int centerX = gridSize / 2;
    int centerY = gridSize / 2;
    layout.addRoom(startRoomId, centerX, centerY, true, true); // Current room is always visited
    queue.add(startRoomId);
    processed.add(startRoomId);

    List<String> visitedRooms = ctx.player().getRoomsVisited();

    while (!queue.isEmpty()) {
      String currentRoomId = queue.poll();
      Room currentRoom = ctx.world().getRoomById(currentRoomId).orElse(null);

      if (currentRoom == null) {
        continue;
      }

      MapLayout.RoomPosition currentPos = layout.getRoomPosition(currentRoomId);
      if (currentPos == null) {
        continue;
      }

      // Explore connected rooms
      Map<String, String> exits = currentRoom.getExits();
      if (exits != null) {
        for (Map.Entry<String, String> exit : exits.entrySet()) {
          String direction = exit.getKey();
          String connectedRoomId = exit.getValue();

          if (processed.contains(connectedRoomId)) {
            continue;
          }

          // Calculate new position based on direction
          int newX = currentPos.xCoordinate;
          int newY = currentPos.yCoordinate;

          switch (direction.toLowerCase()) {
            case "north":
              newY -= 2; // Leave space for connection
              break;
            case "south":
              newY += 2;
              break;
            case "east":
              newX += 2;
              break;
            case "west":
              newX -= 2;
              break;
            default:
              continue; // Skip UP/DOWN for 2D grid
          }

          // Check bounds
          if (newX < 0 || newX >= gridSize || newY < 0 || newY >= gridSize) {
            continue;
          }

          // Check if position is already occupied
          if (layout.isPositionOccupied(newX, newY)) {
            continue;
          }

          boolean isVisited = visitedRooms.contains(connectedRoomId);
          boolean isCurrentRoom = connectedRoomId.equals(ctx.player().getRoomId());

          // Add room if it should be included
          if (includeAllVisited
              ? isVisited
              : (isVisited || isAdjacentToVisited(ctx, connectedRoomId))) {
            layout.addRoom(connectedRoomId, newX, newY, isCurrentRoom, isVisited);
            layout.addConnection(currentRoomId, connectedRoomId, direction);

            if (isVisited) {
              queue.add(connectedRoomId);
            }
            processed.add(connectedRoomId);
          }
        }
      }
    }

    return layout;
  }

  /**
   * Checks if a room is adjacent to any visited room.
   *
   * @param ctx the game context
   * @param roomId the room ID to check
   * @return true if the room is adjacent to a visited room
   */
  private boolean isAdjacentToVisited(GameContext ctx, String roomId) {
    List<String> visitedRooms = ctx.player().getRoomsVisited();

    for (String visitedRoomId : visitedRooms) {
      Room visitedRoom = ctx.world().getRoomById(visitedRoomId).orElse(null);
      if (visitedRoom != null && visitedRoom.getExits() != null) {
        if (visitedRoom.getExits().containsValue(roomId)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Creates the ASCII grid from the map layout.
   *
   * @param layout the map layout
   * @param gridSize the size of the grid
   * @return 2D array representing the ASCII grid
   */
  private String[][] createAsciiGrid(MapLayout layout, int gridSize) {
    String[][] grid = new String[gridSize][gridSize];

    // Initialize grid with empty spaces
    for (int y = 0; y < gridSize; y++) {
      for (int x = 0; x < gridSize; x++) {
        grid[y][x] = EMPTY_SPACE;
      }
    }

    // Place rooms
    for (MapLayout.RoomPosition roomPos : layout.getAllRooms()) {
      String symbol;
      if (roomPos.isCurrentRoom) {
        symbol = CURRENT_ROOM_SYMBOL;
      } else if (roomPos.isVisited) {
        symbol = VISITED_ROOM_SYMBOL;
      } else {
        symbol = UNVISITED_ROOM_SYMBOL;
      }
      grid[roomPos.yCoordinate][roomPos.xCoordinate] = symbol;
    }

    // Add connections
    for (MapLayout.Connection connection : layout.getAllConnections()) {
      MapLayout.RoomPosition from = layout.getRoomPosition(connection.fromRoomId);
      MapLayout.RoomPosition to = layout.getRoomPosition(connection.toRoomId);

      if (from != null && to != null) {
        addConnectionToGrid(grid, from, to, connection.direction);
      }
    }

    return grid;
  }

  /**
   * Adds connection lines between rooms in the grid.
   *
   * @param grid the ASCII grid
   * @param from the source room position
   * @param to the destination room position
   * @param direction the direction of connection
   */
  private void addConnectionToGrid(
      String[][] grid, MapLayout.RoomPosition from, MapLayout.RoomPosition to, String direction) {
    int gridSize = grid.length;
    switch (direction.toLowerCase()) {
      case "north":
      case "south":
        int x = from.xCoordinate;
        int y = Math.min(from.yCoordinate, to.yCoordinate) + 1;
        if (y >= 0 && y < gridSize && x >= 0 && x < gridSize) {
          grid[y][x] = VERTICAL_CONNECTION;
        }
        break;
      case "east":
      case "west":
        int x2 = Math.min(from.xCoordinate, to.xCoordinate) + 1;
        int y2 = from.yCoordinate;
        if (y2 >= 0 && y2 < gridSize && x2 >= 0 && x2 < gridSize) {
          grid[y2][x2] = HORIZONTAL_CONNECTION;
        }
        break;
    }
  }

  /**
   * Generates a directory of all rooms displayed on the full map.
   *
   * @param ctx the game context
   * @param layout the map layout
   * @return formatted room directory
   */
  private String generateRoomDirectory(GameContext ctx, MapLayout layout) {
    StringBuilder directory = new StringBuilder();

    directory.append("╔═══════════════════════════════════════════════╗\n");
    directory.append("║              ROOM DIRECTORY                  ║\n");
    directory.append("╠═══════════════════════════════════════════════╣\n");

    List<MapLayout.RoomPosition> rooms = layout.getAllRooms();

    // Sort rooms: current room first, then by name
    rooms.sort(
        (r1, r2) -> {
          if (r1.isCurrentRoom) return -1;
          if (r2.isCurrentRoom) return 1;

          Room room1 = ctx.world().getRoomById(r1.roomId).orElse(null);
          Room room2 = ctx.world().getRoomById(r2.roomId).orElse(null);

          if (room1 == null) return 1;
          if (room2 == null) return -1;

          return room1.getName().compareTo(room2.getName());
        });

    int count = 0;
    for (MapLayout.RoomPosition roomPos : rooms) {
      Room room = ctx.world().getRoomById(roomPos.roomId).orElse(null);
      if (room != null) {
        count++;
        String symbol =
            roomPos.isCurrentRoom ? "[*]" : (layout.isRoomVisited(roomPos.roomId) ? "[ ]" : "[?]");

        String roomName = room.getName();
        // Truncate if too long
        if (roomName.length() > 38) {
          roomName = roomName.substring(0, 35) + "...";
        }

        String line = String.format("║ %s %-40s ║", symbol, roomName);
        directory.append(line).append("\n");
      }
    }

    if (count == 0) {
      directory.append("║ No rooms to display                          ║\n");
    }

    directory.append("╚═══════════════════════════════════════════════╝");

    return directory.toString();
  }

  /**
   * Generates the legend and current room information.
   *
   * @param ctx the game context
   * @param currentRoom the current room
   * @return formatted legend string
   */
  private String generateLegend(GameContext ctx, Room currentRoom) {
    StringBuilder legend = new StringBuilder();

    legend.append("┌─────────────────────────────────────────────┐\n");
    legend.append("│ LEGEND                                      │\n");
    legend.append("├─────────────────────────────────────────────┤\n");
    legend.append("│ [*] = Your Current Location (Highlighted)   │\n");
    legend.append("│ [ ] = Explored Room (Visited)               │\n");
    legend.append("│ [?] = Adjacent Unexplored Room              │\n");
    legend.append("│ ─── = East-West Connection                  │\n");
    legend.append("│  |  = North-South Connection                │\n");
    legend.append("└─────────────────────────────────────────────┘\n\n");

    legend.append("┌─────────────────────────────────────────────┐\n");
    legend.append("│ CURRENT LOCATION                            │\n");
    legend.append("├─────────────────────────────────────────────┤\n");
    legend.append("│ ").append(String.format("%-44s", currentRoom.getName())).append("│\n");

    // Wrap description if too long
    String description = currentRoom.getDescription();
    if (description.length() > 42) {
      // Split into multiple lines
      List<String> descLines = wrapText(description, 42);
      for (String line : descLines) {
        legend.append("│ ").append(String.format("%-44s", line)).append("│\n");
      }
    } else {
      legend.append("│ ").append(String.format("%-44s", description)).append("│\n");
    }

    legend.append("└─────────────────────────────────────────────┘\n\n");

    legend.append("┌─────────────────────────────────────────────┐\n");
    legend.append("│ AVAILABLE EXITS                             │\n");
    legend.append("├─────────────────────────────────────────────┤\n");

    Map<String, String> exits = currentRoom.getExits();
    if (exits != null && !exits.isEmpty()) {
      for (String direction : exits.keySet()) {
        legend.append("│ → ").append(String.format("%-41s", direction.toUpperCase())).append("│\n");
      }
    } else {
      legend.append("│ → ").append(String.format("%-41s", "NONE - Dead End")).append("│\n");
    }

    legend.append("└─────────────────────────────────────────────┘\n\n");

    // Statistics
    int totalExplored = ctx.player().getRoomsVisited().size();
    legend.append("Progress: ").append(totalExplored).append(" room(s) explored\n");
    legend.append("Hint: Type 'go [direction]' to move, 'map full' for complete map\n");

    return legend.toString();
  }

  /**
   * Wraps text to fit within specified width.
   *
   * @param text the text to wrap
   * @param width the maximum width
   * @return list of wrapped lines
   */
  private List<String> wrapText(String text, int width) {
    List<String> lines = new ArrayList<>();
    String[] words = text.split(" ");
    StringBuilder currentLine = new StringBuilder();

    for (String word : words) {
      if (currentLine.length() + word.length() + 1 > width) {
        if (currentLine.length() > 0) {
          lines.add(currentLine.toString());
          currentLine = new StringBuilder();
        }
      }
      if (currentLine.length() > 0) {
        currentLine.append(" ");
      }
      currentLine.append(word);
    }

    if (currentLine.length() > 0) {
      lines.add(currentLine.toString());
    }

    return lines;
  }

  /** Helper class to manage map layout and room positions. */
  private static class MapLayout {
    private final Map<String, RoomPosition> roomPositions = new HashMap<>();
    private final List<Connection> connections = new ArrayList<>();
    private final Set<String> visitedRooms = new HashSet<>();

    void addRoom(String roomId, int x, int y, boolean isCurrentRoom, boolean isVisited) {
      roomPositions.put(roomId, new RoomPosition(roomId, x, y, isCurrentRoom, isVisited));
      if (isVisited && !isCurrentRoom) {
        visitedRooms.add(roomId);
      }
    }

    void addConnection(String fromRoomId, String toRoomId, String direction) {
      connections.add(new Connection(fromRoomId, toRoomId, direction));
    }

    RoomPosition getRoomPosition(String roomId) {
      return roomPositions.get(roomId);
    }

    boolean isPositionOccupied(int x, int y) {
      return roomPositions.values().stream()
          .anyMatch(pos -> pos.xCoordinate == x && pos.yCoordinate == y);
    }

    boolean isRoomVisited(String roomId) {
      return visitedRooms.contains(roomId);
    }

    List<RoomPosition> getAllRooms() {
      return new ArrayList<>(roomPositions.values());
    }

    List<Connection> getAllConnections() {
      return new ArrayList<>(connections);
    }

    static class RoomPosition {
      final String roomId;
      final int xCoordinate;
      final int yCoordinate;
      final boolean isCurrentRoom;
      final boolean isVisited;

      RoomPosition(String roomId, int x, int y, boolean isCurrentRoom, boolean isVisited) {
        this.roomId = roomId;
        this.xCoordinate = x;
        this.yCoordinate = y;
        this.isCurrentRoom = isCurrentRoom;
        this.isVisited = isVisited;
      }
    }

    static class Connection {
      final String fromRoomId;
      final String toRoomId;
      final String direction;

      Connection(String fromRoomId, String toRoomId, String direction) {
        this.fromRoomId = fromRoomId;
        this.toRoomId = toRoomId;
        this.direction = direction;
      }
    }
  }
}
