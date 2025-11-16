package avengers.service;

import avengers.domain.utils.CommandResult;
import avengers.domain.utils.GameContext;

/** Service interface for handling exploration-related operations including player stats display. */
public interface ExplorationService {

  /**
   * Generates a formatted display of the player's current stats including health, attack, defense,
   * and equipped items.
   *
   * @param ctx the game context containing player and world information
   * @return formatted string displaying player stats and equipped items
   */
  String showStats(GameContext ctx);

  /**
   * Explores and describes the current room, showing name, description, items, exits, and any alive
   * monsters.
   *
   * @param ctx the game context
   * @return CommandResult with room exploration details
   */
  CommandResult explore(GameContext ctx);

  /**
   * Describes the current room without changing game state.
   *
   * @param ctx the game context
   * @return formatted description of the current room
   */
  String describeCurrentRoom(GameContext ctx);

  /**
   * Moves the player in the specified direction if a valid exit exists.
   *
   * @param ctx the game context
   * @param direction the direction to move (north, south, east, west)
   * @return CommandResult indicating success or failure of movement
   */
  CommandResult move(GameContext ctx, String direction);
}
