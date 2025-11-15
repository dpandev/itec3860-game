package avengers.service.spi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import avengers.domain.utils.SaveData;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileSaveRepositoryTest {

  @TempDir Path tempDir;

  private FileSaveRepository repository;
  private UUID testPlayerId;
  private SaveData testSaveData;

  @BeforeEach
  void setUp() {
    repository = new FileSaveRepository(tempDir);
    testPlayerId = UUID.randomUUID();
    testSaveData =
        new SaveData(
            testPlayerId,
            "TestPlayer",
            "RM-01",
            List.of("ITM-01", "ITM-02"),
            Map.of("WEAPON", "ITM-05", "ARMOR", "ITM-10"),
            100,
            100,
            25,
            15,
            List.of(),
            List.of(),
            List.of(),
            Instant.now());
  }

  @AfterEach
  void tearDown() throws IOException {
    // Clean up test files
    Files.walk(tempDir)
        .filter(Files::isRegularFile)
        .forEach(
            path -> {
              try {
                Files.deleteIfExists(path);
              } catch (IOException e) {
                // Ignore cleanup errors in tests
              }
            });
  }

  @Test
  void testRepositoryCreation() {
    assertNotNull(repository);
  }

  @Test
  void testSavesDirectoryIsCreated() {
    assertTrue(Files.exists(tempDir));
    assertTrue(Files.isDirectory(tempDir));
  }

  @Test
  void testUpsertCreatesSaveFile() {
    repository.upsert(testSaveData);

    Path saveFile = tempDir.resolve("save_" + testPlayerId + ".json");
    assertTrue(Files.exists(saveFile));
  }

  @Test
  void testUpsertWithNullSaveDataThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> repository.upsert(null));
  }

  @Test
  void testUpsertWithNullPlayerIdThrowsException() {
    SaveData invalidSave =
        new SaveData(
            null,
            "Player",
            "RM-01",
            List.of(),
            Map.of(),
            100,
            100,
            10,
            5,
            List.of(),
            List.of(),
            List.of(),
            Instant.now());

    assertThrows(IllegalArgumentException.class, () -> repository.upsert(invalidSave));
  }

  @Test
  void testFindByPlayerIdReturnsEmptyWhenNotFound() {
    UUID nonExistentId = UUID.randomUUID();

    Optional<SaveData> result = repository.findByPlayerId(nonExistentId);

    assertFalse(result.isPresent());
  }

  @Test
  void testFindByPlayerIdWithNullReturnsEmpty() {
    Optional<SaveData> result = repository.findByPlayerId(null);

    assertFalse(result.isPresent());
  }

  @Test
  void testSaveAndLoad() {
    // Save
    repository.upsert(testSaveData);

    // Load
    Optional<SaveData> loaded = repository.findByPlayerId(testPlayerId);

    assertTrue(loaded.isPresent());
    assertEquals(testSaveData.playerId(), loaded.get().playerId());
    assertEquals(testSaveData.playerName(), loaded.get().playerName());
    assertEquals(testSaveData.roomId(), loaded.get().roomId());
  }

  @Test
  void testSaveDataFieldsPreserved() {
    repository.upsert(testSaveData);
    Optional<SaveData> loaded = repository.findByPlayerId(testPlayerId);

    assertTrue(loaded.isPresent());
    SaveData savedData = loaded.get();

    assertEquals(testSaveData.playerId(), savedData.playerId());
    assertEquals(testSaveData.playerName(), savedData.playerName());
    assertEquals(testSaveData.roomId(), savedData.roomId());
    assertEquals(testSaveData.itemIds(), savedData.itemIds());
    assertEquals(testSaveData.equippedItems(), savedData.equippedItems());
    assertEquals(testSaveData.currentHealth(), savedData.currentHealth());
    assertEquals(testSaveData.maxHealth(), savedData.maxHealth());
    assertEquals(testSaveData.baseAttack(), savedData.baseAttack());
    assertEquals(testSaveData.baseDefense(), savedData.baseDefense());
    assertNotNull(savedData.savedAt());
  }

  @Test
  void testInventoryItemsPreserved() {
    repository.upsert(testSaveData);
    Optional<SaveData> loaded = repository.findByPlayerId(testPlayerId);

    assertTrue(loaded.isPresent());
    assertEquals(2, loaded.get().itemIds().size());
    assertTrue(loaded.get().itemIds().contains("ITM-01"));
    assertTrue(loaded.get().itemIds().contains("ITM-02"));
  }

  @Test
  void testEquippedItemsPreserved() {
    repository.upsert(testSaveData);
    Optional<SaveData> loaded = repository.findByPlayerId(testPlayerId);

    assertTrue(loaded.isPresent());
    assertEquals(2, loaded.get().equippedItems().size());
    assertEquals("ITM-05", loaded.get().equippedItems().get("WEAPON"));
    assertEquals("ITM-10", loaded.get().equippedItems().get("ARMOR"));
  }

  @Test
  void testUpsertOverwritesExistingSave() {
    // Save initial data
    repository.upsert(testSaveData);

    // Create updated data
    SaveData updatedData =
        new SaveData(
            testPlayerId,
            "TestPlayer",
            "RM-02",
            List.of("ITM-03"),
            Map.of("WEAPON", "ITM-06"),
            80,
            100,
            30,
            20,
            List.of(),
            List.of(),
            List.of(),
            Instant.now());

    // Upsert with updated data
    repository.upsert(updatedData);

    // Load and verify
    Optional<SaveData> loaded = repository.findByPlayerId(testPlayerId);

    assertTrue(loaded.isPresent());
    assertEquals("RM-02", loaded.get().roomId());
    assertEquals(1, loaded.get().itemIds().size());
    assertEquals("ITM-03", loaded.get().itemIds().get(0));
    assertEquals(80, loaded.get().currentHealth());
  }

  @Test
  void testMultipleSavesForDifferentPlayers() {
    UUID player1Id = UUID.randomUUID();
    UUID player2Id = UUID.randomUUID();

    SaveData save1 =
        new SaveData(
            player1Id,
            "Player1",
            "RM-01",
            List.of(),
            Map.of(),
            100,
            100,
            10,
            5,
            List.of(),
            List.of(),
            List.of(),
            Instant.now());

    SaveData save2 =
        new SaveData(
            player2Id,
            "Player2",
            "RM-05",
            List.of(),
            Map.of(),
            50,
            100,
            15,
            10,
            List.of(),
            List.of(),
            List.of(),
            Instant.now());

    repository.upsert(save1);
    repository.upsert(save2);

    Optional<SaveData> loaded1 = repository.findByPlayerId(player1Id);
    Optional<SaveData> loaded2 = repository.findByPlayerId(player2Id);

    assertTrue(loaded1.isPresent());
    assertTrue(loaded2.isPresent());
    assertEquals("Player1", loaded1.get().playerName());
    assertEquals("Player2", loaded2.get().playerName());
    assertEquals("RM-01", loaded1.get().roomId());
    assertEquals("RM-05", loaded2.get().roomId());
  }

  @Test
  void testSaveFileNamingFormat() {
    repository.upsert(testSaveData);

    String expectedFilename = "save_" + testPlayerId + ".json";
    Path expectedPath = tempDir.resolve(expectedFilename);

    assertTrue(Files.exists(expectedPath));
  }

  @Test
  void testInstantSerializationAndDeserialization() {
    Instant beforeSave = Instant.now();
    repository.upsert(testSaveData);
    Instant afterSave = Instant.now();

    Optional<SaveData> loaded = repository.findByPlayerId(testPlayerId);

    assertTrue(loaded.isPresent());
    Instant savedTime = loaded.get().savedAt();

    assertNotNull(savedTime);
    // Saved time should be between before and after
    assertTrue(
        !savedTime.isBefore(beforeSave.minusSeconds(1)),
        "Saved time should not be before save operation");
    assertTrue(
        !savedTime.isAfter(afterSave.plusSeconds(1)),
        "Saved time should not be after save operation");
  }

  @Test
  void testEmptyInventoryAndEquipment() {
    SaveData emptyData =
        new SaveData(
            testPlayerId,
            "EmptyPlayer",
            "RM-01",
            List.of(),
            Map.of(),
            100,
            100,
            10,
            5,
            List.of(),
            List.of(),
            List.of(),
            Instant.now());

    repository.upsert(emptyData);
    Optional<SaveData> loaded = repository.findByPlayerId(testPlayerId);

    assertTrue(loaded.isPresent());
    assertTrue(loaded.get().itemIds().isEmpty());
    assertTrue(loaded.get().equippedItems().isEmpty());
  }

  @Test
  void testHealthAndStatsPreserved() {
    repository.upsert(testSaveData);
    Optional<SaveData> loaded = repository.findByPlayerId(testPlayerId);

    assertTrue(loaded.isPresent());
    assertEquals(100, loaded.get().currentHealth());
    assertEquals(100, loaded.get().maxHealth());
    assertEquals(25, loaded.get().baseAttack());
    assertEquals(15, loaded.get().baseDefense());
  }
}
