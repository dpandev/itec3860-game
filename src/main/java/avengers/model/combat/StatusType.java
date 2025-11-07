package avengers.model.combat;

/**
 * Represents the various status effects that can be applied to entities during combat.
 * <p>
 * Each status type corresponds to a specific effect, such as bleeding, freezing, burning, etc.
 * This enum is used to identify and manage these effects within the combat system.
 */
public enum StatusType {
  BLEED,
  FREEZE,
  BURN,
  POISON,
  DROWN,
  STUNNED,
  FEAR,
  SLOW
}
