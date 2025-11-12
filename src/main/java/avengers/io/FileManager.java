package avengers.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/** Manages file operations such as reading and writing JSON files. */
public final class FileManager {
  private final Path root;
  private final Gson gson;

  /**
   * Constructor for FileManager.
   *
   * @param root The base path for file operations
   */
  public FileManager(Path root) {
    this.root = root;
    this.gson = new GsonBuilder().setPrettyPrinting().create();

    // Ensure the root directory exists
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      System.err.println("Failed to create directory: " + root + " - " + e.getMessage());
    }
  }

  /**
   * Writes the given object as JSON to the specified path.
   *
   * @param p The path relative to root to write the JSON file to.
   * @param o The object to serialize to JSON.
   * @return true if the write was successful, false otherwise.
   */
  public boolean writeJson(Path p, Object o) {
    try {
      Path fullPath = root.resolve(p);
      // Ensure parent directories exist
      if (fullPath.getParent() != null) {
        Files.createDirectories(fullPath.getParent());
      }

      String json = gson.toJson(o);
      Files.writeString(fullPath, json);
      return true;
    } catch (IOException e) {
      System.err.println("Failed to write JSON to " + p + ": " + e.getMessage());
      return false;
    }
  }

  /**
   * Reads a JSON file from the specified path and deserializes it into an object of the given
   * class.
   *
   * @param p The path relative to root to the JSON file.
   * @param c The class of the object to deserialize into.
   * @param <T> The type of the object to deserialize into.
   * @return An Optional containing the deserialized object, or empty if reading or deserialization
   *     fails.
   */
  public <T> Optional<T> readJson(Path p, Class<T> c) {
    try {
      Path fullPath = root.resolve(p);
      if (!Files.exists(fullPath)) {
        return Optional.empty();
      }

      String json = Files.readString(fullPath);
      T obj = gson.fromJson(json, c);
      return Optional.ofNullable(obj);
    } catch (IOException e) {
      System.err.println("Failed to read JSON from " + p + ": " + e.getMessage());
      return Optional.empty();
    }
  }

  /**
   * Checks if a file exists at the specified path.
   *
   * @param p The path relative to root to check.
   * @return true if the file exists, false otherwise.
   */
  public boolean fileExists(Path p) {
    return Files.exists(root.resolve(p));
  }

  /**
   * Deletes a file at the specified path.
   *
   * @param p The path relative to root to delete.
   * @return true if the file was deleted, false otherwise.
   */
  public boolean deleteFile(Path p) {
    try {
      return Files.deleteIfExists(root.resolve(p));
    } catch (IOException e) {
      System.err.println("Failed to delete file " + p + ": " + e.getMessage());
      return false;
    }
  }

  /**
   * Gets the root path used by this FileManager.
   *
   * @return The root path.
   */
  public Path getRoot() {
    return root;
  }
}
