package avengers.domain.model;

import java.util.Map;
import java.util.Optional;

/** Represents the entire game world, containing rooms, items, puzzles, and monsters. */
public final class World {
  private final Map<String, Room> roomsById;
  private final Map<String, Item> itemsById;
  private final Map<String, Puzzle> puzzlesById;
  private final String startRoomId;
  private final Map<String, Monster> monstersById;

  /** Constructor for World. */
  public World(
      Map<String, Room> rooms,
      Map<String, Item> items,
      Map<String, Puzzle> puzzles,
      Map<String, Monster> monsters,
      String startRoomId) {
    this.roomsById = rooms;
    this.itemsById = items;
    this.puzzlesById = puzzles;
    this.monstersById = monsters;
    this.startRoomId = startRoomId;
  }

  /**
   * Gets the items in the world.
   *
   * @return A map of items in the world.
   */
  public Map<String, Item> getItems() {
    return itemsById;
  }

  /**
   * Gets the rooms in the world.
   *
   * @return A map of rooms in the world.
   */
  public Map<String, Room> getRooms() {
    return roomsById;
  }

  /**
   * Gets the puzzles in the world.
   *
   * @return A map of puzzles in the world.
   */
  public Map<String, Puzzle> getPuzzles() {
    return puzzlesById;
  }

  /**
   * Gets the monsters in the world.
   *
   * @return A map of monsters in the world.
   */
  public Map<String, Monster> getMonsters() {
    return monstersById;
  }

  /**
   * Gets the ID of the starting room.
   *
   * @return The ID of the starting room.
   */
  public String getStartRoomId() {
    return startRoomId;
  }

  /**
   * Retrieves a room by its ID.
   *
   * @param roomId The ID of the room to retrieve.
   * @return An Optional containing the Room if found, or empty if not found.
   */
  public Optional<Room> getRoomById(String roomId) {
    return Optional.ofNullable(roomsById.get(roomId));
  }

  /**
   * Finds a puzzle by its ID.
   *
   * @param puzzleId The ID of the puzzle to find.
   * @return An Optional containing the Puzzle if found, or empty if not found.
   */
  public Optional<Puzzle> findPuzzle(String puzzleId) {
    return Optional.ofNullable(puzzlesById.get(puzzleId));
  }

  /**
   * Finds a room by its ID.
   *
   * @param roomId The ID of the room to find.
   * @return An Optional containing the Room if found, or empty if not found.
   */
  public Optional<Room> findRoom(String roomId) {
    return Optional.ofNullable(roomsById.get(roomId));
  }

  /**
   * Finds an item by its ID.
   *
   * @param itemId The ID of the item to find.
   * @return An Optional containing the Item if found, or empty if not found.
   */
  public Optional<Item> findItem(String itemId) {
    return Optional.ofNullable(itemsById.get(itemId));
  }

  /**
   * Finds a monster by its ID.
   *
   * @param monsterId The ID of the monster to find.
   * @return An Optional containing the Monster if found, or empty if not found.
   */
  public Optional<Monster> findMonster(String monsterId) {
    return Optional.ofNullable(monstersById.get(monsterId));
  }

  /**
   * Finds a monster by its name (case-insensitive).
   *
   * @param monsterName The name of the monster to find.
   * @return An Optional containing the Monster if found, or empty if not found.
   */
  public Optional<Monster> findMonsterByName(String monsterName) {
    return monstersById.values().stream()
        .filter(monster -> monster.getName().equalsIgnoreCase(monsterName))
        .findFirst();
  }
}

//
