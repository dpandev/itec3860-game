package avengers.service;

import avengers.domain.utils.CommandResult;
import avengers.domain.utils.GameContext;

/** Service interface for handling combat operations. */
public interface CombatService {

  /**
   * Starts combat with the specified monster.
   *
   * @param ctx the game context
   * @param monsterName the name of the monster to attack
   * @return CommandResult indicating combat start or error
   */
  CommandResult startCombat(GameContext ctx, String monsterName);

  /**
   * Executes a player attack turn in combat.
   *
   * @param ctx the game context
   * @return CommandResult with attack results
   */
  CommandResult playerAttack(GameContext ctx);

  /**
   * Executes a monster attack turn in combat.
   *
   * @param ctx the game context
   * @return CommandResult with attack results
   */
  CommandResult monsterAttack(GameContext ctx);

  /**
   * Player chooses to defend, reducing incoming damage.
   *
   * @param ctx the game context
   * @return CommandResult with defend action results
   */
  CommandResult defend(GameContext ctx);

  /**
   * Ignores/removes a monster from the current room.
   *
   * @param ctx the game context
   * @param monsterName the name of the monster to ignore
   * @return CommandResult indicating success or failure
   */
  CommandResult ignoreMonster(GameContext ctx, String monsterName);

  /**
   * Summons allies to attack in combat.
   *
   * @param ctx the game context
   * @return CommandResult with summon results
   */
  CommandResult summonAllies(GameContext ctx);

  /**
   * Handles loot distribution after monster death.
   *
   * @param ctx the game context
   * @param monsterId the ID of the defeated monster
   * @return formatted loot message
   */
  String handleLoot(GameContext ctx, String monsterId);
}
