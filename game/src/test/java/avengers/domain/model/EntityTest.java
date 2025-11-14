package avengers.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EntityTest {
  @Test
  void testEntityCreation() {
    Entity entity = new Entity("test-id", "Test Entity");
    assertEquals("test-id", entity.getId());
    assertEquals("Test Entity", entity.getName());
  }
}
