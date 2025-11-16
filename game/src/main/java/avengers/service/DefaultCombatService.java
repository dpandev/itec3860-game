package avengers.service;

import avengers.domain.model.Monster;
import avengers.domain.model.Player;
import avengers.domain.model.Room;
import avengers.domain.model.World;
import avengers.domain.utils.GameContext;
import java.util.Optional;
import java.util.Random;

/** Default implementation of CombatService with critical hit mechanics. */
public class DefaultCombatService implements CombatService {
  //

  private static final double CRITICAL_HIT_CHANCE = 0.15; // 15% chance
  private static final double CRITICAL_HIT_MULTIPLIER = 1.5; // 1.5x damage
  private final Random random;
  private boolean playerDefending = false;

  /** Constructs DefaultCombatService with default random generator. */
  public DefaultCombatService() {
    this.random = new Random();
  }

  /**
   * Constructs DefaultCombatService with seeded random for deterministic testing.
   *
   * @param seed the random seed
   */
  public DefaultCombatService(long seed) {
    this.random = new Random(seed);
  }

  @Override
  public CombatResult attack(GameContext ctx, String monsterName) {
    if (monsterName == null || monsterName.isBlank()) {
      return CombatResult.ongoing("Attack what? Specify a monster name.");
    }

    World world = ctx.world();
    Player player = ctx.player();

    // Check if already in combat
    if (ctx.isInCombat()) {
      return CombatResult.ongoing("You're already in combat!");
    }

    // Find monster in current room
    Optional<Room> roomOpt = world.findRoom(player.getRoomId());
    if (roomOpt.isEmpty()) {
      return CombatResult.ongoing("Error: Current room not found.");
    }

    Room room = roomOpt.get();

    // Find monster by name
    Optional<Monster> monsterOpt =
        room.getMonsterIds().stream()
            .map(world::findMonster)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(m -> m.getName().equalsIgnoreCase(monsterName))
            .findFirst();

    if (monsterOpt.isEmpty()) {
      return CombatResult.ongoing("There is no '" + monsterName + "' here.");
    }

    Monster monster = monsterOpt.get();

    // Check if monster is already dead
    if (!monster.isAlive()) {
      return CombatResult.ongoing(monster.getName() + " is already defeated.");
    }

    // Start combat
    ctx.startCombat(room.getMonsterIds().get(0)); // Store first matching monster ID

    // Player attacks first
    return performPlayerAttack(ctx, player, monster);
  }

  @Override
  public CombatResult defend(GameContext ctx) {
    if (!ctx.isInCombat()) {
      return CombatResult.ongoing("You're not in combat.");
    }

    playerDefending = true;

    StringBuilder sb = new StringBuilder();
    sb.append("\nYou brace for defense, reducing incoming damage!\n");

    // Monster's turn
    Monster monster = getMonsterInCombat(ctx);
    if (monster != null) {
      CombatResult monsterResult = performMonsterAttack(ctx, ctx.player(), monster);
      sb.append(monsterResult.getMessage());
      playerDefending = false; // Reset defense flag

      return new CombatResult(
          monsterResult.isPlayerAlive(),
          monsterResult.isMonsterAlive(),
          sb.toString(),
          monsterResult.isCombatEnded());
    }

    playerDefending = false;
    return CombatResult.ongoing(sb.toString());
  }

  @Override
  public String ignore(GameContext ctx, String monsterName) {
    if (monsterName == null || monsterName.isBlank()) {
      return "Ignore what? Specify a monster name.";
    }

    World world = ctx.world();
    Player player = ctx.player();

    // Find monster in current room
    Optional<Room> roomOpt = world.findRoom(player.getRoomId());
    if (roomOpt.isEmpty()) {
      return "Error: Current room not found.";
    }

    Room room = roomOpt.get();

    // Find monster by name
    Optional<String> monsterIdOpt =
        room.getMonsterIds().stream()
            .filter(
                id -> {
                  Optional<Monster> m = world.findMonster(id);
                  return m.isPresent() && m.get().getName().equalsIgnoreCase(monsterName);
                })
            .findFirst();

    if (monsterIdOpt.isEmpty()) {
      return "There is no '" + monsterName + "' here.";
    }

    String monsterId = monsterIdOpt.get();
    Optional<Monster> monsterOpt = world.findMonster(monsterId);

    if (monsterOpt.isEmpty()) {
      return "Monster not found.";
    }

    Monster monster = monsterOpt.get();

    // Remove monster from room
    room.removeMonster(monsterId);

    // End combat if this was the monster in combat
    if (ctx.isInCombat() && monsterId.equals(ctx.getCombatMonsterId())) {
      ctx.endCombat();
    }

    return "\nYou ignore the " + monster.getName() + ". It fades away into the shadows.\n";
  }

