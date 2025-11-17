package avengers.service;

import avengers.domain.utils.GameContext;

/**
 * Service interface for handling map display operations. Provides ASCII rendering of explored rooms
 * and current player position.
 */
public interface MapService {

  /**
   * Generates an ASCII map showing the current room and nearby rooms. Shows both visited rooms and
   * adjacent unexplored rooms with different symbols. Includes a legend with available exits and
   * current room information.
   *
   * @param ctx the game context containing player and world information
   * @return formatted string displaying the ASCII map with legend
   */
  String showMap(GameContext ctx);

  /**
   * Generates an ASCII map showing all visited rooms. Provides a broader view of the player's
   * exploration progress.
   *
   * @param ctx the game context containing player and world information
   * @return formatted string displaying the full exploration map with legend
   */
  String showFullMap(GameContext ctx);
}
