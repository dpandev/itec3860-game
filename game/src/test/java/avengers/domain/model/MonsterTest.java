package avengers.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Unit tests for the Monster class invariants. */
class MonsterTest {

  @Test
  void constructor_validMonster_createsSuccessfully() {
    Monster monster =
        new Monster(
            "MON-01",
            "Iron-Fanged Lycan",
            "RM-02",
            "A fierce lycan with iron fangs",
            100,
            25,
            List.of("Howl", "Iron Bite"),
            List.of("IT-05", "IT-06"));

    assertEquals("MON-01", monster.getId());
    assertEquals("Iron-Fanged Lycan", monster.getName());
    assertEquals("RM-02", monster.getRoomLocation());
    assertEquals("A fierce lycan with iron fangs", monster.getDescription());
    assertEquals(100, monster.getCurrentHealth());
    assertEquals(100, monster.getMaxHealth());
    assertEquals(25, monster.getBaseAttack());
    assertEquals(25, monster.getDamage());
    assertEquals(List.of("Howl", "Iron Bite"), monster.getSpecialEffects());
    assertEquals(List.of("IT-05", "IT-06"), monster.getItemDrops());
  }

  @Test
  void constructor_nullId_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Monster(null, "Test", "RM-01", "Description", 50, 10, List.of(), List.of()));
  }

  @Test
  void constructor_blankId_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Monster("", "Test", "RM-01", "Description", 50, 10, List.of(), List.of()));
  }

  @Test
  void constructor_nullName_throwsException() {
    // Character constructor validates name
    assertThrows(
        NullPointerException.class,
        () -> new Monster("MON-01", null, "RM-01", "Description", 50, 10, List.of(), List.of()));
  }

  @Test
  void constructor_nullRoomLocation_handledGracefully() {
    // Monster allows null roomLocation and sets it to empty string
    Monster monster =
        new Monster("MON-01", "Test", null, "Description", 50, 10, List.of(), List.of());
    assertEquals("", monster.getRoomLocation());
  }

  @Test
  void constructor_zeroHealth_allowed() {
    // Character constructor doesn't validate health, allows 0
    Monster monster =
        new Monster("MON-01", "Test", "RM-01", "Description", 0, 10, List.of(), List.of());
    assertEquals(0, monster.getCurrentHealth());
    assertEquals(0, monster.getMaxHealth());
  }

  @Test
  void constructor_invalidDamage_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Monster("MON-01", "Test", "RM-01", "Description", 50, -1, List.of(), List.of()));
  }

  @Test
  void constructor_nullCollections_handledGracefully() {
    Monster monster = new Monster("MON-01", "Test", "RM-01", "Description", 50, 10, null, null);

    assertTrue(monster.getSpecialEffects().isEmpty());
    assertTrue(monster.getItemDrops().isEmpty());
  }

  @Test
  void equals_sameId_returnsTrue() {
    Monster monster1 =
        new Monster("MON-01", "Test1", "RM-01", "Desc1", 50, 10, List.of(), List.of());
    Monster monster2 =
        new Monster("MON-01", "Test2", "RM-02", "Desc2", 80, 20, List.of(), List.of());

    assertEquals(monster1, monster2); // Same ID means equal
  }

  @Test
  void equals_differentId_returnsFalse() {
    Monster monster1 =
        new Monster("MON-01", "Test", "RM-01", "Description", 50, 10, List.of(), List.of());
    Monster monster2 =
        new Monster("MON-02", "Test", "RM-01", "Description", 50, 10, List.of(), List.of());

    assertNotEquals(monster1, monster2);
  }

  @Test
  void immutability_collectionsAreCopied() {
    List<String> originalEffects = List.of("Effect1");
    List<String> originalDrops = List.of("IT-01");

    Monster monster =
        new Monster(
            "MON-01", "Test", "RM-01", "Description", 50, 10, originalEffects, originalDrops);

    // Returned collections should be immutable copies
    assertThrows(
        UnsupportedOperationException.class, () -> monster.getSpecialEffects().add("Effect2"));
    assertThrows(UnsupportedOperationException.class, () -> monster.getItemDrops().add("IT-02"));
  }
}
