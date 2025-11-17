package avengers.service;

import avengers.domain.model.Item;
import avengers.domain.model.Monster;
import avengers.domain.model.Player;
import avengers.domain.model.Player.EquipmentSlot;
import avengers.domain.model.Room;
import avengers.domain.model.World;
import avengers.domain.utils.CommandResult;
import avengers.domain.utils.GameContext;
import java.util.Optional;
import java.util.stream.Collectors;

/** Service responsible for managing inventory operations like pickup, drop, and inspect. */
public final class InventoryService {

  /**
   * Displays the player's inventory, showing equipped items first, then carried items.
   *
   * @param ctx the game context containing world and player information
   * @return CommandResult with the inventory display
   */
  public CommandResult showInventory(GameContext ctx) {
    var player = ctx.player();
    var world = ctx.world();
    var output = new StringBuilder();

    output.append("=== INVENTORY ===\n");

    // Show equipped items first
    var equippedItems = player.getEquippedItems();
    if (!equippedItems.isEmpty()) {
      output.append("\n** EQUIPPED ITEMS **\n");
      equippedItems.forEach(
          (slot, itemId) -> {
            var item = world.findItem(itemId);
            if (item.isPresent()) {
              output
                  .append("  ")
                  .append(slot.name())
                  .append(": ")
                  .append(item.get().getName())
                  .append(" - ")
                  .append(item.get().getDescription())
                  .append("\n");
            }
          });
    }

    // Show carried items
    var inventoryItems = player.getInventoryItemIds();
    if (!inventoryItems.isEmpty()) {
      output.append("\n** CARRIED ITEMS **\n");

      // Group items by category for better organization
      var itemsByCategory =
          inventoryItems.stream()
              .map(world::findItem)
              .filter(Optional::isPresent)
              .map(Optional::get)
              .collect(Collectors.groupingBy(Item::getCategory));

      itemsByCategory.forEach(
          (category, items) -> {
            output.append("\n  ").append(category).append(":\n");
            items.forEach(
                item ->
                    output
                        .append("    - ")
                        .append(item.getName())
                        .append(" - ")
                        .append(item.getDescription())
                        .append("\n"));
          });
    }

    if (equippedItems.isEmpty() && inventoryItems.isEmpty()) {
      output.append("\nYour inventory is empty.\n");
    }

    return CommandResult.success(output.toString());
  }

  /**
   * Attempts to pick up an item from the current room.
   *
   * @param ctx the game context
   * @param itemName the name or ID of the item to pick up
   * @return CommandResult indicating success or failure
   */
  public CommandResult pickupItem(GameContext ctx, String itemName) {
    var player = ctx.player();
    var world = ctx.world();
    var currentRoom = world.getRoomById(player.getRoomId());

    if (currentRoom.isEmpty()) {
      return CommandResult.fail("You are not in a valid room.");
    }

    var room = currentRoom.get();
    var itemToPickup = findItemInRoom(world, room, itemName);

    if (itemToPickup.isEmpty()) {
      return CommandResult.fail("There is no item called '" + itemName + "' in this room.");
    }

    var item = itemToPickup.get();

    // Remove item from room and add to player inventory
    room.removeItem(item.getId());
    player.addItemToInventory(item.getId());

    return CommandResult.success(
        "You picked up the " + item.getName() + " (" + item.getCategory() + ").");
  }

  /**
   * Attempts to drop an item from the player's inventory into the current room.
   *
   * @param ctx the game context
   * @param itemName the name or ID of the item to drop
   * @return CommandResult indicating success or failure
   */
  public CommandResult dropItem(GameContext ctx, String itemName) {
    var player = ctx.player();
    var world = ctx.world();
    var currentRoom = world.getRoomById(player.getRoomId());

    if (currentRoom.isEmpty()) {
      return CommandResult.fail("You are not in a valid room.");
    }

    var room = currentRoom.get();
    var itemToDrop = findItemInInventory(world, player, itemName);

    if (itemToDrop.isEmpty()) {
      return CommandResult.fail(
          "You don't have an item called '" + itemName + "' in your inventory.");
    }

    var item = itemToDrop.get();

    // Remove item from player inventory and add to room
    player.removeItemFromInventory(item.getId());
    room.addItem(item.getId());

    return CommandResult.success(
        "You dropped the " + item.getName() + " (" + item.getCategory() + ").");
  }

