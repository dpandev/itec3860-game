package avengers.model.combat;

/**
 * Represents status effects that can be applied to characters during combat. These are temporary
 * conditions that persist for a duration and cause ongoing effects.
 *
 * <p>Status effects are applied by monster abilities or item effects.
 */
public enum StatusType {
  // Damage Over Time effects
  BLEED, // IT-10 (Cerberus Fang), gradual HP loss per turn
  BURN, // MON-06 (Fire Breath), MON-20 (Fire Breath), fire damage over time
  POISON, // MON-05 (Venom Bite), MON-15 (Poison Claw), IT-05 (Kasaka's Venom Fang)
  DROWN, // MON-07 (Drowning Currents), water damage over time

  // Control/Crowd Control effects (affects allies too, area of effect)
  STUNNED, // MON-08 (Earthquake), MON-20 (Dragon Roar), prevents action for turns
  FREEZE, // MON-16 (Ice Prison, Frost Aura), immobilizes for turns

  // Debuffs
  DEFENSE_REDUCED, // MON-04 (War Cry), IT-04 (High Orc Club)

  // Special/Unique effects
  RANDOM_DEBUFF // MON-14 (Reality Warp) - applies random negative effect
}
