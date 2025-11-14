package avengers.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import avengers.domain.model.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameServiceTest {
  private GameService gameService;

  @BeforeEach
  void setUp() {
    gameService = new GameService();
  }

  @Test
  void testCreateEntity() {
    Entity entity = gameService.createEntity("123", "Player");
    assertNotNull(entity);
    assertEquals("123", entity.getId());
    assertEquals("Player", entity.getName());
  }

  @Test
  void testProcessEntity() {
    Entity entity = new Entity("456", "Enemy");
    String result = gameService.processEntity(entity);
    assertEquals("Processing: Enemy", result);
  }
}
