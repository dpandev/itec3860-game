package avengers.service.spi;

import avengers.domain.utils.SaveData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * File-based implementation of SaveRepository that stores save data as JSON files.
 *
 * <p>Save files are stored in the "saves/" directory with naming pattern: save_{playerId}.json
 *
 * <p>Best practices implemented: - Resource management with try-with-resources - Proper error
 * handling and logging - Directory creation if not exists - UTF-8 encoding - Gson with
 * pretty-printing and Instant adapter
 */
public final class FileSaveRepository implements SaveRepository {

  private static final Logger LOGGER = Logger.getLogger(FileSaveRepository.class.getName());
  private static final String SAVES_DIRECTORY = "saves";
  private static final String FILE_EXTENSION = ".json";

  private final Gson gson;
  private final Path savesPath;

  /** Constructs a FileSaveRepository with default configuration (saves/ directory). */
  public FileSaveRepository() {
    this(Paths.get(SAVES_DIRECTORY));
  }

  /**
   * Constructs a FileSaveRepository with a custom saves directory path.
   *
   * @param savesPath the directory path where save files will be stored
   */
  public FileSaveRepository(Path savesPath) {
    this.savesPath = savesPath;
    this.gson =
        new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
            .create();

    // Ensure the saves directory exists
    ensureSavesDirectoryExists();
  }

  /**
   * Constructs a FileSaveRepository with custom Gson and saves directory.
   *
   * @param gson the Gson instance to use for serialization/deserialization
   * @param savesPath the directory path where save files will be stored
   */
  public FileSaveRepository(Gson gson, Path savesPath) {
    this.gson = gson;
    this.savesPath = savesPath;
    ensureSavesDirectoryExists();
  }

