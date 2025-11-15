package avengers.service.spi;

import avengers.domain.utils.SaveData;
import java.util.Optional;
import java.util.UUID;

/** Repository interface (Service Provider Interface) for managing player save data. */
public interface SaveRepository {

  /**
   * Finds the save data for a player by their unique identifier.
   *
   * @param id The unique identifier of the player.
   * @return An Optional containing the SaveData if found, or empty if not found.
   */
  Optional<SaveData> findByPlayerId(UUID id);

  /**
   * Inserts or updates the save data for a player.
   *
   * @param save The SaveData to be inserted or updated.
   */
  void upsert(SaveData save);
}
