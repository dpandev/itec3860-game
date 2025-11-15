package avengers.client.command;

import avengers.domain.utils.CommandToken;
import avengers.domain.utils.Verb;
import java.util.HashMap;
import java.util.Map;

/**
 * An InputCommandParser that interprets user input into structured commands. Supports verb
 * synonyms, direction shortcuts, and argument extraction.
 */
public class InputCommandParser implements CommandParser {
  private final Map<String, Verb> verbs;
  private final Map<String, String> directionSynonyms;

  /** Construct an InputCommandParser with predefined verbs and direction synonyms. */
  public InputCommandParser() {
    this.verbs = buildVerbs();
    this.directionSynonyms = buildDirectionSynonyms();
  }

  @Override
  public CommandToken parse(String line) {
    // Implementation of command parsing logic goes here
    return null; // Placeholder return
  }

  /**
   * Build the primary verb map with synonyms.
   *
   * @return map of verb strings to Verb enum
   */
  private Map<String, Verb> buildVerbs() {
    Map<String, Verb> verbMap = new HashMap<>();
    verbMap.put("help", Verb.HELP);
    verbMap.put("?", Verb.HELP);
    verbMap.put("map", Verb.MAP);
    verbMap.put("inspect", Verb.INSPECT);
    verbMap.put("go", Verb.GO);
    verbMap.put("pickup", Verb.PICKUP);
    verbMap.put("drop", Verb.DROP);
    verbMap.put("use", Verb.USE);
    verbMap.put("activate", Verb.ACTIVATE);
    verbMap.put("inventory", Verb.INVENTORY);
    verbMap.put("equip", Verb.EQUIP);
    verbMap.put("unequip", Verb.UNEQUIP);
    verbMap.put("attack", Verb.ATTACK);
    verbMap.put("defend", Verb.DEFEND);
    verbMap.put("ignore", Verb.IGNORE);
    verbMap.put("hint", Verb.HINT);
    verbMap.put("solve", Verb.SOLVE);
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
    Verb v = verbs.get(head);
    if (v != null) {
      return v;
    }
    // if the head looks like a direction ("n", "north"), treat as GO
    if (directionSynonyms.containsKey(head)) {
      return Verb.GO;
    }
    return Verb.UNKNOWN;
  }
}
