package avengers.domain.model;

import java.util.List;
import java.util.Map;

/**
 * Represents an immutable room in the game world.
 *
 * <p>Rooms are locations that players can visit, containing exits to other rooms, items to collect,
 * monsters to fight, and puzzles to solve. Each room has a unique identifier and descriptive
 * information.
 */
public final class Room {
  private final String id;
  private final String name;
  private final String description;
  private final Map<Direction, String> exits;
  private final List<String> monsterIds;
  private final List<String> itemIds;
  private final List<String> puzzleIds;

  /**
   * Private constructor used by the Builder.
   *
   * @param builder the builder containing room data
   */
  private Room(Builder builder) {
    if (builder.id == null || builder.id.isBlank()) {
      throw new IllegalArgumentException("Room id cannot be null or blank");
    }
    if (builder.name == null || builder.name.isBlank()) {
      throw new IllegalArgumentException("Room name cannot be null or blank");
    }
    if (builder.description == null || builder.description.isBlank()) {
      throw new IllegalArgumentException("Room description cannot be null or blank");
    }
    if (builder.name.length() > 100) {
      throw new IllegalArgumentException("Room name cannot exceed 100 characters");
    }
    if (builder.description.length() > 500) {
      throw new IllegalArgumentException("Room description cannot exceed 500 characters");
    }

    this.id = builder.id;
    this.name = builder.name;
    this.description = builder.description;
    this.exits = Map.copyOf(builder.exits);
    this.monsterIds = List.copyOf(builder.monsterIds);
    this.itemIds = List.copyOf(builder.itemIds);
    this.puzzleIds = List.copyOf(builder.puzzleIds);
  }

  /**
   * Creates a new Builder for constructing Room instances.
   *
   * @return a new Room.Builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Gets the unique identifier of this room.
   *
   * @return the room ID
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the display name of this room.
   *
   * @return the room name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the description of this room.
   *
   * @return the room description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the exits from this room to other rooms.
   *
   * @return an immutable map of direction to room ID
   */
  public Map<Direction, String> getExits() {
    return exits;
  }

  /**
   * Gets the IDs of monsters in this room.
   *
   * @return an immutable list of monster IDs
   */
  public List<String> getMonsterIds() {
    return monsterIds;
  }

  /**
   * Gets the IDs of items in this room.
   *
   * @return an immutable list of item IDs
   */
  public List<String> getItemIds() {
    return itemIds;
  }

  /**
   * Gets the IDs of puzzles in this room.
   *
   * @return an immutable list of puzzle IDs
   */
  public List<String> getPuzzleIds() {
    return puzzleIds;
  }

  /**
   * Checks if this room has an exit in the specified direction.
   *
   * @param direction the direction to check
   * @return true if an exit exists in that direction, false otherwise
   */
  public boolean hasExit(Direction direction) {
    return exits.containsKey(direction);
  }

  /**
   * Gets the room ID that this room connects to in the specified direction.
   *
   * @param direction the direction to check
   * @return the room ID, or null if no exit exists in that direction
   */
  public String getExitRoomId(Direction direction) {
    return exits.get(direction);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Room room = (Room) obj;
    return id.equals(room.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return String.format(
        "Room{id='%s', name='%s', exits=%d, items=%d, monsters=%d, puzzles=%d}",
        id, name, exits.size(), itemIds.size(), monsterIds.size(), puzzleIds.size());
  }

  /** Builder class for constructing Room instances. */
  public static final class Builder {
    private String id;
    private String name;
    private String description;
    private Map<Direction, String> exits = Map.of();
    private List<String> monsterIds = List.of();
    private List<String> itemIds = List.of();
    private List<String> puzzleIds = List.of();

    private Builder() {}

    /**
     * Sets the room ID.
     *
     * @param id the room ID
     * @return this builder
     */
    public Builder id(String id) {
      this.id = id;
      return this;
    }

    /**
     * Sets the room name.
     *
     * @param name the room name
     * @return this builder
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Sets the room description.
     *
     * @param description the room description
     * @return this builder
     */
    public Builder description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Sets the exits from this room.
     *
     * @param exits map of direction to room ID
     * @return this builder
     */
    public Builder exits(Map<Direction, String> exits) {
      this.exits = exits != null ? exits : Map.of();
      return this;
    }

    /**
     * Sets the monster IDs in this room.
     *
     * @param monsterIds list of monster IDs
     * @return this builder
     */
    public Builder monsterIds(List<String> monsterIds) {
      this.monsterIds = monsterIds != null ? monsterIds : List.of();
      return this;
    }

    /**
     * Sets the item IDs in this room.
     *
     * @param itemIds list of item IDs
     * @return this builder
     */
    public Builder itemIds(List<String> itemIds) {
      this.itemIds = itemIds != null ? itemIds : List.of();
      return this;
    }

    /**
     * Sets the puzzle IDs in this room.
     *
     * @param puzzleIds list of puzzle IDs
     * @return this builder
     */
    public Builder puzzleIds(List<String> puzzleIds) {
      this.puzzleIds = puzzleIds != null ? puzzleIds : List.of();
      return this;
    }

    /**
     * Builds the Room instance.
     *
     * @return a new immutable Room
     * @throws IllegalArgumentException if any required field is invalid
     */
    public Room build() {
      return new Room(this);
    }
  }
}
