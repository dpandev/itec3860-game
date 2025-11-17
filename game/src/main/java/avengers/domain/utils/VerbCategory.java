package avengers.domain.utils;

/** Categories for different types of verbs/commands in the game. */
public enum VerbCategory {
  MOVEMENT,
  INTERACTION,
  INVENTORY,
  COMBAT,
  MAP,
  SYSTEM;

  /**
   * Get the VerbCategory for a given Verb.
   *
   * @param v the verb
   * @return the corresponding VerbCategory
   */
  public static VerbCategory of(Verb v) {
    if (v == null) {
      return SYSTEM;
    }
    return switch (v) {
      case GO, EXPLORE -> MOVEMENT;
      case INVENTORY, PICKUP, DROP, EQUIP, UNEQUIP, USE, INSPECT, ACTIVATE -> INVENTORY;
      case SOLVE, HINT -> INTERACTION;
      case ATTACK, DEFEND, IGNORE, SUMMON -> COMBAT;
      case MAP -> MAP;
      case HELP, STATS, QUIT, SAVE, LOAD, NEW_GAME, UNKNOWN -> SYSTEM;
    };
  }
}