  /**
   * Inspects an item or monster, showing detailed information.
   *
   * @param ctx the game context
   * @param targetName the name or ID of the item/monster to inspect
   * @return CommandResult with detailed information
   */
  public CommandResult inspectTarget(GameContext ctx, String targetName) {
    var player = ctx.player();
    var world = ctx.world();
    var currentRoom = world.getRoomById(player.getRoomId());

    if (currentRoom.isEmpty()) {
      return CommandResult.fail("You are not in a valid room.");
    }

    var room = currentRoom.get();

    // First, try to find an item (inventory, equipped, or room)
    var item = findItemAnywhere(world, player, room, targetName);
    if (item.isPresent()) {
      return CommandResult.success(formatItemDetails(item.get(), player));
    }

    // If not an item, try to find a monster in the current room
    var monster = findMonsterInRoom(world, room, targetName);
    if (monster.isPresent()) {
      return CommandResult.success(formatMonsterDetails(monster.get()));
    }

    return CommandResult.fail(
        "There is no item or monster called '" + targetName + "' that you can inspect.");
  }

  /**
   * Finds an item in the player's inventory by name or ID.
   *
   * @param world the game world
   * @param player the player
   * @param itemName the name or ID to search for
   * @return Optional containing the item if found
   */
  private Optional<Item> findItemInInventory(World world, Player player, String itemName) {
    return player.getInventoryItemIds().stream()
        .map(world::findItem)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(item -> matchesItemName(item, itemName))
        .findFirst();
  }

  /**
   * Finds an item in a room by name or ID.
   *
   * @param world the game world
   * @param room the room to search
   * @param itemName the name or ID to search for
   * @return Optional containing the item if found
   */
  private Optional<Item> findItemInRoom(World world, Room room, String itemName) {
    return room.getItemIds().stream()
        .map(world::findItem)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(item -> matchesItemName(item, itemName))
        .findFirst();
  }

  /**
   * Finds an item anywhere (inventory, equipped, or current room).
   *
   * @param world the game world
   * @param player the player
   * @param room the current room
   * @param itemName the name or ID to search for
   * @return Optional containing the item if found
   */
  private Optional<Item> findItemAnywhere(World world, Player player, Room room, String itemName) {
    // Check inventory first
    var inventoryItem = findItemInInventory(world, player, itemName);
    if (inventoryItem.isPresent()) {
      return inventoryItem;
    }

    // Check equipped items
    var equippedItem =
        player.getEquippedItems().values().stream()
            .map(world::findItem)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(item -> matchesItemName(item, itemName))
            .findFirst();
    if (equippedItem.isPresent()) {
      return equippedItem;
    }

    // Check current room
    return findItemInRoom(world, room, itemName);
  }

  /**
   * Finds a monster in a room by name or ID.
   *
   * @param world the game world
   * @param room the room to search
   * @param monsterName the name or ID to search for
   * @return Optional containing the monster if found
   */
  private Optional<Monster> findMonsterInRoom(World world, Room room, String monsterName) {
    return room.getMonsterIds().stream()
        .map(world::findMonster)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(monster -> matchesMonsterName(monster, monsterName))
        .findFirst();
  }

  /**
   * Checks if an item matches the given name or ID (case-insensitive).
   *
   * @param item the item to check
   * @param name the name or ID to match
   * @return true if the item matches
   */
  private boolean matchesItemName(Item item, String name) {
    return item.getId().equalsIgnoreCase(name) || item.getName().equalsIgnoreCase(name);
  }

  /**
   * Checks if a monster matches the given name or ID (case-insensitive).
   *
   * @param monster the monster to check
   * @param name the name or ID to match
   * @return true if the monster matches
   */
  private boolean matchesMonsterName(Monster monster, String name) {
    return monster.getId().toString().equalsIgnoreCase(name)
        || monster.getName().equalsIgnoreCase(name);
  }

  /**
   * Formats detailed information about an item for inspection.
   *
   * @param item the item to format
   * @param player the player (to check if item is equipped)
   * @return formatted item details
   */
  private String formatItemDetails(Item item, Player player) {
    var output = new StringBuilder();
    output.append("=== ITEM DETAILS ===\n");
    output.append("Name: ").append(item.getName()).append("\n");
    output.append("ID: ").append(item.getId()).append("\n");
    output.append("Category: ").append(item.getCategory()).append("\n");
    output.append("Description: ").append(item.getDescription()).append("\n");

    if (item.hasEffect()) {
      output.append("Effect: ").append(item.getEffect()).append("\n");
    }

    if (item.hasSpecialEffect()) {
      output.append("Special Effect: ").append(item.getSpecialEffect()).append("\n");
    }

    // Check if item is currently equipped
    var equippedSlot =
        player.getEquippedItems().entrySet().stream()
            .filter(entry -> entry.getValue().equals(item.getId()))
            .map(entry -> entry.getKey())
            .findFirst();

    if (equippedSlot.isPresent()) {
      output.append("Status: EQUIPPED (").append(equippedSlot.get().name()).append(")\n");
    } else if (player.hasItemInInventory(item.getId())) {
      output.append("Status: In inventory\n");
    } else {
      output.append("Status: In current room\n");
    }

    return output.toString();
  }

