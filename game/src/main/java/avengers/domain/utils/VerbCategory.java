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
      case GO -> MOVEMENT;
      case INVENTORY -> INVENTORY;
      case INTERACT -> INTERACTION;
      case HELP, QUIT, UNKNOWN -> SYSTEM;
    };
  }
}
