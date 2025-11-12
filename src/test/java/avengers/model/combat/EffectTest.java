package avengers.model.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Test class for Effect. */
class EffectTest {

  @Test
  void testImmediateEffectCreation() {
    Effect effect = new Effect(EffectKind.DAMAGE_BONUS, EffectTiming.PASSIVE, 25.0);

    assertNotNull(effect);
    assertEquals(EffectKind.DAMAGE_BONUS, effect.getKind());
    assertEquals(EffectTiming.PASSIVE, effect.getTiming());
    assertEquals(25.0, effect.getValue());
    assertNull(effect.getStatusType());
    assertEquals(0, effect.getStatusDuration());
    assertEquals(0, effect.getStatusAmount());
    assertFalse(effect.isStatusEffect());
  }

  @Test
  void testStatusEffectCreation() {
    Effect effect = new Effect(EffectTiming.ON_HIT, StatusType.POISON, 3, 5);

    assertNotNull(effect);
    assertEquals(EffectKind.APPLY_STATUS, effect.getKind());
    assertEquals(EffectTiming.ON_HIT, effect.getTiming());
    assertEquals(StatusType.POISON, effect.getStatusType());
    assertEquals(3, effect.getStatusDuration());
    assertEquals(5, effect.getStatusAmount());
    assertTrue(effect.isStatusEffect());
  }

  @Test
  void testDamageBonusEffect() {
    Effect effect = new Effect(EffectKind.DAMAGE_BONUS, EffectTiming.PASSIVE, 40.0);

    assertEquals(EffectKind.DAMAGE_BONUS, effect.getKind());
    assertEquals(40.0, effect.getValue());
    assertFalse(effect.isStatusEffect());
  }

  @Test
  void testMaxHpBonusEffect() {
    Effect effect = new Effect(EffectKind.MAX_HP_BONUS, EffectTiming.ON_EQUIP, 50.0);

    assertEquals(EffectKind.MAX_HP_BONUS, effect.getKind());
    assertEquals(EffectTiming.ON_EQUIP, effect.getTiming());
    assertEquals(50.0, effect.getValue());
  }

  @Test
  void testHealEffect() {
    Effect effect = new Effect(EffectKind.HEAL, EffectTiming.ON_USE, 20.0);

    assertEquals(EffectKind.HEAL, effect.getKind());
    assertEquals(EffectTiming.ON_USE, effect.getTiming());
    assertEquals(20.0, effect.getValue());
  }

  @Test
  void testDamageReductionEffect() {
    Effect effect = new Effect(EffectKind.DAMAGE_REDUCTION, EffectTiming.PASSIVE, 20.0);

    assertEquals(EffectKind.DAMAGE_REDUCTION, effect.getKind());
    assertEquals(20.0, effect.getValue());
  }

  @Test
  void testExtraStrikeEffect() {
    Effect effect = new Effect(EffectKind.EXTRA_STRIKE, EffectTiming.ON_HIT, 1.0);

    assertEquals(EffectKind.EXTRA_STRIKE, effect.getKind());
    assertEquals(1.0, effect.getValue());
  }

  @Test
  void testTripleAttackEffect() {
    Effect effect = new Effect(EffectKind.TRIPLE_ATTACK, EffectTiming.ON_HIT, 3.0);

    assertEquals(EffectKind.TRIPLE_ATTACK, effect.getKind());
    assertEquals(3.0, effect.getValue());
  }

  @Test
  void testHighCritChanceEffect() {
    Effect effect = new Effect(EffectKind.HIGH_CRIT_CHANCE, EffectTiming.PASSIVE, 0.5);

    assertEquals(EffectKind.HIGH_CRIT_CHANCE, effect.getKind());
    assertEquals(0.5, effect.getValue());
  }

  @Test
  void testBerserkRageEffect() {
    Effect effect = new Effect(EffectKind.BERSERK_RAGE, EffectTiming.PASSIVE, 10.0);

    assertEquals(EffectKind.BERSERK_RAGE, effect.getKind());
    assertEquals(10.0, effect.getValue());
  }

