package avengers.model.combat;

/** Represents the timing of an effect in combat. */
public enum EffectTiming {
  ON_HIT, // When an attack lands (weapons with status effects)
  ON_EQUIP, // When item is equipped (armor, artifacts)
  ON_USE, // When item is consumed (healing potions)
  PASSIVE, // Always active (weapon damage bonuses, stat boosts)
  ON_TURN, // At start of turn (summoning allies)
}