  @Override
  public CombatResult processCombatRound(GameContext ctx) {
    if (!ctx.isInCombat()) {
      return CombatResult.ongoing("Not in combat.");
    }

    Monster monster = getMonsterInCombat(ctx);
    if (monster == null) {
      ctx.endCombat();
      return CombatResult.monsterDied("The monster has vanished.");
    }

    Player player = ctx.player();
    return performPlayerAttack(ctx, player, monster);
  }

  /**
   * Performs player's attack on monster.
   *
   * @param ctx game context
   * @param player the player
   * @param monster the monster
   * @return combat result
   */
  private CombatResult performPlayerAttack(GameContext ctx, Player player, Monster monster) {
    StringBuilder sb = new StringBuilder();
    sb.append("\n⚔ Combat Round ⚔\n");

    // Calculate player damage
    int baseDamage = player.getBaseAttack();
    boolean isCritical = random.nextDouble() < CRITICAL_HIT_CHANCE;

    int damage = baseDamage;
    if (isCritical) {
      damage = (int) (baseDamage * CRITICAL_HIT_MULTIPLIER);
      sb.append("💥 CRITICAL HIT! ");
    }

    // Apply damage to monster
    monster.takeDamage(damage);
    sb.append("You attack ")
        .append(monster.getName())
        .append(" for ")
        .append(damage)
        .append(" damage!\n");
    sb.append(monster.getName())
        .append(" HP: ")
        .append(monster.getCurrentHealth())
        .append("/")
        .append(monster.getMaxHealth())
        .append("\n");

    // Check if monster died
    if (!monster.isAlive()) {
      sb.append("\n💀 You defeated ").append(monster.getName()).append("!\n");

      // Handle loot
      String loot = handleLoot(ctx, monster);
      if (!loot.isBlank()) {
        sb.append(loot);
      }

      // Remove monster from room
      Optional<Room> roomOpt = ctx.world().findRoom(player.getRoomId());
      roomOpt.ifPresent(room -> room.removeMonster(ctx.getCombatMonsterId()));

      ctx.endCombat();
      return CombatResult.monsterDied(sb.toString());
    }

    // Monster's turn
    CombatResult monsterResult = performMonsterAttack(ctx, player, monster);
    sb.append(monsterResult.getMessage());

    return new CombatResult(
        monsterResult.isPlayerAlive(), true, sb.toString(), monsterResult.isCombatEnded());
  }

  /**
   * Performs monster's attack on player.
   *
   * @param ctx game context
   * @param player the player
   * @param monster the monster
   * @return combat result
   */
  private CombatResult performMonsterAttack(GameContext ctx, Player player, Monster monster) {
    StringBuilder sb = new StringBuilder();

    // Calculate monster damage
    int baseDamage = monster.getBaseAttack();
    boolean isCritical = random.nextDouble() < CRITICAL_HIT_CHANCE;

    int damage = baseDamage;
    if (isCritical) {
      damage = (int) (baseDamage * CRITICAL_HIT_MULTIPLIER);
      sb.append("💥 CRITICAL HIT! ");
    }

    // Apply defense reduction if player is defending
    if (playerDefending) {
      damage = damage / 2;
      sb.append("(Damage reduced by defense) ");
    }

    // Apply damage to player
    player.takeDamage(damage);
    sb.append(monster.getName()).append(" attacks you for ").append(damage).append(" damage!\n");
    sb.append("Your HP: ")
        .append(player.getCurrentHealth())
        .append("/")
        .append(player.getMaxHealth())
        .append("\n");

    // Check if player died
    if (!player.isAlive()) {
      ctx.endCombat();
      sb.append("\n💀 You have been defeated!\n");
      sb.append("\nGAME OVER\n");
      sb.append("Options: 'load' to restore a save, or 'new game' to start fresh.\n");
      return CombatResult.playerDied(sb.toString());
    }

    return CombatResult.ongoing(sb.toString());
  }

  /**
   * Handles loot drops from defeated monster.
   *
   * @param ctx game context
   * @param monster the defeated monster
   * @return loot message
   */
  private String handleLoot(GameContext ctx, Monster monster) {
    // TODO: Implement loot drops based on monster data when available
    // For now, return empty string
    return "";
  }

  /**
   * Gets the monster currently in combat.
   *
   * @param ctx game context
   * @return the monster or null
   */
  private Monster getMonsterInCombat(GameContext ctx) {
    String monsterId = ctx.getCombatMonsterId();
    if (monsterId == null) {
      return null;
    }
    return ctx.world().findMonster(monsterId).orElse(null);
  }
}
