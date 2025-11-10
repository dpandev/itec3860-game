package avengers.model.world;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Represents the game world containing all rooms. */
public final class World {
  private final Map<String, Room> rooms;
  private final Set<String> visited;

  /**
   * Constructor for World.
   *
   * @param rooms A map of room IDs to Room objects.
   */
  public World(Map<String, Room> rooms) {
    if (rooms == null || rooms.isEmpty()) {
      throw new IllegalArgumentException("World must have at least one room");
    }

    this.rooms = new HashMap<>(rooms);
    this.visited = new HashSet<>();
  }

  /**
   * Gets a room by its ID.
   *
   * @param id The room ID.
   * @return The Room object, or null if not found.
   */
  public Room getRoom(String id) {
    return rooms.get(id);
  }

  /**
   * Marks a room as visited.
   *
   * @param id The room ID to mark as visited.
   */
  public void markVisited(String id) {
    visited.add(id);
  }

  /**
   * Checks if a room has been visited.
   *
   * @param id The room ID to check.
   * @return true if the room has been visited, false otherwise.
   */
  public boolean hasVisited(String id) {
    return visited.contains(id);
  }

  /**
   * Gets all visited room IDs.
   *
   * @return A set of visited room IDs.
   */
  public Set<String> getVisited() {
    return Set.copyOf(visited);
  }

  /**
   * Gets all rooms in the world.
   *
   * @return A map of room IDs to Room objects.
   */
  public Map<String, Room> getRooms() {
    return Map.copyOf(rooms);
  }

  /**
   * Gets the total number of rooms in the world.
   *
   * @return The number of rooms.
   */
  public int getRoomCount() {
    return rooms.size();
  }
}
