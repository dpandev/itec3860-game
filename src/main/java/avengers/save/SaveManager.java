package avengers.save;

import avengers.io.FileManager;
import java.util.List;
import java.util.Optional;

/** Manages saving and loading of game data. */
public final class SaveManager {
  private static final long AUTO_SAVE_COOLDOWN_MS = 180_000;
  private static final int MAX_SLOTS = 3;
  private final FileManager fileManager;
  private long lastAutoSaveMs = 0;

  /**
   * Constructor for SaveManager
   *
   * @param fileManager The FileManager instance to handle file operations
   */
  public SaveManager(FileManager fileManager) {
    this.fileManager = fileManager;
  }

  /**
   * Determines if the game can be saved now based on conditions.
   *
   * @param inCombat Whether the player is currently in combat.
   * @param inPuzzle Whether the player is currently solving a puzzle.
   * @return true if the game can be saved now, false otherwise.
   */
  public boolean canSaveNow(boolean inCombat, boolean inPuzzle) {
    long currentTimeMs = System.currentTimeMillis();
    if (inCombat || inPuzzle) {
      return false;
    }
    if (currentTimeMs - lastAutoSaveMs < AUTO_SAVE_COOLDOWN_MS) {
      return false;
    }
    return true;
  }

  /** Marks the current time as the last auto-save time. */
  public void markAutoSaved() {
    lastAutoSaveMs = System.currentTimeMillis();
  }

  /**
   * Saves the game data to the specified slot.
   *
   * @param slot The save slot number.
   * @param data The SaveData to be saved.
   * @return true if the save was successful, false otherwise.
   */
  public boolean save(int slot, SaveData data) {
    if (slot < 1 || slot > MAX_SLOTS) {
      return false;
    }
    // return fileManager.saveToFile(slot, data);
    // TODO: implement saveToFile in FileManager
    return false;
  }

  /**
   * Loads the save data from the specified slot.
   *
   * @param slot The save slot number.
   * @return An Optional containing the SaveData if found, or empty if not found.
   */
  public Optional<SaveData> load(int slot) {
    if (slot < 1 || slot > MAX_SLOTS) {
      return Optional.empty();
    }
    // return fileManager.loadFromFile(slot);
    // TODO: implement loadFromFile in FileManager
    return Optional.empty();
  }

  /**
   * Lists all available saves with their summaries.
   *
   * @return A list of SaveSummary objects representing the saves.
   */
  public List<SaveSummary> list() {
    // return fileManager.listSaves(MAX_SLOTS);
    // TODO: implement listSaves in FileManager
    return List.of();
  }

  /**
   * Automatically saves the game if conditions are met.
   *
   * @param data The SaveData to be saved.
   * @param inCombat Whether the player is currently in combat.
   * @param inPuzzle Whether the player is currently solving a puzzle.
   */
  public void autoSaveIfDue(SaveData data, boolean inCombat, boolean inPuzzle) {
    if (!canSaveNow(inCombat, inPuzzle)) {
      return;
    }
    markAutoSaved();
    // fileManager.saveToFile(0, data); // 0 for auto-save slot
    // TODO: implement saveToFile in FileManager
  }
}
