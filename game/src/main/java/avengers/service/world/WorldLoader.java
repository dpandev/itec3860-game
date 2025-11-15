package avengers.service.world;

import avengers.domain.model.World;

/** Interface for loading a World. */
public interface WorldLoader {
  /**
   * Loads and returns the game world.
   *
   * @return the loaded World object
   */
  World load();
}
