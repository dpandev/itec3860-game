package avengers.model.combat;

import avengers.model.Ally;
import avengers.model.Monster;
import avengers.model.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Represents a combat session between a player, monster, and optional allies. */
public final class CombatSession {
  private final Player player;
  private final Monster monster;
  private final List<Ally> allies;
  private final Random random;
  private CombatState state;
  private boolean playerDefending;
  private long lastFleeAttemptTime;
  private static final long FLEE_COOLDOWN_MS = 5000;

  /**
   * Constructs a new CombatSession.
   *
   * @param player The player character.
   * @param monster The enemy monster.
   * @param random Random instance for combat calculations.
   */
  public CombatSession(Player player, Monster monster, Random random) {
    this.player = player;
    this.monster = monster;
    this.allies = new ArrayList<>();
    this.random = random;
    this.state = CombatState.IDLE;
    this.playerDefending = false;
    this.lastFleeAttemptTime = 0;
  }

  /** Starts the combat session. */
  public void start() {
    if (state == CombatState.IDLE) {
      state = CombatState.ACTIVE;
    }
  }

  /** Ends the combat session. */
  public void end() {
    state = CombatState.ENDED;
  }

  /**
   * Gets the current combat state.
   *
   * @return The combat state.
   */
  public CombatState getState() {
    return state;
  }

  /**
   * Gets the player.
   *
   * @return The player.
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * Gets the monster.
   *
   * @return The monster.
   */
  public Monster getMonster() {
    return monster;
  }

  /**
   * Gets the list of active allies.
   *
   * @return The allies list.
   */
  public List<Ally> getAllies() {
    return List.copyOf(allies);
  }

  /**
   * Adds an ally to the combat.
   *
   * @param ally The ally to add.
   */
  public void addAlly(Ally ally) {
    allies.add(ally);
  }

  /**
   * Processes a player action.
   *
   * @param action The action to perform.
   * @return A list of combat events describing what happened.
   */
  public List<CombatEvent> processPlayerAction(CombatAction action) {
    if (state != CombatState.ACTIVE) {
      return List.of(new CombatEvent("Combat is not active."));
    }

    List<CombatEvent> events = new ArrayList<>();
    playerDefending = false;

    switch (action) {
      case ATTACK:
        events.addAll(performPlayerAttack());
        break;
      case DEFEND:
        playerDefending = true;
        events.add(new CombatEvent(player.getName() + " takes a defensive stance!"));
        break;
      case RUN:
        events.addAll(attemptFlee());
        break;
    }

    // Process turn if combat is still active
    if (state == CombatState.ACTIVE && !events.isEmpty()) {
      events.addAll(processTurn());
    }

    return events;
  }

  /**
   * Performs a player attack on the monster.
   *
   * @return List of combat events.
   */
  private List<CombatEvent> performPlayerAttack() {
    List<CombatEvent> events = new ArrayList<>();

    // Apply status effects at turn start
    events.addAll(applyStatusEffects(player));

    int damage = player.getBaseDamage();
    boolean isCrit = random.nextDouble() < 0.15; // 15% crit chance

    int finalDamage =
        DamageCalculator.calculateDamage(
            damage, player.getElement(), monster.getElement(), monster.getResistances(), isCrit);

    finalDamage = DamageCalculator.applyDefense(finalDamage, monster.getDefense());

    DamageSource source = new DamageSource(player.getElement(), isCrit);
    monster.takeDamage(finalDamage, source);

    String critText = isCrit ? " (CRITICAL HIT!)" : "";
    events.add(
        new CombatEvent(
            player.getName()
                + " attacks "
                + monster.getName()
                + " for "
                + finalDamage
                + " damage!"
                + critText));

    if (monster.isDead()) {
      events.add(new CombatEvent(monster.getName() + " has been defeated!"));
      end();
    }

    return events;
  }

  /**
   * Attempts to flee from combat.
   *
   * @return List of combat events.
   */
  private List<CombatEvent> attemptFlee() {
    List<CombatEvent> events = new ArrayList<>();

    long currentTime = System.currentTimeMillis();
    if (currentTime - lastFleeAttemptTime < FLEE_COOLDOWN_MS) {
      events.add(new CombatEvent("You must wait before attempting to flee again!"));
      return events;
    }

    lastFleeAttemptTime = currentTime;
    double hpPercent = (double) player.getHp() / player.getMaxHp();
    double fleeProbability = DamageCalculator.calculateFleeProbability(monster, hpPercent);

    if (random.nextDouble() < fleeProbability) {
      events.add(new CombatEvent("You successfully fled from combat!"));
      end();
    } else {
      events.add(new CombatEvent("Failed to escape! The monster blocks your path!"));
    }

    return events;
  }

