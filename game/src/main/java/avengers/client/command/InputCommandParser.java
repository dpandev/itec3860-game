package avengers.client.command;

import avengers.domain.utils.CommandToken;
import avengers.domain.utils.Verb;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An InputCommandParser that interprets user input into structured commands. Supports verb
 * synonyms, direction shortcuts, and argument extraction.
 */
public class InputCommandParser implements CommandParser {
  private final Map<String, Verb> verbs;
  private final Map<String, String> directionSynonyms;

  // Verbs that accept multi-word targets (e.g., "pickup frost sigil")
  private static final Set<Verb> MULTI_WORD_TARGET_VERBS =
      Set.of(
          Verb.INSPECT,
          Verb.PICKUP,
          Verb.DROP,
          Verb.USE,
          Verb.EQUIP,
          Verb.UNEQUIP,
          Verb.ATTACK,
          Verb.IGNORE,
          Verb.SOLVE);

  // Verbs that don't require any target
  private static final Set<Verb> NO_TARGET_VERBS =
      Set.of(Verb.HELP, Verb.MAP, Verb.EXPLORE, Verb.INVENTORY, Verb.QUIT, Verb.DEFEND);

  /** Construct an InputCommandParser with predefined verbs and direction synonyms. */
  public InputCommandParser() {
    this.verbs = buildVerbs();
    this.directionSynonyms = buildDirectionSynonyms();
  }

  @Override
  public CommandToken parse(final String line) {
    final String raw = (line == null) ? "" : line;
    final String normalized = normalize(raw);

    // Handle empty input
    if (normalized.isEmpty()) {
      return new CommandToken(Verb.UNKNOWN, null, List.of(), raw);
    }

    final List<String> tokens = List.of(normalized.split(" "));
    final String head = tokens.get(0);

    // Check if single-token direction command (e.g., "n" or "north")
    if (tokens.size() == 1 && directionSynonyms.containsKey(head)) {
      return new CommandToken(Verb.GO, directionSynonyms.get(head), List.of(), raw);
    }

    // Resolve the verb
    Verb verb = resolveVerb(head);

    // Extract target and arguments based on verb type
    String target;
    List<String> args;

    if (verb == Verb.GO) {
      // GO verb: expects a direction as target
      if (tokens.size() >= 2) {
        String directionToken = tokens.get(1);
        target = directionSynonyms.getOrDefault(directionToken, directionToken);
        args = tokens.subList(1, tokens.size());
      } else {
        // No direction provided - controller should prompt
        target = null;
        args = List.of();
      }
    } else if (MULTI_WORD_TARGET_VERBS.contains(verb)) {
      // Multi-word target verbs: join all remaining tokens as target
      if (tokens.size() >= 2) {
        target = String.join(" ", tokens.subList(1, tokens.size()));
        args = tokens.subList(1, tokens.size());
      } else {
        target = null;
        args = List.of();
      }
    } else if (NO_TARGET_VERBS.contains(verb)) {
      // No-target verbs: ignore any additional tokens
      target = null;
      args = tokens.size() > 1 ? tokens.subList(1, tokens.size()) : List.of();
    } else {
      // Default: single-word target with optional additional arguments
      // Used by: ACTIVATE, HINT, SOLVE, SAVE, LOAD
      if (tokens.size() >= 2) {
        target = tokens.get(1);
        args = tokens.size() > 2 ? tokens.subList(2, tokens.size()) : List.of();
      } else {
        target = null;
        args = List.of();
      }
    }

    return new CommandToken(verb, target, args, raw);
  }

  /**
   * Normalize input string: lowercase, trim, collapse internal whitespace.
   *
   * @param s - input string
   * @return normalized string
   */
  private static String normalize(String s) {
    return s.toLowerCase().trim().replaceAll("\\s+", " ");
  }

  /**
   * Build the primary verb map with synonyms.
   *
   * @return map of verb strings to Verb enum
   */
  private Map<String, Verb> buildVerbs() {
    Map<String, Verb> verbMap = new HashMap<>();
    // Movement verbs
    verbMap.put("go", Verb.GO);
    verbMap.put("explore", Verb.EXPLORE);
    verbMap.put("map", Verb.MAP);

    // Inventory verbs
    verbMap.put("inventory", Verb.INVENTORY);
    verbMap.put("pickup", Verb.PICKUP);
    verbMap.put("drop", Verb.DROP);
    verbMap.put("equip", Verb.EQUIP);
    verbMap.put("unequip", Verb.UNEQUIP);
    verbMap.put("use", Verb.USE);

    // Interaction verbs
    verbMap.put("inspect", Verb.INSPECT);
    verbMap.put("solve", Verb.SOLVE);
    verbMap.put("hint", Verb.HINT);

    // Puzzle-specific action verbs (all map to SOLVE)
    verbMap.put("activate", Verb.SOLVE);
    verbMap.put("kneel", Verb.SOLVE);
    verbMap.put("jump", Verb.SOLVE);
    verbMap.put("strike", Verb.SOLVE);
    verbMap.put("step", Verb.SOLVE);
    verbMap.put("choose", Verb.SOLVE);
    verbMap.put("say", Verb.SOLVE);
    verbMap.put("answer", Verb.SOLVE);
    verbMap.put("place", Verb.SOLVE);
    verbMap.put("input", Verb.SOLVE);
    verbMap.put("collect", Verb.SOLVE);
    verbMap.put("embrace", Verb.SOLVE);
    verbMap.put("resist", Verb.SOLVE);

    // Combat verbs
    verbMap.put("attack", Verb.ATTACK);
    verbMap.put("defend", Verb.DEFEND);
    verbMap.put("ignore", Verb.IGNORE);

    // System verbs
    verbMap.put("help", Verb.HELP);
    verbMap.put("?", Verb.HELP);
    verbMap.put("stats", Verb.STATS);
    verbMap.put("status", Verb.STATS);
    verbMap.put("character", Verb.STATS);
    verbMap.put("info", Verb.STATS);
    verbMap.put("save", Verb.SAVE);
    verbMap.put("load", Verb.LOAD);
    verbMap.put("quit", Verb.QUIT);

    return verbMap;
  }

  /**
   * Build the direction synonyms map.
   *
   * @return map of direction strings to canonical direction
   */
  private static Map<String, String> buildDirectionSynonyms() {
    Map<String, String> dirMap = new HashMap<>();
    dirMap.put("north", "north");
    dirMap.put("n", "north");
    dirMap.put("south", "south");
    dirMap.put("s", "south");
    dirMap.put("west", "west");
    dirMap.put("w", "west");
    dirMap.put("east", "east");
    dirMap.put("e", "east");
    return dirMap;
  }

  /**
   * Resolve the verb from the command head.
   *
   * @param head - the first word of the command
   * @return - the corresponding Verb, or UNKNOWN if not found
   */
  private Verb resolveVerb(String head) {
    return verbs.getOrDefault(head, Verb.UNKNOWN);
  }
}
