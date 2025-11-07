package avengers.model.item;

import java.util.ArrayList;
import java.util.List;

/** Represents a player's inventory in the game. */
public final class Inventory {
  private final List<Item> items = new ArrayList<>();
  private final int capacity = 20;

  /** * Constructor for Inventory. */
  public Inventory() {}

  /**
   * Lists all items in the inventory.
   *
   * @return A list of items in the inventory.
   */
  public List<Item> listItems() {
    return List.copyOf(items);
  }

  /**
   * Gets the capacity of the inventory.
   *
   * @return The maximum number of items the inventory can hold.
   */
  public int getCapacity() {
    return capacity;
  }

  /**
   * Adds an item to the inventory if there is space.
   *
   * @param item The item to add.
   * @return true if the item was added, false if the inventory is full.
   */
  public boolean addItem(Item item) {
    if (items.size() < capacity) {
      items.add(item);
      return true;
    }
    return false;
  }

  /**
   * Removes an item from the inventory.
   *
   * @param item The item to remove.
   * @return true if the item was removed, false if the item was not found.
   */
  public boolean removeItem(Item item) {
    return items.remove(item);
  }

  /**
   * Checks if the inventory contains a specific item.
   *
   * @param item The item to check for.
   * @return true if the item is in the inventory, false otherwise.
   */
  public boolean containsItem(Item item) {
    return items.contains(item);
  }

  /**
   * Checks if the inventory is full.
   *
   * @return true if the inventory is full, false otherwise.
   */
  public boolean isFull() {
    return items.size() >= capacity;
  }
}
