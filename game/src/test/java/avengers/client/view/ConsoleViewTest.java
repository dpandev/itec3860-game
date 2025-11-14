package avengers.client.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConsoleViewTest {

  private ByteArrayOutputStream outputStream;
  private PrintStream originalOut;
  private ByteArrayInputStream inputStream;
  private java.io.InputStream originalIn;

  @BeforeEach
  void setUp() {
    // Capture System.out
    outputStream = new ByteArrayOutputStream();
    originalOut = System.out;
    System.setOut(new PrintStream(outputStream));

    // Save original System.in
    originalIn = System.in;
  }

  @AfterEach
  void tearDown() {
    // Restore System.out and System.in
    System.setOut(originalOut);
    System.setIn(originalIn);
  }

  @Test
  void testConsoleViewCreation() {
    ConsoleView view = new ConsoleView();
    assertNotNull(view);
    view.close();
  }

  @Test
  void testPrintln() {
    ConsoleView view = new ConsoleView();

    view.println("Hello, World!");

    String output = outputStream.toString();
    assertEquals("Hello, World!" + System.lineSeparator(), output);

    view.close();
  }

  @Test
  void testPrintlnWithNull() {
    ConsoleView view = new ConsoleView();

    view.println(null);

    String output = outputStream.toString();
    assertEquals(System.lineSeparator(), output);

    view.close();
  }

  @Test
  void testPrintlnMultipleLines() {
    ConsoleView view = new ConsoleView();

    view.println("Line 1");
    view.println("Line 2");
    view.println("Line 3");

    String output = outputStream.toString();
    String expected =
        "Line 1"
            + System.lineSeparator()
            + "Line 2"
            + System.lineSeparator()
            + "Line 3"
            + System.lineSeparator();
    assertEquals(expected, output);

    view.close();
  }

  @Test
  void testPrintf() {
    ConsoleView view = new ConsoleView();

    view.printf("Hello, %s!", "World");

    String output = outputStream.toString();
    assertEquals("Hello, World!", output);

    view.close();
  }

  @Test
  void testPrintfWithMultipleArgs() {
    ConsoleView view = new ConsoleView();

    view.printf("Name: %s, Age: %d, Score: %.2f", "Alice", 25, 95.5);

    String output = outputStream.toString();
    assertEquals("Name: Alice, Age: 25, Score: 95.50", output);

    view.close();
  }

  @Test
  void testPrintfWithNoArgs() {
    ConsoleView view = new ConsoleView();

    view.printf("No arguments");

    String output = outputStream.toString();
    assertEquals("No arguments", output);

    view.close();
  }

  @Test
  void testReadLine() throws IOException {
    // Set up input stream with test data
    String input = "Test input line" + System.lineSeparator();
    inputStream = new ByteArrayInputStream(input.getBytes());
    System.setIn(inputStream);

    ConsoleView view = new ConsoleView();

    String result = view.readLine();

    assertEquals("Test input line", result);

    view.close();
  }

  @Test
  void testReadLineMultipleLines() throws IOException {
    // Set up input stream with multiple lines
    String input =
        "Line 1"
            + System.lineSeparator()
            + "Line 2"
            + System.lineSeparator()
            + "Line 3"
            + System.lineSeparator();
    inputStream = new ByteArrayInputStream(input.getBytes());
    System.setIn(inputStream);

    ConsoleView view = new ConsoleView();

    assertEquals("Line 1", view.readLine());
    assertEquals("Line 2", view.readLine());
    assertEquals("Line 3", view.readLine());

    view.close();
  }

  @Test
  void testReadLineEmptyInput() throws IOException {
    // Set up empty input stream
    inputStream = new ByteArrayInputStream(new byte[0]);
    System.setIn(inputStream);

    ConsoleView view = new ConsoleView();

    String result = view.readLine();

    assertNull(result);

    view.close();
  }

  @Test
  void testReadLineEndOfInput() throws IOException {
    // Set up input stream with one line
    String input = "Only line" + System.lineSeparator();
    inputStream = new ByteArrayInputStream(input.getBytes());
    System.setIn(inputStream);

    ConsoleView view = new ConsoleView();

    assertEquals("Only line", view.readLine());
    assertNull(view.readLine()); // Should return null after input exhausted

    view.close();
  }

  @Test
  void testReadLineWithEmptyLine() throws IOException {
    // Set up input stream with an empty line
    String input = System.lineSeparator();
    inputStream = new ByteArrayInputStream(input.getBytes());
    System.setIn(inputStream);

    ConsoleView view = new ConsoleView();

    String result = view.readLine();

    assertEquals("", result);

    view.close();
  }

  @Test
  void testClose() {
    ConsoleView view = new ConsoleView();

    // Should not throw exception
    view.close();

    // Closing again should also not throw
    view.close();
  }

  @Test
  void testOutputFlushing() {
    ConsoleView view = new ConsoleView();

    view.println("Test");

    // Output should be immediately available (flushed)
    String output = outputStream.toString();
    assertEquals("Test" + System.lineSeparator(), output);

    view.close();
  }

  @Test
  void testPrintfFlushing() {
    ConsoleView view = new ConsoleView();

    view.printf("Test %d", 123);

    // Output should be immediately available (flushed)
    String output = outputStream.toString();
    assertEquals("Test 123", output);

    view.close();
  }
}