  @Test
  void testBurnStatusEffect() {
    Effect effect = new Effect(EffectTiming.ON_HIT, StatusType.BURN, 3, 10);

    assertTrue(effect.isStatusEffect());
    assertEquals(StatusType.BURN, effect.getStatusType());
    assertEquals(3, effect.getStatusDuration());
    assertEquals(10, effect.getStatusAmount());
  }

  @Test
  void testPoisonStatusEffect() {
    Effect effect = new Effect(EffectTiming.ON_HIT, StatusType.POISON, 3, 5);

    assertTrue(effect.isStatusEffect());
    assertEquals(StatusType.POISON, effect.getStatusType());
    assertEquals(3, effect.getStatusDuration());
    assertEquals(5, effect.getStatusAmount());
  }

  @Test
  void testBleedStatusEffect() {
    Effect effect = new Effect(EffectTiming.ON_HIT, StatusType.BLEED, 3, 8);

    assertTrue(effect.isStatusEffect());
    assertEquals(StatusType.BLEED, effect.getStatusType());
    assertEquals(3, effect.getStatusDuration());
    assertEquals(8, effect.getStatusAmount());
  }

  @Test
  void testStunnedStatusEffect() {
    Effect effect = new Effect(EffectTiming.ON_HIT, StatusType.STUNNED, 2, 0);

    assertTrue(effect.isStatusEffect());
    assertEquals(StatusType.STUNNED, effect.getStatusType());
    assertEquals(2, effect.getStatusDuration());
    assertEquals(0, effect.getStatusAmount());
  }

  @Test
  void testFreezeStatusEffect() {
    Effect effect = new Effect(EffectTiming.ON_HIT, StatusType.FREEZE, 2, 0);

    assertTrue(effect.isStatusEffect());
    assertEquals(StatusType.FREEZE, effect.getStatusType());
    assertEquals(2, effect.getStatusDuration());
  }

  @Test
  void testDefenseReducedStatusEffect() {
    Effect effect = new Effect(EffectTiming.ON_HIT, StatusType.DEFENSE_REDUCED, 3, 20);

    assertTrue(effect.isStatusEffect());
    assertEquals(StatusType.DEFENSE_REDUCED, effect.getStatusType());
    assertEquals(3, effect.getStatusDuration());
    assertEquals(20, effect.getStatusAmount()); // 20% reduction
  }

  @Test
  void testSummonAllyEffect() {
    Effect effect = new Effect(EffectKind.SUMMON_ALLY, EffectTiming.ON_TURN, 2.0);

    assertEquals(EffectKind.SUMMON_ALLY, effect.getKind());
    assertEquals(EffectTiming.ON_TURN, effect.getTiming());
    assertEquals(2.0, effect.getValue()); // 2 allies
  }

  @Test
  void testAoeDamageEffect() {
    Effect effect = new Effect(EffectKind.AOE_DAMAGE, EffectTiming.ON_HIT, 50.0);

    assertEquals(EffectKind.AOE_DAMAGE, effect.getKind());
    assertEquals(50.0, effect.getValue());
  }

  @Test
  void testInstantKillEffect() {
    Effect effect = new Effect(EffectKind.INSTANT_KILL, EffectTiming.ON_HIT, 1.0);

    assertEquals(EffectKind.INSTANT_KILL, effect.getKind());
    assertEquals(1.0, effect.getValue());
  }

  @Test
  void testPiercingAttackEffect() {
    Effect effect = new Effect(EffectKind.PIERCING_ATTACK, EffectTiming.ON_HIT, 0.5);

    assertEquals(EffectKind.PIERCING_ATTACK, effect.getKind());
    assertEquals(0.5, effect.getValue()); // Ignores 50% defense
  }

  @Test
  void testEffectEquality() {
    Effect effect1 = new Effect(EffectKind.DAMAGE_BONUS, EffectTiming.PASSIVE, 25.0);
    Effect effect2 = new Effect(EffectKind.DAMAGE_BONUS, EffectTiming.PASSIVE, 25.0);
    Effect effect3 = new Effect(EffectKind.DAMAGE_BONUS, EffectTiming.PASSIVE, 30.0);

    assertEquals(effect1, effect2);
    assertEquals(effect1.hashCode(), effect2.hashCode());
    assertNotEquals(effect1, effect3);
  }

