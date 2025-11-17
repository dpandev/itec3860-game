package avengers.client.runtime;

import avengers.client.view.ConsoleView;
import avengers.domain.model.Player;
import avengers.domain.model.World;
import avengers.domain.utils.GameContext;
import avengers.service.SaveService;
import avengers.service.world.WorldLoader;
import java.util.List;

/**
 * Main menu for the game startup screen. Provides options to start a new game, load a saved game,
 * view help, or exit.
 */
public class MainMenu {
  private final ConsoleView view;
  private final WorldLoader worldLoader;
  private final SaveService saveService;

  /**
   * Constructs a MainMenu.
   *
   * @param view the console view for output
   * @param worldLoader the world loader for creating new games
   * @param saveService the save service for loading games
   */
  public MainMenu(ConsoleView view, WorldLoader worldLoader, SaveService saveService) {
    this.view = view;
    this.worldLoader = worldLoader;
    this.saveService = saveService;
  }

  /**
   * Displays the main menu and returns the selected game context (or null to exit).
   *
   * @return GameContext for a new or loaded game, or null to exit
   */
  public GameContext show() {
    try {
      return showInternal();
    } catch (java.io.IOException e) {
      view.println("\nError reading input: " + e.getMessage());
      return null;
    }
  }

  /**
   * Internal method that displays the main menu (allows IOException to propagate).
   *
   * @return GameContext for a new or loaded game, or null to exit
   * @throws java.io.IOException if an I/O error occurs
   */
  private GameContext showInternal() throws java.io.IOException {
    while (true) {
      displayMainMenu();
      String choice = view.readLine().trim().toLowerCase();

      switch (choice) {
        case "1":
        case "start":
        case "new":
          return startNewGame();

        case "2":
        case "load":
          GameContext loadedContext = loadGame();
          if (loadedContext != null) {
            return loadedContext;
          }
          // If load failed or cancelled, loop back to main menu
          break;

        case "3":
        case "help":
          displayHelp();
          break;

        case "4":
        case "exit":
        case "quit":
          view.println("\nThank you for playing Solo Leveling!");
          view.println("May the shadows be with you...\n");
          return null;

        default:
          view.println("\nInvalid choice. Please enter 1-4 or a menu option.\n");
      }
    }
  }

  /** Displays the main menu screen. */
  private void displayMainMenu() {
    view.println("╔═══════════════════════════════════════════════════════════╗");
    view.println("║                                                           ║");
    view.println("║              ⚔  SOLO LEVELING: SHADOW QUEST  ⚔           ║");
    view.println("║                                                           ║");
    view.println("║            Rise from E-Rank to Shadow Monarch            ║");
    view.println("║                                                           ║");
    view.println("╠═══════════════════════════════════════════════════════════╣");
    view.println("║                      MAIN MENU                            ║");
    view.println("╠═══════════════════════════════════════════════════════════╣");
    view.println("║                                                           ║");
    view.println("║  [1] Start Game    - Begin a new adventure               ║");
    view.println("║  [2] Load Game     - Continue a saved game               ║");
    view.println("║  [3] Help          - View commands and instructions      ║");
    view.println("║  [4] Exit          - Quit the game                       ║");
    view.println("║                                                           ║");
    view.println("╚═══════════════════════════════════════════════════════════╝");
    view.println("");
    view.println("Enter your choice (1-4):");
  }

  /**
   * Starts a new game by loading the world and creating a new player.
   *
   * @return GameContext for the new game
   */
  private GameContext startNewGame() {
    view.println("\n╔═══════════════════════════════════════════════════════════╗");
    view.println("║                    STARTING NEW GAME...                   ║");
    view.println("╚═══════════════════════════════════════════════════════════╝\n");

    World world = worldLoader.load();
    Player player = new Player("Hunter", world.getStartRoomId());

    view.println("Welcome, Hunter!");
    view.println("You awaken as an E-Rank Hunter in a world of dungeons and monsters.");
    view.println("Your journey to become the Shadow Monarch begins now...\n");

    return new GameContext(world, player);
  }

  /**
   * Loads a saved game by displaying available saves and prompting for selection.
   *
   * @return GameContext for the loaded game, or null if cancelled/failed
   * @throws java.io.IOException if an I/O error occurs
   */
  private GameContext loadGame() throws java.io.IOException {
    view.println("\n╔═══════════════════════════════════════════════════════════╗");
    view.println("║                      LOAD SAVED GAME                      ║");
    view.println("╚═══════════════════════════════════════════════════════════╝\n");

    List<String> availableSaves = saveService.listSaveFiles();

    if (availableSaves.isEmpty()) {
      view.println("No saved games found.");
      view.println("Press Enter to return to main menu...");
      view.readLine();
      return null;
    }

    view.println("Available saved games:\n");
    for (int i = 0; i < availableSaves.size(); i++) {
      view.println(String.format("  [%d] %s", i + 1, availableSaves.get(i)));
    }
    view.println("\n  [0] Cancel - Return to main menu");

    view.println("\nSelect a save file (0-" + availableSaves.size() + "):");
    String input = view.readLine().trim();

    try {
      int choice = Integer.parseInt(input);

      if (choice == 0) {
        return null; // Cancelled
      }

      if (choice < 1 || choice > availableSaves.size()) {
        view.println("\nInvalid selection.\n");
        return null;
      }

      String saveFileName = availableSaves.get(choice - 1);
      GameContext loadedContext = saveService.loadGame(worldLoader, saveFileName);

      if (loadedContext != null) {
        view.println("\n✓ Game loaded successfully!");
        view.println("Welcome back, " + loadedContext.player().getName() + "!\n");
        return loadedContext;
      } else {
        view.println("\n✗ Failed to load game file.\n");
        return null;
      }

    } catch (NumberFormatException e) {
      view.println("\nInvalid input. Please enter a number.\n");
      return null;
    }
  }

