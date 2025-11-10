package avengers.model.combat;

import avengers.model.Element;
import avengers.model.Monster;
import java.util.Map;

/** Utility class for calculating combat damage with elements and resistances. */
public final class DamageCalculator {

  private static final Map<Element, Map<Element, Double>> ELEMENT_MULTIPLIERS =
    Map.of(
      Element.FIRE, Map.of(Element.WATER, 0.5, Element.EARTH, 1.5),
      Element.WATER, Map.of(Element.FIRE, 1.5, Element.EARTH, 0.5),
      Element.EARTH, Map.of(Element.AIR, 1.5, Element.WATER, 1.5),
      Element.AIR, Map.of(Element.EARTH, 0.5, Element.FIRE, 1.0),
      Element.SHADOW, Map.of(Element.SHADOW, 0.8));

  private DamageCalculator() {}

  /**
   * Calculates damage after applying element multipliers and resistances.
   *
   * @param baseDamage The base damage before modifiers.
   * @param attackerElement The element of the attacker.
   * @param defenderElement The element of the defender.
   * @param defenderResistances Resistance map of the defender (empty for non-monsters).
   * @param isCritical Whether this is a critical hit.
   * @return The final damage value.
   */
  public static int calculateDamage(
    int baseDamage,
    Element attackerElement,
    Element defenderElement,
    Map<Element, Double> defenderResistances,
    boolean isCritical) {

    double damage = baseDamage;

    // Apply critical multiplier
    if (isCritical) {
      damage *= 1.5;
    }

    // Apply element multipliers
    if (attackerElement != null && defenderElement != null) {
      double multiplier = getElementMultiplier(attackerElement, defenderElement);
      damage *= multiplier;
    }

    // Apply resistances (if defender is a monster)
    if (attackerElement != null && defenderResistances.containsKey(attackerElement)) {
      double resistance = defenderResistances.get(attackerElement);
      damage *= (1.0 - resistance);
    }

    return Math.max(1, (int) Math.round(damage));
  }

  /**
   * Gets the element multiplier for attacker vs defender elements.
   *
   * @param attackerElement The attacker's element.
   * @param defenderElement The defender's element.
   * @return The damage multiplier.
   */
  private static double getElementMultiplier(Element attackerElement, Element defenderElement) {
    if (attackerElement == Element.NEUTRAL || defenderElement == Element.NEUTRAL) {
      return 1.0;
    }

    Map<Element, Double> matchups = ELEMENT_MULTIPLIERS.get(attackerElement);
    if (matchups != null && matchups.containsKey(defenderElement)) {
      return matchups.get(defenderElement);
    }

    return 1.0;
  }

  /**
   * Applies defense reduction to damage.
   *
   * @param damage The damage before defense.
   * @param defense The defense value.
   * @return The reduced damage.
   */
  public static int applyDefense(int damage, int defense) {
    // Defense reduces damage by a percentage, with diminishing returns
    double reduction = defense / (defense + 100.0);
    int reduced = (int) Math.round(damage * (1.0 - reduction));
    return Math.max(1, reduced);
  }

  /**
   * Calculates flee success probability based on monster tier and player HP.
   *
   * @param monster The monster being fought.
   * @param playerHpPercent Player's current HP as a percentage of max HP (0.0 to 1.0).
   * @return The probability of successful flee (0.0 to 1.0).
   */
  public static double calculateFleeProbability(Monster monster, double playerHpPercent) {
    double baseProbability;

    switch (monster.getBossType()) {
      case FIRST:
        baseProbability = 0.5;
        break;
      case FINAL:
        baseProbability = 0.1;
        break;
      default:
        baseProbability = monster.isMonarchClass() ? 0.3 : 0.7;
    }

    // Lower HP increases flee chance (desperation bonus)
    if (playerHpPercent < 0.3) {
      baseProbability += 0.2;
    }

    return Math.min(0.95, baseProbability);
  }
}
