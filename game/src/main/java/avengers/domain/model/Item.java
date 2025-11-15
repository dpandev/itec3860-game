package avengers.domain.model;

/**
 * Represents an item in the game with properties such as name, description, category, and effects.
 */
public final class Item extends Entity {
  private final String description;
  private final String category;
  private final String effect;
  private final String specialEffect;

  /**
   * Constructs an Item with the specified properties.
   *
   * @param id the unique identifier for the item
   * @param name the name of the item
   * @param description the description of the item
   * @param category the category of the item (e.g., "Weapon", "Armor", "Consumable")
   * @param effect the primary effect of the item (e.g., "+25 Damage", "+20 HP")
   * @param specialEffect any special effects or abilities the item provides
   */
  public Item(
      String id,
      String name,
      String description,
      String category,
      String effect,
      String specialEffect) {
    super(id, name);
    this.description = description;
    this.category = category;
    this.effect = effect;
    this.specialEffect = specialEffect;
  }

  /**
   * Gets the description of the item.
   *
   * @return the item's description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the category of the item.
   *
   * @return the item's category
   */
  public String getCategory() {
    return category;
  }

  /**
   * Gets the primary effect of the item.
   *
   * @return the item's effect
   */
  public String getEffect() {
    return effect;
  }

  /**
   * Gets the special effect of the item.
   *
   * @return the item's special effect
   */
  public String getSpecialEffect() {
    return specialEffect;
  }

  /**
   * Checks if the item has any effect.
   *
   * @return true if the item has a non-empty effect, false otherwise
   */
  public boolean hasEffect() {
    return effect != null && !effect.trim().isEmpty();
  }

  /**
   * Checks if the item has any special effect.
   *
   * @return true if the item has a non-empty special effect, false otherwise
   */
  public boolean hasSpecialEffect() {
    return specialEffect != null && !specialEffect.trim().isEmpty();
  }

  /**
   * Checks if the item is equippable based on its category.
   *
   * @return true if the item can be equipped, false otherwise
   */
  public boolean isEquippable() {
    return "Weapon".equalsIgnoreCase(category)
        || "Armor".equalsIgnoreCase(category)
        || "Artifact".equalsIgnoreCase(category);
  }

  /**
   * Checks if the item is consumable based on its category.
   *
   * @return true if the item is consumable, false otherwise
   */
  public boolean isConsumable() {
    return "Consumable".equalsIgnoreCase(category);
  }

  @Override
  public String toString() {
    return String.format("%s (%s)", getName(), category);
  }
}
