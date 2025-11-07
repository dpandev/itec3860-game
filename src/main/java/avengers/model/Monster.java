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
   * @param bossType the type of the boss.
   * @param monarchClass whether the monster is of monarch class.
   * @param critThreshold the critical hit threshold of the monster.
   */
  public Monster(
      String name,
      String description,
      int hp,
      int maxHp,
      int baseDamage,
      int defense,
      BossType bossType,
      boolean monarchClass,
      double critThreshold) {
    super(name, description, hp, maxHp, baseDamage, defense);
    this.bossType = bossType;
    this.monarchClass = monarchClass;
    this.critThreshold = critThreshold;
  }

  /**
   * Rolls damage for the monster's attack.
   *
   * @param random the Random instance to use for rolling.
   * @return the rolled damage value.
   */
  public int rollDamage(Random random) {
    // TODO: implement damage roll logic
    return 0;
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
   * @param element the element type.
   * @param value the resistance value.
   */
  public void addResistance(Element element, double value) {
    resistances.put(element, value);
  }
}
