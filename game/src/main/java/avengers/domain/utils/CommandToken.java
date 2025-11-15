package avengers.domain.utils;

import java.util.List;

/**
 * A record representing a parsed command token.
 *
 * @param verb - the command verb
 * @param target - the command target, can be null
 * @param args - list of command arguments, never null
 * @param raw - the raw input string, never null
 */
public record CommandToken(Verb verb, String target, List<String> args, String raw) {

  /**
   * Construct a CommandToken, ensuring non-null fields and immutability of args list.
   *
   * @param verb - if null, defaults to Verb.UNKNOWN
   * @param target - if null or blank, defaults to null
   * @param args - if null, defaults to empty list; otherwise, makes an immutable copy
   * @param raw - if null, defaults to empty string
   */
  public CommandToken {
    verb = (verb == null) ? Verb.UNKNOWN : verb;
    target = (target == null || target.isBlank()) ? null : target;
    args = (args == null) ? List.of() : List.copyOf(args);
    raw = (raw == null) ? "" : raw;
  }

  /**
   * Check if the command has a non-blank target.
   *
   * @return true if target is non-null and not blank, false otherwise
   */
  public boolean hasTarget() {
    return target != null && !target.isBlank();
  }
}
