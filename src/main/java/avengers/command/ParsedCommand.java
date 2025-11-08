package avengers.command;

import java.util.List;

/** Represents a parsed command with its associated arguments. */
public final class ParsedCommand {
  private final Command command;
  private final List<String> args;

  /**
   * Constructor for ParsedCommand.
   *
   * @param command The command that was parsed.
   * @param args The list of arguments associated with the command.
   */
  public ParsedCommand(Command command, List<String> args) {
    this.command = command;
    this.args = args != null ? List.copyOf(args) : List.of();
  }

  /**
   * Gets the command.
   *
   * @return The command.
   */
  public Command getCommand() {
    return command;
  }

  /**
   * Gets the arguments.
   *
   * @return An immutable list of arguments.
   */
  public List<String> getArgs() {
    return args;
  }

  /**
   * Gets a specific argument by index.
   *
   * @param index The index of the argument.
   * @return The argument at the specified index.
   * @throws IndexOutOfBoundsException if the index is out of range.
   */
  public String getArg(int index) {
    return args.get(index);
  }

  /**
   * Gets the number of arguments.
   *
   * @return The number of arguments.
   */
  public int getArgCount() {
    return args.size();
  }
}
