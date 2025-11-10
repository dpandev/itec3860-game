package avengers.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

/** Represents a monster character in the game. */
public final class Monster extends Character {
  private final BossType bossType;
  private final boolean monarchClass;
  private final EnumMap<Element, Double> resistances = new EnumMap<>(Element.class);
  private final double critThreshold;

  /**
   * Constructor for the Monster class.
   *
   * @param id The unique identifier for the monster.
   * @param name The name of the monster.
   * @param description A description of the monster.
   * @param hp The current health points.
   * @param maxHp The maximum health points.
   * @param baseDamage The base damage output.
   * @param defense The defense value.
   * @param bossType The type of the boss.
   * @param monarchClass Whether the monster is of monarch class.
   * @param critThreshold The critical hit threshold of the monster.
   */
  public Monster(
      String id,
      String name,
      String description,
      int hp,
      int maxHp,
      int baseDamage,
      int defense,
      BossType bossType,
      boolean monarchClass,
      double critThreshold) {
    super(id, name, description, hp, maxHp, baseDamage, defense);
    this.bossType = bossType;
    this.monarchClass = monarchClass;
    this.critThreshold = critThreshold;
  }

  /**
   * Rolls damage for the monster's attack.
   *
   * @param random The Random instance to use for rolling.
   * @return The rolled damage value.
   */
  public int rollDamage(Random random) {
    // Roll damage with variance (±20%)
    double variance = 0.8 + (random.nextDouble() * 0.4);
    return (int) Math.round(baseDamage * variance);
  }

  public double getCritThreshold() {
    return critThreshold;
  }

  public BossType getBossType() {
    return bossType;
  }

  public boolean isMonarchClass() {
    return monarchClass;
  }

  public Map<Element, Double> getResistances() {
    return Collections.unmodifiableMap(resistances);
  }

  /**
   * Adds a resistance value for a specific element.
   *
   * @param element The element type.
   * @param value The resistance value (0.0 to 1.0, where 1.0 = 100% resistance).
   */
  public void addResistance(Element element, double value) {
    resistances.put(element, value);
  }
}
