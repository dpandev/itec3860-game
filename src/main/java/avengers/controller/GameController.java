package avengers.controller;

import avengers.command.Command;
import avengers.command.CommandParser;
import avengers.command.ParsedCommand;
import avengers.model.world.Direction;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/** Controller to handle/route game commands. */
public final class GameController {
  private final CommandParser parser;
  private final Scanner scanner;
  private boolean running;

  /** Creates a new GameController with initialized parser and scanner. */
  public GameController() {
    this.parser = new CommandParser(buildCommandDictionary(), buildDirectionSynonyms());
    this.scanner = new Scanner(System.in);
    this.running = false;

    // TODO: add other controller dependencies (e.g., CombatController, InventoryController, etc.)
  }

  /** Starts the game loop. */
  public void start() {
    running = true;
    System.out.println("Welcome to Solo Leveling!");
    System.out.println("Type 'help' for available commands.");

    while (running) {
      System.out.print("> ");
      String input = scanner.nextLine();
      route(input);
    }

    scanner.close();
  }

  /**
   * Routes the given command to the appropriate handler.
   *
   * @param input The raw input string from the user.
   */
  public void route(String input) {
    ParsedCommand parsed = parser.parse(input);

    if (parsed == null) {
      System.out.println("Unknown command. Type 'help' for available commands.");
      return;
    }

    Command command = parsed.getCommand();

    // Route to appropriate handler based on command type
    switch (command) {
      case HELP:
        showHelp();
        break;
      case GO:
        handleGo(parsed);
        break;
      case INVENTORY:
        handleInventory();
        break;
      // TODO: Add more command handlers
      default:
        System.out.println("Command not yet implemented: " + command);
    }
  }

  private void showHelp() {
    System.out.println("Available commands:");
    System.out.println("  help - Show this help message");
    System.out.println("  go <direction> - Move in a direction (n, s, e, w, up, down)");
    System.out.println("  inventory - Show your inventory");
    // TODO: Add more help text
  }

  private void handleGo(ParsedCommand parsed) {
    // TODO: pass to movement controller
  }

  private void handleInventory() {
    // TODO: pass to inventory controller
  }

  private Map<String, Command> buildCommandDictionary() {
    Map<String, Command> dict = new HashMap<>();
    dict.put("explore", Command.EXPLORE);
    dict.put("go", Command.GO);
    dict.put("move", Command.GO);
    dict.put("map", Command.MAP);
    dict.put("attack", Command.ATTACK);
    dict.put("fight", Command.ATTACK);
    dict.put("defend", Command.DEFEND);
    dict.put("block", Command.DEFEND);
    dict.put("run", Command.RUN);
    dict.put("flee", Command.RUN);
    dict.put("use", Command.USE);
    dict.put("equip", Command.EQUIP);
    dict.put("unequip", Command.UNEQUIP);
    dict.put("pickup", Command.PICKUP);
    dict.put("take", Command.PICKUP);
    dict.put("drop", Command.DROP);
    dict.put("activate", Command.ACTIVATE);
    dict.put("inventory", Command.INVENTORY);
    dict.put("inv", Command.INVENTORY);
    dict.put("save", Command.SAVE);
    dict.put("load", Command.LOAD);
    dict.put("help", Command.HELP);
    dict.put("inspect", Command.INSPECT);
    dict.put("look", Command.INSPECT);
    dict.put("examine", Command.INSPECT);
    // TODO: Add puzzle-specific commands as needed
    return dict;
  }

  private Map<String, Direction> buildDirectionSynonyms() {
    Map<String, Direction> dirs = new HashMap<>();
    dirs.put("n", Direction.N);
    dirs.put("north", Direction.N);
    dirs.put("s", Direction.S);
    dirs.put("south", Direction.S);
    dirs.put("e", Direction.E);
    dirs.put("east", Direction.E);
    dirs.put("w", Direction.W);
    dirs.put("west", Direction.W);
    dirs.put("up", Direction.UP);
    dirs.put("u", Direction.UP);
    dirs.put("down", Direction.DOWN);
    dirs.put("d", Direction.DOWN);
    return dirs;
  }
}
