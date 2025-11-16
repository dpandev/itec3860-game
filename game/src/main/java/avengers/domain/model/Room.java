package avengers.domain.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Represents a room in the game world with exits, items, monsters, and puzzles. */
public final class Room {
  private final String id;
  private final String name;
  private final String description;
  private final Map<String, String> exits;
  private final List<String> monsterIds;
  private final List<String> itemIds;
  private final List<String> puzzleIds;

  /**
   * Constructs a Room with the specified properties.
   *
   * @param id the unique identifier for the room
   * @param name the name of the room
   * @param description the description of the room
   * @param exits a map of direction to room ID
   * @param monsterIds list of monster IDs in this room
   * @param itemIds list of item IDs in this room
   * @param puzzleIds list of puzzle IDs in this room
   */
  public Room(
      String id,
      String name,
      String description,
      Map<String, String> exits,
      List<String> monsterIds,
      List<String> itemIds,
      List<String> puzzleIds) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.exits = new HashMap<>(exits != null ? exits : Map.of());
    this.monsterIds = new ArrayList<>(monsterIds != null ? monsterIds : List.of());
    this.itemIds = new ArrayList<>(itemIds != null ? itemIds : List.of());
    this.puzzleIds = new ArrayList<>(puzzleIds != null ? puzzleIds : List.of());
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Gets the exit room ID for a given direction.
   *
   * @param direction the direction to check
   * @return the room ID for that direction, or null if no exit exists
   */
  public String getExit(String direction) {
    return exits.get(direction != null ? direction.toUpperCase() : null);
  }

  /**
   * Gets all exits from this room.
   *
   * @return an immutable map of direction to room ID
   */
  public Map<String, String> getExits() {
    return Map.copyOf(exits);
  }

  /**
   * Checks if an exit exists in the given direction.
   *
   * @param direction the direction to check
   * @return true if an exit exists, false otherwise
   */
  public boolean hasExit(String direction) {
    return exits.containsKey(direction != null ? direction.toUpperCase() : null);
  }

  /**
   * Gets the list of monster IDs in this room.
   *
   * @return a mutable list of monster IDs
   */
  public List<String> getMonsterIds() {
    return monsterIds;
  }

  /**
   * Gets the list of item IDs in this room.
   *
   * @return a mutable list of item IDs
   */
  public List<String> getItemIds() {
    return itemIds;
  }

  /**
   * Gets the list of puzzle IDs in this room.
   *
   * @return a mutable list of puzzle IDs
   */
  public List<String> getPuzzleIds() {
    return puzzleIds;
  }

  /**
   * Adds a monster ID to this room.
   *
   * @param monsterId the monster ID to add
   */
  public void addMonster(String monsterId) {
    if (monsterId != null && !monsterIds.contains(monsterId)) {
      monsterIds.add(monsterId);
    }
  }

  /**
   * Removes a monster ID from this room.
   *
   * @param monsterId the monster ID to remove
   * @return true if the monster was removed, false otherwise
   */
  public boolean removeMonster(String monsterId) {
    return monsterIds.remove(monsterId);
  }

  /**
   * Adds an item ID to this room.
   *
   * @param itemId the item ID to add
   */
  public void addItem(String itemId) {
    if (itemId != null && !itemIds.contains(itemId)) {
      itemIds.add(itemId);
    }
  }

  /**
   * Removes an item ID from this room.
   *
   * @param itemId the item ID to remove
   * @return true if the item was removed, false otherwise
   */
  public boolean removeItem(String itemId) {
    return itemIds.remove(itemId);
  }

  /**
   * Checks if this room has any monsters.
   *
   * @return true if monsters are present, false otherwise
   */
  public boolean hasMonsters() {
    return !monsterIds.isEmpty();
  }

  /**
   * Checks if this room has any items.
   *
   * @return true if items are present, false otherwise
   */
  public boolean hasItems() {
    return !itemIds.isEmpty();
  }

  /**
   * Checks if this room has any puzzles.
   *
   * @return true if puzzles are present, false otherwise
   */
  public boolean hasPuzzles() {
    return !puzzleIds.isEmpty();
  }
}
