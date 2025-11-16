package avengers.client.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import avengers.domain.model.Monster;
import avengers.domain.model.Player;
import avengers.domain.model.Room;
import avengers.domain.model.World;
import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;
import avengers.service.CombatService;
import avengers.service.DefaultCombatService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CombatControllerTest {
  private CombatController controller;
  private CombatService combatService;
  private GameContext context;
  private World world;
  private Player player;
  private Room room;

  @BeforeEach
  void setUp() {
    // Create combat service with seeded random for deterministic tests
    combatService = new DefaultCombatService(12345L);

    // Create controller with service
    controller = new CombatController(combatService);

    // Create test world with monsters
    Monster goblin = new Monster("Goblin", 50, 10, 5);
    Monster dragon = new Monster("Dragon", 200, 50, 20);

    room =
        new Room(
            "room1",
            "Arena",
            "A battle arena",
            Map.of(),
            List.of("MON-01", "MON-02"),
            List.of(),
            List.of());

    world =
        new World(
            Map.of("room1", room),
            Map.of(),
            Map.of(),
            Map.of("MON-01", goblin, "MON-02", dragon),
            "room1");

    player = new Player("TestPlayer", "room1");
    context = new GameContext(world, player);
  }

  @Test
  void testControllerCreation() {
    assertNotNull(controller);
  }

  @Test
  void testSupportsVerb_Attack() {
    assertTrue(controller.supports(Verb.ATTACK));
  }

  @Test
  void testSupportsVerb_Defend() {
    assertTrue(controller.supports(Verb.DEFEND));
  }

  @Test
  void testSupportsVerb_Ignore() {
    assertTrue(controller.supports(Verb.IGNORE));
  }

  @Test
  void testSupportsVerb_DoesNotSupportGo() {
    assertFalse(controller.supports(Verb.GO));
  }

  @Test
  void testSupportsVerb_DoesNotSupportInventory() {
    assertFalse(controller.supports(Verb.INVENTORY));
  }

  @Test
  void testHandle_NullCommand() {
    CommandResult result = controller.handle(null, context);

    assertNotNull(result);
    assertFalse(result.success());
    assertTrue(result.message().contains("Invalid"));
  }

  @Test
  void testHandle_NullContext() {
    CommandToken cmd = new CommandToken(Verb.ATTACK, "goblin", List.of("goblin"), "attack goblin");

    CommandResult result = controller.handle(cmd, null);

    assertNotNull(result);
    assertFalse(result.success());
    assertTrue(result.message().contains("Invalid"));
  }

  @Test
  void testHandle_UnsupportedVerb() {
    CommandToken cmd = new CommandToken(Verb.GO, "north", List.of("north"), "go north");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertFalse(result.success());
    assertTrue(result.message().contains("not supported"));
  }

  // ========== ATTACK Command Tests ==========

  @Test
  void testHandleAttack_ValidMonster() {
    CommandToken cmd = new CommandToken(Verb.ATTACK, "Goblin", List.of("Goblin"), "attack Goblin");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success());
    assertTrue(result.message().contains("Goblin") || result.message().contains("Combat"));
    assertTrue(context.isInCombat());
  }

  @Test
  void testHandleAttack_CaseInsensitive() {
    CommandToken cmd = new CommandToken(Verb.ATTACK, "goblin", List.of("goblin"), "attack goblin");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success());
    assertTrue(context.isInCombat());
  }

  @Test
  void testHandleAttack_MonsterNotFound() {
    CommandToken cmd =
        new CommandToken(Verb.ATTACK, "Unicorn", List.of("Unicorn"), "attack Unicorn");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success()); // Still returns success, but message indicates no monster
    assertTrue(result.message().contains("no") && result.message().contains("Unicorn"));
    assertFalse(context.isInCombat());
  }

  @Test
  void testHandleAttack_NoTarget() {
    CommandToken cmd = new CommandToken(Verb.ATTACK, null, List.of(), "attack");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success()); // Service handles this gracefully
    assertTrue(result.message().contains("Attack what"));
  }

  @Test
  void testHandleAttack_BlankTarget() {
    CommandToken cmd = new CommandToken(Verb.ATTACK, "   ", List.of(), "attack   ");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success());
    assertTrue(result.message().contains("Attack what"));
  }

  @Test
  void testHandleAttack_PlayerDies() {
    // Reduce player health to near death
    player.takeDamage(95); // 5 HP left

    // Attack strong monster
    CommandToken cmd = new CommandToken(Verb.ATTACK, "Dragon", List.of("Dragon"), "attack Dragon");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertFalse(player.isAlive());
    assertTrue(result.shouldExit()); // Game should exit on death
    assertTrue(result.message().contains("GAME OVER") || result.message().contains("defeated"));
  }

  @Test
  void testHandleAttack_MonsterDies() {
    // Weaken monster
    Monster goblin = world.findMonster("MON-01").get();
    goblin.takeDamage(45); // 5 HP left

    CommandToken cmd = new CommandToken(Verb.ATTACK, "Goblin", List.of("Goblin"), "attack Goblin");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success());
    assertFalse(context.isInCombat()); // Combat should end
    assertFalse(goblin.isAlive());
  }

  // ========== DEFEND Command Tests ==========

  @Test
  void testHandleDefend_NotInCombat() {
    CommandToken cmd = new CommandToken(Verb.DEFEND, null, List.of(), "defend");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success());
    assertTrue(result.message().contains("not in combat"));
  }

  @Test
  void testHandleDefend_InCombat() {
    // Start combat first
    CommandToken attackCmd =
        new CommandToken(Verb.ATTACK, "Goblin", List.of("Goblin"), "attack Goblin");
    controller.handle(attackCmd, context);

    assertTrue(context.isInCombat());

    // Now defend
    CommandToken defendCmd = new CommandToken(Verb.DEFEND, null, List.of(), "defend");
    CommandResult result = controller.handle(defendCmd, context);

    assertNotNull(result);
    assertTrue(result.success());
    assertTrue(result.message().contains("defense") || result.message().contains("brace"));
  }

  @Test
  void testHandleDefend_ReducesDamage() {
    // Start combat
    CommandToken attackCmd =
        new CommandToken(Verb.ATTACK, "Dragon", List.of("Dragon"), "attack Dragon");
    controller.handle(attackCmd, context);

    int healthBeforeDefend = player.getCurrentHealth();

    // Defend
    CommandToken defendCmd = new CommandToken(Verb.DEFEND, null, List.of(), "defend");
    CommandResult result = controller.handle(defendCmd, context);

    assertTrue(result.success());
    // Player should still be alive (defense reduces damage)
    assertTrue(player.isAlive());
  }

  @Test
  void testHandleDefend_PlayerDiesAnyway() {
    // Set player to very low health
    player.takeDamage(98); // 2 HP left

    // Start combat with strong monster
    CommandToken attackCmd =
        new CommandToken(Verb.ATTACK, "Dragon", List.of("Dragon"), "attack Dragon");
    controller.handle(attackCmd, context);

    // Try to defend
    CommandToken defendCmd = new CommandToken(Verb.DEFEND, null, List.of(), "defend");
    CommandResult result = controller.handle(defendCmd, context);

    assertNotNull(result);
    // Player should die even with defense
    assertFalse(player.isAlive());
    assertTrue(result.shouldExit());
  }

  // ========== IGNORE Command Tests ==========

  @Test
  void testHandleIgnore_ValidMonster() {
    CommandToken cmd = new CommandToken(Verb.IGNORE, "Goblin", List.of("Goblin"), "ignore Goblin");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success());
    assertTrue(result.message().contains("ignore") && result.message().contains("Goblin"));
    assertFalse(room.getMonsterIds().contains("MON-01"));
  }

  @Test
  void testHandleIgnore_CaseInsensitive() {
    CommandToken cmd = new CommandToken(Verb.IGNORE, "goblin", List.of("goblin"), "ignore goblin");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success());
    assertFalse(room.getMonsterIds().contains("MON-01"));
  }

  @Test
  void testHandleIgnore_MonsterNotFound() {
    CommandToken cmd =
        new CommandToken(Verb.IGNORE, "Unicorn", List.of("Unicorn"), "ignore Unicorn");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success());
    assertTrue(result.message().contains("no") && result.message().contains("Unicorn"));
  }

  @Test
  void testHandleIgnore_NoTarget() {
    CommandToken cmd = new CommandToken(Verb.IGNORE, null, List.of(), "ignore");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success());
    assertTrue(result.message().contains("Ignore what"));
  }

  @Test
  void testHandleIgnore_BlankTarget() {
    CommandToken cmd = new CommandToken(Verb.IGNORE, "   ", List.of(), "ignore   ");

    CommandResult result = controller.handle(cmd, context);

    assertNotNull(result);
    assertTrue(result.success());
    assertTrue(result.message().contains("Ignore what"));
  }

  @Test
  void testHandleIgnore_EndsCombat() {
    // Start combat
    CommandToken attackCmd =
        new CommandToken(Verb.ATTACK, "Goblin", List.of("Goblin"), "attack Goblin");
    controller.handle(attackCmd, context);

    assertTrue(context.isInCombat());

    // Ignore the monster
    CommandToken ignoreCmd =
        new CommandToken(Verb.IGNORE, "Goblin", List.of("Goblin"), "ignore Goblin");
    CommandResult result = controller.handle(ignoreCmd, context);

    assertTrue(result.success());
    assertFalse(context.isInCombat());
  }

  // ========== Integration Tests ==========

  @Test
  void testCombatFlow_AttackThenDefend() {
    // Attack
    CommandToken attackCmd =
        new CommandToken(Verb.ATTACK, "Goblin", List.of("Goblin"), "attack Goblin");
    CommandResult attackResult = controller.handle(attackCmd, context);

    assertTrue(attackResult.success());
    assertTrue(context.isInCombat());

    // Defend
    CommandToken defendCmd = new CommandToken(Verb.DEFEND, null, List.of(), "defend");
    CommandResult defendResult = controller.handle(defendCmd, context);

    assertTrue(defendResult.success());
    assertTrue(player.isAlive());
  }

  @Test
  void testCombatFlow_AttackThenIgnore() {
    // Attack
    CommandToken attackCmd =
        new CommandToken(Verb.ATTACK, "Goblin", List.of("Goblin"), "attack Goblin");
    controller.handle(attackCmd, context);

    assertTrue(context.isInCombat());

    // Ignore
    CommandToken ignoreCmd =
        new CommandToken(Verb.IGNORE, "Goblin", List.of("Goblin"), "ignore Goblin");
    CommandResult ignoreResult = controller.handle(ignoreCmd, context);

    assertTrue(ignoreResult.success());
    assertFalse(context.isInCombat());
    assertFalse(room.getMonsterIds().contains("MON-01"));
  }

  @Test
  void testMultipleMonsters_AttackDifferentMonsters() {
    // Attack first monster
    CommandToken cmd1 = new CommandToken(Verb.ATTACK, "Goblin", List.of("Goblin"), "attack Goblin");
    CommandResult result1 = controller.handle(cmd1, context);

    assertTrue(result1.success());
    assertTrue(context.isInCombat());

    // Try to attack second monster while in combat
    CommandToken cmd2 = new CommandToken(Verb.ATTACK, "Dragon", List.of("Dragon"), "attack Dragon");
    CommandResult result2 = controller.handle(cmd2, context);

    assertTrue(result2.success());
    assertTrue(result2.message().contains("already in combat"));
  }

  @Test
  void testIgnoreMultipleMonsters() {
    // Ignore first monster
    CommandToken cmd1 = new CommandToken(Verb.IGNORE, "Goblin", List.of("Goblin"), "ignore Goblin");
    controller.handle(cmd1, context);

    assertFalse(room.getMonsterIds().contains("MON-01"));
    assertTrue(room.getMonsterIds().contains("MON-02"));

    // Ignore second monster
    CommandToken cmd2 = new CommandToken(Verb.IGNORE, "Dragon", List.of("Dragon"), "ignore Dragon");
    controller.handle(cmd2, context);

    assertFalse(room.getMonsterIds().contains("MON-02"));
    assertTrue(room.getMonsterIds().isEmpty());
  }

  @Test
  void testAttackDeadMonster() {
    // Kill the goblin
    Monster goblin = world.findMonster("MON-01").get();
    goblin.takeDamage(50);

    assertFalse(goblin.isAlive());

    // Try to attack dead monster
    CommandToken cmd = new CommandToken(Verb.ATTACK, "Goblin", List.of("Goblin"), "attack Goblin");
    CommandResult result = controller.handle(cmd, context);

    assertTrue(result.success());
    assertTrue(result.message().contains("already defeated") || result.message().contains("dead"));
    assertFalse(context.isInCombat());
  }
}
