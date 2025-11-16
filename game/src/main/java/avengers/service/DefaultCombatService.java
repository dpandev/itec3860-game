package avengers.service;

import avengers.domain.model.Monster;
import avengers.domain.model.Player;
import avengers.domain.model.Room;
import avengers.domain.model.World;
import avengers.domain.utils.CommandResult;
import avengers.domain.utils.GameContext;
import java.util.Optional;
import java.util.Random;

/** Default implementation of CombatService for managing combat encounters. */
public class DefaultCombatService implements CombatService {

  private final Random random;
  private static final double CRITICAL_HIT_CHANCE = 0.15; // 15% chance
  private static final double CRITICAL_MULTIPLIER = 1.5;

  /** Constructs a DefaultCombatService with default random generator. */
  public DefaultCombatService() {
    this.random = new Random();
  }

  /**
   * Constructs a DefaultCombatService with seeded random for deterministic testing.
   *
   * @param seed the random seed
   */
  public DefaultCombatService(long seed) {
    this.random = new Random(seed);
  }

  @Override
  public CommandResult startCombat(GameContext ctx, String monsterName) {
    if (ctx.isInCombat()) {
      return CommandResult.fail("You are already in combat!");
    }

    if (monsterName == null || monsterName.isBlank()) {
      return CommandResult.fail(
          "Which creature do you want to attack? Usage: attack <monster name>");
    }

    Player player = ctx.player();
    World world = ctx.world();

    // Find current room
    Optional<Room> roomOpt = world.getRoomById(player.getRoomId());
    if (roomOpt.isEmpty()) {
      return CommandResult.fail("You cannot fight here.");
    }

    Room room = roomOpt.get();

    // Find monster by name in current room
    Optional<Monster> monsterOpt = findMonsterInRoom(world, room, monsterName);
    if (monsterOpt.isEmpty()) {
      return CommandResult.fail("There is no creature called '" + monsterName + "' here.");
    }

    Monster monster = monsterOpt.get();

    if (!monster.isAlive()) {
      return CommandResult.fail("The " + monster.getName() + " is already defeated.");
    }

    // Start combat
    ctx.startCombat(monster.getId());

    return CommandResult.success(
        "You engage the "
            + monster.getName()
            + " in combat!\n"
            + formatCombatStatus(ctx)
            + "\n\nType 'attack' to strike or 'defend' to block.");
  }

  @Override
  public CommandResult playerAttack(GameContext ctx) {
    if (!ctx.isInCombat()) {
      return CommandResult.fail("You are not in combat.");
    }

    Player player = ctx.player();
    World world = ctx.world();

    Optional<Monster> monsterOpt = world.findMonster(ctx.getCombatMonsterId());
    if (monsterOpt.isEmpty()) {
      ctx.endCombat();
      return CommandResult.fail("Combat target no longer exists.");
    }

    Monster monster = monsterOpt.get();

    // Calculate damage
    int baseDamage = player.getTotalAttack(world);
    boolean isCritical = random.nextDouble() < CRITICAL_HIT_CHANCE;
    int damage = isCritical ? (int) (baseDamage * CRITICAL_MULTIPLIER) : baseDamage;

    // Apply damage
    monster.takeDamage(damage);

    StringBuilder result = new StringBuilder();
    result.append("You attack the ").append(monster.getName());
    if (isCritical) {
      result.append(" with a CRITICAL HIT");
    }
    result.append(" for ").append(damage).append(" damage!\n");

    // Check if monster is defeated
    if (!monster.isAlive()) {
      result.append("\nThe ").append(monster.getName()).append(" has been defeated!\n");

      // Handle loot
      String lootMsg = handleLoot(ctx, monster.getId());
      if (!lootMsg.isBlank()) {
        result.append(lootMsg);
      }

      // Remove monster from room
      Optional<Room> roomOpt = world.getRoomById(player.getRoomId());
      roomOpt.ifPresent(room -> room.removeMonster(monster.getId()));

      // End combat
      ctx.endCombat();

      return CommandResult.success(result.toString());
    }

    // Monster counter-attacks
    CommandResult monsterResult = monsterAttack(ctx);
    result.append("\n").append(monsterResult.message());

    return CommandResult.success(result.toString());
  }

  @Override
  public CommandResult monsterAttack(GameContext ctx) {
    if (!ctx.isInCombat()) {
      return CommandResult.fail("");
    }

    Player player = ctx.player();
    World world = ctx.world();

    Optional<Monster> monsterOpt = world.findMonster(ctx.getCombatMonsterId());
    if (monsterOpt.isEmpty()) {
      return CommandResult.success("");
    }

    Monster monster = monsterOpt.get();

    // Calculate damage (reduced by player defense)
    int baseDamage = monster.getBaseAttack();
    int defense = player.getTotalDefense(world);
    int damage = Math.max(1, baseDamage - (defense / 2)); // Defense reduces damage

    // Apply damage to player
    player.takeDamage(damage);

    StringBuilder result = new StringBuilder();
    result
        .append("The ")
        .append(monster.getName())
        .append(" attacks you for ")
        .append(damage)
        .append(" damage!\n");

    // Check if player is defeated
    if (!player.isAlive()) {
      result.append("\n=== GAME OVER ===\n");
      result.append("You have been defeated by the ").append(monster.getName()).append(".\n");
      result.append(
          "Your journey ends here... (Type 'load' to restore or 'new game' to start over)");

      ctx.endCombat();
      return CommandResult.success(result.toString());
    }

    result.append(formatCombatStatus(ctx));

    return CommandResult.success(result.toString());
  }

