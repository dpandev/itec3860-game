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
      Map<String, Room> rooms = parseRooms(worldData.rooms); // updated by Daniel
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
   * Parses room data from JSON into Room objects.
   *
   * @param roomJsonList list of room JSON data
   * @return map of room ID to Room objects
   */
  private Map<String, Room> parseRooms(List<RoomJson> roomJsonList) {
    Map<String, Room> rooms = new HashMap<>();
    if (roomJsonList == null) {
      return rooms;
    }

    for (RoomJson roomJson : roomJsonList) {
      Room room =
          new Room(
              roomJson.id,
              roomJson.name,
              roomJson.description,
              roomJson.exits != null ? roomJson.exits : Map.of(),
              roomJson.monsters != null ? roomJson.monsters : List.of(),
              roomJson.items != null ? roomJson.items : List.of(),
              roomJson.puzzles != null ? roomJson.puzzles : List.of());
      rooms.put(roomJson.id, room);
    }

    LOGGER.fine(String.format("Parsed %d rooms", rooms.size()));
    return rooms;
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
      Map<String, Item> items = new HashMap<>();

      if (data != null && data.items != null) {
        for (ItemJson itemJson : data.items) {
          Item item =
              new Item(
                  itemJson.id,
                  itemJson.name,
                  itemJson.description,
                  itemJson.category,
                  itemJson.effect != null ? itemJson.effect : "",
                  itemJson.specialEffect != null ? itemJson.specialEffect : "");
          items.put(itemJson.id, item);
        }
      }

      LOGGER.fine(String.format("Loaded %d items", items.size()));
      return items;
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
      Map<String, Monster> monsters = new HashMap<>();

      if (data != null && data.monsters != null) {
        for (MonsterJson monsterJson : data.monsters) {
          Monster monster =
              new Monster(
                  monsterJson.name,
                  monsterJson.maxHp,
                  monsterJson.damage,
                  0 // Base defense, can be extracted from special effects if needed
                  );
          monsters.put(monsterJson.id, monster);
        }
      }

      LOGGER.fine(String.format("Loaded %d monsters", monsters.size()));
      return monsters;
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
      Map<String, Puzzle> puzzles = new HashMap<>();

      if (data != null && data.puzzles != null) {
        for (PuzzleJson puzzleJson : data.puzzles) {
          // Parse numberOfAttempts - handle both int and string "Unlimited"
          int attempts = 1;
          if (puzzleJson.numberOfAttempts != null) {
            if (puzzleJson.numberOfAttempts instanceof Number) {
              attempts = ((Number) puzzleJson.numberOfAttempts).intValue();
            } else if (puzzleJson.numberOfAttempts instanceof String) {
              String attemptsStr = (String) puzzleJson.numberOfAttempts;
              if (attemptsStr.toLowerCase().contains("unlimited")) {
                attempts = -1;
              } else {
                try {
                  attempts = Integer.parseInt(attemptsStr);
                } catch (NumberFormatException e) {
                  attempts = 1;
                }
              }
            }
          }

          Puzzle puzzle =
              new Puzzle(
                  puzzleJson.id,
                  puzzleJson.name,
                  puzzleJson.description,
                  puzzleJson.solution != null ? puzzleJson.solution : "",
                  puzzleJson.reward != null ? puzzleJson.reward : "",
                  puzzleJson.failureConsequence != null ? puzzleJson.failureConsequence : "",
                  puzzleJson.commandUsed != null ? puzzleJson.commandUsed : "",
                  attempts);
          puzzles.put(puzzleJson.id, puzzle);
        }
      }

      LOGGER.fine(String.format("Loaded %d puzzles", puzzles.size()));
      return puzzles;
    } catch (JsonSyntaxException e) {
      throw new WorldLoadException("Invalid JSON syntax in puzzles.json", e);
    } catch (IOException e) {
      throw new WorldLoadException("Failed to read puzzles.json", e);
    }
  }

  //

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
    private Object numberOfAttempts; // Can be int or string "Unlimited"
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
