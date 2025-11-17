package avengers.domain.model;

import avengers.domain.utils.StatBonus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a player in the game with attributes such as name, current room, inventory items,
 * puzzles solved, and rooms visited.
 */
public final class Player extends Character {
  private final UUID id;
  private String roomId;
  private final List<String> inventoryItems;
  private List<String> puzzlesSolved;
  private final List<String> roomsVisited;
  private final List<String> allies;
  private final Map<EquipmentSlot, String> equippedItems = new HashMap<>();
  private final List<String> activatedArtifacts;
  private DestinyChoice destinyChoice = DestinyChoice.UNDECIDED;

  /** Enum representing the player's destiny choice from PUZ-12. */
  public enum DestinyChoice {
    UNDECIDED,
    SHADOW_MONARCH,
    HUNTER_KING
  }

  /** Enum representing different equipment slots for the player. */
  public enum EquipmentSlot {
    WEAPON,
    ARMOR,
    ARTIFACT
  }

  /**
   * Constructs a new Player with the specified name.
   *
   * @param name The name of the player.
   * @param startingRoomId The ID of the starting room for the player.
   */
  public Player(String name, String startingRoomId) {
    super(name, 10000);
    this.id = UUID.randomUUID();
    this.roomId = startingRoomId;
    this.allies = new ArrayList<String>();
    this.inventoryItems = new ArrayList<String>();
    this.puzzlesSolved = new ArrayList<String>();
    this.roomsVisited = new ArrayList<String>();
    this.activatedArtifacts = new ArrayList<>();
    increaseBaseAttack(10000);
    increaseBaseDefense(0);
  }

