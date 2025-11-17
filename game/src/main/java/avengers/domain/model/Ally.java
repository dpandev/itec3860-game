package avengers.domain.model;

/**
 * Represents an ally character that can assist the player.
 *
 * <p>Allies are friendly characters that provide combat bonuses and special abilities to help the
 * player progress through the game. This is a minimal implementation that can be expanded as ally
 * mechanics are developed.
 */
public final class Ally extends Character {
  private final String id;
  private final String description;
  private final int attackBonus;
  private final int defenseBonus;
  private final String specialAbility;

  /**
   * Constructs an Ally with the specified properties.
   *
   * @param id the unique identifier for this ally
   * @param name the display name of the ally
   * @param description the description of the ally
   * @param hp the health points of the ally
   * @param attackBonus the attack bonus this ally provides
   * @param defenseBonus the defense bonus this ally provides
   * @param specialAbility the special ability of this ally (optional)
   * @throws IllegalArgumentException if any required field is null or invalid
   */
  public Ally(
      String id,
      String name,
      String description,
      int hp,
      int attackBonus,
      int defenseBonus,
      String specialAbility) {
    super(name, hp);

    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("Ally id cannot be null or blank");
    }
    if (description == null || description.isBlank()) {
      throw new IllegalArgumentException("Ally description cannot be null or blank");
    }
    if (name.length() > 100) {
      throw new IllegalArgumentException("Ally name cannot exceed 100 characters");
    }
    if (description.length() > 500) {
      throw new IllegalArgumentException("Ally description cannot exceed 500 characters");
    }

    this.id = id;
    this.description = description;
    this.attackBonus = attackBonus;
    this.defenseBonus = defenseBonus;
    this.specialAbility = specialAbility != null ? specialAbility : "";

    // Apply bonuses to base stats
    increaseBaseAttack(attackBonus);
    increaseBaseDefense(defenseBonus);
  }

  /**
   * Gets the unique identifier of this ally.
   *
   * @return the ally ID
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the description of this ally.
   *
   * @return the ally description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the attack bonus this ally provides.
   *
   * @return the attack bonus
   */
  public int getAttackBonus() {
    return attackBonus;
  }

  /**
   * Gets the defense bonus this ally provides.
   *
   * @return the defense bonus
   */
  public int getDefenseBonus() {
    return defenseBonus;
  }

  /**
   * Gets the special ability of this ally.
   *
   * @return the special ability description
   */
  public String getSpecialAbility() {
    return specialAbility;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Ally ally = (Ally) obj;
    return id.equals(ally.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return String.format(
        "Ally{id='%s', name='%s', attackBonus=%d, defenseBonus=%d}",
        id, getName(), attackBonus, defenseBonus);
  }
}
