package avengers.core;

import avengers.controller.GameController;

/** Main game class to run the Avengers game. */
public final class Game {

  private final GameController controller;

  /** Creates a new Game instance with a GameController. */
  public Game() {
    this.controller = new GameController();
  }

  /**
   * Entry point of the application.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    Game game = new Game();
    game.run();
    game.loopOnce("example input");
  }

  /** Starts the game. */
  public void run() {
    System.out.println("Game is running...");
  }

  /** Processes a single loop iteration with the given input. */
  public void loopOnce(String input) {
    System.out.println("Processing input: " + input);
    controller.route(input);
  }
}
