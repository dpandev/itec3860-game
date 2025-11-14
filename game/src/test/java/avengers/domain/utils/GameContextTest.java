package avengers.domain.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class GameContextTest {
  @Test
  void testGameContextCreation() {
    GameContext context = new GameContext();
    assertNotNull(context);
  }

  @Test
  void testMultipleInstancesAreIndependent() {
    GameContext context1 = new GameContext();
    GameContext context2 = new GameContext();
    assertNotNull(context1);
    assertNotNull(context2);
  }
}
