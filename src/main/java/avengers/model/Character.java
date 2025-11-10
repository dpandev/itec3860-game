package avengers.model;

import avengers.model.combat.DamageSource;
import avengers.model.combat.StatusInstance;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Represents a character in the game with attributes. */
public abstract class Character {
  final UUID id = UUID.randomUUID();
  String name;
  String description;
  int hp;
  int maxHp;
  int baseDamage;
  int defense;
  Element element;
  final List<StatusInstance> effects = new ArrayList<>();

  /**
   * Constructor for Character.
   *
   * @param name The name of the character.
   * @param description A description of the character.
   * @param hp The current health points of the character.
   * @param maxHp The maximum health points of the character.
   * @param baseDamage The base damage the character can deal.
   * @param defense The defense value of the character.
   * @throws IllegalArgumentException if any validation fails
   */
  public Character(
      String name, String description, int hp, int maxHp, int baseDamage, int defense) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Name cannot be null or empty");
    }
    if (maxHp <= 0) {
      throw new IllegalArgumentException("Maximum HP must be positive");
    }
    if (hp < 0) {
      throw new IllegalArgumentException("HP cannot be negative");
    }
    if (hp > maxHp) {
      throw new IllegalArgumentException("HP cannot be greater than maximum HP");
    }
    if (baseDamage < 0) {
      throw new IllegalArgumentException("Base damage cannot be negative");
    }
    if (defense < 0) {
      throw new IllegalArgumentException("Defense cannot be negative");
    }

    this.name = name;
    this.description = description;
    this.hp = hp;
    this.maxHp = maxHp;
    this.baseDamage = baseDamage;
    this.defense = defense;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public int getHp() {
    return hp;
  }

  public int getMaxHp() {
    return maxHp;
  }

  public int getBaseDamage() {
    return baseDamage;
  }

  public int getDefense() {
    return defense;
  }

  public Element getElement() {
    return element;
  }

  public List<StatusInstance> getEffects() {
    return List.copyOf(effects);
  }

  public void setElement(Element element) {
    this.element = element;
  }

  /**
   * Applies damage to this character.
   *
   * @param amount The amount of damage to take.
   * @param src The source of the damage (for future elemental interactions).
   */
  public void takeDamage(int amount, DamageSource src) {
    this.hp = Math.max(0, this.hp - amount);
  }

  /**
   * Heals the character.
   *
   * @param amount The amount of HP to restore.
   */
  public void heal(int amount) {
    this.hp = Math.min(this.maxHp, this.hp + amount);
  }

  /**
   * Checks if the character is dead.
   *
   * @return true if HP is 0 or less, false otherwise.
   */
  public boolean isDead() {
    return this.hp <= 0;
  }

  /**
   * Adds a status effect to this character.
   *
   * @param effect The status effect to add.
   */
  public void addStatus(StatusInstance effect) {
    this.effects.add(effect);
  }

  /**
   * Removes a status effect from this character.
   *
   * @param effect The status effect to remove.
   */
  public void removeStatus(StatusInstance effect) {
    this.effects.remove(effect);
  }

  /**
   * Gets the elemental resistances for this character. Default implementation returns empty map (no
   * resistances). Monsters override this to provide their resistance values.
   *
   * @return Map of element to resistance values.
   */
  public java.util.Map<Element, Double> getResistances() {
    return java.util.Map.of();
  }
}
