package avengers.client.controller;

import avengers.domain.model.Room;
import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;
import avengers.service.InteractionService;
import java.util.Optional;

/** Controller responsible for handling interactions within the game. */
public class InteractionController implements CommandController {
  private final InteractionService interactionService;

  /**
   * Constructs InteractionController with interaction service.
   *
   * @param interactionService the service for puzzle interactions
   */
  public InteractionController(InteractionService interactionService) {
    this.interactionService = interactionService;
  }

  @Override
  public boolean supports(Verb verb) {
    return verb == Verb.ACTIVATE || verb == Verb.SOLVE || verb == Verb.HINT || verb == Verb.INSPECT;
  }

  @Override
  public CommandResult handle(CommandToken cmd, GameContext ctx) {
    // Implementation for handling interaction commands goes here
    if (cmd == null || ctx == null) {
      return CommandResult.fail("Invalid command or context.");
    }

    return switch (cmd.verb()) {
      case SOLVE -> handleSolve(cmd, ctx);
      case HINT -> handleHint(ctx);
      case INSPECT -> handleInspect(cmd, ctx);
      case ACTIVATE -> handleActivate(cmd, ctx);
      default -> CommandResult.fail("Interaction command not supported: " + cmd.verb());
    };
  }

  /**
   * Handles SOLVE command.
   *
   * @param cmd command token
   * @param ctx game context
   * @return command result
   */
  private CommandResult handleSolve(CommandToken cmd, GameContext ctx) {
    if (!ctx.isAwaitingPuzzleAnswer()) {
      return CommandResult.fail("There's no puzzle waiting for an answer.");
    }

    if (!cmd.hasTarget()) {
      return CommandResult.fail("What's your answer? Usage: solve <answer>");
    }

    String answer = cmd.target();

    // Find puzzle in current room
    Optional<Room> roomOpt = ctx.world().findRoom(ctx.player().getRoomId());
    if (roomOpt.isEmpty()) {
      return CommandResult.fail("Error: Current room not found.");
    }

    Room room = roomOpt.get();
    if (!room.hasPuzzles()) {
      return CommandResult.fail("No puzzle in this room.");
    }

    // Get first unsolved puzzle
    String puzzleId =
        room.getPuzzleIds().stream()
            .filter(id -> ctx.world().findPuzzle(id).map(p -> !p.isSolved()).orElse(false))
            .findFirst()
            .orElse(null);

    if (puzzleId == null) {
      return CommandResult.fail("No active puzzle in this room.");
    }

    InteractionService.SolveResult result = interactionService.solve(ctx, puzzleId, answer);
    return CommandResult.success(result.getMessage());
  }

  /**
   * Handles HINT command.
   *
   * @param ctx game context
   * @return command result
   */
  private CommandResult handleHint(GameContext ctx) {
    // Find puzzle in current room
    Optional<Room> roomOpt = ctx.world().findRoom(ctx.player().getRoomId());
    if (roomOpt.isEmpty()) {
      return CommandResult.fail("Error: Current room not found.");
    }

    Room room = roomOpt.get();
    if (!room.hasPuzzles()) {
      return CommandResult.fail("There's no puzzle here.");
    }

    // Get first unsolved puzzle
    var puzzleOpt =
        room.getPuzzleIds().stream()
            .map(ctx.world()::findPuzzle)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(p -> !p.isSolved())
            .findFirst();

    if (puzzleOpt.isEmpty()) {
      return CommandResult.fail("All puzzles in this room are solved.");
    }

    return CommandResult.success(
        "\n💡 Hint: Read the puzzle description carefully. The answer is often hidden in plain sight.\n");
  }

  /**
   * Handles INSPECT command.
   *
   * @param cmd command token
   * @param ctx game context
   * @return command result
   */
  private CommandResult handleInspect(CommandToken cmd, GameContext ctx) {
    if (!cmd.hasTarget()) {
      return CommandResult.fail("Inspect what? Specify an item or monster.");
    }

    String target = cmd.target();

    // Try to find item
    var itemOpt =
        ctx.world().getItems().values().stream()
            .filter(item -> item.getName().equalsIgnoreCase(target))
            .findFirst();

    if (itemOpt.isPresent()) {
      var item = itemOpt.get();
      StringBuilder sb = new StringBuilder();
      sb.append("\n=== ").append(item.getName()).append(" ===\n");
      sb.append(item.getDescription()).append("\n");
      sb.append("\nCategory: ").append(item.getCategory()).append("\n");
      if (item.getEffect() != null && !item.getEffect().isBlank()) {
        sb.append("Effect: ").append(item.getEffect()).append("\n");
      }
      if (item.hasSpecialEffect()) {
        sb.append("Special: ").append(item.getSpecialEffect()).append("\n");
      }
      return CommandResult.success(sb.toString());
    }

    // Try to find monster
    var monsterOpt =
        ctx.world().getMonsters().values().stream()
            .filter(monster -> monster.getName().equalsIgnoreCase(target))
            .findFirst();

    if (monsterOpt.isPresent()) {
      var monster = monsterOpt.get();
      StringBuilder sb = new StringBuilder();
      sb.append("\n=== ").append(monster.getName()).append(" ===\n");
      sb.append("HP: ")
          .append(monster.getCurrentHealth())
          .append("/")
          .append(monster.getMaxHealth())
          .append("\n");
      sb.append("Attack: ").append(monster.getBaseAttack()).append("\n");
      sb.append("Defense: ").append(monster.getBaseDefense()).append("\n");
      return CommandResult.success(sb.toString());
    }

    return CommandResult.fail("You don't see '" + target + "' here.");
  }

  /**
   * Handles ACTIVATE command.
   *
   * @param cmd command token
   * @param ctx game context
   * @return command result
   */
  private CommandResult handleActivate(CommandToken cmd, GameContext ctx) {
    if (!cmd.hasTarget()) {
      return CommandResult.fail("Activate what?");
    }

    return CommandResult.success(
        "You attempt to activate " + cmd.target() + ", but nothing happens.");
  }
}
