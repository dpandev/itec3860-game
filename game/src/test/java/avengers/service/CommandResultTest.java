package avengers.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CommandResultTest {

  @Test
  void testSuccessCreation() {
    CommandResult result = CommandResult.success("Operation completed");

    assertTrue(result.success());
    assertEquals("Operation completed", result.message());
    assertFalse(result.shouldExit());
  }

  @Test
  void testSuccessWithNullMessage() {
    CommandResult result = CommandResult.success(null);

    assertTrue(result.success());
    assertEquals("", result.message());
    assertFalse(result.shouldExit());
  }

  @Test
  void testFailCreation() {
    CommandResult result = CommandResult.fail("Invalid command");

    assertFalse(result.success());
    assertEquals("Invalid command", result.message());
    assertFalse(result.shouldExit());
  }

  @Test
  void testFailWithNullMessage() {
    CommandResult result = CommandResult.fail(null);

    assertFalse(result.success());
    assertEquals("", result.message());
    assertFalse(result.shouldExit());
  }

  @Test
  void testExitCreation() {
    CommandResult result = CommandResult.exit("Goodbye!");

    assertTrue(result.success());
    assertEquals("Goodbye!", result.message());
    assertTrue(result.shouldExit());
  }

  @Test
  void testExitWithNullMessage() {
    CommandResult result = CommandResult.exit(null);

    assertTrue(result.success());
    assertEquals("", result.message());
    assertTrue(result.shouldExit());
  }

  @Test
  void testRecordEquality() {
    CommandResult result1 = CommandResult.success("test");
    CommandResult result2 = CommandResult.success("test");

    assertEquals(result1, result2);
  }

  @Test
  void testRecordHashCode() {
    CommandResult result1 = CommandResult.success("test");
    CommandResult result2 = CommandResult.success("test");

    assertEquals(result1.hashCode(), result2.hashCode());
  }

  @Test
  void testRecordToString() {
    CommandResult result = CommandResult.success("test message");

    assertNotNull(result.toString());
    assertTrue(result.toString().contains("test message"));
  }
}
