package avengers.model.combat;

/** Represents an instance of a status effect applied to a character in combat. */
public final class StatusInstance {
  private final StatusType type;
  private int remainingTurns;
  private int effectAmount;

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
    // TODO: implement status effect application logic
  }
}
