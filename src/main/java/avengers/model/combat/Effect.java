package avengers.model.combat;

import avengers.model.Character;
import avengers.model.Element;
import java.util.Objects;

/** Represents an effect that can be applied during combat. */
public final class Effect {
  private final EffectKind kind;
  private final EffectTiming timing;
  private final int value;
  private final StatusType statusType;
  private final int statusDuration;
  private final Element element;

  /**
   * Private constructor for Effect. Use builder methods to create instances.
   *
   * @param kind The kind of effect.
   * @param timing When the effect triggers.
   * @param value The numeric value of the effect.
   * @param statusType The status type to apply (if kind is APPLY_STATUS).
   * @param statusDuration The duration of the status effect.
   * @param element The elemental type of the effect (optional).
   */

  /** Constructor for Effect. */
  public Effect(
      EffectKind kind,
      EffectTiming timing,
      int value,
      StatusType statusType,
      int statusDuration,
      Element element) {
    this.kind = kind;
    this.timing = timing;
    this.value = value;
    this.statusType = statusType;
    this.statusDuration = statusDuration;
    this.element = element;
  }

  /**
   * Creates an effect that applies a status condition.
   *
   * @param timing When to apply the status.
   * @param statusType The type of status to apply.
   * @param duration How many turns the status lasts.
   * @param amount The magnitude of the status effect.
   * @return A new Effect instance.
   */
  public static Effect applyStatus(
      EffectTiming timing, StatusType statusType, int duration, int amount) {
    return new Effect(EffectKind.APPLY_STATUS, timing, amount, statusType, duration, null);
  }

  /**
   * Creates an effect that grants extra strikes/attacks.
   *
   * @param timing When to grant extra strikes.
   * @param count Number of extra strikes.
   * @return A new Effect instance.
   */
  public static Effect extraStrikes(EffectTiming timing, int count) {
    return new Effect(EffectKind.EXTRA_STRIKES, timing, count, null, 0, null);
  }

  /**
   * Creates an effect that adds bonus damage to strikes.
   *
   * @param timing When to apply the strike bonus.
   * @param bonusDamage The amount of bonus damage.
   * @param element The element of the bonus damage (optional).
   * @return A new Effect instance.
   */
  public static Effect strikeBonus(EffectTiming timing, int bonusDamage, Element element) {
    return new Effect(EffectKind.STRIKE_BONUS, timing, bonusDamage, null, 0, element);
  }

  /**
   * Creates an effect that increases maximum HP.
   *
   * @param timing When to apply the max HP bonus.
   * @param bonusHp The amount of max HP to add.
   * @return A new Effect instance.
   */
  public static Effect maxHpBonus(EffectTiming timing, int bonusHp) {
    return new Effect(EffectKind.MAX_HP_BONUS, timing, bonusHp, null, 0, null);
  }

  /**
   * Creates an effect that reduces incoming damage.
   *
   * @param timing When to apply damage reduction.
   * @param reductionPercent The percentage of damage to reduce (0-100).
   * @return A new Effect instance.
   */
  public static Effect damageReduction(EffectTiming timing, int reductionPercent) {
    return new Effect(EffectKind.DAMAGE_REDUCTION, timing, reductionPercent, null, 0, null);
  }

  public EffectKind getKind() {
    return kind;
  }

  public EffectTiming getTiming() {
    return timing;
  }

  public int getValue() {
    return value;
  }

  public StatusType getStatusType() {
    return statusType;
  }

  public int getStatusDuration() {
    return statusDuration;
  }

  public Element getElement() {
    return element;
  }

  /**
   * Applies this effect to a character.
   *
   * @param target The character to apply the effect to.
   */
  public void applyTo(Character target) {
    switch (kind) {
      case APPLY_STATUS:
        if (statusType != null) {
          StatusInstance status = new StatusInstance(statusType, statusDuration, value);
          target.addStatus(status);
        }
        break;

      case MAX_HP_BONUS:
        // This would be handled by the character/equipment system
        // For now, just a placeholder for the logic
        break;

      case STRIKE_BONUS:
      case EXTRA_STRIKES:
      case DAMAGE_REDUCTION:
        // These are handled during combat calculations
        // The effect is checked when needed rather than directly applied
        break;
    }
  }

  /**
   * Checks if this effect should trigger at the given timing.
   *
   * @param currentTiming The current timing in combat.
   * @return true if this effect should trigger now.
   */
  public boolean shouldTrigger(EffectTiming currentTiming) {
    return this.timing == currentTiming;
  }

  /**
   * Calculates modified damage when this effect is a strike bonus.
   *
   * @param baseDamage The base damage before bonus.
   * @return The modified damage.
   */
  public int applyStrikeBonus(int baseDamage) {
    if (kind == EffectKind.STRIKE_BONUS) {
      return baseDamage + value;
    }
    return baseDamage;
  }

  /**
   * Calculates modified damage when this effect is damage reduction.
   *
   * @param incomingDamage The incoming damage.
   * @return The reduced damage.
   */
  public int applyDamageReduction(int incomingDamage) {
    if (kind == EffectKind.DAMAGE_REDUCTION) {
      double reduction = value / 100.0;
      return (int) Math.round(incomingDamage * (1.0 - reduction));
    }
    return incomingDamage;
  }

  /**
   * Gets the number of extra strikes provided by this effect.
   *
   * @return The number of extra strikes, or 0 if not an EXTRA_STRIKES effect.
   */
  public int getExtraStrikes() {
    if (kind == EffectKind.EXTRA_STRIKES) {
      return value;
    }
    return 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Effect effect = (Effect) o;
    return value == effect.value
        && statusDuration == effect.statusDuration
        && kind == effect.kind
        && timing == effect.timing
        && statusType == effect.statusType
        && element == effect.element;
  }

  @Override
  public int hashCode() {
    return Objects.hash(kind, timing, value, statusType, statusDuration, element);
  }

  @Override
  public String toString() {
    return "Effect{"
        + "kind="
        + kind
        + ", timing="
        + timing
        + ", value="
        + value
        + ", statusType="
        + statusType
        + ", statusDuration="
        + statusDuration
        + ", element="
        + element
        + '}';
  }
}
