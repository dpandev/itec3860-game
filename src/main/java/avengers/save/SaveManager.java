package avengers.save;

import avengers.io.FileManager;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Manages saving and loading of game data. */
public final class SaveManager {
  private static final long AUTO_SAVE_COOLDOWN_MS = 180_000;
  private static final int MAX_SLOTS = 10;
  private static final String SAVE_FILE_PREFIX = "save_slot_";
  private static final String SAVE_FILE_EXTENSION = ".json";

  private final FileManager files;
  private long lastAutosaveMs = 0;

  /**
   * Constructor for SaveManager.
   *
   * @param files The FileManager instance to handle file operations
   */
  public SaveManager(FileManager files) {
    this.files = files;
  }

  /**
   * Determines if the game can be saved now based on conditions.
   *
   * @param inCombat Whether the player is currently in combat.
   * @param inPuzzle Whether the player is currently solving a puzzle.
   * @return true if the game can be saved now, false otherwise.
   */
  public boolean canSaveNow(boolean inCombat, boolean inPuzzle) {
    return !inCombat && !inPuzzle;
  }

  /**
   * Saves the game data to the specified slot.
   *
   * @param slot The save slot number (1-10).
   * @param data The SaveData to be saved.
   * @return true if the save was successful, false otherwise.
   */
  public boolean save(int slot, SaveData data) {
    if (slot < 1 || slot > MAX_SLOTS) {
      return false;
    }
    Path savePath = Path.of(SAVE_FILE_PREFIX + slot + SAVE_FILE_EXTENSION);
    return files.writeJson(savePath, data);
  }

  /**
   * Loads the save data from the specified slot.
   *
   * @param slot The save slot number (1-10).
   * @return An Optional containing the SaveData if found, or empty if not found.
   */
  public Optional<SaveData> load(int slot) {
    if (slot < 1 || slot > MAX_SLOTS) {
      return Optional.empty();
    }
    Path savePath = Path.of(SAVE_FILE_PREFIX + slot + SAVE_FILE_EXTENSION);
    return files.readJson(savePath, SaveData.class);
  }

  /**
   * Lists all available saves with their summaries.
   *
   * @return A list of SaveSummary objects representing the saves.
   */
  public List<SaveSummary> list() {
    List<SaveSummary> summaries = new ArrayList<>();
    for (int slot = 1; slot <= MAX_SLOTS; slot++) {
      Optional<SaveData> saveData = load(slot);
      if (saveData.isPresent()) {
        SaveData data = saveData.get();
        SaveSummary summary =
            new SaveSummary(
                slot,
                formatTimestamp(data.getTimePlayedMs()),
                data.getCurrentRoom(),
                data.getPlayer().getHp());
        summaries.add(summary);
      }
    }
    return summaries;
  }

  /**
   * Automatically saves the game if conditions are met.
   *
   * @param data The SaveData to be saved.
   */
  public void autosaveIfDue(SaveData data) {
    long currentTimeMs = System.currentTimeMillis();
    if (currentTimeMs - lastAutosaveMs < AUTO_SAVE_COOLDOWN_MS) {
      return;
    }

    // Save to slot 0 (autosave slot)
    Path autosavePath = Path.of("autosave" + SAVE_FILE_EXTENSION);
    if (files.writeJson(autosavePath, data)) {
      lastAutosaveMs = currentTimeMs;
    }
  }

  /**
   * Formats time played in milliseconds to a readable timestamp.
   *
   * @param timePlayedMs The time played in milliseconds.
   * @return A formatted timestamp string.
   */
  private String formatTimestamp(long timePlayedMs) {
    long hours = timePlayedMs / 3_600_000;
    long minutes = (timePlayedMs % 3_600_000) / 60_000;
    return String.format("%dh %dm", hours, minutes);
  }
}
