package avengers.domain.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for parsing and managing stat bonuses from item effects.
 *
 * <p>Supports parsing effects in the format: "+10 Attack, +5 Defense, +20 HP"
 */
public final class StatBonus {
  private final Map<String, Integer> bonuses;

  /**
   * Constructs a StatBonus with the given bonuses map.
   *
   * @param bonuses map of stat names to bonus values
   */
  public StatBonus(Map<String, Integer> bonuses) {
    this.bonuses = new HashMap<>(bonuses);
  }

  /**
   * Parses an effect string into a StatBonus object.
   *
   * @param effectString the effect string to parse (e.g., "+10 Attack, +5 Defense")
   * @return StatBonus object containing parsed bonuses
   * @throws IllegalArgumentException if the effect string format is invalid
   */
  public static StatBonus parseEffect(String effectString) {
    Map<String, Integer> bonuses = new HashMap<>();

    if (effectString == null || effectString.trim().isEmpty()) {
      return new StatBonus(bonuses);
    }

    String[] effects = effectString.split(",");
    for (String effect : effects) {
      effect = effect.trim();
      if (effect.isEmpty()) {
        continue;
      }

      // Parse format: "+10 Attack" or "-5 Defense"
      String[] parts = effect.split("\\s+", 2);
      if (parts.length != 2) {
        throw new IllegalArgumentException("Invalid effect format: " + effect);
      }

      try {
        int value = Integer.parseInt(parts[0]);
        String statName = parts[1];
        bonuses.put(statName, bonuses.getOrDefault(statName, 0) + value);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid number in effect: " + effect, e);
      }
    }

    return new StatBonus(bonuses);
  }

  /**
   * Gets the bonus value for a specific stat.
   *
   * @param statName the name of the stat (case-sensitive)
   * @return the bonus value, or 0 if no bonus exists for this stat
   */
  public int getBonus(String statName) {
    return bonuses.getOrDefault(statName, 0);
  }

  /**
   * Gets the attack bonus from this StatBonus.
   *
   * @return the attack bonus value
   */
  public int getAttackBonus() {
    return getBonus("Attack");
  }

  /**
   * Gets the defense bonus from this StatBonus.
   *
   * @return the defense bonus value
   */
  public int getDefenseBonus() {
    return getBonus("Defense");
  }

  /**
   * Gets the health bonus from this StatBonus.
   *
   * @return the health bonus value (supports both "HP" and "Health")
   */
  public int getHealthBonus() {
    return getBonus("HP") + getBonus("Health");
  }

  /**
   * Gets all bonuses as a map.
   *
   * @return copy of the bonuses map
   */
  public Map<String, Integer> getAllBonuses() {
    return new HashMap<>(bonuses);
  }

  /**
   * Checks if this StatBonus has any bonuses.
   *
   * @return true if there are any bonuses, false otherwise
   */
  public boolean hasBonuses() {
    return !bonuses.isEmpty();
  }

  @Override
  public String toString() {
    if (bonuses.isEmpty()) {
      return "No bonuses";
    }

    StringBuilder sb = new StringBuilder();
    bonuses.forEach(
        (stat, value) -> {
          if (sb.length() > 0) {
            sb.append(", ");
          }
          sb.append(value >= 0 ? "+" : "").append(value).append(" ").append(stat);
        });

    return sb.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    StatBonus statBonus = (StatBonus) obj;
    return bonuses.equals(statBonus.bonuses);
  }

  @Override
  public int hashCode() {
    return bonuses.hashCode();
  }
}
