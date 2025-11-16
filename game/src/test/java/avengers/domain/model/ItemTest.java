package avengers.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** Unit tests for the Item class invariants. */
class ItemTest {

  @Test
  void constructor_validItem_createsSuccessfully() {
    Item item = new Item("IT-01", "Test Item", "A test item", "Consumable", "+10 HP", "None");

    assertEquals("IT-01", item.getId());
    assertEquals("Test Item", item.getName());
    assertEquals("A test item", item.getDescription());
    assertEquals("Consumable", item.getCategory());
    assertEquals("+10 HP", item.getEffect());
    assertEquals("None", item.getSpecialEffect());
    assertFalse(item.isKeyItem());
    assertTrue(item.isRemovable());
  }

  @Test
  void constructor_keyItem_setsCorrectFlags() {
    Item keyItem =
        new Item(
            "IT-19",
            "Blessed Sword",
            "A sword from puzzle",
            "Key Item",
            "+10 Damage",
            "Required to break Final Gate.");

    assertTrue(keyItem.isKeyItem());
    assertFalse(keyItem.isRemovable()); // Key items cannot be removed
  }

  @Test
  void constructor_systemBlessing_notRemovable() {
    Item systemBlessing =
        new Item(
            "IT-20",
            "System Blessing",
            "A permanent buff",
            "Artifact",
            "+50 Max HP",
            "Cannot be removed; permanently increases HP.");

    assertFalse(systemBlessing.isKeyItem()); // IT-20 is not a key item
    assertFalse(systemBlessing.isRemovable()); // But it cannot be removed
  }

  @Test
  void constructor_nullId_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Item(null, "Test", "Description", "Consumable", "", ""));
  }

  @Test
  void constructor_blankId_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Item("", "Test", "Description", "Consumable", "", ""));
  }

  @Test
  void constructor_nullName_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Item("IT-01", null, "Description", "Consumable", "", ""));
  }

  @Test
  void constructor_blankName_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Item("IT-01", "", "Description", "Consumable", "", ""));
  }

  @Test
  void constructor_nullDescription_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Item("IT-01", "Test", null, "Consumable", "", ""));
  }

  @Test
  void constructor_nullCategory_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Item("IT-01", "Test", "Description", null, "", ""));
  }

  @Test
  void constructor_nameTooLong_throwsException() {
    String longName = "A".repeat(101);
    assertThrows(
        IllegalArgumentException.class,
        () -> new Item("IT-01", longName, "Description", "Consumable", "", ""));
  }

  @Test
  void constructor_descriptionTooLong_throwsException() {
    String longDescription = "A".repeat(501);
    assertThrows(
        IllegalArgumentException.class,
        () -> new Item("IT-01", "Test", longDescription, "Consumable", "", ""));
  }

  @Test
  void constructor_nullEffectAndSpecialEffect_handledGracefully() {
    Item item = new Item("IT-01", "Test", "Description", "Consumable", null, null);

    assertEquals("", item.getEffect());
    assertEquals("", item.getSpecialEffect());
  }

  @Test
  void equals_sameId_returnsTrue() {
    Item item1 = new Item("IT-01", "Test1", "Desc1", "Consumable", "", "");
    Item item2 = new Item("IT-01", "Test2", "Desc2", "Weapon", "", "");

    assertEquals(item1, item2); // Same ID means equal
  }

  @Test
  void equals_differentId_returnsFalse() {
    Item item1 = new Item("IT-01", "Test", "Desc", "Consumable", "", "");
    Item item2 = new Item("IT-02", "Test", "Desc", "Consumable", "", "");

    assertNotEquals(item1, item2);
  }

  @Test
  void hashCode_sameId_sameHashCode() {
    Item item1 = new Item("IT-01", "Test1", "Desc1", "Consumable", "", "");
    Item item2 = new Item("IT-01", "Test2", "Desc2", "Weapon", "", "");

    assertEquals(item1.hashCode(), item2.hashCode());
  }
}
