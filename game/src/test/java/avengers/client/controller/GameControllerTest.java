package avengers.client.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControllerTest {
  private GameController controller;

  @BeforeEach
  void setUp() {
    controller = new GameController();
  }

  @Test
  void testControllerCreation() {
    assertNotNull(controller);
  }

  @Test
  void testHandleInput() {
    assertDoesNotThrow(() -> controller.handleInput("move north"));
  }
}
