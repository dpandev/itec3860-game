package avengers.service;

import avengers.domain.model.Item;
import avengers.domain.model.Player;
import avengers.domain.model.Room;
import avengers.domain.model.World;
import avengers.domain.utils.CommandResult;
import avengers.domain.utils.GameContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Default implementation of ExplorationService for handling exploration-related operations. */
public class DefaultExplorationService implements ExplorationService {

  // Direction aliases for canonical direction mapping
  private static final Map<String, String> DIRECTION_ALIASES = new HashMap<>();

  static {
    DIRECTION_ALIASES.put("n", "north");
    DIRECTION_ALIASES.put("s", "south");
    DIRECTION_ALIASES.put("e", "east");
    DIRECTION_ALIASES.put("w", "west");
    DIRECTION_ALIASES.put("north", "north");
    DIRECTION_ALIASES.put("south", "south");
    DIRECTION_ALIASES.put("east", "east");
    DIRECTION_ALIASES.put("west", "west");
  }

  private final InteractionService interactionService;

  /** Constructs a DefaultExplorationService with a new InteractionService instance. */
  public DefaultExplorationService() {
    this.interactionService = new DefaultInteractionService();
  }

  /**
   * Constructs a DefaultExplorationService with the specified InteractionService.
   *
   * @param interactionService the interaction service to use
   */
  public DefaultExplorationService(InteractionService interactionService) {
    this.interactionService = interactionService;
  }

  @Override
  public String showStats(GameContext ctx) {
    Player player = ctx.player();
    World world = ctx.world();

    StringBuilder stats = new StringBuilder();
    stats.append("=== Player Stats ===\n");

    // Health display
    stats.append(
        String.format(
            "Health: %d / %d\n", player.getCurrentHealth(), player.getTotalMaxHealth(world)));

    // Attack display
    int baseAttack = player.getBaseAttack();
    int totalAttack = player.getTotalAttack(world);
    int attackBonus = totalAttack - baseAttack;
    if (attackBonus > 0) {
      stats.append(
          String.format(
              "Attack: %d (+%d from equipment) = %d total\n",
              baseAttack, attackBonus, totalAttack));
    } else {
      stats.append(String.format("Attack: %d\n", baseAttack));
    }

    // Defense display
    int baseDefense = player.getBaseDefense();
    int totalDefense = player.getTotalDefense(world);
    int defenseBonus = totalDefense - baseDefense;
    if (defenseBonus > 0) {
      stats.append(
          String.format(
              "Defense: %d (+%d from equipment) = %d total\n",
              baseDefense, defenseBonus, totalDefense));
    } else {
      stats.append(String.format("Defense: %d\n", baseDefense));
    }

    stats.append("\nEquipped Items:\n");

    // Display equipped items
    boolean hasEquippedItems = false;
    for (Player.EquipmentSlot slot : Player.EquipmentSlot.values()) {
      String itemId = player.getEquippedItem(slot);
      String itemName = "(none)";

      if (itemId != null) {
        Item item = world.findItem(itemId).orElse(null);
        if (item != null) {
          itemName = item.getName();
          hasEquippedItems = true;
        }
      }

      stats.append(String.format("- %s: %s\n", formatSlotName(slot), itemName));
    }

    if (!hasEquippedItems) {
      stats.append("  No items equipped\n");
    }

    return stats.toString();
  }

  @Override
  public CommandResult explore(GameContext ctx) {
    String description = describeCurrentRoom(ctx);

    // Check if current room has a puzzle and present it if LOCKED
    Player player = ctx.player();
    Room currentRoom = ctx.world().getRoomById(player.getRoomId()).orElse(null);

    if (currentRoom != null && !currentRoom.getPuzzleIds().isEmpty()) {
      // Present puzzle if any exists and is in LOCKED state
      CommandResult puzzleResult = interactionService.checkAndPresentPuzzle(ctx);
      if (puzzleResult != null) {
        return CommandResult.success(description + "\n\n" + puzzleResult.message());
      }
    }

    return CommandResult.success(description);
  }

