package avengers.domain.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class VerbTest {

  @Test
  void testVerbValues() {
    Verb[] verbs = Verb.values();

    assertEquals(6, verbs.length);
  }

  @Test
  void testVerbGo() {
    assertEquals("GO", Verb.GO.name());
  }

  @Test
  void testVerbInventory() {
    assertEquals("INVENTORY", Verb.INVENTORY.name());
  }

  @Test
  void testVerbInteract() {
    assertEquals("INTERACT", Verb.INTERACT.name());
  }

  @Test
  void testVerbHelp() {
    assertEquals("HELP", Verb.HELP.name());
  }

  @Test
  void testVerbQuit() {
    assertEquals("QUIT", Verb.QUIT.name());
  }

  @Test
  void testVerbUnknown() {
    assertEquals("UNKNOWN", Verb.UNKNOWN.name());
  }

  @Test
  void testValueOf() {
    assertEquals(Verb.GO, Verb.valueOf("GO"));
    assertEquals(Verb.INVENTORY, Verb.valueOf("INVENTORY"));
    assertEquals(Verb.INTERACT, Verb.valueOf("INTERACT"));
    assertEquals(Verb.HELP, Verb.valueOf("HELP"));
    assertEquals(Verb.QUIT, Verb.valueOf("QUIT"));
    assertEquals(Verb.UNKNOWN, Verb.valueOf("UNKNOWN"));
  }

  @Test
  void testEnumNotNull() {
    for (Verb verb : Verb.values()) {
      assertNotNull(verb);
    }
  }
}
