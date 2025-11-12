package avengers.model.combat;

/**
 * Represents immediate or passive combat modifiers (NOT status effects). These are instant effects
 * or persistent modifiers from items/abilities.
 *
 * <p>Use StatusType for time-based debuffs/DoT effects. Use EffectKind for stat modifications,
 * attack patterns, and instant effects.
 */
public enum EffectKind {
  // Attack Pattern Modifiers
  EXTRA_STRIKE, // MON-01 (Fast Bite), MON-10 (Triple Bite), IT-11 (strike twice)
  TRIPLE_ATTACK, // MON-10 (Triple Bite) - 3 consecutive attacks in one turn

  // Damage Modifiers
  DAMAGE_BONUS, // IT-03 to IT-19 (weapon damage bonuses)
  HIGH_CRIT_CHANCE, // MON-11 (Shadow Slash), MON-17 (Brutal Slash), IT-03 (Orc Axe)
  CRITICAL_HIT, // MON-18 (Backstab Critical) - guaranteed or high crit
  BERSERK_RAGE, // MON-03 (+10 damage when HP < 100)
  HIGH_BURST_DAMAGE, // MON-09 (Lightning Strike) - single high damage attack
  BONUS_VS_MONARCH, // IT-17 (Dragon's Fang Sword) - bonus damage to Monarch-class

  // Defensive Modifiers
  DAMAGE_REDUCTION, // IT-15 (Ant King's Carapace 20%), IT-23 (Dragonbone Armor 10% fire)
  ELEMENTAL_RESISTANCE, // IT-23 (fire damage reduction)

  // Stat Modifiers
  MAX_HP_BONUS, // IT-20, IT-21, IT-22, IT-23, IT-24 (HP increases)
  STAT_BOOST, // IT-12 (Demon King's Crown +15% all stats)

  // Healing
  HEAL, // IT-01 (+20 HP), IT-02 (+50 HP)

  // Summoning/Minions
  // MON-07 (Clone Summon), MON-13 (Summon Skeletons), MON-19 (Summon Constructs),
  // MON-20 (Shadow Dominion), IT-13 (Summon 2 skeletons)
  SUMMON_ALLY,
  SHADOW_ARMY_BOOST, // IT-18 (Shadow Monarch's Cloak +20% army size)

  // Area of Effect
  AOE_DAMAGE, // MON-06 (Lava Geyser), MON-12 (Lightning Storm)

  // Special Mechanics
  APPLY_STATUS, // Wrapper to apply a StatusType effect
  INSTANT_KILL, // MON-02 (Stone Smash if puzzle failed)
  PIERCING_ATTACK, // MON-19 (Energy Beam) - ignores some defense
  KNOCKBACK // MON-09 (Cyclone Push) - positional (push player where? just display it...)
}
