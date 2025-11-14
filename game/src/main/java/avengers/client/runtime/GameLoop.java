package avengers.client.runtime;

/** Example runtime/game loop class. Replace with actual game loop implementation. */
public class GameLoop {
  private boolean running;

  /** Starts the game loop. */
  public void start() {
    running = true;
    System.out.println("Game loop started");
  }

  /** Stops the game loop. */
  public void stop() {
    running = false;
    System.out.println("Game loop stopped");
  }

  public boolean isRunning() {
    return running;
  }
}
