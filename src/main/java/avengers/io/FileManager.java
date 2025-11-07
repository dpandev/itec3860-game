package avengers.io;

import java.nio.file.Path;
import java.util.Optional;

/** Manages file operations such as reading and writing JSON files. */
public final class FileManager {
  private final Path path;

  /**
   * Constructor for FileManager
   *
   * @param path The base path for file operations
   */
  public FileManager(Path path) {
    this.path = path;
  }

  /**
   * Writes the given object as JSON to the specified path.
   *
   * @param p The path to write the JSON file to.
   * @param o The object to serialize to JSON.
   * @return true if the write was successful, false otherwise.
   */
  public boolean writeJson(Path p, Object o) {
    // TODO: implement JSON writing logic
    return false;
  }

  /**
   * Reads a JSON file from the specified path and deserializes it into an object of the given
   * class.
   *
   * @param p The path to the JSON file.
   * @param clazz The class of the object to deserialize into.
   * @param <T> The type of the object to deserialize into.
   * @return An Optional containing the deserialized object, or empty if reading or deserialization
   *     fails.
   */
  public <T> Optional<T> readJson(Path p, Class<T> clazz) {
    // TODO: implement JSON reading logic
    return Optional.empty();
  }
}
