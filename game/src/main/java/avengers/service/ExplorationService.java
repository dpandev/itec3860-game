package avengers.service;

import avengers.domain.utils.GameContext;

/** Service interface for handling exploration-related operations including player stats display. */
public interface ExplorationService {
  //

  /**
   * Generates a formatted display of the player's current stats including health, attack, defense,
   * and equipped items.
   *
   * @param ctx the game context containing player and world information
   * @return formatted string displaying player stats and equipped items
   */
  String showStats(GameContext ctx);
}
