package avengers.client.runtime;

/** Client application entry point. */
public class ClientApp {
  /** Main method to start the client application. */
  public static void main(String[] args) {
    GameLoop gameLoop = new GameLoop();
    gameLoop.start();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    gameLoop.stop();
  }
}
