package avengers.model.combat;

import static org.junit.jupiter.api.Assertions.*;

import avengers.model.Element;
import avengers.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test class for Effect. */
class EffectTest {

  private Player player;

  @BeforeEach
  void setUp() {
    player = new Player("TestPlayer", "A test player", 100, 100, 20, 10);
  }

  @Test
  void testApplyStatusEffect() {
    Effect effect = Effect.applyStatus(EffectTiming.ON_HIT, StatusType.BURN, 3, 5);

    assertEquals(EffectKind.APPLY_STATUS, effect.getKind());
    assertEquals(EffectTiming.ON_HIT, effect.getTiming());
    assertEquals(StatusType.BURN, effect.getStatusType());
    assertEquals(3, effect.getStatusDuration());
    assertEquals(5, effect.getValue());
  }

  @Test
  void testApplyStatusToCharacter() {
    Effect effect = Effect.applyStatus(EffectTiming.ON_HIT, StatusType.POISON, 2, 3);

    assertEquals(0, player.getEffects().size());

    effect.applyTo(player);

    assertEquals(1, player.getEffects().size());
    StatusInstance status = player.getEffects().get(0);
    assertEquals(StatusType.POISON, status.getType());
    assertEquals(2, status.getRemainingTurns());
    assertEquals(3, status.getEffectAmount());
  }

  @Test
  void testExtraStrikesEffect() {
    Effect effect = Effect.extraStrikes(EffectTiming.ON_TURN, 2);

    assertEquals(EffectKind.EXTRA_STRIKES, effect.getKind());
    assertEquals(EffectTiming.ON_TURN, effect.getTiming());
    assertEquals(2, effect.getValue());
    assertEquals(2, effect.getExtraStrikes());
  }

  @Test
  void testStrikeBonusEffect() {
    Effect effect = Effect.strikeBonus(EffectTiming.ON_HIT, 10, Element.FIRE);

    assertEquals(EffectKind.STRIKE_BONUS, effect.getKind());
    assertEquals(EffectTiming.ON_HIT, effect.getTiming());
    assertEquals(10, effect.getValue());
    assertEquals(Element.FIRE, effect.getElement());
  }

  @Test
  void testStrikeBonusApplication() {
    Effect effect = Effect.strikeBonus(EffectTiming.ON_HIT, 15, Element.SHADOW);

    int baseDamage = 20;
    int modifiedDamage = effect.applyStrikeBonus(baseDamage);

    assertEquals(35, modifiedDamage);
  }

  @Test
  void testStrikeBonusNotAppliedToWrongEffectKind() {
    Effect effect = Effect.extraStrikes(EffectTiming.ON_TURN, 2);

    int baseDamage = 20;
    int modifiedDamage = effect.applyStrikeBonus(baseDamage);

    assertEquals(20, modifiedDamage); // No change
  }

  @Test
  void testMaxHpBonusEffect() {
    Effect effect = Effect.maxHpBonus(EffectTiming.ON_EQUIP, 50);

    assertEquals(EffectKind.MAX_HP_BONUS, effect.getKind());
    assertEquals(EffectTiming.ON_EQUIP, effect.getTiming());
    assertEquals(50, effect.getValue());
  }

  @Test
  void testDamageReductionEffect() {
    Effect effect = Effect.damageReduction(EffectTiming.PASSIVE, 25);

    assertEquals(EffectKind.DAMAGE_REDUCTION, effect.getKind());
    assertEquals(EffectTiming.PASSIVE, effect.getTiming());
    assertEquals(25, effect.getValue());
  }

  @Test
  void testDamageReductionApplication() {
    Effect effect = Effect.damageReduction(EffectTiming.PASSIVE, 50);

    int incomingDamage = 100;
    int reducedDamage = effect.applyDamageReduction(incomingDamage);

    assertEquals(50, reducedDamage); // 50% reduction
  }

  @Test
  void testDamageReductionPartial() {
    Effect effect = Effect.damageReduction(EffectTiming.PASSIVE, 30);

    int incomingDamage = 100;
    int reducedDamage = effect.applyDamageReduction(incomingDamage);

    assertEquals(70, reducedDamage); // 30% reduction
  }

  @Test
  void testDamageReductionNotAppliedToWrongEffectKind() {
    Effect effect = Effect.strikeBonus(EffectTiming.ON_HIT, 10, null);

    int incomingDamage = 100;
    int reducedDamage = effect.applyDamageReduction(incomingDamage);

    assertEquals(100, reducedDamage); // No change
  }

  @Test
  void testShouldTriggerOnCorrectTiming() {
    Effect effect = Effect.strikeBonus(EffectTiming.ON_HIT, 10, null);

    assertTrue(effect.shouldTrigger(EffectTiming.ON_HIT));
    assertFalse(effect.shouldTrigger(EffectTiming.ON_TURN));
    assertFalse(effect.shouldTrigger(EffectTiming.PASSIVE));
  }

