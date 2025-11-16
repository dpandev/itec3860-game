package avengers.domain.utils;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents the saved data of a player's game state.
 *
 * @param playerId the unique identifier of the player
 * @param playerName the name of the player
 * @param roomId the current room ID
 * @param itemIds the list of items in inventory
 * @param equippedItems map of equipment slot to item ID
 * @param currentHealth the player's current health
 * @param maxHealth the player's maximum health
 * @param baseAttack the player's base attack stat
 * @param baseDefense the player's base defense stat
 * @param allies list of ally IDs the player has
 * @param puzzlesSolved list of puzzle IDs the player has solved
 * @param roomsVisited list of room IDs the player has visited
 * @param savedAt the timestamp when the game was saved
 */
public record SaveData(
    UUID playerId,
    String playerName,
    String roomId,
    List<String> itemIds,
    Map<String, String> equippedItems,
    int currentHealth,
    int maxHealth,
    int baseAttack,
    int baseDefense,
    List<String> allies,
    List<String> puzzlesSolved,
    List<String> roomsVisited,
    // TODO: add field for world/room states (monsters defeated, items taken left in room, etc.)
    Instant savedAt) {}
