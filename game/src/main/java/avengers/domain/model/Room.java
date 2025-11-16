package avengers.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Represents a room in the game world with items, monsters, and navigation connections. */
public final class Room {
  private final String id;
  private final String name;
  private final String description;
  private final List<String> itemIds;
  private final List<String> monsterIds;
  private final Map<String, String> exits;
  private final List<String> puzzleIds;
  private final boolean isVisited;

  /**
   * Constructs a Room with the specified properties.
   *
   * @param id the unique identifier for the room
   * @param name the name of the room
   * @param description the description of the room
   * @param itemIds list of item IDs present in the room
   * @param monsterIds list of monster IDs present in the room
   * @param exits map of direction to room ID for navigation
   * @param puzzleIds list of puzzle IDs in the room
   * @param isVisited whether the room has been visited before
   */
  public Room(
      String id,
      String name,
      String description,
      List<String> itemIds,
      List<String> monsterIds,
      Map<String, String> exits,
      List<String> puzzleIds,
      boolean isVisited) {
    if (id == null || id.trim().isEmpty()) {
      throw new IllegalArgumentException("Room ID cannot be null or blank");
    }
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Room name cannot be null or blank");
    }

    this.id = id;
    this.name = name;
    this.description = description != null ? description : "";
    this.itemIds = new ArrayList<>(itemIds != null ? itemIds : new ArrayList<>());
    this.monsterIds = new ArrayList<>(monsterIds != null ? monsterIds : new ArrayList<>());
    this.exits = exits;
    this.puzzleIds = new ArrayList<>(puzzleIds != null ? puzzleIds : new ArrayList<>());
    this.isVisited = isVisited;
  }

  /**
   * Gets the unique identifier of this room.
   *
   * @return the room's ID
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the name of this room.
   *
   * @return the room's name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the description of the room.
   *
   * @return the room's description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the list of item IDs in the room.
   *
   * @return a copy of the item IDs list
   */
  public List<String> getItemIds() {
    return new ArrayList<>(itemIds);
  }

  /**
   * Gets the list of monster IDs in the room.
   *
   * @return a copy of the monster IDs list
   */
  public List<String> getMonsterIds() {
    return new ArrayList<>(monsterIds);
  }

  /**
   * Gets the exits map for navigation.
   *
   * @return the exits map
   */
  public Map<String, String> getExits() {
    return exits;
  }

  /**
   * Gets the list of puzzle IDs in the room.
   *
   * @return a copy of the puzzle IDs list
   */
  public List<String> getPuzzleIds() {
    return new ArrayList<>(puzzleIds);
  }

  /**
   * Checks if the room has been visited.
   *
   * @return true if the room has been visited, false otherwise
   */
  public boolean isVisited() {
    return isVisited;
  }

  /**
   * Adds an item to the room.
   *
   * @param itemId the ID of the item to add
   * @return true if the item was added, false if it was already present
   */
  public boolean addItem(String itemId) {
    if (!itemIds.contains(itemId)) {
      return itemIds.add(itemId);
    }
    return false;
  }

  /**
   * Removes an item from the room.
   *
   * @param itemId the ID of the item to remove
   * @return true if the item was removed, false if it wasn't present
   */
  public boolean removeItem(String itemId) {
    return itemIds.remove(itemId);
  }

  /**
   * Checks if the room contains a specific item.
   *
   * @param itemId the ID of the item to check
   * @return true if the item is in the room, false otherwise
   */
  public boolean hasItem(String itemId) {
    return itemIds.contains(itemId);
  }

  /**
   * Adds a monster to the room.
   *
   * @param monsterId the ID of the monster to add
   * @return true if the monster was added, false if it was already present
   */
  public boolean addMonster(String monsterId) {
    if (!monsterIds.contains(monsterId)) {
      return monsterIds.add(monsterId);
    }
    return false;
  }

  /**
   * Removes a monster from the room.
   *
   * @param monsterId the ID of the monster to remove
   * @return true if the monster was removed, false if it wasn't present
   */
  public boolean removeMonster(String monsterId) {
    return monsterIds.remove(monsterId);
  }

  /**
   * Checks if the room contains a specific monster.
   *
   * @param monsterId the ID of the monster to check
   * @return true if the monster is in the room, false otherwise
   */
  public boolean hasMonster(String monsterId) {
    return monsterIds.contains(monsterId);
  }

  /**
   * Checks if the room has any items.
   *
   * @return true if the room has items, false otherwise
   */
  public boolean hasItems() {
    return !itemIds.isEmpty();
  }

  /**
   * Checks if the room has any monsters.
   *
   * @return true if the room has monsters, false otherwise
   */
  public boolean hasMonsters() {
    return !monsterIds.isEmpty();
  }

  /**
   * Gets the number of items in the room.
   *
   * @return the count of items
   */
  public int getItemCount() {
    return itemIds.size();
  }

  /**
   * Gets the number of monsters in the room.
   *
   * @return the count of monsters
   */
  public int getMonsterCount() {
    return monsterIds.size();
  }

  /**
   * Checks if the room has an exit in the specified direction.
   *
   * @param direction the direction to check
   * @return true if there's an exit in that direction, false otherwise
   */
  public boolean hasExit(Direction direction) {
    return exits != null && exits.containsKey(direction.toString().toLowerCase());
  }

  /**
   * Gets the room ID for the exit in the specified direction.
   *
   * @param direction the direction to check
   * @return the room ID for that direction, or null if no exit exists
   */
  public String getExitRoomId(Direction direction) {
    if (exits == null) {
      return null;
    }
    return exits.get(direction.toString().toLowerCase());
  }

  @Override
  public String toString() {
    return String.format(
        "%s - Items: %d, Monsters: %d", getName(), getItemCount(), getMonsterCount());
  }
}
//
