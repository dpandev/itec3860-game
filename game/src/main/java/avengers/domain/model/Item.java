package avengers.domain.model;

/** Represents an item in the game with properties like name, description, and effects. */
public final class Item {
  private final String id;
  private final String name;
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
   * @param category the category of the item (e.g., Weapon, Consumable, Armor)
   * @param effect the effect of the item (e.g., "+20 HP", "+25 Damage")
   * @param specialEffect any special effect the item has
   */
  public Item(
      String id,
      String name,
      String description,
      String category,
      String effect,
      String specialEffect) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.category = category;
    this.effect = effect;
    this.specialEffect = specialEffect;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getCategory() {
    return category;
  }

  public String getEffect() {
    return effect;
  }

  public String getSpecialEffect() {
    return specialEffect;
  }

  /**
   * Checks if this item is a weapon.
   *
   * @return true if the category is "Weapon", false otherwise
   */
  public boolean isWeapon() {
    return "Weapon".equalsIgnoreCase(category);
  }

  /**
   * Checks if this item is armor.
   *
   * @return true if the category is "Armor", false otherwise
   */
  public boolean isArmor() {
    return "Armor".equalsIgnoreCase(category);
  }

  /**
   * Checks if this item is consumable.
   *
   * @return true if the category is "Consumable", false otherwise
   */
  public boolean isConsumable() {
    return "Consumable".equalsIgnoreCase(category);
  }

  /**
   * Checks if this item is a key item.
   *
   * @return true if the category is "Key Item", false otherwise
   */
  public boolean isKeyItem() {
    return "Key Item".equalsIgnoreCase(category);
  }

  /**
   * Checks if this item is an artifact.
   *
   * @return true if the category is "Artifact", false otherwise
   */
  public boolean isArtifact() {
    return "Artifact".equalsIgnoreCase(category);
  }

  /**
   * Checks if this item has a special effect.
   *
   * @return true if special effect is not null or empty, false otherwise
   */
  public boolean hasSpecialEffect() {
    return specialEffect != null
        && !specialEffect.isBlank()
        && !"None".equalsIgnoreCase(specialEffect);
  }
}
