package avengers.domain.utils;

import avengers.domain.model.Player;
import avengers.domain.model.World;
import java.util.Objects;

/** A context object encapsulating the game world and the current player. */
public final class GameContext {
  private World world;
  private Player player;
  // Flag indicating if the game is awaiting a puzzle answer from the player
  private boolean awaitingPuzzleAnswer = false;
  // Combat state
  private boolean inCombat = false;
  private String combatMonsterId = null;

  /**
   * Constructs a GameContext with the specified world and player.
   *
   * @param world the game world
   * @param player the current player
   * @throws NullPointerException if either world or player is null
   */
  public GameContext(World world, Player player) {
    this.world = Objects.requireNonNull(world, "world must not be null");
    this.player = Objects.requireNonNull(player, "player must not be null");
  }

  /**
   * Returns the game world.
   *
   * @return the game world
   */
  public World world() {
    return world;
  }

  /**
   * Returns the current player.
   *
   * @return the current player
   */
  public Player player() {
    return player;
  }

  /**
   * Check if the game is awaiting a puzzle answer from the player.
   *
   * @return true if awaiting puzzle answer, false otherwise
   */
  public boolean isAwaitingPuzzleAnswer() {
    return awaitingPuzzleAnswer;
  }

  /**
   * Set whether the game is awaiting a puzzle answer.
   *
   * @param awaiting true if awaiting answer, false otherwise
   */
  public void setAwaitingPuzzleAnswer(boolean awaiting) {
    this.awaitingPuzzleAnswer = awaiting;
  }

  /**
   * Check if the player is currently in combat.
   *
   * @return true if in combat, false otherwise
   */
  public boolean isInCombat() {
    return inCombat;
  }

  /**
   * Set whether the player is in combat.
   *
   * @param inCombat true if in combat, false otherwise
   */
  public void setInCombat(boolean inCombat) {
    this.inCombat = inCombat;
  }

  /**
   * Get the ID of the monster currently being fought.
   *
   * @return the monster ID, or null if not in combat
   */
  public String getCombatMonsterId() {
    return combatMonsterId;
  }

  /**
   * Set the ID of the monster currently being fought.
   *
   * @param monsterId the monster ID
   */
  public void setCombatMonsterId(String monsterId) {
    this.combatMonsterId = monsterId;
  }

  /**
   * Start combat with a monster.
   *
   * @param monsterId the ID of the monster to fight
   */
  public void startCombat(String monsterId) {
    this.inCombat = true;
    this.combatMonsterId = monsterId;
  }

  /** End combat and clear combat state. */
  public void endCombat() {
    this.inCombat = false;
    this.combatMonsterId = null;
  }

  /**
   * Reset the game by replacing the world and player with fresh instances.
   *
   * @param newWorld the freshly loaded world
   * @param playerName the name for the new player
   */
  public void resetGame(World newWorld, String playerName) {
    this.world = Objects.requireNonNull(newWorld, "world must not be null");
    this.player = new Player(playerName, newWorld.getStartRoomId());

    // Reset game state flags
    this.awaitingPuzzleAnswer = false;
    this.inCombat = false;
    this.combatMonsterId = null;
  }
}
