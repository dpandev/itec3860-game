package avengers.domain.model;

/**
 * Represents an immutable item in the game world.
 *
 * <p>Items can be consumables, weapons, armor, key items, or artifacts. Each item has properties
 * that determine its behavior, including whether it can be removed from inventory and whether it's
 * a key item required for progression.
 */
public final class Item {
  private final String id;
  private final String name;
  private final String description;
  private final String category;
  private final String effect;
  private final String specialEffect;
  private final boolean isKeyItem;
  private final boolean isRemovable;

  /**
   * Constructs a new Item with the specified properties.
   *
   * @param id the unique identifier for this item (e.g., "IT-01")
   * @param name the display name of the item
   * @param description the detailed description of the item
   * @param category the category of the item (Consumable, Weapon, Armor, Key Item, Artifact)
   * @param effect the mechanical effect of the item (e.g., "+20 HP")
   * @param specialEffect any special effects or restrictions
   * @throws IllegalArgumentException if any required field is null or blank
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
   * Gets the category of this item.
   *
   * @return the item category
   */
  public String getCategory() {
    return category;
  }

  /**
   * Gets the primary effect of the item.
   *
   * @return the item's effect
   * Gets the mechanical effect of this item.
   *
   * @return the item effect
   */
  public String getEffect() {
    return effect;
  }

  /**
   * Gets the special effect of the item.
   *
   * @return the item's special effect
   * Gets any special effects or restrictions of this item.
   *
   * @return the special effect description
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
   * Checks if this item is a key item required for game progression.
   *
   * @return true if this is a key item, false otherwise
   */
  public boolean isKeyItem() {
    return isKeyItem;
  }

  /**
   * Checks if this item can be removed from the player's inventory.
   *
   * @return true if the item can be removed, false otherwise
   */
  public boolean isRemovable() {
    return isRemovable;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Item item = (Item) obj;
    return id.equals(item.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return String.format("Item{id='%s', name='%s', category='%s'}", id, name, category);
  }
}
