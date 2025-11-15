package avengers.domain.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class CommandTokenTest {

  @Test
  void testCommandTokenCreation() {
    CommandToken token = new CommandToken(Verb.GO, "north", List.of("north"), "go north");

    assertNotNull(token);
    assertEquals(Verb.GO, token.verb());
    assertEquals("north", token.target());
    assertEquals(1, token.args().size());
    assertEquals("go north", token.raw());
  }

  @Test
  void testCommandTokenWithNullValues() {
    CommandToken token = new CommandToken(null, null, null, null);

    assertNotNull(token);
    assertEquals(Verb.UNKNOWN, token.verb()); // defaults to UNKNOWN
    assertNull(token.target());
    assertTrue(token.args().isEmpty()); // defaults to empty list
    assertEquals("", token.raw()); // defaults to empty string
  }

  @Test
  void testCommandTokenWithEmptyStrings() {
    CommandToken token = new CommandToken(Verb.HELP, "", List.of(), "");

    assertNotNull(token);
    assertEquals(Verb.HELP, token.verb());
    assertNull(token.target()); // blank target becomes null
    assertTrue(token.args().isEmpty());
    assertEquals("", token.raw());
  }

  @Test
  void testHasTarget() {
    CommandToken withTarget =
        new CommandToken(Verb.PICKUP, "sword", List.of("sword"), "pickup sword");
    CommandToken withoutTarget = new CommandToken(Verb.HELP, null, List.of(), "help");

    assertTrue(withTarget.hasTarget());
    assertFalse(withoutTarget.hasTarget());
  }

  @Test
  void testRecordEquality() {
    CommandToken token1 = new CommandToken(Verb.INSPECT, "door", List.of("door"), "inspect door");
    CommandToken token2 = new CommandToken(Verb.INSPECT, "door", List.of("door"), "inspect door");

    assertEquals(token1, token2);
  }

  @Test
  void testRecordInequality() {
    CommandToken token1 = new CommandToken(Verb.GO, "north", List.of("north"), "go north");
    CommandToken token2 = new CommandToken(Verb.GO, "south", List.of("south"), "go south");

    assertNotEquals(token1, token2);
  }

  @Test
  void testRecordHashCode() {
    CommandToken token1 = new CommandToken(Verb.PICKUP, "item", List.of("item"), "pickup item");
    CommandToken token2 = new CommandToken(Verb.PICKUP, "item", List.of("item"), "pickup item");

    assertEquals(token1.hashCode(), token2.hashCode());
  }

  @Test
  void testRecordToString() {
    CommandToken token = new CommandToken(Verb.HELP, null, List.of(), "help");

    assertNotNull(token.toString());
    assertTrue(token.toString().contains("HELP"));
  }

  @Test
  void testArgsImmutable() {
    List<String> mutableArgs = List.of("arg1", "arg2");
    CommandToken token = new CommandToken(Verb.USE, "item", mutableArgs, "use item arg1 arg2");

    assertEquals(2, token.args().size());
    assertEquals("arg1", token.args().get(0));
    assertEquals("arg2", token.args().get(1));
  }

  @Test
  void testBlankTargetBecomesNull() {
    CommandToken token = new CommandToken(Verb.GO, "   ", List.of(), "go");

    assertNull(token.target());
    assertFalse(token.hasTarget());
  }
}
