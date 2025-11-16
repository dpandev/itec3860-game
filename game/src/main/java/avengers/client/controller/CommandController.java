package avengers.client.controller;

import avengers.domain.utils.CommandResult;
import avengers.domain.utils.CommandToken;
import avengers.domain.utils.GameContext;
import avengers.domain.utils.Verb;

/**
 * Interface for handling commands in the Avengers game. Implementations should specify which verbs
 * they support and how to handle them.
 */
public interface CommandController {
  //

  /**
   * Checks if this controller supports handling the given verb.
   *
   * @param verb the verb to check
   * @return true if the verb is supported, false otherwise
   */
  default boolean supports(Verb verb) {
    return true;
  }

  /**
   * Handles the given command within the provided game context.
   *
   * @param cmd the command token to handle
   * @param ctx the current game context
   * @return the result of handling the command
   */
  CommandResult handle(CommandToken cmd, GameContext ctx);
}
