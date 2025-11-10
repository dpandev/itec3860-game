package avengers.model;

/** Represents a summoned ally that fights alongside the player. */
public final class Ally extends Character {
  private int remainingTurns;

  /**
   * Constructor for Ally.
   *
   * @param name The name of the ally.
   * @param description A description of the ally.
   * @param hp The current health points.
   * @param maxHp The maximum health points.
   * @param baseDamage The base damage output.
   * @param defense The defense value.
   * @param element The elemental type.
   * @param remainingTurns Number of turns this ally will remain active.
   */
  public Ally(
    String name,
    String description,
    int hp,
    int maxHp,
    int baseDamage,
    int defense,
    Element element,
    int remainingTurns) {
    super(name, description, hp, maxHp, baseDamage, defense);
    this.element = element;
    this.remainingTurns = remainingTurns;
  }

  /**
   * Gets the number of remaining turns for this ally.
   *
   * @return The remaining turns.
   */
  public int getRemainingTurns() {
    return remainingTurns;
  }

  /** Decrements the remaining turns by one. */
  public void decrementTurns() {
    if (remainingTurns > 0) {
      remainingTurns--;
    }
  }

  /**
   * Checks if this ally has expired.
   *
   * @return true if the ally has no remaining turns, false otherwise.
   */
  public boolean isExpired() {
    return remainingTurns <= 0;
  }
}