  /**
   * Formats detailed information about a monster for inspection.
   *
   * @param monster the monster to format
   * @return formatted monster details
   */
  private String formatMonsterDetails(Monster monster) {
    var output = new StringBuilder();
    output.append("=== MONSTER DETAILS ===\n");
    output.append("Name: ").append(monster.getName()).append("\n");
    output.append("Description: ").append(monster.getDescription()).append("\n\n");
    output
        .append("Health: ")
        .append(monster.getCurrentHealth())
        .append("/")
        .append(monster.getMaxHealth())
        .append("\n");
    output.append("Attack: ").append(monster.getBaseAttack()).append("\n");
    output.append("Defense: ").append(monster.getBaseDefense()).append("\n");
    output.append("Status: ").append(monster.isAlive() ? "Alive" : "Defeated").append("\n");

    // Show special effects if any
    if (monster.getSpecialEffects() != null && !monster.getSpecialEffects().isEmpty()) {
      output.append("\nSpecial Abilities:\n");
      for (String effect : monster.getSpecialEffects()) {
        output.append("  - ").append(effect).append("\n");
      }
    }

    return output.toString();
  }

  /**
   * Attempts to equip an item from the player's inventory.
   *
   * @param ctx the game context
   * @param itemName the name or ID of the item to equip
   * @return CommandResult indicating success or failure
   */
  public CommandResult equipItem(GameContext ctx, String itemName) {
    var player = ctx.player();
    var world = ctx.world();

    // Find the item in player's inventory
    var itemToEquip = findItemInInventory(world, player, itemName);
    if (itemToEquip.isEmpty()) {
      return CommandResult.fail(
          "You don't have an item called '" + itemName + "' in your inventory.");
    }

    var item = itemToEquip.get();

    // Check if item is equippable
    if (!item.isEquippable()) {
      return CommandResult.fail("Item cannot be equipped.");
    }

    // Determine equipment slot based on category
    EquipmentSlot slot = determineEquipmentSlot(item);
    if (slot == null) {
      return CommandResult.fail("Item cannot be equipped to any slot.");
    }

    // Check if there's already an item in that slot
    String previousItemId = null;
    if (player.hasEquippedItem(slot)) {
      previousItemId = player.getEquippedItem(slot);
    }

    // Remove item from inventory and equip it
    player.removeItemFromInventory(item.getId());
    player.equipItem(slot, item.getId());

    // If there was a previous item, return it to inventory
    if (previousItemId != null) {
      player.addItemToInventory(previousItemId);
      var previousItem = world.findItem(previousItemId);
      String previousItemName =
          previousItem.isPresent() ? previousItem.get().getName() : previousItemId;
      return CommandResult.success(
          "You equipped the "
              + item.getName()
              + " ("
              + slot.name()
              + "). The "
              + previousItemName
              + " was returned to your inventory.");
    } else {
      return CommandResult.success(
          "You equipped the " + item.getName() + " (" + slot.name() + ").");
    }
  }

  /**
   * Attempts to unequip an item from the specified equipment slot.
   *
   * @param ctx the game context
   * @param slotName the name of the equipment slot to unequip from
   * @return CommandResult indicating success or failure
   */
  public CommandResult unequipItem(GameContext ctx, String slotName) {
    var player = ctx.player();

    // Parse equipment slot
    EquipmentSlot slot;
    try {
      slot = EquipmentSlot.valueOf(slotName.toUpperCase());
    } catch (IllegalArgumentException e) {
      return CommandResult.fail(
          "Invalid equipment slot: " + slotName + ". Valid slots are: WEAPON, ARMOR, ARTIFACT.");
    }

    // Check if there's an item in that slot
    if (!player.hasEquippedItem(slot)) {
      return CommandResult.fail("No item is equipped in the " + slot.name() + " slot.");
    }

    // Unequip the item and return it to inventory
    String itemId = player.unequipItem(slot);
    player.addItemToInventory(itemId);

    var world = ctx.world();
    var item = world.findItem(itemId);
    String itemName = item.isPresent() ? item.get().getName() : itemId;

    return CommandResult.success(
        "You unequipped the " + itemName + " from the " + slot.name() + " slot.");
  }

