package avengers.service;

import avengers.domain.model.Player;
import avengers.domain.utils.CommandResult;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.SaveData;
import avengers.service.spi.SaveRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Service responsible for saving and loading game data. */
public final class SaveService {
  private static final int MAX_SAVE_SLOTS = 10;
  private final SaveRepository repo;

  /**
   * Constructs a SaveService with the given SaveRepository.
   *
   * @param repo the repository for saving and loading game data
   */
  public SaveService(SaveRepository repo) {
    this.repo = Objects.requireNonNull(repo); // need to ensure repo is not null
  }

  /**
   * Save the current game state for the player in the given context.
   *
   * @param ctx the game context containing world and player information
   */
  public void saveData(GameContext ctx) {
    var world = ctx.world();
    var player = ctx.player();

    // Convert EquipmentSlot enum to String for serialization
    Map<String, String> equippedItemsMap = new java.util.HashMap<>();
    player.getEquippedItems().forEach((slot, itemId) -> equippedItemsMap.put(slot.name(), itemId));

    SaveData data =
        new SaveData(
            player.getId(),
            player.getName(),
            player.getRoomId(),
            List.copyOf(player.getInventoryItemIds()),
            equippedItemsMap,
            player.getCurrentHealth(),
            player.getMaxHealth(),
            player.getBaseAttack(),
            player.getBaseDefense(),
            List.copyOf(player.getAllies()),
            List.copyOf(player.getPuzzlesSolved()),
            List.copyOf(player.getRoomsVisited()),
            Instant.now());

    repo.upsert(data); // save or update the save data if exists
  }

  /**
   * Load the save data for a given player ID.
   *
   * @param playerId the UUID of the player
   * @return an Optional containing the SaveData if found, otherwise empty
   */
  public Optional<SaveData> load(UUID playerId) {
    return repo.findByPlayerId(playerId);
  }

  /**
   * Apply the loaded save data to the current game context.
   *
   * @param ctx the current game context
   * @param data the loaded save data
   * @return CommandResult indicating success or failure of applying the save data
   */
  public CommandResult applySave(GameContext ctx, SaveData data) {
    var player = ctx.player();
    var world = ctx.world();

    // apply save data to player but keep the current player uuid
    player.setName(data.playerName());
    player.setRoomId(data.roomId());

    // restore inventory
    player.getInventoryItemIds().clear();
    player.getInventoryItemIds().addAll(data.itemIds());

    // restore equipped items
    player.getEquippedItems().clear();
    data.equippedItems()
        .forEach(
            (slotName, itemId) -> {
              try {
                var slot = Player.EquipmentSlot.valueOf(slotName);
                player.equipItem(slot, itemId);
              } catch (IllegalArgumentException e) {
                // skip invalid slot names (in case save data is corrupted)
              }
            });

    // restore health and stats
    player.setCurrentHealth(data.currentHealth());
    player.setMaxHealth(data.maxHealth());
    player.setBaseAttack(data.baseAttack());
    player.setBaseDefense(data.baseDefense());

    // restore puzzles solved
    player.getPuzzlesSolved().clear();
    player.getPuzzlesSolved().addAll(data.puzzlesSolved());

    // restore rooms visited
    player.getRoomsVisited().clear();
    player.getRoomsVisited().addAll(data.roomsVisited());

    return CommandResult.success(
        "Game loaded successfully. You are now in room " + data.roomId() + ".");
  }

  /**
   * Gets the maximum number of save slots allowed.
   *
   * @return the maximum number of save slots
   */
  public int getMaxSaveSlots() {
    return MAX_SAVE_SLOTS;
  }

  /**
   * Checks if the maximum number of save slots has been reached.
   *
   * @return true if at maximum capacity, false otherwise
   */
  public boolean isAtMaxCapacity() {
    return repo.listAllSaves().size() >= MAX_SAVE_SLOTS;
  }

  /**
   * Deletes a save file by name.
   *
   * @param fileName the name of the save file to delete
   * @return true if deleted successfully, false otherwise
   */
  public boolean deleteSave(String fileName) {
    return repo.deleteSave(fileName);
  }

  /**
   * Lists all available save files.
   *
   * @return list of save file names
   */
  public List<String> listSaveFiles() {
    return repo.listAllSaves();
  }

  /**
   * Loads a game from a save file and creates a new GameContext.
   *
   * @param worldLoader the world loader to create the world
   * @param saveFileName the name of the save file
   * @return GameContext with loaded player state, or null if load failed
   */
  public GameContext loadGame(avengers.service.world.WorldLoader worldLoader, String saveFileName) {
    try {
      // Load save data from file
      Optional<SaveData> saveDataOpt = repo.loadFromFile(saveFileName);
      if (saveDataOpt.isEmpty()) {
        return null;
      }

      SaveData saveData = saveDataOpt.get();

      // Load the world
      avengers.domain.model.World world = worldLoader.load();

      // IMPORTANT: Create player with the ORIGINAL UUID from the save file
      // This ensures saves update the same file instead of creating new ones
      avengers.domain.model.Player player =
          new avengers.domain.model.Player(
              saveData.playerId(), saveData.playerName(), saveData.roomId());

      // Create context
      GameContext ctx = new GameContext(world, player);

      // Apply saved state to player (but player already has correct ID)
      applySave(ctx, saveData);

      return ctx;
    } catch (Exception e) {
      return null;
    }
  }
}
