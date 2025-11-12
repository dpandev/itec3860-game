package avengers.io;

import avengers.model.item.Item;
import avengers.model.world.World;
import java.nio.file.Path;
import java.util.List;

/** Loads game resources from JSON files. */
public final class ResourceLoader {
  private final FileManager fileManager;

  /**
   * Constructor for ResourceLoader.
   *
   * @param fileManager The FileManager to use for loading files.
   */
  public ResourceLoader(FileManager fileManager) {
    this.fileManager = fileManager;
  }

  /**
   * Loads items from a JSON file.
   *
   * @param path The path to the items JSON file.
   * @return A list of Item objects.
   */
  public List<Item> loadItems(Path path) {
    // TODO: Implement item loading from JSON
    return List.of();
  }

  /**
   * Loads the world from a JSON file.
   *
   * @param path The path to the world JSON file.
   * @return A World object, or null if loading fails.
   */
  public World loadWorld(Path path) {
    // TODO: Implement world loading from JSON
    return null;
  }

  /**
   * Loads puzzles from a JSON file.
   *
   * @param path The path to the puzzles JSON file.
   * @return A list of Puzzle objects.
   */
  public List<Object> loadPuzzles(Path path) {
    // TODO: Implement puzzle loading from JSON (Puzzle class not yet implemented)
    return List.of();
  }
}
