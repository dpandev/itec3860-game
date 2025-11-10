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
  private final CombatController combatController;
  private boolean running;

  /** Creates a new GameController with initialized parser and scanner. */
  public GameController() {
    this.parser = new CommandParser(buildCommandDictionary(), buildDirectionSynonyms());
    this.scanner = new Scanner(System.in);
    this.combatController = new CombatController();
    this.running = false;

    // TODO: add other controller dependencies (e.g., InventoryController, etc.)
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
      case ATTACK:
        handleAttack();
        break;
      case DEFEND:
        handleDefend();
        break;
      case RUN:
        handleRun();
        break;
      case SAVE:
        handleSave();
        break;
      // TODO: Add more command handlers
      default:
        System.out.println("Command not yet implemented: " + command);
    }
  }

  /**
   * Gets the combat controller.
   *
   * @return The combat controller instance.
   */
  public CombatController getCombatController() {
    return combatController;
  }

  /**
   * Checks if saving is allowed.
   *
   * @return true if saving is allowed, false if blocked.
   */
  public boolean canSave() {
    return !combatController.inCombat();
  }

  private void showHelp() {
    System.out.println("Available commands:");
    System.out.println("  help - Show this help message");
    System.out.println("  go <direction> - Move in a direction (n, s, e, w, up, down)");
    System.out.println("  inventory - Show your inventory");
    System.out.println("\nCombat commands:");
    System.out.println("  attack - Attack the enemy");
    System.out.println("  defend - Take a defensive stance");
    System.out.println("  run - Attempt to flee from combat");
    System.out.println("\nOther commands:");
    System.out.println("  save - Save your game (not available during combat)");
    // TODO: Add more help text
  }

  private void handleGo(ParsedCommand parsed) {
    if (combatController.inCombat()) {
      System.out.println("You cannot move during combat!");
      return;
    }
    // TODO: pass to movement controller
    System.out.println("Movement not yet implemented.");
  }

  private void handleInventory() {
    // TODO: pass to inventory controller
    System.out.println("Inventory not yet implemented.");
  }

  private void handleAttack() {
    if (!combatController.inCombat()) {
      System.out.println("There is nothing to attack!");
      return;
    }

    for (String message : combatController.attack()) {
      System.out.println(message);
    }
  }

  private void handleDefend() {
    if (!combatController.inCombat()) {
      System.out.println("There is nothing to defend against!");
      return;
    }

    for (String message : combatController.defend()) {
      System.out.println(message);
    }
  }

  private void handleRun() {
    if (!combatController.inCombat()) {
      System.out.println("You are not in combat!");
      return;
    }

    for (String message : combatController.run()) {
      System.out.println(message);
    }
  }

  private void handleSave() {
    if (!canSave()) {
      System.out.println("Cannot save during combat!");
      return;
    }
    // TODO: implement save logic
    System.out.println("Save not yet implemented.");
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
