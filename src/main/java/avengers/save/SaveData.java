package avengers.save;

import avengers.model.Player;
import java.util.Set;
import java.util.UUID;

/** Represents the saved data of a player's game state. */
public final class SaveData {
  private final Player player;
  private final UUID currentRoomId;
  private final Set<UUID> visitedRooms;
  private final long timePlayedMs;

  /**
   * Constructor for SaveData
   *
   * @param player The player object representing the player's state
   * @param currentRoomId The UUID of the current room the player is in
   * @param visitedRooms A set of UUIDs representing rooms the player has visited
   * @param timePlayedMs The total time played in milliseconds
   */
  public SaveData(Player player, UUID currentRoomId, Set<UUID> visitedRooms, long timePlayedMs) {
    this.player = player;
    this.currentRoomId = currentRoomId;
    this.visitedRooms = visitedRooms;
    this.timePlayedMs = timePlayedMs;
  }
}
