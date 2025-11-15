package avengers.client.controller;

import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;
import avengers.service.InventoryService;

/** Controller for managing inventory-related commands in the Avengers game. */
public class InventoryController implements CommandController {
  private final InventoryService inventoryService;

  /** Constructs an InventoryController with a new InventoryService instance. */
  public InventoryController() {
    this.inventoryService = new InventoryService();
  }

  @Override
  public boolean supports(Verb verb) {
    return verb == Verb.INVENTORY
        || verb == Verb.PICKUP
        || verb == Verb.DROP
        || verb == Verb.INSPECT;
  }

  @Override
  public CommandResult handle(CommandToken cmd, GameContext ctx) {
    return switch (cmd.verb()) {
      case INVENTORY -> inventoryService.showInventory(ctx);
      case PICKUP -> handlePickup(cmd, ctx);
      case DROP -> handleDrop(cmd, ctx);
      case INSPECT -> handleInspect(cmd, ctx);
      default -> CommandResult.fail("Unsupported inventory command: " + cmd.verb());
    };
  }

  /**
   * Handles the PICKUP command.
   *
   * @param cmd the command token
   * @param ctx the game context
   * @return the result of the pickup operation
   */
  private CommandResult handlePickup(CommandToken cmd, GameContext ctx) {
    if (cmd.args().isEmpty()) {
      return CommandResult.fail("What do you want to pick up? Usage: pickup <item name>");
    }

    String itemName = String.join(" ", cmd.args());
    return inventoryService.pickupItem(ctx, itemName);
  }

  /**
   * Handles the DROP command.
   *
   * @param cmd the command token
   * @param ctx the game context
   * @return the result of the drop operation
   */
  private CommandResult handleDrop(CommandToken cmd, GameContext ctx) {
    if (cmd.args().isEmpty()) {
      return CommandResult.fail("What do you want to drop? Usage: drop <item name>");
    }

    String itemName = String.join(" ", cmd.args());
    return inventoryService.dropItem(ctx, itemName);
  }

  /**
   * Handles the INSPECT command.
   *
   * @param cmd the command token
   * @param ctx the game context
   * @return the result of the inspect operation
   */
  private CommandResult handleInspect(CommandToken cmd, GameContext ctx) {
    if (cmd.args().isEmpty()) {
      return CommandResult.fail("What do you want to inspect? Usage: inspect <item/monster name>");
    }

    String targetName = String.join(" ", cmd.args());
    return inventoryService.inspectTarget(ctx, targetName);
  }
}
