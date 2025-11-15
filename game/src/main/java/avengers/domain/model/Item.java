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
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("Item id cannot be null or blank");
    }
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Item name cannot be null or blank");
    }
    if (description == null || description.isBlank()) {
      throw new IllegalArgumentException("Item description cannot be null or blank");
    }
    if (category == null || category.isBlank()) {
      throw new IllegalArgumentException("Item category cannot be null or blank");
    }
    if (name.length() > 100) {
      throw new IllegalArgumentException("Item name cannot exceed 100 characters");
    }
    if (description.length() > 500) {
      throw new IllegalArgumentException("Item description cannot exceed 500 characters");
    }

    this.id = id;
    this.name = name;
    this.description = description;
    this.category = category;
    this.effect = effect != null ? effect : "";
    this.specialEffect = specialEffect != null ? specialEffect : "";

    // Determine if this is a key item based on category
    this.isKeyItem = "Key Item".equals(category);

    // Determine if this item can be removed
    // Key Items and System Blessing (IT-20) cannot be removed
    this.isRemovable =
        !isKeyItem
            && !"IT-20".equals(id)
            && (specialEffect == null
                || !specialEffect.toLowerCase().contains("cannot be removed"));
  }

  /**
   * Gets the unique identifier of this item.
   *
   * @return the item ID
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the display name of this item.
   *
   * @return the item name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the description of this item.
   *
   * @return the item description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the category of this item.
   *
   * @return the item category
   */
  public String getCategory() {
    return category;
  }

  /**
   * Gets the mechanical effect of this item.
   *
   * @return the item effect
   */
  public String getEffect() {
    return effect;
  }

  /**
   * Gets any special effects or restrictions of this item.
   *
   * @return the special effect description
   */
  public String getSpecialEffect() {
    return specialEffect;
  }

  /**
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
