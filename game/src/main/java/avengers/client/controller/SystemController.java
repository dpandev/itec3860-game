package avengers.client.controller;

import avengers.domain.model.World;
import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;
import avengers.service.ExplorationService;
import avengers.service.SaveService;
import avengers.service.world.WorldLoader;

/** Controller to handle system commands like help, save, load, and quit. */
public class SystemController implements CommandController {
  private final SaveService save;
  private final WorldLoader worldLoader;
  private final ExplorationService explorationService;

  /**
   * Constructs a SystemController with the given SaveService, WorldLoader, and ExplorationService.
   */
  public SystemController(
      SaveService save, WorldLoader worldLoader, ExplorationService explorationService) {
    this.save = save;
    this.worldLoader = worldLoader;
    this.explorationService = explorationService;
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
                  + "  stats - Display player stats and equipped items\n"
                  + "  save - Save your game\n"
                  + "  quit - Save and quit the game");
      case SAVE -> {
        // Check if at max capacity
        if (save.isAtMaxCapacity()) {
          yield CommandResult.success(
              "╔═══════════════════════════════════════════════════════════╗\n"
                  + "║          MAXIMUM SAVE SLOTS REACHED (10/10)             ║\n"
                  + "╠═══════════════════════════════════════════════════════════╣\n"
                  + "║  You have reached the maximum number of save slots.     ║\n"
                  + "║  Please use 'load' command to select a save to          ║\n"
                  + "║  overwrite, or manually delete old saves.               ║\n"
                  + "║                                                          ║\n"
                  + "║  Current save files:                                    ║\n"
                  + "║  "
                  + String.format(
                      "%-55s", save.listSaveFiles().size() + " save(s) in saves/ directory")
                  + "║\n"
                  + "╚═══════════════════════════════════════════════════════════╝\n"
                  + "\nTip: The game will automatically overwrite your current\n"
                  + "     save file when you continue playing and save again.");
        }
        save.saveData(ctx);
        int currentCount = save.listSaveFiles().size();
        yield CommandResult.success(
            "Game saved successfully. (Save slot "
                + currentCount
                + "/"
                + save.getMaxSaveSlots()
                + ")");
      }
      case LOAD -> {
        // List all available save files for player to choose from
        var availableSaves = save.listSaveFiles();

        if (availableSaves.isEmpty()) {
          yield CommandResult.fail(
              "No saved games found.\n"
                  + "Save files should be located in: saves/\n"
                  + "Use 'save' command to create a new save.");
        }

        // Build save selection menu
        StringBuilder loadMenu = new StringBuilder();
        loadMenu.append("\n╔═══════════════════════════════════════════════════════════╗\n");
        loadMenu.append("║                    AVAILABLE SAVE FILES                  ║\n");
        loadMenu.append("╠═══════════════════════════════════════════════════════════╣\n");

        for (int i = 0; i < availableSaves.size(); i++) {
          String fileName = availableSaves.get(i);
          loadMenu.append(String.format("║  [%d] %-54s║\n", i + 1, fileName));
        }

        loadMenu.append("╚═══════════════════════════════════════════════════════════╝\n");
        loadMenu.append("\nNote: Use the main menu 'Load Game' option to select a save.\n");
        loadMenu.append("The in-game 'load' command is for reference only.\n");
        loadMenu.append("To load a different save, please restart the game and use\n");
        loadMenu.append("the main menu.");

        yield CommandResult.success(loadMenu.toString());
      }
      case NEW_GAME -> {
        // Reload the world from scratch to reset all state properly
        World freshWorld = worldLoader.load();
        ctx.resetGame(freshWorld, "Player");

        yield CommandResult.success(
            "\n=== NEW GAME STARTED ===\n"
                + "Your adventure begins anew!\n"
                + "All monsters, items, and puzzles have been reset.\n"
                + "Type 'explore' to see your surroundings.");
      }
      case Verb.STATS -> CommandResult.success(explorationService.showStats(ctx));
      case QUIT -> {
        save.saveData(ctx);
        yield CommandResult.exit("Game saved. Goodbye!");
      }
      default -> CommandResult.fail("Unknown command: " + cmd.verb());
    };
  }
}
