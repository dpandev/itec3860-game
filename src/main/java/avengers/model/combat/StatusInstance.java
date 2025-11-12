package avengers.model.combat;

import avengers.model.Character;
import avengers.model.Element;

/** Represents an instance of a status effect applied to a character in combat. */
public final class StatusInstance {
  private final StatusType type;
  private int remainingTurns;
  private final int effectAmount;

  /**
   * Constructor for StatusInstance.
   *
   * @param type The type of the status effect.
   * @param remainingTurns The number of turns the status effect will last.
   * @param effectAmount The magnitude of the status effect.
   */
  public StatusInstance(StatusType type, int remainingTurns, int effectAmount) {
    this.type = type;
    this.remainingTurns = remainingTurns;
    this.effectAmount = effectAmount;
  }

  /**
   * Returns the type of the status effect.
   *
   * @return the status effect type
   */
  public StatusType getType() {
    return type;
  }

  /**
   * Returns the number of remaining turns for this status effect.
   *
   * @return the number of remaining turns
   */
  public int getRemainingTurns() {
    return remainingTurns;
  }

  /**
   * Returns the magnitude of the status effect.
   *
   * @return the effect amount
   */
  public int getEffectAmount() {
    return effectAmount;
  }

  /**
   * Applies the status effect to the target character.
   *
   * @param target The character to which the status effect is applied.
   */
  public void apply(Character target) {
    if (remainingTurns <= 0) {
      return;
    }

    // Apply damage based on status type
    switch (type) {
      case BLEED:
      case BURN:
      case POISON:
      case DROWN:
        DamageSource source = new DamageSource(getElementForStatus(type), false);
        target.takeDamage(effectAmount, source);
        break;
      case FREEZE:
      case STUNNED:
        break;
      case DEFENSE_REDUCED:
        break;
      default:
        // Unknown status type - no effect
        break;
    }

    remainingTurns--;
  }

  /**
   * Checks if this is a damage-over-time status effect.
   *
   * @return true if this status causes damage each turn.
   */
  public boolean isDamageOverTime() {
    return type == StatusType.BLEED
        || type == StatusType.BURN
        || type == StatusType.POISON
        || type == StatusType.DROWN;
  }

  /**
   * Checks if this is a control effect that prevents actions.
   *
   * @return true if this status prevents the character from acting.
   */
  public boolean isControlEffect() {
    return type == StatusType.FREEZE || type == StatusType.STUNNED;
  }

  /**
   * Checks if this is a debuff that modifies stats.
   *
   * @return true if this status modifies character stats.
   */
  public boolean isDebuff() {
    return type == StatusType.DEFENSE_REDUCED;
  }

  /**
   * Gets the defense modifier from this status (if applicable).
   *
   * @return The defense multiplier (e.g., 0.8 for 20% reduction).
   */
  public double getDefenseModifier() {
    if (type == StatusType.DEFENSE_REDUCED) {
      return 1.0 - (effectAmount / 100.0);
    }
    return 1.0;
  }

  /**
   * Gets the element associated with a status type for damage calculation.
   *
   * @param type The status type.
   * @return The associated element.
   */
  private Element getElementForStatus(StatusType type) {
    return switch (type) {
      case BURN -> Element.FIRE;
      case FREEZE -> Element.WATER;
      case POISON, BLEED -> Element.SHADOW;
      case DROWN -> Element.WATER;
      default -> Element.NEUTRAL;
    };
  }

  /**
   * Checks if this status effect has expired.
   *
   * @return true if remaining turns is 0 or less.
   */
  public boolean isExpired() {
    return remainingTurns <= 0;
  }
}