  @Override
  public Optional<SaveData> findByPlayerId(UUID id) {
    if (id == null) {
      LOGGER.warning("Attempted to find save with null player ID");
      return Optional.empty();
    }

    Path saveFile = getSaveFilePath(id);

    if (!Files.exists(saveFile)) {
      LOGGER.fine(String.format("No save file found for player ID: %s", id));
      return Optional.empty();
    }

    try (Reader reader = Files.newBufferedReader(saveFile, StandardCharsets.UTF_8)) {
      SaveData saveData = gson.fromJson(reader, SaveData.class);

      if (saveData == null) {
        LOGGER.warning(String.format("Save file exists but contains no data: %s", saveFile));
        return Optional.empty();
      }

      LOGGER.info(
          String.format(
              "Successfully loaded save for player: %s (ID: %s)",
              saveData.playerName(), saveData.playerId()));
      return Optional.of(saveData);

    } catch (JsonSyntaxException e) {
      LOGGER.log(Level.SEVERE, String.format("Invalid JSON in save file: %s", saveFile), e);
      return Optional.empty();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, String.format("Failed to read save file: %s", saveFile), e);
      return Optional.empty();
    }
  }

  @Override
  public void upsert(SaveData save) {
    if (save == null) {
      throw new IllegalArgumentException("SaveData cannot be null");
    }

    if (save.playerId() == null) {
      throw new IllegalArgumentException("SaveData must have a valid player ID");
    }

    Path saveFile = getSaveFilePath(save.playerId());

    try (Writer writer = Files.newBufferedWriter(saveFile, StandardCharsets.UTF_8)) {
      gson.toJson(save, writer);

      LOGGER.info(
          String.format(
              "Successfully saved game for player: %s (ID: %s) at %s",
              save.playerName(), save.playerId(), save.savedAt()));

    } catch (IOException e) {
      LOGGER.log(
          Level.SEVERE, String.format("Failed to save game for player: %s", save.playerName()), e);
      throw new SaveException("Failed to save game data", e);
    }
  }

  @Override
  public java.util.List<String> listAllSaves() {
    try {
      if (!Files.exists(savesPath)) {
        return java.util.Collections.emptyList();
      }

      return Files.list(savesPath)
          .filter(path -> path.toString().endsWith(FILE_EXTENSION))
          .map(path -> path.getFileName().toString())
          .sorted()
          .collect(java.util.stream.Collectors.toList());

    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Failed to list save files", e);
      return java.util.Collections.emptyList();
    }
  }

  @Override
  public Optional<SaveData> loadFromFile(String fileName) {
    if (fileName == null || fileName.isBlank()) {
      LOGGER.warning("Attempted to load save with null or blank file name");
      return Optional.empty();
    }

    Path saveFile = savesPath.resolve(fileName);

    if (!Files.exists(saveFile)) {
      LOGGER.fine(String.format("Save file not found: %s", fileName));
      return Optional.empty();
    }

    try (Reader reader = Files.newBufferedReader(saveFile, StandardCharsets.UTF_8)) {
      SaveData saveData = gson.fromJson(reader, SaveData.class);

      if (saveData == null) {
        LOGGER.warning(String.format("Save file exists but contains no data: %s", fileName));
        return Optional.empty();
      }

      LOGGER.info(
          String.format(
              "Successfully loaded save from file: %s (Player: %s)",
              fileName, saveData.playerName()));
      return Optional.of(saveData);

    } catch (JsonSyntaxException e) {
      LOGGER.log(Level.SEVERE, String.format("Invalid JSON in save file: %s", fileName), e);
      return Optional.empty();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, String.format("Failed to read save file: %s", fileName), e);
      return Optional.empty();
    }
  }

  @Override
  public boolean deleteSave(String fileName) {
    if (fileName == null || fileName.isBlank()) {
      LOGGER.warning("Attempted to delete save with null or blank file name");
      return false;
    }

    Path saveFile = savesPath.resolve(fileName);

    if (!Files.exists(saveFile)) {
      LOGGER.fine(String.format("Save file not found for deletion: %s", fileName));
      return false;
    }

    try {
      Files.delete(saveFile);
      LOGGER.info(String.format("Successfully deleted save file: %s", fileName));
      return true;
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, String.format("Failed to delete save file: %s", fileName), e);
      return false;
    }
  }

  /**
   * Gets the file path for a save file based on player ID.
   *
   * @param playerId the player's unique identifier
   * @return the Path to the save file
   */
  private Path getSaveFilePath(UUID playerId) {
    String filename = "save_" + playerId.toString() + FILE_EXTENSION;
    return savesPath.resolve(filename);
  }

  /** Ensures the saves directory exists, creating it if necessary. */
  private void ensureSavesDirectoryExists() {
    try {
      if (!Files.exists(savesPath)) {
        Files.createDirectories(savesPath);
        LOGGER.info(String.format("Created saves directory: %s", savesPath.toAbsolutePath()));
      }
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, String.format("Failed to create saves directory: %s", savesPath), e);
      throw new SaveException("Failed to create saves directory", e);
    }
  }

  /** Custom exception for save/load errors. */
  public static class SaveException extends RuntimeException {
    /**
     * Constructs a SaveException with a message.
     *
     * @param message the exception message
     */
    public SaveException(String message) {
      super(message);
    }

    /**
     * Constructs a SaveException with a message and cause.
     *
     * @param message the exception message
     * @param cause the underlying cause
     */
    public SaveException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  /**
   * Custom Gson TypeAdapter for java.time.Instant.
   *
   * <p>Serializes Instant as ISO-8601 string, deserializes from ISO-8601 string.
   */
  private static class InstantTypeAdapter extends com.google.gson.TypeAdapter<Instant> {
    @Override
    public void write(com.google.gson.stream.JsonWriter out, Instant value) throws IOException {
      if (value == null) {
        out.nullValue();
      } else {
        out.value(value.toString());
      }
    }

    @Override
    public Instant read(com.google.gson.stream.JsonReader in) throws IOException {
      if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
        in.nextNull();
        return null;
      }
      String timestamp = in.nextString();
      return Instant.parse(timestamp);
    }
  }
}
