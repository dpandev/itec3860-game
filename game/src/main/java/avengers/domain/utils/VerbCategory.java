package avengers.domain.utils;

/** Categories for different types of verbs/commands in the game. */
public enum VerbCategory {
  MOVEMENT,
  INTERACTION,
  INVENTORY,
  COMBAT,
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
      case GO, EXPLORE, MAP -> MOVEMENT;
      case INVENTORY, PICKUP, DROP, EQUIP, UNEQUIP, USE, INSPECT -> INVENTORY;
      case ACTIVATE, SOLVE, HINT -> INTERACTION;
      case ATTACK, DEFEND, IGNORE -> COMBAT;
      case HELP, STATS, QUIT, SAVE, LOAD, NEW_GAME, UNKNOWN -> SYSTEM;
    };
  }
}
