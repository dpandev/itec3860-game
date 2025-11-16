package avengers.service;

import avengers.domain.utils.GameContext;

/** Service for managing combat encounters. */
public interface CombatService {
  //

  /**
   * Starts combat with a monster.
   *
   * @param ctx the game context
   * @param monsterName the name of the monster to attack
   * @return result of starting combat
   */
  CombatResult attack(GameContext ctx, String monsterName);

  /**
   * Player defends in combat (reduces incoming damage).
   *
   * @param ctx the game context
   * @return result of defending
   */
  CombatResult defend(GameContext ctx);

  /**
   * Ignores a monster, removing it from the room.
   *
   * @param ctx the game context
   * @param monsterName the name of the monster to ignore
   * @return result of ignoring the monster
   */
  String ignore(GameContext ctx, String monsterName);

  /**
   * Processes one round of combat.
   *
   * @param ctx the game context
   * @return result of the combat round
   */
  CombatResult processCombatRound(GameContext ctx);

  /** Result of a combat action. */
  class CombatResult {
    private final boolean playerAlive;
    private final boolean monsterAlive;
    private final String message;
    private final boolean combatEnded;

    public CombatResult(
        boolean playerAlive, boolean monsterAlive, String message, boolean combatEnded) {
      this.playerAlive = playerAlive;
      this.monsterAlive = monsterAlive;
      this.message = message;
      this.combatEnded = combatEnded;
    }

    public boolean isPlayerAlive() {
      return playerAlive;
    }

    public boolean isMonsterAlive() {
      return monsterAlive;
    }

    public String getMessage() {
      return message;
    }

    public boolean isCombatEnded() {
      return combatEnded;
    }

    public static CombatResult playerDied(String message) {
      return new CombatResult(false, true, message, true);
    }

    public static CombatResult monsterDied(String message) {
      return new CombatResult(true, false, message, true);
    }

    public static CombatResult ongoing(String message) {
      return new CombatResult(true, true, message, false);
    }
  }
}