  @Override
  public String describeCurrentRoom(GameContext ctx) {
    Player player = ctx.player();
    World world = ctx.world();

    Optional<Room> roomOpt = world.getRoomById(player.getRoomId());
    if (roomOpt.isEmpty()) {
      return "You are in an unknown location.";
    }

    Room room = roomOpt.get();
    StringBuilder description = new StringBuilder();

    // Room name and description
    description.append("=== ").append(room.getName()).append(" ===\n");
    description.append(room.getDescription()).append("\n\n");

    // Items in room
    if (room.hasItems()) {
      description.append("Items here:\n");
      for (String itemId : room.getItemIds()) {
        world
            .findItem(itemId)
            .ifPresent(item -> description.append("  - ").append(item.getName()).append("\n"));
      }
      description.append("\n");
    }

    // Monsters in room
    if (room.hasMonsters()) {
      description.append("Creatures:\n");
      for (String monsterId : room.getMonsterIds()) {
        world
            .findMonster(monsterId)
            .ifPresent(
                monster -> {
                  if (monster.isAlive()) {
                    description
                        .append("  - ")
                        .append(monster.getName())
                        .append(" (HP: ")
                        .append(monster.getCurrentHealth())
                        .append("/")
                        .append(monster.getMaxHealth())
                        .append(")\n");
                  }
                });
      }
      description.append("\n");
    }

    // Available exits
    description.append("Exits: ");
    Map<String, String> exits = room.getExits();
    if (exits != null && !exits.isEmpty()) {
      description.append(String.join(", ", exits.keySet()));
    } else {
      description.append("None");
    }

    // Mark room as visited
    if (!player.getRoomsVisited().contains(room.getId())) {
      player.addRoomToRoomsVisited(room.getId());
    }

    return description.toString();
  }

  @Override
  public CommandResult move(GameContext ctx, String direction) {
    if (direction == null || direction.isBlank()) {
      return CommandResult.fail("Which direction do you want to go? (north, south, east, west)");
    }

    // Normalize direction using aliases
    String normalizedDirection = DIRECTION_ALIASES.get(direction.toLowerCase());
    if (normalizedDirection == null) {
      return CommandResult.fail(
          "Invalid direction: '"
              + direction
              + "'. Use: north (n), south (s), east (e), or west (w)");
    }

    Player player = ctx.player();
    World world = ctx.world();

    // Get current room
    Optional<Room> currentRoomOpt = world.getRoomById(player.getRoomId());
    if (currentRoomOpt.isEmpty()) {
      return CommandResult.fail("You cannot move from your current location.");
    }

    Room currentRoom = currentRoomOpt.get();

    // Check if exit exists in specified direction
    Map<String, String> exits = currentRoom.getExits();
    if (exits == null || !exits.containsKey(normalizedDirection)) {
      return CommandResult.fail("There is no exit to the " + normalizedDirection + ".");
    }

    // Get destination room ID
    String destinationRoomId = exits.get(normalizedDirection);
    Optional<Room> destinationRoomOpt = world.getRoomById(destinationRoomId);
    if (destinationRoomOpt.isEmpty()) {
      return CommandResult.fail("The path to the " + normalizedDirection + " is blocked.");
    }

    // Reset puzzle state if leaving a puzzle room with in-progress puzzle
    if (ctx.isAwaitingPuzzleAnswer()) {
      ctx.setAwaitingPuzzleAnswer(false);
      // Note: Puzzle state reset is handled by InteractionService
    }

    // Move player to new room
    player.setRoomId(destinationRoomId);

    // Get description of new room
    String newRoomDescription = describeCurrentRoom(ctx);

    // Check for puzzles in new room
    Room newRoom = destinationRoomOpt.get();
    if (!newRoom.getPuzzleIds().isEmpty()) {
      CommandResult puzzleResult = interactionService.checkAndPresentPuzzle(ctx);
      if (puzzleResult != null) {
        return CommandResult.success(
            "You move "
                + normalizedDirection
                + ".\n\n"
                + newRoomDescription
                + "\n\n"
                + puzzleResult.message());
      }
    }

    return CommandResult.success("You move " + normalizedDirection + ".\n\n" + newRoomDescription);
  }

  /**
   * Formats the equipment slot name for display.
   *
   * @param slot the equipment slot
   * @return formatted slot name
   */
  private String formatSlotName(Player.EquipmentSlot slot) {
    return switch (slot) {
      case WEAPON -> "Weapon";
      case ARMOR -> "Armor";
      case ARTIFACT -> "Artifact";
    };
  }
}
