package avengers.client.controller;

import avengers.service.GameService;

/**
 * Example controller class. Replace with actual controllers (GameController, MenuController, etc.)
 */
public class GameController {
  private final GameService gameService;

  public GameController() {
    this.gameService = new GameService();
  }

  public void handleInput(String input) {
    System.out.println("Controller handling: " + input);
  }
}
