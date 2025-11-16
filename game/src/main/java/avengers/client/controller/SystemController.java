package avengers.client.controller;

import avengers.domain.model.World;
import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.service.SaveService;
import avengers.service.world.WorldLoader;

/** Controller to handle system commands like help, save, load, and quit. */
public class SystemController implements CommandController {
  private final SaveService save;
  private final WorldLoader worldLoader;

  /** Constructs a SystemController with the given SaveService, ConsoleView, and WorldLoader. */
  public SystemController(SaveService save, WorldLoader worldLoader) {
    this.save = save;
    this.worldLoader = worldLoader;
    //
  }

  /** Handles system commands and returns the result. */
  @Override
  public CommandResult handle(CommandToken cmd, GameContext ctx) {
    return switch (cmd.verb()) {
      case HELP ->
          CommandResult.success(
              "Available commands:\n"
                  + "  explore - Look around the room\n"
                  + "  map - Display a map of nearby rooms\n"
                  + "  go <dir> - Move in a direction (north, south, east, west, up, down)\n"
                  + "  inventory - View your inventory\n"
                  + "  pickup <item> - Pick up an item\n"
                  + "  drop <item> - Drop an item\n"
                  + "  inspect <item/monster> - Examine an item or monster\n"
                  + "  equip <item> - Equip an item\n"
                  + "  unequip <item> - Unequip an item\n"
                  + "  use <item> - Use an item\n"
                  + "  attack <monster> - Attack a monster (starts combat)\n"
                  + "  ignore <monster> - Ignore a monster (makes it disappear)\n"
                  + "  save - Save your game\n"
                  + "  quit - Save and quit the game");
      case SAVE -> {
        save.saveData(ctx);
        yield CommandResult.success("Game saved successfully.");
      }
      case LOAD -> {
        var playerId = ctx.player().getId();
        var loadedCtx = save.load(playerId); // no multiple saves per player, just one
        if (loadedCtx.isEmpty()) {
          yield CommandResult.fail("No saved game found for player ID: " + playerId);
        }
        yield save.applySave(ctx, loadedCtx.get()); // this also returns CommandResult
      }
      case NEW_GAME -> {
        // Reload the world from scratch to reset all state properly
        World freshWorld = worldLoader.load();
        ctx.resetGame(freshWorld, "Player");

        yield CommandResult.success(
            "\n=== NEW GAME STARTED ===\n"
                + "Your adventure begins anew!\n"
                + "All monsters, items, and puzzles have been reset.\n"
                + "Type 'look' to see your surroundings.");
      }
      case QUIT -> {
        save.saveData(ctx);
        yield CommandResult.exit("Game saved. Goodbye!");
      }
      default -> CommandResult.fail("Unknown command: " + cmd.verb());
    };
  }
}