  /**
   * Gets the unique identifier of this player.
   *
   * @return The player ID.
   */
  public UUID getId() {
    return id;
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
   * Gets the list of activated artifact IDs.
   *
   * @return A list of activated artifact IDs.
   */
  public List<String> getActivatedArtifacts() {
    return new ArrayList<>(activatedArtifacts);
  }

  /**
   * Activates an artifact to enable its passive effects.
   *
   * @param artifactId The ID of the artifact to activate.
   * @return true if the artifact was activated, false if already activated.
   */
  public boolean activateArtifact(String artifactId) {
    if (activatedArtifacts.contains(artifactId)) {
      return false; // Already activated
    }
    activatedArtifacts.add(artifactId);
    return true;
  }

  /**
   * Checks if an artifact is activated.
   *
   * @param artifactId The ID of the artifact to check.
   * @return true if the artifact is activated, false otherwise.
   */
  public boolean isArtifactActivated(String artifactId) {
    return activatedArtifacts.contains(artifactId);
  }

  /**
   * Deactivates an artifact, removing its passive effects.
   *
   * @param artifactId The ID of the artifact to deactivate.
   * @return true if the artifact was deactivated, false if not activated.
   */
  public boolean deactivateArtifact(String artifactId) {
    return activatedArtifacts.remove(artifactId);
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

  /**
   * Calculates equipment bonuses for the player based on equipped items. This method requires a
   * World instance to look up item effects.
   *
   * @param world the game world containing item definitions
   * @return StatBonus object containing all equipment bonuses
   */
  public StatBonus calculateEquipmentBonuses(World world) {
    Map<String, Integer> totalBonuses = new HashMap<>();
    Map<String, Double> totalPercentageBonuses = new HashMap<>();

    for (String itemId : equippedItems.values()) {
      if (itemId != null) {
        world
            .findItem(itemId)
            .ifPresent(
                item -> {
                  if (item.hasEffect()) {
                    StatBonus itemBonus = StatBonus.parseEffect(item.getEffect());
                    // Combine flat bonuses
                    itemBonus
                        .getAllBonuses()
                        .forEach((stat, bonus) -> totalBonuses.merge(stat, bonus, Integer::sum));
                    // Combine percentage bonuses
                    itemBonus
                        .getAllPercentageBonuses()
                        .forEach(
                            (stat, bonus) ->
                                totalPercentageBonuses.merge(stat, bonus, Double::sum));
                  }
                });
      }
    }

    return new StatBonus(totalBonuses, totalPercentageBonuses);
  }

  /**
   * Calculates passive bonuses from ACTIVATED artifacts in inventory. Artifacts must be activated
   * using the "activate" command before their passive effects apply (FR-IT-06).
   *
   * @param world the game world containing item definitions
   * @return StatBonus object containing all passive bonuses from activated artifacts
   */
  public StatBonus calculatePassiveInventoryBonuses(World world) {
    Map<String, Integer> totalBonuses = new HashMap<>();
    Map<String, Double> totalPercentageBonuses = new HashMap<>();

    // Only process activated artifacts
    for (String artifactId : activatedArtifacts) {
      if (artifactId != null && inventoryItems.contains(artifactId)) {
        world
            .findItem(artifactId)
            .ifPresent(
                item -> {
                  // Verify it's still an artifact and has effects
                  String category = item.getCategory();
                  if ((category.equalsIgnoreCase("Artifact")
                          || category.equalsIgnoreCase("Key Item"))
                      && item.hasEffect()) {
                    StatBonus itemBonus = StatBonus.parseEffect(item.getEffect());
                    // Combine flat bonuses
                    itemBonus
                        .getAllBonuses()
                        .forEach((stat, bonus) -> totalBonuses.merge(stat, bonus, Integer::sum));
                    // Combine percentage bonuses
                    itemBonus
                        .getAllPercentageBonuses()
                        .forEach(
                            (stat, bonus) ->
                                totalPercentageBonuses.merge(stat, bonus, Double::sum));
                  }
                });
      }
    }

    return new StatBonus(totalBonuses, totalPercentageBonuses);
  }

  /**
   * Gets the total attack including base attack and equipment bonuses.
   *
   * @param world the game world to look up equipment effects
   * @return the total attack value
   */
  public int getTotalAttack(World world) {
    StatBonus equipmentBonus = calculateEquipmentBonuses(world);
    StatBonus passiveBonus = calculatePassiveInventoryBonuses(world);

    int totalAttack =
        getBaseAttack() + equipmentBonus.getAttackBonus() + passiveBonus.getAttackBonus();

    // Apply percentage bonuses from both equipped and passive items
    double percentageBonus =
        equipmentBonus.getPercentageBonus("Attack")
            + equipmentBonus.getPercentageBonus("all stats")
            + passiveBonus.getPercentageBonus("Attack")
            + passiveBonus.getPercentageBonus("all stats");
    if (percentageBonus != 0) {
      totalAttack = (int) Math.round(totalAttack * (1 + percentageBonus / 100.0));
    }

    return totalAttack;
  }

  /**
   * Gets the total defense including base defense and equipment bonuses.
   *
   * @param world the game world to look up equipment effects
   * @return the total defense value
   */
  public int getTotalDefense(World world) {
    StatBonus equipmentBonus = calculateEquipmentBonuses(world);
    StatBonus passiveBonus = calculatePassiveInventoryBonuses(world);

    int totalDefense =
        getBaseDefense() + equipmentBonus.getDefenseBonus() + passiveBonus.getDefenseBonus();

    // Apply percentage bonuses from both equipped and passive items
    double percentageBonus =
        equipmentBonus.getPercentageBonus("Defense")
            + equipmentBonus.getPercentageBonus("all stats")
            + passiveBonus.getPercentageBonus("Defense")
            + passiveBonus.getPercentageBonus("all stats");
    if (percentageBonus != 0) {
      totalDefense = (int) Math.round(totalDefense * (1 + percentageBonus / 100.0));
    }

    return totalDefense;
  }

  /**
   * Gets the player's destiny choice from PUZ-12.
   *
   * @return the destiny choice
   */
  public DestinyChoice getDestinyChoice() {
    return destinyChoice;
  }

  /**
   * Sets the player's destiny choice from PUZ-12.
   *
   * @param choice the destiny choice
   */
  public void setDestinyChoice(DestinyChoice choice) {
    this.destinyChoice = choice;
  }

  /**
   * Gets the total maximum health including base health and equipment bonuses.
   *
   * @param world the game world to look up equipment effects
   * @return the total maximum health value
   */
  public int getTotalMaxHealth(World world) {
    StatBonus equipmentBonus = calculateEquipmentBonuses(world);
    StatBonus passiveBonus = calculatePassiveInventoryBonuses(world);

    int totalMaxHealth =
        getMaxHealth() + equipmentBonus.getHealthBonus() + passiveBonus.getHealthBonus();

    // Apply percentage bonuses from both equipped and passive items
    double percentageBonus =
        equipmentBonus.getPercentageBonus("HP")
            + equipmentBonus.getPercentageBonus("Health")
            + equipmentBonus.getPercentageBonus("all stats")
            + passiveBonus.getPercentageBonus("HP")
            + passiveBonus.getPercentageBonus("Health")
            + passiveBonus.getPercentageBonus("all stats");
    if (percentageBonus != 0) {
      totalMaxHealth = (int) Math.round(totalMaxHealth * (1 + percentageBonus / 100.0));
    }

    return totalMaxHealth;
  }
}
