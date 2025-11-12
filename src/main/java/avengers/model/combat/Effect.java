package avengers.model.combat;

import java.util.Objects;

/**
 * Represents an effect that can be applied during combat. Effects can be immediate (damage, heal,
 * stat boost) or apply status conditions.
 */
public final class Effect {
  private final EffectKind kind;
  private final EffectTiming timing;
  private final double value;
  private final StatusType statusType;
  private final int statusDuration;
  private final int statusAmount;

  /**
   * Constructor for immediate effects (damage bonus, heal, stat boost, etc.).
   *
   * @param kind The type of effect.
   * @param timing When the effect is applied.
   * @param value The magnitude of the effect (damage, heal amount, percentage, etc.).
   */
  public Effect(EffectKind kind, EffectTiming timing, double value) {
    this.kind = kind;
    this.timing = timing;
    this.value = value;
    this.statusType = null;
    this.statusDuration = 0;
    this.statusAmount = 0;
  }

  /**
   * Constructor for effects that apply status conditions.
   *
   * @param timing When the effect is applied.
   * @param statusType The type of status to apply.
   * @param statusDuration How many turns the status lasts.
   * @param statusAmount The magnitude of the status (damage per turn, etc.).
   */
  public Effect(EffectTiming timing, StatusType statusType, int statusDuration, int statusAmount) {
    this.kind = EffectKind.APPLY_STATUS;
    this.timing = timing;
    this.value = statusAmount;
    this.statusType = statusType;
    this.statusDuration = statusDuration;
    this.statusAmount = statusAmount;
  }

  /**
   * Gets the effect kind.
   *
   * @return The effect kind.
   */
  public EffectKind getKind() {
    return kind;
  }

  /**
   * Gets the effect timing.
   *
   * @return When the effect is applied.
   */
  public EffectTiming getTiming() {
    return timing;
  }

  /**
   * Gets the effect value.
   *
   * @return The magnitude of the effect.
   */
  public double getValue() {
    return value;
  }

  /**
   * Gets the status type (if this effect applies a status).
   *
   * @return The status type, or null if not a status effect.
   */
  public StatusType getStatusType() {
    return statusType;
  }

  /**
   * Gets the status duration.
   *
   * @return The number of turns the status lasts.
   */
  public int getStatusDuration() {
    return statusDuration;
  }

  /**
   * Gets the status amount.
   *
   * @return The magnitude of the status effect.
   */
  public int getStatusAmount() {
    return statusAmount;
  }

  /**
   * Checks if this effect applies a status condition.
   *
   * @return true if this is a status-applying effect.
   */
  public boolean isStatusEffect() {
    return kind == EffectKind.APPLY_STATUS && statusType != null;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Effect effect = (Effect) obj;
    return Double.compare(effect.value, value) == 0
        && statusDuration == effect.statusDuration
        && statusAmount == effect.statusAmount
        && kind == effect.kind
        && timing == effect.timing
        && statusType == effect.statusType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(kind, timing, value, statusType, statusDuration, statusAmount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Effect{kind=").append(kind);
    sb.append(", timing=").append(timing);
    sb.append(", value=").append(value);
    if (statusType != null) {
      sb.append(", statusType=").append(statusType);
      sb.append(", duration=").append(statusDuration);
    }
    sb.append("}");
    return sb.toString();
  }
}
