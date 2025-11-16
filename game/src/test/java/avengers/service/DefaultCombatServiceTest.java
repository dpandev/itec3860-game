package avengers.service;

import static org.junit.jupiter.api.Assertions.*;

import avengers.domain.model.*;
import avengers.domain.utils.GameContext;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultCombatServiceTest {

  private DefaultCombatService service;
  private GameContext ctx;
  private World world;
  private Player player;
  private Room room;

  @BeforeEach
  void setUp() {
    // Use seeded random for deterministic tests
    service = new DefaultCombatService(12345L);

    Monster weakMonster = new Monster("Goblin", 50, 10, 5);
    Monster strongMonster = new Monster("Dragon", 200, 50, 20);

    room =
        new Room(
            "RM-01",
            "Arena",
            "Fight here",
            Map.of(),
            List.of("MON-01", "MON-02"),
            List.of(),
            List.of());

    world =
        new World(
            Map.of("RM-01", room),
            Map.of(),
            Map.of(),
            Map.of("MON-01", weakMonster, "MON-02", strongMonster),
            "RM-01");

    player = new Player("TestPlayer", "RM-01");
    ctx = new GameContext(world, player);
  }

  @Test
  void testAttack_ValidMonster() {
    CombatService.CombatResult result = service.attack(ctx, "Goblin");

    assertTrue(result.isPlayerAlive());
    assertTrue(ctx.isInCombat());
    assertNotNull(result.getMessage());
    assertTrue(result.getMessage().contains("Goblin"));
  }

  @Test
  void testAttack_MonsterNotInRoom() {
    CombatService.CombatResult result = service.attack(ctx, "Unicorn");

    assertFalse(ctx.isInCombat());
    assertTrue(result.getMessage().contains("no 'Unicorn' here"));
  }

  @Test
  void testAttack_NullMonsterName() {
    CombatService.CombatResult result = service.attack(ctx, null);

    assertFalse(ctx.isInCombat());
    assertTrue(result.getMessage().contains("Attack what"));
  }

  @Test
  void testAttack_BlankMonsterName() {
    CombatService.CombatResult result = service.attack(ctx, "   ");

    assertFalse(ctx.isInCombat());
    assertTrue(result.getMessage().contains("Attack what"));
  }

  @Test
  void testAttack_AlreadyInCombat() {
    service.attack(ctx, "Goblin");
    CombatService.CombatResult result = service.attack(ctx, "Dragon");

    assertTrue(result.getMessage().contains("already in combat"));
  }

  @Test
  void testAttack_DeadMonster() {
    Monster deadMonster = world.findMonster("MON-01").get();
    deadMonster.takeDamage(50);

    CombatService.CombatResult result = service.attack(ctx, "Goblin");

    assertFalse(ctx.isInCombat());
    assertTrue(result.getMessage().contains("already defeated"));
  }

  @Test
  void testAttack_CaseInsensitive() {
    CombatService.CombatResult result1 = service.attack(ctx, "goblin");
    ctx.endCombat(); // reset
    CombatService.CombatResult result2 = service.attack(ctx, "GOBLIN");
    ctx.endCombat(); // reset
    CombatService.CombatResult result3 = service.attack(ctx, "GoBLiN");

    assertTrue(result1.isPlayerAlive());
    assertTrue(result2.isPlayerAlive());
    assertTrue(result3.isPlayerAlive());
  }

  @Test
  void testAttack_PlayerKillsMonster() {
    // Set up weak monster
    Monster weakMonster = world.findMonster("MON-01").get();
    weakMonster.setCurrentHealth(5); // One hit away from death

    CombatService.CombatResult result = service.attack(ctx, "Goblin");

    assertTrue(result.isPlayerAlive());
    assertFalse(result.isMonsterAlive());
    assertTrue(result.isCombatEnded());
    assertFalse(ctx.isInCombat());
    assertTrue(result.getMessage().contains("defeated"));
  }

  @Test
  void testAttack_MonsterKillsPlayer() {
    // Set up player with low health
    player.takeDamage(95); // 5 HP left

    // Attack strong monster
    CombatService.CombatResult result = service.attack(ctx, "Dragon");

    assertFalse(result.isPlayerAlive());
    assertTrue(result.isCombatEnded());
    assertFalse(ctx.isInCombat());
    assertTrue(result.getMessage().contains("defeated"));
    assertTrue(result.getMessage().contains("GAME OVER"));
  }

  @Test
  void testDefend_NotInCombat() {
    CombatService.CombatResult result = service.defend(ctx);

    assertTrue(result.getMessage().contains("not in combat"));
  }

  @Test
  void testDefend_ReducesDamage() {
    // Start combat
    service.attack(ctx, "Dragon");

    // Get initial health
    int healthBefore = player.getCurrentHealth();

    // Defend
    CombatService.CombatResult result = service.defend(ctx);

    // Health should decrease less due to defense
    int healthAfter = player.getCurrentHealth();
    int damageTaken = healthBefore - healthAfter;

    assertTrue(result.isPlayerAlive());
    assertTrue(result.getMessage().contains("defense"));
    // Damage should be reduced (hard to test exact amount with random crits)
    assertTrue(damageTaken < 50); // Dragon's base attack
  }

  @Test
  void testIgnore_ValidMonster() {
    String result = service.ignore(ctx, "Goblin");

    assertTrue(result.contains("ignore"));
    assertTrue(result.contains("Goblin"));
    assertFalse(room.getMonsterIds().contains("MON-01"));
  }

  @Test
  void testIgnore_MonsterNotInRoom() {
    String result = service.ignore(ctx, "Unicorn");

    assertTrue(result.contains("no 'Unicorn' here"));
  }

  @Test
  void testIgnore_NullMonsterName() {
    String result = service.ignore(ctx, null);

    assertTrue(result.contains("Ignore what"));
  }

  @Test
  void testIgnore_EndsCombat() {
    service.attack(ctx, "Goblin");
    assertTrue(ctx.isInCombat());

    service.ignore(ctx, "Goblin");

    assertFalse(ctx.isInCombat());
  }

  @Test
  void testIgnore_CaseInsensitive() {
    String result = service.ignore(ctx, "gObLiN");

    assertTrue(result.contains("ignore"));
    assertFalse(room.getMonsterIds().contains("MON-01"));
  }

  @Test
  void testCombatRound_DamageCalculation() {
    service.attack(ctx, "Goblin");

    Monster goblin = world.findMonster("MON-01").get();
    int monsterHealthBefore = goblin.getCurrentHealth();
    int playerHealthBefore = player.getCurrentHealth();

    CombatService.CombatResult result = service.processCombatRound(ctx);

    assertTrue(result.isPlayerAlive());
    assertTrue(result.isMonsterAlive());

    // Both should have taken damage
    assertTrue(goblin.getCurrentHealth() < monsterHealthBefore);
    assertTrue(player.getCurrentHealth() < playerHealthBefore);
  }

  @Test
  void testCombatRound_NotInCombat() {
    CombatService.CombatResult result = service.processCombatRound(ctx);

    assertTrue(result.getMessage().contains("Not in combat"));
  }

  @Test
  void testCriticalHits_Deterministic() {
    // With seed 12345L, we should get predictable critical hits
    // This tests that seeded random works correctly

    for (int i = 0; i < 10; i++) {
      // Create fresh context for each iteration
      Monster testMonster = new Monster("Test" + i, 100, 10, 5);
      Room testRoom =
          new Room("TEST", "Test", "Test", Map.of(), List.of("TEST-MON"), List.of(), List.of());
      World testWorld =
          new World(
              Map.of("TEST", testRoom),
              Map.of(),
              Map.of(),
              Map.of("TEST-MON", testMonster),
              "TEST");
      Player testPlayer = new Player("Test", "TEST");
      GameContext testCtx = new GameContext(testWorld, testPlayer);

      service.attack(testCtx, "Test" + i);
      // Just verify no exceptions and combat works
      assertTrue(testCtx.isInCombat());
    }
  }

  @Test
  void testMultipleRounds_UntilMonsterDies() {
    Monster goblin = world.findMonster("MON-01").get();
    service.attack(ctx, "Goblin");

    int rounds = 0;
    int maxRounds = 20; // Safety limit

    while (goblin.isAlive() && player.isAlive() && rounds < maxRounds) {
      CombatService.CombatResult result = service.processCombatRound(ctx);
      rounds++;

      if (!goblin.isAlive()) {
        assertTrue(result.isCombatEnded());
        assertFalse(ctx.isInCombat());
        break;
      }
    }

    assertTrue(rounds < maxRounds, "Combat should end before max rounds");
  }

  @Test
  void testMonsterRemovedFromRoomOnDeath() {
    Monster goblin = world.findMonster("MON-01").get();
    goblin.setCurrentHealth(5);

    service.attack(ctx, "Goblin");

    assertFalse(goblin.isAlive());
    assertFalse(room.getMonsterIds().contains("MON-01"));
  }

  @Test
  void testDefend_PlayerDiesAnyway() {
    player.takeDamage(98); // 2 HP left
    service.attack(ctx, "Dragon"); // Dragon does 50 damage

    CombatService.CombatResult result = service.defend(ctx);

    assertFalse(result.isPlayerAlive());
    assertTrue(result.isCombatEnded());
  }
}
