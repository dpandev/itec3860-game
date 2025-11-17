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

  /**
   * Lists all available save file names.
   *
   * @return List of save file names
   */
  java.util.List<String> listAllSaves();

  /**
   * Loads save data from a specific file by name.
   *
   * @param fileName The name of the save file
   * @return An Optional containing the SaveData if found, or empty if not found
   */
  Optional<SaveData> loadFromFile(String fileName);

  /**
   * Deletes a save file by name.
   *
   * @param fileName The name of the save file to delete
   * @return true if deleted successfully, false otherwise
   */
  boolean deleteSave(String fileName);
}
