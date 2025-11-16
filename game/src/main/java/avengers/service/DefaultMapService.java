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

  private static final int MAX_GRID_SIZE = 7;
  private static final char CURRENT_ROOM_SYMBOL = '@';
  private static final char VISITED_ROOM_SYMBOL = '#';
  private static final char UNVISITED_ROOM_SYMBOL = '?';
  private static final char EMPTY_SPACE = ' ';
  private static final char HORIZONTAL_CONNECTION = '-';
  private static final char VERTICAL_CONNECTION = '|';

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

    // Build room layout using BFS
    MapLayout layout = buildMapLayout(ctx, currentRoomId, showFullMap);

    // Generate ASCII grid
    String[][] grid = createAsciiGrid(layout);

    // Build the final output
    StringBuilder result = new StringBuilder();
    result.append("=== MAP ===\n");

    // Add the ASCII grid
    for (String[] row : grid) {
      for (String cell : row) {
        result.append(cell);
      }
      result.append("\n");
    }

    result.append("\n");
    result.append(generateLegend(ctx, currentRoom));

    return result.toString();
  }

  /**
   * Builds the map layout using BFS to explore connected rooms.
   *
   * @param ctx the game context
   * @param startRoomId the starting room ID
   * @param includeAllVisited whether to include all visited rooms
   * @return MapLayout containing room positions and connections
   */
  private MapLayout buildMapLayout(GameContext ctx, String startRoomId, boolean includeAllVisited) {
    MapLayout layout = new MapLayout();
    Queue<String> queue = new LinkedList<>();
    Set<String> processed = new HashSet<>();

    // Start with current room at center
    int centerX = MAX_GRID_SIZE / 2;
    int centerY = MAX_GRID_SIZE / 2;
    layout.addRoom(startRoomId, centerX, centerY, true);
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
          if (newX < 0 || newX >= MAX_GRID_SIZE || newY < 0 || newY >= MAX_GRID_SIZE) {
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
            layout.addRoom(connectedRoomId, newX, newY, isCurrentRoom);
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
   * @return 2D array representing the ASCII grid
   */
  private String[][] createAsciiGrid(MapLayout layout) {
    String[][] grid = new String[MAX_GRID_SIZE][MAX_GRID_SIZE];

    // Initialize grid with empty spaces
    for (int y = 0; y < MAX_GRID_SIZE; y++) {
      for (int x = 0; x < MAX_GRID_SIZE; x++) {
        grid[y][x] = String.valueOf(EMPTY_SPACE);
      }
    }

    // Place rooms
    for (MapLayout.RoomPosition roomPos : layout.getAllRooms()) {
      char symbol =
          roomPos.isCurrentRoom
              ? CURRENT_ROOM_SYMBOL
              : (layout.isRoomVisited(roomPos.roomId)
                  ? VISITED_ROOM_SYMBOL
                  : UNVISITED_ROOM_SYMBOL);
      grid[roomPos.yCoordinate][roomPos.xCoordinate] = String.valueOf(symbol);
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
    switch (direction.toLowerCase()) {
      case "north":
      case "south":
        int x = from.xCoordinate;
        int y = Math.min(from.yCoordinate, to.yCoordinate) + 1;
        if (y >= 0 && y < MAX_GRID_SIZE && x >= 0 && x < MAX_GRID_SIZE) {
          grid[y][x] = String.valueOf(VERTICAL_CONNECTION);
        }
        break;
      case "east":
      case "west":
        int x2 = Math.min(from.xCoordinate, to.xCoordinate) + 1;
        int y2 = from.yCoordinate;
        if (y2 >= 0 && y2 < MAX_GRID_SIZE && x2 >= 0 && x2 < MAX_GRID_SIZE) {
          grid[y2][x2] = String.valueOf(HORIZONTAL_CONNECTION);
        }
        break;
    }
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

    legend.append("LEGEND:\n");
    legend.append("@ = You are here\n");
    legend.append("# = Visited room\n");
    legend.append("? = Adjacent unexplored room\n");
    legend.append("- | = Connections\n\n");

    legend.append("CURRENT LOCATION:\n");
    legend.append(currentRoom.getName()).append("\n");
    legend.append(currentRoom.getDescription()).append("\n\n");

    legend.append("AVAILABLE EXITS:\n");
    Map<String, String> exits = currentRoom.getExits();
    if (exits != null && !exits.isEmpty()) {
      for (String direction : exits.keySet()) {
        legend.append("- ").append(direction.toUpperCase()).append("\n");
      }
    } else {
      legend.append("- None\n");
    }

    legend.append("\nHint: Use 'go [direction]' to move.\n");

    return legend.toString();
  }

  /** Helper class to manage map layout and room positions. */
  private static class MapLayout {
    private final Map<String, RoomPosition> roomPositions = new HashMap<>();
    private final List<Connection> connections = new ArrayList<>();
    private final Set<String> visitedRooms = new HashSet<>();

    void addRoom(String roomId, int x, int y, boolean isCurrentRoom) {
      roomPositions.put(roomId, new RoomPosition(roomId, x, y, isCurrentRoom));
      if (!isCurrentRoom) {
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

      RoomPosition(String roomId, int x, int y, boolean isCurrentRoom) {
        this.roomId = roomId;
        this.xCoordinate = x;
        this.yCoordinate = y;
        this.isCurrentRoom = isCurrentRoom;
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
