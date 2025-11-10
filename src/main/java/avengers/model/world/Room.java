package avengers.model.world;

import avengers.model.Monster;
import avengers.model.item.Item;
import avengers.model.puzzle.Puzzle;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

/** Represents a room in the game world. */
public final class Room {
  private final String id;
  private final String name;
  private final String description;
  private final EnumMap<Direction, String> exits;
  private final List<Item> items;
  private final List<Monster> monsters;
  private final List<Puzzle> puzzles;
  private final boolean safeZone;
  private boolean visited;

  /**
   * Constructor for Room.
   *
   * @param id The unique identifier for the room.
   * @param name The name of the room.
   * @param description A description of the room.
   * @param exits A map of directions to connected room IDs.
   * @param items A list of items in the room.
   * @param monsters A list of monsters in the room.
   * @param puzzles A list of puzzles in the room.
   * @param safeZone Whether this room is a safe zone.
   */
  public Room(
      String id,
      String name,
      String description,
      EnumMap<Direction, String> exits,
      List<Item> items,
      List<Monster> monsters,
      List<Puzzle> puzzles,
      boolean safeZone) {
    if (id == null || id.trim().isEmpty()) {
      throw new IllegalArgumentException("Room ID cannot be null or empty");
    }
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Room name cannot be null or empty");
    }
    if (description == null || description.trim().isEmpty()) {
      throw new IllegalArgumentException("Room description cannot be null or empty");
    }
    if (exits == null) {
      throw new IllegalArgumentException("Exits cannot be null");
    }

    // Validate safe zone: safe zones cannot have monsters
    List<Monster> monsterList = monsters != null ? monsters : new ArrayList<>();
    if (safeZone && !monsterList.isEmpty()) {
      throw new IllegalArgumentException("Safe zones cannot contain monsters");
    }

    this.id = id;
    this.name = name;
    this.description = description;
    this.exits = new EnumMap<>(exits);
    this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    this.monsters = new ArrayList<>(monsterList);
    this.puzzles = puzzles != null ? new ArrayList<>(puzzles) : new ArrayList<>();
    this.safeZone = safeZone;
    this.visited = false;
  }

  /**
   * Gets the exit room ID for a given direction.
   *
   * @param direction The direction to check.
   * @return An Optional containing the room ID if an exit exists, empty otherwise.
   */
  public Optional<String> exit(Direction direction) {
    return Optional.ofNullable(exits.get(direction));
  }

  /**
   * Checks if an exit exists in the given direction.
   *
   * @param direction The direction to check.
   * @return true if an exit exists in that direction, false otherwise.
   */
  public boolean hasExit(Direction direction) {
    return exits.containsKey(direction);
  }

  /**
   * Adds an item to the room.
   *
   * @param item The item to add.
   */
  public void addItem(Item item) {
    if (item == null) {
      throw new IllegalArgumentException("Item cannot be null");
    }
    items.add(item);
  }

  /**
   * Removes an item from the room by its ID.
   *
   * @param itemId The ID of the item to remove.
   */
  public void removeItem(String itemId) {
    if (itemId == null || itemId.trim().isEmpty()) {
      throw new IllegalArgumentException("Item ID cannot be null or empty");
    }
    items.removeIf(item -> item.getId().equals(itemId));
  }

  /**
   * Adds a puzzle to the room.
   *
   * @param puzzle The puzzle to add.
   */
  public void addPuzzle(Puzzle puzzle) {
    if (puzzle == null) {
      throw new IllegalArgumentException("Puzzle cannot be null");
    }
    puzzles.add(puzzle);
  }

  /**
   * Removes a puzzle from the room.
   *
   * @param puzzle The puzzle to remove.
   */
  public void removePuzzle(Puzzle puzzle) {
    if (puzzle == null) {
      throw new IllegalArgumentException("Puzzle cannot be null");
    }
    puzzles.remove(puzzle);
  }

  /**
   * Gets the room ID.
   *
   * @return The room ID.
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the room name.
   *
   * @return The room name.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the room description.
   *
   * @return The room description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets all exits from this room.
   *
   * @return A map of directions to room IDs.
   */
  public EnumMap<Direction, String> getExits() {
    return new EnumMap<>(exits);
  }

  /**
   * Gets all items in the room.
   *
   * @return A list of items.
   */
  public List<Item> getItems() {
    return List.copyOf(items);
  }

  /**
   * Gets all monsters in the room.
   *
   * @return A list of monsters.
   */
  public List<Monster> getMonsters() {
    return List.copyOf(monsters);
  }

  /**
   * Gets all puzzles in the room.
   *
   * @return A list of puzzles.
   */
  public List<Puzzle> getPuzzles() {
    return List.copyOf(puzzles);
  }

  /**
   * Checks if this room is a safe zone.
   *
   * @return true if the room is a safe zone, false otherwise.
   */
  public boolean isSafeZone() {
    return safeZone;
  }

  /**
   * Checks if this room has been visited.
   *
   * @return true if the room has been visited, false otherwise.
   */
  public boolean isVisited() {
    return visited;
  }

  /** Marks this room as visited. */
  public void setVisited(boolean visited) {
    this.visited = visited;
  }

  /**
   * Checks if the room has any items.
   *
   * @return true if the room contains items, false otherwise.
   */
  public boolean hasItems() {
    return !items.isEmpty();
  }

  /**
   * Checks if the room has any monsters.
   *
   * @return true if the room contains monsters, false otherwise.
   */
  public boolean hasMonsters() {
    return !monsters.isEmpty();
  }

  /**
   * Checks if the room has any puzzles.
   *
   * @return true if the room contains puzzles, false otherwise.
   */
  public boolean hasPuzzles() {
    return !puzzles.isEmpty();
  }

  /**
   * Checks if the room has any exits.
   *
   * @return true if the room has at least one exit, false otherwise.
   */
  public boolean hasExits() {
    return !exits.isEmpty();
  }

  /**
   * Gets the number of available exits.
   *
   * @return The number of exits from this room.
   */
  public int getExitCount() {
    return exits.size();
  }
}