  @Override
  public CommandResult defend(GameContext ctx) {
    if (!ctx.isInCombat()) {
      return CommandResult.fail("You are not in combat.");
    }

    Player player = ctx.player();
    World world = ctx.world();

    Optional<Monster> monsterOpt = world.findMonster(ctx.getCombatMonsterId());
    if (monsterOpt.isEmpty()) {
      ctx.endCombat();
      return CommandResult.fail("Combat target no longer exists.");
    }

    Monster monster = monsterOpt.get();

    // Defending reduces incoming damage significantly
    int baseDamage = monster.getBaseAttack();
    int defense = player.getTotalDefense(world);
    int reducedDamage = Math.max(0, baseDamage - defense);

    player.takeDamage(reducedDamage);

    StringBuilder result = new StringBuilder();
    result.append("You raise your guard and defend!\n");
    result
        .append("The ")
        .append(monster.getName())
        .append(" attacks, but you block most of the damage (")
        .append(reducedDamage)
        .append(" damage taken).\n\n");

    // Check if player is defeated
    if (!player.isAlive()) {
      result.append("\n=== GAME OVER ===\n");
      result.append("Despite your defense, you have been overwhelmed.\n");
      result.append(
          "Your journey ends here... (Type 'load' to restore or 'new game' to start over)");

      ctx.endCombat();
      return CommandResult.success(result.toString());
    }

    result.append(formatCombatStatus(ctx));

    return CommandResult.success(result.toString());
  }

  @Override
  public CommandResult ignoreMonster(GameContext ctx, String monsterName) {
    if (monsterName == null || monsterName.isBlank()) {
      return CommandResult.fail(
          "Which creature do you want to ignore? Usage: ignore <monster name>");
    }

    Player player = ctx.player();
    World world = ctx.world();

    // Find current room
    Optional<Room> roomOpt = world.getRoomById(player.getRoomId());
    if (roomOpt.isEmpty()) {
      return CommandResult.fail("Cannot find current location.");
    }

    Room room = roomOpt.get();

    // Find monster by name
    Optional<Monster> monsterOpt = findMonsterInRoom(world, room, monsterName);
    if (monsterOpt.isEmpty()) {
      return CommandResult.fail("There is no creature called '" + monsterName + "' here.");
    }

    Monster monster = monsterOpt.get();

    // Remove monster from room
    room.removeMonster(monster.getId());

    return CommandResult.success(
        "You decide to ignore the "
            + monster.getName()
            + ". It wanders away and is no longer a threat.");
  }

  @Override
  public String handleLoot(GameContext ctx, String monsterId) {
    World world = ctx.world();
    Optional<Monster> monsterOpt = world.findMonster(monsterId);

    if (monsterOpt.isEmpty() || monsterOpt.get().getItemDrops().isEmpty()) {
      return "";
    }

    Monster monster = monsterOpt.get();
    StringBuilder loot = new StringBuilder();
    loot.append("\nLoot dropped:\n");

    for (String itemIdOrName : monster.getItemDrops()) {
      // Try to find item by ID or name
      Optional<avengers.domain.model.Item> itemOpt = world.findItem(itemIdOrName);
      if (itemOpt.isPresent()) {
        ctx.player().addItemToInventory(itemOpt.get().getId());
        loot.append("  - ").append(itemOpt.get().getName()).append("\n");
      } else {
        loot.append("  - ").append(itemIdOrName).append("\n");
      }
    }

    return loot.toString();
  }

  /**
   * Finds a monster in the given room by name (case-insensitive).
   *
   * @param world the game world
   * @param room the room to search
   * @param monsterName the monster name to find
   * @return Optional containing the monster if found
   */
  private Optional<Monster> findMonsterInRoom(World world, Room room, String monsterName) {
    return room.getMonsterIds().stream()
        .map(world::findMonster)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(monster -> monster.getName().equalsIgnoreCase(monsterName))
        .findFirst();
  }

  /**
   * Formats combat status display.
   *
   * @param ctx the game context
   * @return formatted combat status
   */
  private String formatCombatStatus(GameContext ctx) {
    Player player = ctx.player();
    World world = ctx.world();

    Optional<Monster> monsterOpt = world.findMonster(ctx.getCombatMonsterId());
    if (monsterOpt.isEmpty()) {
      return "";
    }

    Monster monster = monsterOpt.get();

    return String.format(
        "\n=== Combat Status ===\n" + "Your HP: %d/%d\n" + "%s HP: %d/%d",
        player.getCurrentHealth(),
        player.getTotalMaxHealth(world),
        monster.getName(),
        monster.getCurrentHealth(),
        monster.getMaxHealth());
  }
}
