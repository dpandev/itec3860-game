package avengers.service.world;

import avengers.domain.model.Item;
import avengers.domain.model.Monster;
import avengers.domain.model.Puzzle;
import avengers.domain.model.PuzzleType;
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

      // Parse rooms from worldData
      Map<String, Room> rooms = new HashMap<>();
      if (worldData.rooms != null) {
        for (RoomJson roomJson : worldData.rooms) {
          Room room =
              new Room(
                  roomJson.id,
                  roomJson.name,
                  roomJson.description,
                  roomJson.items,
                  roomJson.monsters,
                  roomJson.exits,
                  roomJson.puzzles,
                  false); // not visited initially
          rooms.put(room.getId(), room);
        }
      }

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
    try (Reader reader = getResourceReader(ITEMS_JSON_PATH)) {
      ItemsData data = gson.fromJson(reader, ItemsData.class);
      if (data == null || data.items == null) {
        throw new WorldLoadException("Invalid items data: items array is null");
      }

      Map<String, Item> itemsMap = new HashMap<>();
      for (ItemJson itemJson : data.items) {
        Item item =
            new Item(
                itemJson.id,
                itemJson.name,
                itemJson.description,
                itemJson.category,
                itemJson.effect,
                itemJson.specialEffect);
        itemsMap.put(item.getId(), item);
      }

      LOGGER.fine(String.format("Loaded %d items", itemsMap.size()));
      return itemsMap;
    } catch (JsonSyntaxException e) {
      throw new WorldLoadException("Invalid JSON syntax in items.json", e);
    } catch (IOException e) {
      throw new WorldLoadException("Failed to read items.json", e);
    }
  }

  /**
   * Loads monster data from monsters.json.
   *
   * @return Map of monster ID to Monster objects
   * @throws WorldLoadException if loading fails
   */
  private Map<String, Monster> loadMonsters() {
    try (Reader reader = getResourceReader(MONSTERS_JSON_PATH)) {
      MonstersData data = gson.fromJson(reader, MonstersData.class);
      if (data == null || data.monsters == null) {
        throw new WorldLoadException("Invalid monsters data: monsters array is null");
      }

      Map<String, Monster> monstersMap = new HashMap<>();
      for (MonsterJson monsterJson : data.monsters) {
        Monster monster =
            new Monster(
                monsterJson.id,
                monsterJson.name,
                monsterJson.roomLocation,
                monsterJson.description,
                monsterJson.maxHp,
                monsterJson.damage,
                monsterJson.specialEffects,
                monsterJson.itemDrops);
        monstersMap.put(monster.getId(), monster);
      }

      LOGGER.fine(String.format("Loaded %d monsters", monstersMap.size()));
      return monstersMap;
    } catch (JsonSyntaxException e) {
      throw new WorldLoadException("Invalid JSON syntax in monsters.json", e);
    } catch (IOException e) {
      throw new WorldLoadException("Failed to read monsters.json", e);
    }
  }

  /**
   * Loads puzzle data from puzzles.json.
   *
   * @return Map of puzzle ID to Puzzle objects
   * @throws WorldLoadException if loading fails
   */
  private Map<String, Puzzle> loadPuzzles() {
    try (Reader reader = getResourceReader(PUZZLES_JSON_PATH)) {
      PuzzlesData data = gson.fromJson(reader, PuzzlesData.class);
      if (data == null || data.puzzles == null) {
        throw new WorldLoadException("Invalid puzzles data: puzzles array is null");
      }

      Map<String, Puzzle> puzzlesMap = new HashMap<>();
      for (PuzzleJson puzzleJson : data.puzzles) {
        // Parse numberOfAttempts - could be integer or string like "Unlimited"
        int attempts = parseAttempts(puzzleJson.numberOfAttempts);

        Puzzle puzzle =
            new Puzzle(
                puzzleJson.id,
                puzzleJson.name,
                puzzleJson.description,
                puzzleJson.solution,
                puzzleJson.reward,
                puzzleJson.failureConsequence,
                puzzleJson.commandUsed,
                attempts,
                PuzzleType.RIDDLE); // Default type, can be enhanced later
        puzzlesMap.put(puzzle.getId(), puzzle);
      }

      LOGGER.fine(String.format("Loaded %d puzzles", puzzlesMap.size()));
      return puzzlesMap;
    } catch (JsonSyntaxException e) {
      throw new WorldLoadException("Invalid JSON syntax in puzzles.json", e);
    } catch (IOException e) {
      throw new WorldLoadException("Failed to read puzzles.json", e);
    }
  }

  /**
   * Parse numberOfAttempts from JSON which can be either an integer or a string.
   *
   * @param attemptsObj the object to parse
   * @return the number of attempts, or 0 for unlimited
   */
  private int parseAttempts(Object attemptsObj) {
    if (attemptsObj == null) {
      return 0;
    }
    if (attemptsObj instanceof Number) {
      return ((Number) attemptsObj).intValue();
    }
    if (attemptsObj instanceof String) {
      String str = (String) attemptsObj;
      // If it contains "unlimited" or similar text, return 0
      if (str.toLowerCase().contains("unlimited") || str.toLowerCase().contains("retry")) {
        return 0;
      }
      // Try to parse as integer
      try {
        return Integer.parseInt(str);
      } catch (NumberFormatException e) {
        LOGGER.warning(
            "Could not parse numberOfAttempts: " + str + ", defaulting to 0 (unlimited)");
        return 0;
      }
    }
    return 0;
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

  /** Data class for deserializing individual room data from JSON. */
  private static class RoomJson {
    private String id;
    private String name;
    private String description;
    private Map<String, String> exits;
    private List<String> monsters;
    private List<String> items;
    private List<String> puzzles;
  }

  /** Data class for deserializing items.json structure. */
  private static class ItemsData {
    private List<ItemJson> items;
  }

  /** Data class for deserializing individual item data from JSON. */
  private static class ItemJson {
    private String id;
    private String name;
    private String description;
    private String category;
    private String effect;
    private String specialEffect;
  }

  /** Data class for deserializing monsters.json structure. */
  private static class MonstersData {
    private List<MonsterJson> monsters;
  }

  /** Data class for deserializing individual monster data from JSON. */
  private static class MonsterJson {
    private String id;
    private String name;
    private String roomLocation;
    private String description;
    private int hp;
    private int maxHp;
    private int damage;
    private List<String> specialEffects;
    private List<String> itemDrops;
  }

  /** Data class for deserializing puzzles.json structure. */
  private static class PuzzlesData {
    private List<PuzzleJson> puzzles;
  }

  /** Data class for deserializing individual puzzle data from JSON. */
  private static class PuzzleJson {
    private String id;
    private String name;
    private String description;
    private String solution;
    private String reward;
    private String failureConsequence;
    private String commandUsed;
    private Object numberOfAttempts; // Can be int or String
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
