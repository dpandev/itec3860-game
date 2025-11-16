package avengers.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Unit tests for the Room class and its Builder. */
class RoomTest {

  @Test
  void builder_validRoom_createsSuccessfully() {
    Room room =
        Room.builder()
            .id("RM-01")
            .name("Test Room")
            .description("A test room for unit testing")
            .exits(Map.of(Direction.NORTH, "RM-02"))
            .itemIds(List.of("IT-01", "IT-02"))
            .monsterIds(List.of("MON-01"))
            .puzzleIds(List.of("PUZ-01"))
            .build();

    assertEquals("RM-01", room.getId());
    assertEquals("Test Room", room.getName());
    assertEquals("A test room for unit testing", room.getDescription());
    assertEquals(Map.of(Direction.NORTH, "RM-02"), room.getExits());
    assertEquals(List.of("IT-01", "IT-02"), room.getItemIds());
    assertEquals(List.of("MON-01"), room.getMonsterIds());
    assertEquals(List.of("PUZ-01"), room.getPuzzleIds());
  }

  @Test
  void builder_minimalRoom_createsSuccessfully() {
    Room room =
        Room.builder().id("RM-01").name("Minimal Room").description("A minimal room").build();

    assertEquals("RM-01", room.getId());
    assertEquals("Minimal Room", room.getName());
    assertEquals("A minimal room", room.getDescription());
    assertTrue(room.getExits().isEmpty());
    assertTrue(room.getItemIds().isEmpty());
    assertTrue(room.getMonsterIds().isEmpty());
    assertTrue(room.getPuzzleIds().isEmpty());
  }

  @Test
  void builder_nullId_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Room.builder().name("Test Room").description("Description").build());
  }

  @Test
  void builder_blankId_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Room.builder().id("").name("Test Room").description("Description").build());
  }

  @Test
  void builder_nullName_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Room.builder().id("RM-01").description("Description").build());
  }

  @Test
  void builder_nullDescription_throwsException() {
    assertThrows(
        IllegalArgumentException.class, () -> Room.builder().id("RM-01").name("Test Room").build());
  }

  @Test
  void builder_nameTooLong_throwsException() {
    String longName = "A".repeat(101);
    assertThrows(
        IllegalArgumentException.class,
        () -> Room.builder().id("RM-01").name(longName).description("Description").build());
  }

  @Test
  void builder_descriptionTooLong_throwsException() {
    String longDescription = "A".repeat(501);
    assertThrows(
        IllegalArgumentException.class,
        () -> Room.builder().id("RM-01").name("Test Room").description(longDescription).build());
  }

  @Test
  void builder_nullCollections_handledGracefully() {
    Room room =
        Room.builder()
            .id("RM-01")
            .name("Test Room")
            .description("Description")
            .exits(null)
            .itemIds(null)
            .monsterIds(null)
            .puzzleIds(null)
            .build();

    assertTrue(room.getExits().isEmpty());
    assertTrue(room.getItemIds().isEmpty());
    assertTrue(room.getMonsterIds().isEmpty());
    assertTrue(room.getPuzzleIds().isEmpty());
  }

  @Test
  void hasExit_existingDirection_returnsTrue() {
    Room room =
        Room.builder()
            .id("RM-01")
            .name("Test Room")
            .description("Description")
            .exits(Map.of(Direction.NORTH, "RM-02"))
            .build();

    assertTrue(room.hasExit(Direction.NORTH));
    assertFalse(room.hasExit(Direction.SOUTH));
  }

  @Test
  void getExitRoomId_existingDirection_returnsRoomId() {
    Room room =
        Room.builder()
            .id("RM-01")
            .name("Test Room")
            .description("Description")
            .exits(Map.of(Direction.NORTH, "RM-02"))
            .build();

    assertEquals("RM-02", room.getExitRoomId(Direction.NORTH));
    assertNull(room.getExitRoomId(Direction.SOUTH));
  }

  @Test
  void equals_sameId_returnsTrue() {
    Room room1 = Room.builder().id("RM-01").name("Room 1").description("Description 1").build();

    Room room2 = Room.builder().id("RM-01").name("Room 2").description("Description 2").build();

    assertEquals(room1, room2); // Same ID means equal
  }

  @Test
  void equals_differentId_returnsFalse() {
    Room room1 = Room.builder().id("RM-01").name("Room").description("Description").build();

    Room room2 = Room.builder().id("RM-02").name("Room").description("Description").build();

    assertNotEquals(room1, room2);
  }

  @Test
  void immutability_collectionsAreCopied() {
    Map<Direction, String> originalExits = Map.of(Direction.NORTH, "RM-02");
    List<String> originalItems = List.of("IT-01");

    Room room =
        Room.builder()
            .id("RM-01")
            .name("Test Room")
            .description("Description")
            .exits(originalExits)
            .itemIds(originalItems)
            .build();

    // Returned collections should be immutable copies
    assertThrows(
        UnsupportedOperationException.class, () -> room.getExits().put(Direction.SOUTH, "RM-03"));
    assertThrows(UnsupportedOperationException.class, () -> room.getItemIds().add("IT-02"));
  }
}
