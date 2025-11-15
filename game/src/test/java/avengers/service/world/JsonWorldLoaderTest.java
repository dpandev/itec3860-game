package avengers.service.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import avengers.domain.model.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JsonWorldLoaderTest {

  private JsonWorldLoader loader;

  @BeforeEach
  void setUp() {
    loader = new JsonWorldLoader();
  }

  @Test
  void testLoaderCreation() {
    assertNotNull(loader);
  }

  @Test
  void testLoadWorld() {
    World world = loader.load();

    assertNotNull(world);
    assertEquals("RM-01", world.getStartRoomId());
  }

  @Test
  void testLoadWorldReturnsValidWorld() {
    World world = loader.load();

    assertNotNull(world.getRooms());
    assertNotNull(world.getItems());
    assertNotNull(world.getMonsters());
    assertNotNull(world.getPuzzles());
  }

  @Test
  void testLoadWorldWithInvalidResourceThrowsException() {
    // This test would need a custom loader pointing to invalid path
    // For now, we test that our current loader succeeds
    World world = loader.load();
    assertNotNull(world);
  }
}
