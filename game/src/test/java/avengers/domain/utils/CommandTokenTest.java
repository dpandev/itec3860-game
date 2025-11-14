package avengers.domain.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CommandTokenTest {

  @Test
  void testCommandTokenCreation() {
    CommandToken token = new CommandToken("go", "north");

    assertNotNull(token);
    assertEquals("go", token.s());
    assertEquals("north", token.t());
  }

  @Test
  void testCommandTokenWithNullValues() {
    CommandToken token = new CommandToken(null, null);

    assertNotNull(token);
    assertEquals(null, token.s());
    assertEquals(null, token.t());
  }

  @Test
  void testCommandTokenWithEmptyStrings() {
    CommandToken token = new CommandToken("", "");

    assertNotNull(token);
    assertEquals("", token.s());
    assertEquals("", token.t());
  }

  @Test
  void testRecordEquality() {
    CommandToken token1 = new CommandToken("look", "around");
    CommandToken token2 = new CommandToken("look", "around");

    assertEquals(token1, token2);
  }

  @Test
  void testRecordInequality() {
    CommandToken token1 = new CommandToken("go", "north");
    CommandToken token2 = new CommandToken("go", "south");

    assertNotEquals(token1, token2);
  }

  @Test
  void testRecordHashCode() {
    CommandToken token1 = new CommandToken("take", "item");
    CommandToken token2 = new CommandToken("take", "item");

    assertEquals(token1.hashCode(), token2.hashCode());
  }

  @Test
  void testRecordToString() {
    CommandToken token = new CommandToken("help", "inventory");

    assertNotNull(token.toString());
    assertTrue(token.toString().contains("help"));
    assertTrue(token.toString().contains("inventory"));
  }
}
