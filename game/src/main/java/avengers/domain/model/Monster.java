package avengers.domain.model;

import java.util.List;

/**
 * Represents an immutable monster character in the game world.
 *
 * <p>Monsters are enemies that players encounter in rooms. Each monster has combat stats, special
 * abilities, and may drop items when defeated.
 */
public final class Monster extends Character {
  private final String id;
  private final String roomLocation;
  private final String description;
  private final int damage;
  private final List<String> specialEffects;
  private final List<String> itemDrops;

  /**
   * Constructs a Monster with the specified properties.
   *
   * @param id the unique identifier for this monster (e.g., "MON-01")
   * @param name the display name of the monster
   * @param roomLocation the room(s) where this monster can be found
   * @param description the detailed description of the monster
   * @param hp the current and maximum health points
   * @param damage the base damage this monster deals
   * @param specialEffects list of special abilities or effects
   * @param itemDrops list of items this monster may drop when defeated
   * @throws IllegalArgumentException if any required field is null or invalid
   */
  public Monster(
      String id,
      String name,
      String roomLocation,
      String description,
      int hp,
      int damage,
      List<String> specialEffects,
      List<String> itemDrops) {
    super(name, hp);

    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("Monster id cannot be null or blank");
    }
    if (description == null || description.isBlank()) {
      throw new IllegalArgumentException("Monster description cannot be null or blank");
    }
    if (damage < 0) {
      throw new IllegalArgumentException("Monster damage cannot be negative");
    }
    if (name.length() > 100) {
      throw new IllegalArgumentException("Monster name cannot exceed 100 characters");
    }
    if (description.length() > 500) {
      throw new IllegalArgumentException("Monster description cannot exceed 500 characters");
    }

    this.id = id;
    this.roomLocation = roomLocation != null ? roomLocation : "";
    this.description = description;
    this.damage = damage;
    this.specialEffects = specialEffects != null ? List.copyOf(specialEffects) : List.of();
    this.itemDrops = itemDrops != null ? List.copyOf(itemDrops) : List.of();

    // Set base attack to the damage value
    increaseBaseAttack(damage);
  }

  /**
   * Gets the unique identifier of this monster.
   *
   * @return the monster ID
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the room location where this monster can be found.
   *
   * @return the room location
   */
  public String getRoomLocation() {
    return roomLocation;
  }

  /**
   * Gets the description of this monster.
   *
   * @return the monster description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the base damage this monster deals in combat.
   *
   * @return the damage value
   */
  public int getDamage() {
    return damage;
  }

  /**
   * Gets the list of special effects or abilities this monster has.
   *
   * @return an immutable list of special effects
   */
  public List<String> getSpecialEffects() {
    return specialEffects;
  }

  /**
   * Gets the list of items this monster may drop when defeated.
   *
   * @return an immutable list of item drops
   */
  public List<String> getItemDrops() {
    return itemDrops;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Monster monster = (Monster) obj;
    return id.equals(monster.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return String.format(
        "Monster{id='%s', name='%s', hp=%d, damage=%d}", id, getName(), getCurrentHealth(), damage);
  }
}
