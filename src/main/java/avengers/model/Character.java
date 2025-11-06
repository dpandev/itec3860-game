package avengers.model;

import avengers.model.combat.DamageSource;
import avengers.model.combat.StatusInstance;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** * Represents a character in the game with attributes. */
abstract class Character {
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
   * * Constructor for Character.
   *
   * @param name The name of the character.
   * @param description A description of the character.
   * @param hp The current health points of the character.
   * @param maxHp The maximum health points of the character.
   * @param baseDamage The base damage the character can deal.
   * @param defense The defense value of the character.
   */
  public Character(
      String name, String description, int hp, int maxHp, int baseDamage, int defense) {
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
    return effects;
  }

  public void setElement(Element element) {
    this.element = element;
  }

  public void takeDamage(int amount, DamageSource src) {
    // TODO: implement damage calculation considering defense and effects
  }

  public void heal(int amount) {
    this.hp = Math.min(this.maxHp, this.hp + amount);
  }

  public boolean isDead() {
    return this.hp <= 0;
  }

  public void addStatus(StatusInstance effect) {
    this.effects.add(effect);
  }
}
