package avengers.service.world;

import avengers.domain.model.Item;
import avengers.domain.model.Monster;
import avengers.domain.model.Puzzle;
import avengers.domain.model.Room;
import avengers.domain.model.World;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads game world data from JSON files in the resources/data directory.
 *
 * <p>Uses Gson to deserialize JSON data into World, Room, Item, Monster, and Puzzle objects.
 * Follows best practices: - Resource management with try-with-resources - Proper error handling and
 * logging - Immutable collections where possible - Clear separation of concerns
 */
public class JsonWorldLoader implements WorldLoader {

  private static final Logger LOGGER = Logger.getLogger(JsonWorldLoader.class.getName());

  private static final String WORLD_JSON_PATH = "/data/world.json";
  private static final String ITEMS_JSON_PATH = "/data/items.json";
  private static final String MONSTERS_JSON_PATH = "/data/monsters.json";
  private static final String PUZZLES_JSON_PATH = "/data/puzzles.json";

  private final Gson gson;

  /** Constructs a JsonWorldLoader with default Gson configuration. */
  public JsonWorldLoader() {
    this.gson = new GsonBuilder().setPrettyPrinting().create();
  }

  /**
   * Constructs a JsonWorldLoader with a custom Gson instance.
   *
   * @param gson the Gson instance to use for deserialization
   */
  public JsonWorldLoader(Gson gson) {
    this.gson = gson;
  }

  @Override
  public World load() {
    try {
      LOGGER.info("Loading world data from JSON files...");

      // Load all data from JSON files
      WorldData worldData = loadWorldData();
      Map<String, Item> items = loadItems();
      Map<String, Monster> monsters = loadMonsters();
      Map<String, Puzzle> puzzles = loadPuzzles();

      // TODO: Once Room class is fully implemented, parse rooms from worldData
      Map<String, Room> rooms = new HashMap<>();
      // When Room is implemented with proper fields, update this section

      LOGGER.info(
          String.format(
              "World loaded successfully: %d rooms, %d items, %d monsters, %d puzzles",
              rooms.size(), items.size(), monsters.size(), puzzles.size()));

      return new World(rooms, items, puzzles, monsters, worldData.startingRoomId);

    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to load world data", e);
      throw new WorldLoadException("Failed to load world data", e);
    }
  }

  /**
   * Loads world structure data (rooms and starting room ID) from world.json.
   *
   * @return WorldData containing room definitions and starting room ID
   * @throws WorldLoadException if loading fails
   */
  private WorldData loadWorldData() {
    try (Reader reader = getResourceReader(WORLD_JSON_PATH)) {
      WorldData data = gson.fromJson(reader, WorldData.class);
      if (data == null || data.startingRoomId == null || data.startingRoomId.isBlank()) {
        throw new WorldLoadException("Invalid world data: missing starting room ID");
      }
      LOGGER.fine(String.format("Loaded world data with starting room: %s", data.startingRoomId));
      return data;
    } catch (JsonSyntaxException e) {
      throw new WorldLoadException("Invalid JSON syntax in world.json", e);
    } catch (IOException e) {
      throw new WorldLoadException("Failed to read world.json", e);
    }
  }

  /**
   * Loads item data from items.json.
   *
   * @return Map of item ID to Item objects
   * @throws WorldLoadException if loading fails
   */
  private Map<String, Item> loadItems() {
    // TODO: Once Item class is fully implemented with fields, update this method
    // When Item is implemented, deserialize from JSON and create proper Item objects

    LOGGER.fine("Loading items (currently placeholder - Item class not yet implemented)");
    return new HashMap<>();
  }

  /**
   * Loads monster data from monsters.json.
   *
   * @return Map of monster ID to Monster objects
   * @throws WorldLoadException if loading fails
   */
  private Map<String, Monster> loadMonsters() {
    // TODO: Once Monster class is fully implemented with all fields, update this method
    // When Monster is extended with proper fields (roomLocation, specialEffects, itemDrops, etc.),
    // deserialize fully from JSON

    LOGGER.fine("Loading monsters (currently placeholder - Monster class not fully implemented)");
    return new HashMap<>();
  }

  /**
   * Loads puzzle data from puzzles.json.
   *
   * @return Map of puzzle ID to Puzzle objects
   * @throws WorldLoadException if loading fails
   */
  private Map<String, Puzzle> loadPuzzles() {
    // TODO: Once Puzzle class is fully implemented with fields, update this method
    // When Puzzle is implemented, deserialize from JSON

    LOGGER.fine("Loading puzzles (currently placeholder - Puzzle class not yet implemented)");
    return new HashMap<>();
  }

  /**
   * Gets a Reader for a resource file.
   *
   * @param path the resource path
   * @return Reader for the resource
   * @throws IOException if resource cannot be found or read
   */
  private Reader getResourceReader(String path) throws IOException {
    InputStream inputStream = getClass().getResourceAsStream(path);
    if (inputStream == null) {
      throw new IOException("Resource not found: " + path);
    }
    return new InputStreamReader(inputStream, StandardCharsets.UTF_8);
  }

  /** Data class for deserializing world.json structure. */
  private static class WorldData {
    private List<RoomJson> rooms;
    private String startingRoomId;
  }

  /**
   * Data class for deserializing individual room data from JSON.
   *
   * <p>TODO: Once Room class is implemented, use this to construct Room objects
   */
  private static class RoomJson {
    private String id;
    private String name;
    private String description;
    private Map<String, String> exits;
    private List<String> monsters;
    private List<String> items;
    private List<String> puzzles;
  }

  /** Custom exception for world loading errors. */
  public static class WorldLoadException extends RuntimeException {
    /** Serial version UID for serialization. */
    public WorldLoadException(String message) {
      super(message);
    }

    /** Serial version UID for serialization. */
    public WorldLoadException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
