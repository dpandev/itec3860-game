package avengers.save;

import avengers.model.Player;
import java.util.Map;
import java.util.Set;

/** Represents the saved data of a player's game state. */
public final class SaveData {
  private final Player player;
  private final String currentRoom;
  private final Set<String> visited;
  private final long timePlayedMs;
  private final Map<String, Boolean> puzzleStates; // puzzleId -> completed
  private final Set<String> defeatedMonsters; // monsterIds that have been defeated

  /**
   * Constructor for SaveData.
   *
   * @param player The player object representing the player's state
   * @param currentRoom The ID of the current room the player is in
   * @param visited A set of IDs representing rooms the player has visited
   * @param timePlayedMs The total time played in milliseconds
   * @param puzzleStates Map of puzzle IDs to their completion status
   * @param defeatedMonsters Set of monster IDs that have been defeated
   */
  public SaveData(
      Player player,
      String currentRoom,
      Set<String> visited,
      long timePlayedMs,
      Map<String, Boolean> puzzleStates,
      Set<String> defeatedMonsters) {
    this.player = player;
    this.currentRoom = currentRoom;
    this.visited = Set.copyOf(visited);
    this.timePlayedMs = timePlayedMs;
    this.puzzleStates = puzzleStates != null ? Map.copyOf(puzzleStates) : Map.of();
    this.defeatedMonsters = defeatedMonsters != null ? Set.copyOf(defeatedMonsters) : Set.of();
  }

  /** Returns the player object representing the player's state. */
  public Player getPlayer() {
    return player;
  }

  /** Returns the ID of the current room the player is in. */
  public String getCurrentRoom() {
    return currentRoom;
  }

  /** Returns a set of IDs representing rooms the player has visited. */
  public Set<String> getVisited() {
    return visited;
  }

  /** Returns the total time played in milliseconds. */
  public long getTimePlayedMs() {
    return timePlayedMs;
  }

  /** Returns a map of puzzle IDs to their completion status. */
  public Map<String, Boolean> getPuzzleStates() {
    return puzzleStates;
  }

  /** Returns a set of monster IDs that have been defeated. */
  public Set<String> getDefeatedMonsters() {
    return defeatedMonsters;
  }
}
