package avengers.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import avengers.domain.model.Item;
import avengers.domain.model.Monster;
import avengers.domain.model.Player;
import avengers.domain.model.Room;
import avengers.domain.model.World;
import avengers.domain.utils.CommandResult;
import avengers.domain.utils.GameContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventoryServiceTest {

  private InventoryService inventoryService;
  private GameContext gameContext;
  private World world;
  private Player player;
  private Room testRoom;
  private Item testItem;
  private Monster testMonster;

  @BeforeEach
  void setUp() {
    inventoryService = new InventoryService();

    // Create test item
    testItem =
        new Item("IT-01", "Test Sword", "A basic test sword", "Weapon", "+10 Damage", "None");

    // Create test monster
    testMonster = new Monster("Orc", 50, 15, 5);

    // Create test room with the item and monster
    List<String> roomItems = new ArrayList<>();
    roomItems.add("IT-01");
    List<String> roomMonsters = new ArrayList<>();
    roomMonsters.add(testMonster.getId().toString());

    testRoom =
        new Room(
            "room1",
            "Test Room",
            "A room for testing",
            roomItems,
            roomMonsters,
            new HashMap<>(),
            new ArrayList<>(),
            false);

    // Create world with items, monsters, and rooms
    Map<String, Item> items = Map.of("IT-01", testItem);
    Map<String, Monster> monsters = Map.of(testMonster.getId().toString(), testMonster);
    Map<String, Room> rooms = Map.of("room1", testRoom);

    world = new World(rooms, items, new HashMap<>(), monsters, "room1");

    // Create player
    player = new Player("TestPlayer", "room1");

    // Create game context
    gameContext = new GameContext(world, player);
  }

  @Test
  void testShowInventoryWhenEmpty() {
    CommandResult result = inventoryService.showInventory(gameContext);

    assertNotNull(result);
    assertTrue(result.success());
    assertTrue(result.message().contains("Your inventory is empty"));
  }

  @Test
  void testShowInventoryWithItems() {
    // Add item to player inventory
    player.addItemToInventory("IT-01");

    CommandResult result = inventoryService.showInventory(gameContext);

    assertNotNull(result);
    assertTrue(result.success());
    assertTrue(result.message().contains("CARRIED ITEMS"));
    assertTrue(result.message().contains("Test Sword"));
  }

  @Test
  void testShowInventoryWithEquippedItems() {
    // Add and equip item
    player.addItemToInventory("IT-01");
    player.equipItem(Player.EquipmentSlot.WEAPON, "IT-01");

    CommandResult result = inventoryService.showInventory(gameContext);

    assertNotNull(result);
    assertTrue(result.success());
    assertTrue(result.message().contains("EQUIPPED ITEMS"));
    assertTrue(result.message().contains("WEAPON: Test Sword"));
  }

  @Test
  void testPickupItemSuccess() {
    CommandResult result = inventoryService.pickupItem(gameContext, "Test Sword");

    assertNotNull(result);
    assertTrue(result.success());
    assertTrue(result.message().contains("You picked up the Test Sword"));
    assertTrue(player.hasItemInInventory("IT-01"));
    assertFalse(testRoom.hasItem("IT-01"));
  }

  @Test
  void testPickupItemByIdSuccess() {
    CommandResult result = inventoryService.pickupItem(gameContext, "IT-01");

    assertNotNull(result);
    assertTrue(result.success());
    assertTrue(result.message().contains("You picked up the Test Sword"));
    assertTrue(player.hasItemInInventory("IT-01"));
  }

  @Test
  void testPickupItemNotInRoom() {
    CommandResult result = inventoryService.pickupItem(gameContext, "Nonexistent Item");

    assertNotNull(result);
    assertFalse(result.success());
    assertTrue(
        result.message().contains("There is no item called 'Nonexistent Item' in this room"));
  }

  @Test
  void testDropItemSuccess() {
    // First add item to inventory
    player.addItemToInventory("IT-01");
    testRoom.removeItem("IT-01"); // Remove from room since we picked it up

    CommandResult result = inventoryService.dropItem(gameContext, "Test Sword");

    assertNotNull(result);
    assertTrue(result.success());
    assertTrue(result.message().contains("You dropped the Test Sword"));
    assertFalse(player.hasItemInInventory("IT-01"));
    assertTrue(testRoom.hasItem("IT-01"));
  }

  @Test
  void testDropItemNotInInventory() {
    CommandResult result = inventoryService.dropItem(gameContext, "Nonexistent Item");

    assertNotNull(result);
    assertFalse(result.success());
    assertTrue(
        result
            .message()
            .contains("You don't have an item called 'Nonexistent Item' in your inventory"));
  }

  @Test
  void testInspectItemInRoom() {
    CommandResult result = inventoryService.inspectTarget(gameContext, "Test Sword");

    assertNotNull(result);
    assertTrue(result.success());
    assertTrue(result.message().contains("ITEM DETAILS"));
    assertTrue(result.message().contains("Test Sword"));
    assertTrue(result.message().contains("A basic test sword"));
    assertTrue(result.message().contains("Status: In current room"));
  }

  @Test
  void testInspectItemInInventory() {
    // Add item to inventory and remove from room
    player.addItemToInventory("IT-01");
    testRoom.removeItem("IT-01");

    CommandResult result = inventoryService.inspectTarget(gameContext, "Test Sword");

    assertNotNull(result);
    assertTrue(result.success());
    assertTrue(result.message().contains("Status: In inventory"));
  }

  @Test
  void testInspectEquippedItem() {
    // Add and equip item
    player.addItemToInventory("IT-01");
    player.equipItem(Player.EquipmentSlot.WEAPON, "IT-01");
    testRoom.removeItem("IT-01");

    CommandResult result = inventoryService.inspectTarget(gameContext, "Test Sword");

    assertNotNull(result);
    assertTrue(result.success());
    assertTrue(result.message().contains("Status: EQUIPPED (WEAPON)"));
  }

  @Test
  void testInspectMonster() {
    CommandResult result = inventoryService.inspectTarget(gameContext, "Orc");

    assertNotNull(result);
    assertTrue(result.success());
    assertTrue(result.message().contains("MONSTER DETAILS"));
    assertTrue(result.message().contains("Orc"));
    assertTrue(result.message().contains("Health: 50/50"));
    assertTrue(result.message().contains("Attack: 15"));
    assertTrue(result.message().contains("Defense: 5"));
  }

  @Test
  void testInspectNonexistentTarget() {
    CommandResult result = inventoryService.inspectTarget(gameContext, "Nonexistent");

    assertNotNull(result);
    assertFalse(result.success());
    assertTrue(
        result
            .message()
            .contains("There is no item or monster called 'Nonexistent' that you can inspect"));
  }

  @Test
  void testPickupFromInvalidRoom() {
    // Create player in invalid room
    player.setRoomId("invalid-room");

    CommandResult result = inventoryService.pickupItem(gameContext, "Test Sword");

    assertNotNull(result);
    assertFalse(result.success());
    assertTrue(result.message().contains("You are not in a valid room"));
  }
}
