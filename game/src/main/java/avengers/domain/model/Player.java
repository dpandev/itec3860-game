package avengers.domain.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a player in the game with attributes such as name, current room, inventory items,
 * puzzles solved, and rooms visited.
 */
public final class Player extends Character {
  //
  private String roomId;
  private final List<String> inventoryItems;
  private List<String> puzzlesSolved;
  private final List<String> roomsVisited;
  private final List<String> allies;
  private final Map<EquipmentSlot, String> equippedItems = new HashMap<>();

  /** Enum representing different equipment slots for the player. */
  public enum EquipmentSlot {
    WEAPON,
    HELMET,
    ARMOR,
    OTHER // its an additional slot for equipped items like IT-13, see SRS or game data
  }

  /**
   * Constructs a new Player with the specified name.
   *
   * @param name The name of the player.
   * @param startingRoomId The ID of the starting room for the player.
   */
  public Player(String name, String startingRoomId) {
    super(name, 100);
    this.roomId = startingRoomId;
    this.allies = new ArrayList<String>();
    this.inventoryItems = new ArrayList<String>();
    this.puzzlesSolved = new ArrayList<String>();
    this.roomsVisited = new ArrayList<String>();
    increaseBaseAttack(10);
    increaseBaseDefense(0);
  }

  /**
   * Gets the current room ID for the player.
   *
   * @return The ID of the current room.
   */
  public String getRoomId() {
    return roomId;
  }

  /**
   * Sets the current room ID for the player.
   *
   * @param roomId The ID of the room to set as the current room.
   */
  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }

  /**
   * Gets the list of allies associated with the player.
   *
   * @return A list of ally IDs.
   */
  public List<String> getAllies() {
    return allies;
  }

  /**
   * Gets the list of item IDs in the player's inventory.
   *
   * @return A list of item IDs.
   */
  public List<String> getInventoryItemIds() {
    return inventoryItems;
  }

  /**
   * Adds an item to the player's inventory.
   *
   * @param itemId The ID of the item to add.
   */
  public void addItemToInventory(String itemId) {
    this.inventoryItems.add(itemId);
  }

  /**
   * Removes an item from the player's inventory.
   *
   * @param itemId The ID of the item to remove.
   * @return true if the item was removed, false otherwise.
   */
  public boolean removeItemFromInventory(String itemId) {
    return this.inventoryItems.remove(itemId);
  }

  /**
   * Checks if the player has a specific item in their inventory.
   *
   * @param itemId The ID of the item to check.
   * @return true if the item is in the inventory, false otherwise.
   */
  public boolean hasItemInInventory(String itemId) {
    return this.inventoryItems.contains(itemId);
  }

  /**
   * Gets the list of puzzles solved by the player.
   *
   * @return A list of puzzle IDs that the player has solved.
   */
  public List<String> getPuzzlesSolved() {
    return puzzlesSolved;
  }

  /**
   * Sets the list of puzzles solved by the player.
   *
   * @param puzzlesSolved A list of puzzle IDs that the player has solved.
   */
  public void setPuzzlesSolved(List<String> puzzlesSolved) {
    this.puzzlesSolved = puzzlesSolved;
  }

  /**
   * Gets the list of rooms visited by the player.
   *
   * @return A list of room IDs that the player has visited.
   */
  public List<String> getRoomsVisited() {
    return roomsVisited;
  }

  /**
   * Adds a room to the list of rooms visited by the player.
   *
   * @param roomId The ID of the room to add.
   */
  public void addRoomToRoomsVisited(String roomId) {
    this.roomsVisited.add(roomId);
  }

  /**
   * Gets a map of equipped items by equipment slot.
   *
   * @return A map where the key is the equipment slot and the value is the item ID.
   */
  public Map<EquipmentSlot, String> getEquippedItems() {
    return new HashMap<>(equippedItems);
  }

  /**
   * Checks if the player has an item equipped in the specified equipment slot.
   *
   * @param slot The equipment slot to check.
   * @return true if an item is equipped in that slot, false otherwise.
   */
  public boolean hasEquippedItem(EquipmentSlot slot) {
    return equippedItems.containsKey(slot);
  }

  /**
   * Gets the item ID equipped in the specified equipment slot.
   *
   * @param slot The equipment slot to check.
   * @return The ID of the equipped item, or null if no item is equipped in that slot.
   */
  public String getEquippedItem(EquipmentSlot slot) {
    return equippedItems.get(slot);
  }

  /**
   * Equips an item in the specified equipment slot.
   *
   * @param slot The equipment slot to equip the item in.
   * @param itemId The ID of the item to equip.
   * @return The ID of the previously equipped item in that slot, or null if there was none.
   */
  public String equipItem(EquipmentSlot slot, String itemId) {
    return equippedItems.put(slot, itemId);
  }

  /**
   * Unequips an item from the specified equipment slot.
   *
   * @param slot The equipment slot to unequip the item from.
   * @return The ID of the item that was unequipped, or null if the slot was empty.
   */
  public String unequipItem(EquipmentSlot slot) {
    return equippedItems.remove(slot);
  }
}
