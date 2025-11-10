package avengers.controller;

import avengers.model.Monster;
import avengers.model.Player;
import avengers.model.combat.CombatAction;
import avengers.model.combat.CombatSession;
import avengers.model.combat.CombatState;
import java.util.List;
import java.util.Random;

/** Controller for managing combat sessions and actions. */
public final class CombatController {
  private CombatSession activeSession;
  private final Random random;

  /**
   * Constructor for CombatController.
   */
  public CombatController() {
    this.random = new Random();
    this.activeSession = null;
  }

  /**
   * Constructor with specified Random instance (for testing).
   *
   * @param random The Random instance to use.
   */
  public CombatController(Random random) {
    this.random = random;
    this.activeSession = null;
  }

  /**
   * Starts a new combat session.
   *
   * @param player The player character.
   * @param monster The enemy monster.
   * @return A message describing the start of combat.
   */
  public String startCombat(Player player, Monster monster) {
    if (inCombat()) {
      return "Already in combat!";
    }

    activeSession = new CombatSession(player, monster, random);
    activeSession.start();

    return "Combat started with " + monster.getName() + "!";
  }

  /**
   * Checks if there is an active combat session.
   *
   * @return true if in combat, false otherwise.
   */
  public boolean inCombat() {
    return activeSession != null && activeSession.getState() == CombatState.ACTIVE;
  }

  /**
   * Gets the active combat session.
   *
   * @return The active session, or null if not in combat.
   */
  public CombatSession getActiveSession() {
    return activeSession;
  }

  /**
   * Executes a player combat action.
   *
   * @param action The action to perform.
   * @return A list of messages describing what happened.
   */
  public List<String> executeAction(CombatAction action) {
    if (!inCombat()) {
      return List.of("Not in combat!");
    }

    List<CombatSession.CombatEvent> events = activeSession.processPlayerAction(action);
    List<String> messages =
      events.stream().map(CombatSession.CombatEvent::getMessage).toList();

    // Clean up if combat ended
    if (activeSession.getState() == CombatState.ENDED) {
      activeSession = null;
    }

    return messages;
  }

  /**
   * Executes an attack action.
   *
   * @return A list of combat messages.
   */
  public List<String> attack() {
    return executeAction(CombatAction.ATTACK);
  }

  /**
   * Executes a defend action.
   *
   * @return A list of combat messages.
   */
  public List<String> defend() {
    return executeAction(CombatAction.DEFEND);
  }

  /**
   * Attempts to run from combat.
   *
   * @return A list of combat messages.
   */
  public List<String> run() {
    return executeAction(CombatAction.RUN);
  }

  /**
   * Ends the current combat session forcefully.
   */
  public void endCombat() {
    if (activeSession != null) {
      activeSession.end();
      activeSession = null;
    }
  }
}