  @Test
  void testPassiveEffectTiming() {
    Effect effect = Effect.damageReduction(EffectTiming.PASSIVE, 20);

    assertTrue(effect.shouldTrigger(EffectTiming.PASSIVE));
    assertFalse(effect.shouldTrigger(EffectTiming.ON_HIT));
  }

  @Test
  void testOnEquipTiming() {
    Effect effect = Effect.maxHpBonus(EffectTiming.ON_EQUIP, 30);

    assertTrue(effect.shouldTrigger(EffectTiming.ON_EQUIP));
    assertFalse(effect.shouldTrigger(EffectTiming.ON_TURN));
  }

  @Test
  void testOnTurnTiming() {
    Effect effect = Effect.extraStrikes(EffectTiming.ON_TURN, 1);

    assertTrue(effect.shouldTrigger(EffectTiming.ON_TURN));
    assertFalse(effect.shouldTrigger(EffectTiming.ON_TURN_END));
  }

  @Test
  void testOnTurnEndTiming() {
    Effect effect = Effect.applyStatus(EffectTiming.ON_TURN_END, StatusType.BLEED, 2, 5);

    assertTrue(effect.shouldTrigger(EffectTiming.ON_TURN_END));
    assertFalse(effect.shouldTrigger(EffectTiming.ON_TURN));
  }

  @Test
  void testEffectEquality() {
    Effect effect1 = Effect.strikeBonus(EffectTiming.ON_HIT, 10, Element.FIRE);
    Effect effect2 = Effect.strikeBonus(EffectTiming.ON_HIT, 10, Element.FIRE);
    Effect effect3 = Effect.strikeBonus(EffectTiming.ON_HIT, 15, Element.FIRE);

    assertEquals(effect1, effect2);
    assertNotEquals(effect1, effect3);
  }

  @Test
  void testEffectHashCode() {
    Effect effect1 = Effect.strikeBonus(EffectTiming.ON_HIT, 10, Element.FIRE);
    Effect effect2 = Effect.strikeBonus(EffectTiming.ON_HIT, 10, Element.FIRE);

    assertEquals(effect1.hashCode(), effect2.hashCode());
  }

  @Test
  void testEffectToString() {
    Effect effect = Effect.strikeBonus(EffectTiming.ON_HIT, 10, Element.FIRE);

    String str = effect.toString();

    assertTrue(str.contains("STRIKE_BONUS"));
    assertTrue(str.contains("ON_HIT"));
    assertTrue(str.contains("10"));
    assertTrue(str.contains("FIRE"));
  }

  @Test
  void testMultipleStatusApplications() {
    Effect burn = Effect.applyStatus(EffectTiming.ON_HIT, StatusType.BURN, 3, 5);
    Effect poison = Effect.applyStatus(EffectTiming.ON_HIT, StatusType.POISON, 2, 3);

    burn.applyTo(player);
    poison.applyTo(player);

    assertEquals(2, player.getEffects().size());
  }

  @Test
  void testGetExtraStrikesReturnsZeroForNonExtraStrikesEffect() {
    Effect effect = Effect.strikeBonus(EffectTiming.ON_HIT, 10, null);

    assertEquals(0, effect.getExtraStrikes());
  }

  @Test
  void testNullElementAllowed() {
    Effect effect = Effect.strikeBonus(EffectTiming.ON_HIT, 10, null);

    assertNull(effect.getElement());
    assertEquals(10, effect.getValue());
  }

  @Test
  void testComplexEffectChain() {
    // Simulate an item with multiple effects
    Effect effect1 = Effect.strikeBonus(EffectTiming.ON_HIT, 5, Element.FIRE);
    Effect effect2 = Effect.applyStatus(EffectTiming.ON_HIT, StatusType.BURN, 2, 3);
    Effect effect3 = Effect.damageReduction(EffectTiming.PASSIVE, 10);

    // Apply strike bonus
    int damage = 20;
    damage = effect1.applyStrikeBonus(damage);
    assertEquals(25, damage);

    // Apply status
    effect2.applyTo(player);
    assertEquals(1, player.getEffects().size());

    // Apply damage reduction
    int incoming = 50;
    incoming = effect3.applyDamageReduction(incoming);
    assertEquals(45, incoming);
  }

  @Test
  void testZeroValueEffects() {
    Effect effect = Effect.strikeBonus(EffectTiming.ON_HIT, 0, null);

    assertEquals(0, effect.getValue());
    assertEquals(20, effect.applyStrikeBonus(20));
  }

  @Test
  void testHighDamageReduction() {
    Effect effect = Effect.damageReduction(EffectTiming.PASSIVE, 90);

    int reduced = effect.applyDamageReduction(100);
    assertEquals(10, reduced); // 90% reduction
  }

  @Test
  void testFullDamageReduction() {
    Effect effect = Effect.damageReduction(EffectTiming.PASSIVE, 100);

    int reduced = effect.applyDamageReduction(100);
    assertEquals(0, reduced); // 100% reduction
  }
}