  /**
   * Displays the help screen with game instructions and commands.
   *
   * @throws java.io.IOException if an I/O error occurs
   */
  private void displayHelp() throws java.io.IOException {
    view.println("\n╔═══════════════════════════════════════════════════════════╗");
    view.println("║                     GAME INSTRUCTIONS                     ║");
    view.println("╠═══════════════════════════════════════════════════════════╣");
    view.println("║                                                           ║");
    view.println("║  OBJECTIVE:                                               ║");
    view.println("║  Rise from E-Rank Hunter to the Shadow Monarch by        ║");
    view.println("║  exploring dungeons, defeating monsters, solving          ║");
    view.println("║  puzzles, and collecting powerful artifacts.              ║");
    view.println("║                                                           ║");
    view.println("╠═══════════════════════════════════════════════════════════╣");
    view.println("║  MOVEMENT COMMANDS:                                       ║");
    view.println("╠═══════════════════════════════════════════════════════════╣");
    view.println("║  go <direction>  - Move in a direction (north/south/etc.) ║");
    view.println("║  explore         - Look around current room               ║");
    view.println("║  map             - View nearby explored areas             ║");
    view.println("║  map full        - View complete exploration map          ║");
    view.println("║                                                           ║");
    view.println("╠═══════════════════════════════════════════════════════════╣");
    view.println("║  COMBAT COMMANDS:                                         ║");
    view.println("╠═══════════════════════════════════════════════════════════╣");
    view.println("║  attack <monster>  - Engage in combat                     ║");
    view.println("║  defend            - Block incoming attack                ║");
    view.println("║  ignore <monster>  - Avoid fighting a creature            ║");
    view.println("║                                                           ║");
    view.println("╠═══════════════════════════════════════════════════════════╣");
    view.println("║  INVENTORY COMMANDS:                                      ║");
    view.println("╠═══════════════════════════════════════════════════════════╣");
    view.println("║  inventory         - View your items and equipment        ║");
    view.println("║  pickup <item>     - Take an item from the room           ║");
    view.println("║  drop <item>       - Drop an item in current room         ║");
    view.println("║  equip <item>      - Equip weapon/armor/artifact          ║");
    view.println("║  unequip <slot>    - Remove equipped item                 ║");
    view.println("║  use <item>        - Use a consumable item                ║");
    view.println("║  activate <item>   - Activate an artifact's power         ║");
    view.println("║  inspect <target>  - Examine item or monster              ║");
    view.println("║                                                           ║");
    view.println("╠═══════════════════════════════════════════════════════════╣");
    view.println("║  PUZZLE COMMANDS:                                         ║");
    view.println("╠═══════════════════════════════════════════════════════════╣");
    view.println("║  solve <answer>    - Answer an active puzzle              ║");
    view.println("║  hint              - Get a hint for current puzzle        ║");
    view.println("║  ignore            - Skip puzzle (no rewards)             ║");
    view.println("║                                                           ║");
    view.println("╠═══════════════════════════════════════════════════════════╣");
    view.println("║  SYSTEM COMMANDS:                                         ║");
    view.println("╠═══════════════════════════════════════════════════════════╣");
    view.println("║  help              - Show this help screen                ║");
    view.println("║  stats             - View character statistics            ║");
    view.println("║  save              - Save your current progress           ║");
    view.println("║  load              - Load a saved game                    ║");
    view.println("║  quit              - Exit the game                        ║");
    view.println("║                                                           ║");
    view.println("╠═══════════════════════════════════════════════════════════╣");
    view.println("║  TIPS:                                                    ║");
    view.println("╠═══════════════════════════════════════════════════════════╣");
    view.println("║  • Explore thoroughly to find hidden items and puzzles   ║");
    view.println("║  • Activate artifacts to gain passive bonuses            ║");
    view.println("║  • Save frequently to preserve your progress             ║");
    view.println("║  • Use the map to navigate complex dungeons              ║");
    view.println("║  • Some puzzles require specific items to access         ║");
    view.println("║                                                           ║");
    view.println("╚═══════════════════════════════════════════════════════════╝");
    view.println("\nPress Enter to return to main menu...");
    view.readLine();
  }
}