  /**
   * Attempts to use a consumable item from the player's inventory.
   *
   * @param ctx the game context
   * @param itemName the name or ID of the item to use
   * @return CommandResult indicating success or failure
   */
  public CommandResult useItem(GameContext ctx, String itemName) {
    var player = ctx.player();
    var world = ctx.world();

    // Find the item in player's inventory
    var itemToUse = findItemInInventory(world, player, itemName);
    if (itemToUse.isEmpty()) {
      return CommandResult.fail(
          "You don't have an item called '" + itemName + "' in your inventory.");
    }

    var item = itemToUse.get();

    // Check if item is consumable
    if (!item.getCategory().equalsIgnoreCase("Consumable")) {
      return CommandResult.fail(
          "The " + item.getName() + " cannot be consumed. Only Consumable items can be used.");
    }

    // Parse the effect (e.g., "+20 HP")
    if (!item.hasEffect() || item.getEffect().isBlank()) {
      return CommandResult.fail("The " + item.getName() + " has no effect.");
    }

    String effect = item.getEffect().trim();

    // Apply the effect
    StringBuilder result = new StringBuilder();
    result.append("You use the ").append(item.getName()).append(".\n");

    // Parse HP restoration effects
    if (effect.matches(".*\\+\\d+\\s*HP.*")) {
      // Extract the HP value (e.g., "+20 HP" -> 20)
      String[] parts = effect.split("\\+");
      if (parts.length > 1) {
        String hpPart = parts[1].replaceAll("[^0-9]", "");
        try {
          int hpRestore = Integer.parseInt(hpPart);
          int currentHp = player.getCurrentHealth();
          int maxHp = player.getTotalMaxHealth(world);

          // Heal the player
          int actualRestore = Math.min(hpRestore, maxHp - currentHp);
          player.heal(actualRestore);

          result
              .append("Restored ")
              .append(actualRestore)
              .append(" HP! (")
              .append(player.getCurrentHealth())
              .append("/")
              .append(maxHp)
              .append(")");
        } catch (NumberFormatException e) {
          result.append("Effect: ").append(effect);
        }
      }
    } else {
      // Generic effect message for other consumables
      result.append("Effect: ").append(effect);
    }

    // Remove the item from inventory after use
    player.removeItemFromInventory(item.getId());

    return CommandResult.success(result.toString());
  }

  /**
   * Attempts to activate an artifact from the player's inventory to enable passive effects.
   *
   * @param ctx the game context
   * @param itemName the name or ID of the artifact to activate
   * @return CommandResult indicating success or failure
   */
  public CommandResult activateArtifact(GameContext ctx, String itemName) {
    var player = ctx.player();
    var world = ctx.world();

    // Find the item in player's inventory
    var itemToActivate = findItemInInventory(world, player, itemName);
    if (itemToActivate.isEmpty()) {
      return CommandResult.fail(
          "You don't have an item called '" + itemName + "' in your inventory.");
    }

    var item = itemToActivate.get();

    // Check if item is an artifact or key item
    String category = item.getCategory();
    if (!category.equalsIgnoreCase("Artifact") && !category.equalsIgnoreCase("Key Item")) {
      return CommandResult.fail(
          "Only artifacts and key items can be activated. "
              + item.getName()
              + " is a "
              + category
              + ".");
    }

    // Check if artifact has any effects
    if (!item.hasEffect() || item.getEffect().isBlank()) {
      return CommandResult.fail(item.getName() + " has no passive effects to activate.");
    }

    // Try to activate the artifact
    boolean activated = player.activateArtifact(item.getId());
    if (!activated) {
      return CommandResult.fail(item.getName() + " is already activated.");
    }

    // Build success message with effect details
    StringBuilder message = new StringBuilder();
    message.append("You activate the ").append(item.getName()).append("!\n\n");
    message.append("Passive Effect Applied: ").append(item.getEffect()).append("\n");

    if (item.hasSpecialEffect() && !item.getSpecialEffect().isBlank()) {
      message.append("Special Effect: ").append(item.getSpecialEffect()).append("\n");
    }

    message.append("\nYour stats have been updated with the artifact's bonuses.");

    return CommandResult.success(message.toString());
  }

  /**
   * Determines the appropriate equipment slot for an item based on its category.
   *
   * @param item the item to determine the slot for
   * @return the appropriate EquipmentSlot, or null if the item cannot be equipped
   */
  private EquipmentSlot determineEquipmentSlot(Item item) {
    String category = item.getCategory();
    return switch (category.toLowerCase()) {
      case "weapon" -> EquipmentSlot.WEAPON;
      case "armor" -> EquipmentSlot.ARMOR;
      case "artifact" -> EquipmentSlot.ARTIFACT;
      default -> null;
    };
  }
}
