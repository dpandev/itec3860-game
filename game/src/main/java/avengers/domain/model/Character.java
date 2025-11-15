package avengers.domain.model;

/** Represents a character in the game with attributes. */
public abstract class Character {
  private String name;
  private int maxHealth;
  private int currentHealth;
  private int baseAttack = 0;
  private int baseDefense = 0;

  /**
   * Constructs a Character with the specified name and maximum health.
   *
   * @param name the name of the character
   * @param maxHealth the maximum health of the character
   */
  public Character(String name, int maxHealth) {
    this.name = name;
    this.maxHealth = maxHealth;
    this.currentHealth = maxHealth;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getMaxHealth() {
    return maxHealth;
  }

  public int getCurrentHealth() {
    return currentHealth;
  }

  public boolean isAlive() {
    return currentHealth > 0;
  }

  /**
   * Reduces the character's current health by the specified damage amount, not going below zero.
   *
   * @param damage the amount of damage to take
   */
  public void takeDamage(int damage) {
    currentHealth = Math.max(0, currentHealth - damage);
  }

  /**
   * Heals the character by the specified amount, not exceeding maximum health.
   *
   * @param amount the amount to heal
   */
  public void heal(int amount) {
    currentHealth = Math.min(maxHealth, currentHealth + amount);
  }

  /**
   * Increases the base attack by the specified amount.
   *
   * @param amount the amount to increase the base attack by
   */
  public void increaseBaseAttack(int amount) {
    this.baseAttack += amount;
  }

  /**
   * Increases the base defense by the specified amount.
   *
   * @param amount the amount to increase the base defense by
   */
  public void increaseBaseDefense(int amount) {
    this.baseDefense += amount;
  }

  public int getBaseAttack() {
    return baseAttack;
  }

  public int getBaseDefense() {
    return baseDefense;
  }

  /**
   * Decreases the base defense by the specified amount, ensuring it does not go below zero.
   *
   * @param amount the amount to decrease the base defense by
   */
  public void decreaseBaseDefense(int amount) {
    this.baseDefense = Math.max(0, this.baseDefense - amount);
  }

  /**
   * Decreases the base attack by the specified amount, ensuring it does not go below zero.
   *
   * @param amount the amount to decrease the base attack by
   */
  public void decreaseBaseAttack(int amount) {
    this.baseAttack = Math.max(0, this.baseAttack - amount);
  }

  /**
   * Sets the current health.
   *
   * @param health the health value to set
   */
  public void setCurrentHealth(int health) {
    this.currentHealth = Math.max(0, Math.min(health, maxHealth));
  }

  /**
   * Sets the maximum health.
   *
   * @param health the maximum health value to set
   */
  public void setMaxHealth(int health) {
    this.maxHealth = Math.max(1, health);
  }

  /**
   * Sets the base attack.
   *
   * @param attack the base attack value to set
   */
  public void setBaseAttack(int attack) {
    this.baseAttack = Math.max(0, attack);
  }

  /**
   * Sets the base defense.
   *
   * @param defense the base defense value to set
   */
  public void setBaseDefense(int defense) {
    this.baseDefense = Math.max(0, defense);
  }
}
