package avengers.domain.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class VerbCategoryTest {

  @Test
  void testVerbCategoryValues() {
    VerbCategory[] categories = VerbCategory.values();

    assertEquals(5, categories.length);
  }

  @Test
  void testVerbCategoryNames() {
    assertEquals("MOVEMENT", VerbCategory.MOVEMENT.name());
    assertEquals("INTERACTION", VerbCategory.INTERACTION.name());
    assertEquals("INVENTORY", VerbCategory.INVENTORY.name());
    assertEquals("COMBAT", VerbCategory.COMBAT.name());
    assertEquals("SYSTEM", VerbCategory.SYSTEM.name());
  }

  @Test
  void testOfWithGo() {
    assertEquals(VerbCategory.MOVEMENT, VerbCategory.of(Verb.GO));
  }

  @Test
  void testOfWithInventory() {
    assertEquals(VerbCategory.INVENTORY, VerbCategory.of(Verb.INVENTORY));
  }

  @Test
  void testOfWithActivate() {
    assertEquals(VerbCategory.INTERACTION, VerbCategory.of(Verb.ACTIVATE));
  }

  @Test
  void testOfWithHelp() {
    assertEquals(VerbCategory.SYSTEM, VerbCategory.of(Verb.HELP));
  }

  @Test
  void testOfWithQuit() {
    assertEquals(VerbCategory.SYSTEM, VerbCategory.of(Verb.QUIT));
  }

  @Test
  void testOfWithUnknown() {
    assertEquals(VerbCategory.SYSTEM, VerbCategory.of(Verb.UNKNOWN));
  }

  @Test
  void testOfWithNull() {
    assertEquals(VerbCategory.SYSTEM, VerbCategory.of(null));
  }

  @Test
  void testValueOf() {
    assertEquals(VerbCategory.MOVEMENT, VerbCategory.valueOf("MOVEMENT"));
    assertEquals(VerbCategory.INTERACTION, VerbCategory.valueOf("INTERACTION"));
    assertEquals(VerbCategory.INVENTORY, VerbCategory.valueOf("INVENTORY"));
    assertEquals(VerbCategory.COMBAT, VerbCategory.valueOf("COMBAT"));
    assertEquals(VerbCategory.SYSTEM, VerbCategory.valueOf("SYSTEM"));
  }

  @Test
  void testEnumNotNull() {
    for (VerbCategory category : VerbCategory.values()) {
      assertNotNull(category);
    }
  }
}