  @Test
  void testStatusEffectEquality() {
    Effect effect1 = new Effect(EffectTiming.ON_HIT, StatusType.POISON, 3, 5);
    Effect effect2 = new Effect(EffectTiming.ON_HIT, StatusType.POISON, 3, 5);
    Effect effect3 = new Effect(EffectTiming.ON_HIT, StatusType.BURN, 3, 5);

    assertEquals(effect1, effect2);
    assertEquals(effect1.hashCode(), effect2.hashCode());
    assertNotEquals(effect1, effect3);
  }

  @Test
  void testEffectToString() {
    Effect immediateEffect = new Effect(EffectKind.DAMAGE_BONUS, EffectTiming.PASSIVE, 25.0);
    String immediateStr = immediateEffect.toString();

    assertTrue(immediateStr.contains("DAMAGE_BONUS"));
    assertTrue(immediateStr.contains("PASSIVE"));
    assertTrue(immediateStr.contains("25.0"));

    Effect statusEffect = new Effect(EffectTiming.ON_HIT, StatusType.POISON, 3, 5);
    String statusStr = statusEffect.toString();

    assertTrue(statusStr.contains("APPLY_STATUS"));
    assertTrue(statusStr.contains("POISON"));
    assertTrue(statusStr.contains("duration=3"));
  }

  @Test
  void testMultipleEffectTimings() {
    Effect onHit = new Effect(EffectKind.DAMAGE_BONUS, EffectTiming.ON_HIT, 10.0);
    Effect onEquip = new Effect(EffectKind.MAX_HP_BONUS, EffectTiming.ON_EQUIP, 50.0);
    Effect onUse = new Effect(EffectKind.HEAL, EffectTiming.ON_USE, 20.0);
    Effect passive = new Effect(EffectKind.DAMAGE_REDUCTION, EffectTiming.PASSIVE, 10.0);
    Effect onTurn = new Effect(EffectKind.SUMMON_ALLY, EffectTiming.ON_TURN, 1.0);

    assertEquals(EffectTiming.ON_HIT, onHit.getTiming());
    assertEquals(EffectTiming.ON_EQUIP, onEquip.getTiming());
    assertEquals(EffectTiming.ON_USE, onUse.getTiming());
    assertEquals(EffectTiming.PASSIVE, passive.getTiming());
    assertEquals(EffectTiming.ON_TURN, onTurn.getTiming());
  }

  @Test
  void testEffectValueAsInteger() {
    Effect effect = new Effect(EffectKind.DAMAGE_BONUS, EffectTiming.PASSIVE, 25.0);

    // Value can be cast to int for damage calculations
    int damageBonus = (int) effect.getValue();
    assertEquals(25, damageBonus);
  }

  @Test
  void testEffectValueAsPercentage() {
    Effect effect = new Effect(EffectKind.HIGH_CRIT_CHANCE, EffectTiming.PASSIVE, 0.15);

    // Value represents 15% crit chance
    double critChance = effect.getValue();
    assertEquals(0.15, critChance, 0.001);
  }

  @Test
  void testStatusEffectValueMatchesAmount() {
    Effect effect = new Effect(EffectTiming.ON_HIT, StatusType.POISON, 3, 5);

    // For status effects, value should match statusAmount
    assertEquals(effect.getStatusAmount(), (int) effect.getValue());
  }

  @Test
  void testNonStatusEffectHasNoStatusData() {
    Effect effect = new Effect(EffectKind.DAMAGE_BONUS, EffectTiming.PASSIVE, 25.0);

    assertFalse(effect.isStatusEffect());
    assertNull(effect.getStatusType());
    assertEquals(0, effect.getStatusDuration());
    assertEquals(0, effect.getStatusAmount());
  }

  @Test
  void testStatusEffectHasApplyStatusKind() {
    Effect effect = new Effect(EffectTiming.ON_HIT, StatusType.BURN, 3, 10);

    assertEquals(EffectKind.APPLY_STATUS, effect.getKind());
    assertTrue(effect.isStatusEffect());
  }
}
