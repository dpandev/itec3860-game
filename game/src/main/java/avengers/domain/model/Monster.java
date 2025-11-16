package avengers.domain.model;

/** Represents a monster character in the game with specific attributes. */
public class Monster extends Character {

  //
  /**
   * Constructs a Monster with the specified name, maximum health, base attack, and base defense.
   *
   * @param name the name of the monster
   * @param maxHealth the maximum health of the monster
   * @param baseAttack the base attack value of the monster
   * @param baseDefense the base defense value of the monster
   */
  public Monster(String name, int maxHealth, int baseAttack, int baseDefense) {
    super(name, maxHealth);
    increaseBaseAttack(baseAttack);
    increaseBaseDefense(baseDefense);
  }
}
