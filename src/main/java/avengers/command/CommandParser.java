package avengers.command;

import avengers.model.world.Direction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Parses raw input strings into ParsedCommand objects. */
public final class CommandParser {
  private final Map<String, Command> dictionary;
  private final Map<String, Direction> dirSynonyms;

  /**
   * Constructor for CommandParser.
   *
   * @param dictionary Map of command words to Command enum values.
   * @param dirSynonyms Map of direction words to Direction enum values.
   */
  public CommandParser(Map<String, Command> dictionary, Map<String, Direction> dirSynonyms) {
    this.dictionary = dictionary;
    this.dirSynonyms = dirSynonyms;
  }

  /**
   * Parses a raw input string into a ParsedCommand.
   *
   * @param input The raw input string from the user.
   * @return A ParsedCommand object, or null if the command is not recognized.
   */
  public ParsedCommand parse(String input) {
    if (input == null || input.trim().isEmpty()) {
      return null;
    }

    String[] tokens = input.trim().toLowerCase().split("\\s+");
    String commandWord = tokens[0];

    Command command = dictionary.get(commandWord);
    if (command == null) {
      return null;
    }

    List<String> args = new ArrayList<>();
    if (tokens.length > 1) {
      args.addAll(Arrays.asList(tokens).subList(1, tokens.length));
    }

    return new ParsedCommand(command, args);
  }

  /**
   * Gets the Direction enum from a direction string using synonyms.
   *
   * @param directionStr The direction string to parse.
   * @return The Direction enum, or null if not recognized.
   */
  public Direction parseDirection(String directionStr) {
    if (directionStr == null) {
      return null;
    }
    return dirSynonyms.get(directionStr.toLowerCase());
  }
}
