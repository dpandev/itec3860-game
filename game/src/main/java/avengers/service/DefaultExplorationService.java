package avengers.service;

import avengers.domain.model.Item;
import avengers.domain.model.Player;
import avengers.domain.model.World;
import avengers.domain.utils.GameContext;

/** Default implementation of ExplorationService for handling exploration-related operations. */
public class DefaultExplorationService implements ExplorationService {
  //

  @Override
  public String showStats(GameContext ctx) {
    Player player = ctx.player();
    World world = ctx.world();

    StringBuilder stats = new StringBuilder();
    stats.append("=== Player Stats ===\n");

    // Health display
    stats.append(
        String.format(
            "Health: %d / %d\n", player.getCurrentHealth(), player.getTotalMaxHealth(world)));

    // Attack display
    int baseAttack = player.getBaseAttack();
    int totalAttack = player.getTotalAttack(world);
    int attackBonus = totalAttack - baseAttack;
    if (attackBonus > 0) {
      stats.append(
          String.format(
              "Attack: %d (+%d from equipment) = %d total\n",
              baseAttack, attackBonus, totalAttack));
    } else {
      stats.append(String.format("Attack: %d\n", baseAttack));
    }

    // Defense display
    int baseDefense = player.getBaseDefense();
    int totalDefense = player.getTotalDefense(world);
    int defenseBonus = totalDefense - baseDefense;
    if (defenseBonus > 0) {
      stats.append(
          String.format(
              "Defense: %d (+%d from equipment) = %d total\n",
              baseDefense, defenseBonus, totalDefense));
    } else {
      stats.append(String.format("Defense: %d\n", baseDefense));
    }

    stats.append("\nEquipped Items:\n");

    // Display equipped items
    boolean hasEquippedItems = false;
    for (Player.EquipmentSlot slot : Player.EquipmentSlot.values()) {
      String itemId = player.getEquippedItem(slot);
      String itemName = "(none)";

      if (itemId != null) {
        Item item = world.findItem(itemId).orElse(null);
        if (item != null) {
          itemName = item.getName();
          hasEquippedItems = true;
        }
      }

      stats.append(String.format("- %s: %s\n", formatSlotName(slot), itemName));
    }

    if (!hasEquippedItems) {
      stats.append("  No items equipped\n");
    }

    return stats.toString();
  }

  /**
   * Formats the equipment slot name for display.
   *
   * @param slot the equipment slot
   * @return formatted slot name
   */
  private String formatSlotName(Player.EquipmentSlot slot) {
    return switch (slot) {
      case WEAPON -> "Weapon";
      case ARMOR -> "Armor";
      case ARTIFACT -> "Artifact";
    };
  }
}