  /**
   * Processes a full combat turn (allies, monster actions, status effects).
   *
   * @return List of combat events.
   */
  private List<CombatEvent> processTurn() {
    List<CombatEvent> events = new ArrayList<>();

    // Allies attack
    events.addAll(processAllyTurns());

    // Monster's turn (if still alive and combat active)
    if (state == CombatState.ACTIVE && !monster.isDead()) {
      events.addAll(processMonsterTurn());
    }

    // Apply end-of-turn effects
    if (state == CombatState.ACTIVE) {
      events.addAll(processTurnEndEffects());
    }

    return events;
  }

  /**
   * Processes ally turns.
   *
   * @return List of combat events.
   */
  private List<CombatEvent> processAllyTurns() {
    List<CombatEvent> events = new ArrayList<>();
    List<Ally> expiredAllies = new ArrayList<>();

    for (Ally ally : allies) {
      if (!ally.isDead() && !ally.isExpired()) {
        int damage = ally.getBaseDamage();
        boolean isCrit = random.nextDouble() < 0.1;

        int finalDamage =
            DamageCalculator.calculateDamage(
                damage, ally.getElement(), monster.getElement(), monster.getResistances(), isCrit);

        finalDamage = DamageCalculator.applyDefense(finalDamage, monster.getDefense());

        DamageSource source = new DamageSource(ally.getElement(), isCrit);
        monster.takeDamage(finalDamage, source);

        String critText = isCrit ? " (CRITICAL!)" : "";
        events.add(
            new CombatEvent(
                ally.getName()
                    + " attacks "
                    + monster.getName()
                    + " for "
                    + finalDamage
                    + " damage!"
                    + critText));

        ally.decrementTurns();
        if (ally.isExpired()) {
          expiredAllies.add(ally);
          events.add(new CombatEvent(ally.getName() + " has vanished!"));
        }
      }

      if (monster.isDead()) {
        events.add(new CombatEvent(monster.getName() + " has been defeated!"));
        end();
        break;
      }
    }

    allies.removeAll(expiredAllies);
    return events;
  }

  /**
   * Processes the monster's turn.
   *
   * @return List of combat events.
   */
  private List<CombatEvent> processMonsterTurn() {
    List<CombatEvent> events = new ArrayList<>();

    // Apply status effects
    events.addAll(applyStatusEffects(monster));

    if (monster.isDead()) {
      events.add(new CombatEvent(monster.getName() + " succumbs to status effects!"));
      end();
      return events;
    }

    // Monster attacks (simple AI)
    int damage = monster.rollDamage(random);
    boolean isCrit = random.nextDouble() < monster.getCritThreshold();

    int finalDamage =
        DamageCalculator.calculateDamage(
            damage, monster.getElement(), player.getElement(), player.getResistances(), isCrit);

    // Apply defense (doubled if defending)
    int effectiveDefense = playerDefending ? player.getDefense() * 2 : player.getDefense();
    finalDamage = DamageCalculator.applyDefense(finalDamage, effectiveDefense);

    DamageSource source = new DamageSource(monster.getElement(), isCrit);
    player.takeDamage(finalDamage, source);

    String critText = isCrit ? " (CRITICAL HIT!)" : "";
    String defendText = playerDefending ? " (reduced by defense)" : "";
    events.add(
        new CombatEvent(
            monster.getName()
                + " attacks "
                + player.getName()
                + " for "
                + finalDamage
                + " damage!"
                + critText
                + defendText));

    if (player.isDead()) {
      events.add(new CombatEvent(player.getName() + " has fallen in combat!"));
      end();
    }

    return events;
  }

  /**
   * Applies status effects to a character at turn start.
   *
   * @param character The character to apply effects to.
   * @return List of combat events.
   */
  private List<CombatEvent> applyStatusEffects(avengers.model.Character character) {
    List<CombatEvent> events = new ArrayList<>();
    List<StatusInstance> effects = new ArrayList<>(character.getEffects());

    for (StatusInstance effect : effects) {
      if (effect.getRemainingTurns() > 0) {
        effect.apply(character);
        events.add(
            new CombatEvent(
                character.getName()
                    + " takes "
                    + effect.getEffectAmount()
                    + " damage from "
                    + effect.getType()));
      }
    }

    return events;
  }

  /**
   * Processes end-of-turn effects.
   *
   * @return List of combat events.
   */
  private List<CombatEvent> processTurnEndEffects() {
    List<CombatEvent> events = new ArrayList<>();

    // Decrement status effect durations
    // (This would be implemented in StatusInstance.apply)

    return events;
  }

  /** Inner class representing a combat event message. */
  public static final class CombatEvent {
    private final String message;

    public CombatEvent(String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }
  }
}
