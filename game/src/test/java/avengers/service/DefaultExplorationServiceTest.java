package avengers.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import avengers.domain.model.Item;
import avengers.domain.model.Player;
import avengers.domain.model.World;
import avengers.domain.utils.GameContext;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DefaultExplorationServiceTest {

  @Mock private World mockWorld;
  @Mock private GameContext mockContext;

  private DefaultExplorationService explorationService;
  private Player player;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    explorationService = new DefaultExplorationService();
    player = new Player("TestPlayer", "room1");

    when(mockContext.player()).thenReturn(player);
    when(mockContext.world()).thenReturn(mockWorld);
  }

  @Test
  void showStats_withNoEquipment_displaysBaseStats() {
    // Given: Player with no equipped items
    player.setCurrentHealth(85);

    // When
    String result = explorationService.showStats(mockContext);

    // Then
    assertTrue(result.contains("=== Player Stats ==="));
    assertTrue(result.contains("Health: 85 / 100"));
    assertTrue(result.contains("Attack: 10"));
    assertTrue(result.contains("Defense: 0"));
    assertTrue(result.contains("Equipped Items:"));
    assertTrue(result.contains("- Weapon: (none)"));
    assertTrue(result.contains("- Armor: (none)"));
    assertTrue(result.contains("- Artifact: (none)"));
  }

  @Test
  void showStats_withEquippedItems_displaysItemNames() {
    // Given: Player with equipped items
    player.setCurrentHealth(90);
    player.equipItem(Player.EquipmentSlot.WEAPON, "sword1");
    player.equipItem(Player.EquipmentSlot.ARMOR, "armor1");

    Item sword = new Item("sword1", "Iron Sword", "A sturdy iron sword", "Weapon", "+5 Attack", "");
    Item armor =
        new Item("armor1", "Leather Vest", "Basic leather armor", "Armor", "+3 Defense", "");

    when(mockWorld.findItem("sword1")).thenReturn(Optional.of(sword));
    when(mockWorld.findItem("armor1")).thenReturn(Optional.of(armor));

    // When
    String result = explorationService.showStats(mockContext);

    // Then
    assertTrue(result.contains("=== Player Stats ==="));
    assertTrue(result.contains("Health: 90 / 100")); // Base health without bonuses for now
    assertTrue(result.contains("Attack: 10 (+5 from equipment) = 15 total"));
    assertTrue(result.contains("Defense: 0 (+3 from equipment) = 3 total"));
    assertTrue(result.contains("- Weapon: Iron Sword"));
    assertTrue(result.contains("- Armor: Leather Vest"));
    assertTrue(result.contains("- Artifact: (none)"));
  }

  @Test
  void showStats_withMissingItem_displaysNone() {
    // Given: Player with equipped item that doesn't exist in world
    player.equipItem(Player.EquipmentSlot.WEAPON, "missing_sword");

    when(mockWorld.findItem("missing_sword")).thenReturn(Optional.empty());

    // When
    String result = explorationService.showStats(mockContext);

    // Then
    assertTrue(result.contains("- Weapon: (none)"));
  }
}
