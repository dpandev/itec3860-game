package avengers.model.item;

import avengers.model.Element;
import avengers.model.Player;
import avengers.model.combat.Effect;
import java.util.List;

/** Represents an item in the game with various attributes and effects. */
public final class Item {
  private final String id;
  private final ItemType type;
  private final String name;
  private final String description;
  private final boolean critical;
  private final boolean unique;
  private final Element element;
  private final List<Effect> effects;
  private final boolean passiveWhileInInventory;

  /**
   * Constructor for Item.
   *
   * @param id The unique identifier for the item.
   * @param type The type of the item.
   * @param name The name of the item.
   * @param description A description of the item.
   * @param critical Whether the item is critical for game progression.
   * @param unique Whether the item is unique (only one can exist).
   * @param element The elemental attribute of the item.
   * @param effects A list of effects that the item has.
   * @param passiveWhileInInventory Whether the item's effects are passive while in inventory.
   * @throws IllegalArgumentException if any validation fails
   */
  public Item(
      String id,
      ItemType type,
      String name,
      String description,
      boolean critical,
      boolean unique,
      Element element,
      List<Effect> effects,
      boolean passiveWhileInInventory) {
    if (id == null || id.trim().isEmpty()) {
      throw new IllegalArgumentException("ID cannot be null or empty");
    }
    if (type == null) {
      throw new IllegalArgumentException("Type cannot be null");
    }
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Name cannot be null or empty");
    }
    if (effects == null) {
      throw new IllegalArgumentException("Effects list cannot be null");
    }

    this.id = id;
    this.type = type;
    this.name = name;
    this.description = description;
    this.critical = critical;
    this.unique = unique;
    this.element = element;
    this.effects = List.copyOf(effects);
    this.passiveWhileInInventory = passiveWhileInInventory;
  }

  public String getId() {
    return id;
  }

  public ItemType getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public boolean isCritical() {
    return critical;
  }

  public boolean isUnique() {
    return unique;
  }

  public Element getElement() {
    return element;
  }

  public List<Effect> getEffects() {
    return effects;
  }

  public boolean isPassiveWhileInInventory() {
    return passiveWhileInInventory;
  }

  /**
   * Activates the item's effects on the given player.
   *
   * @param player The player on whom the item's effects will be applied.
   */
  public void activate(Player player) {
    // TODO: Implement activation logic
  }
}
