package avengers.core;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameTest {

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private Game game;

  @BeforeEach
  void setUp() {
    System.setOut(new PrintStream(outContent));
    game = new Game();
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  void testRun() {
    game.run();
    assertEquals("Game is running...\n", outContent.toString());
  }

  @Test
  void testLoopOnce() {
    game.loopOnce("test input");
    assertEquals("Processing input: test input\n", outContent.toString());
  }

  @Test
  void testMain() {
    assertDoesNotThrow(() -> Game.main(new String[] {}));
  }
}
