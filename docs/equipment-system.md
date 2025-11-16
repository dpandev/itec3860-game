# Equipment Handling System

## Overview
The Equipment Handling system allows players to equip weapons, armor, and artifacts to enhance their character stats dynamically. This system integrates seamlessly with the existing inventory management and command parsing infrastructure.

## Features

### Equipment Slots
- **WEAPON**: For swords, axes, staffs, etc.
- **ARMOR**: For protective gear like leather armor, plate mail, etc.
- **ARTIFACT**: For special items like rings, crowns, cloaks, etc.

### Stat Bonuses
- **Flat Bonuses**: `+25 Attack`, `+10 Defense`, `+50 HP`
- **Percentage Bonuses**: `+15% all stats`, `+10% Attack`
- **Mixed Effects**: `+10 Attack, +5 Defense, +15% all stats`

### Command Support
- `equip <item name>` - Equips an item from inventory
- `unequip <slot name>` - Unequips item from specified slot
- `inventory` - Shows both inventory and equipped items

## Technical Implementation

### Core Classes

#### StatBonus (`avengers.domain.utils.StatBonus`)
Parses and manages stat bonuses from item effects.

**Key Methods:**
- `parseEffect(String)` - Parses effect strings into bonuses
- `getAttackBonus()` - Returns flat attack bonus
- `getDefenseBonus()` - Returns flat defense bonus  
- `getHealthBonus()` - Returns flat HP bonus
- `getPercentageBonus(String)` - Returns percentage bonus for stat

**Supported Formats:**
```java
"+25 Attack"                    // Flat attack bonus
"+10 Defense, +5 HP"           // Multiple flat bonuses
"+15% all stats"               // Percentage bonus
"+10 Attack, +15% all stats"   // Mixed bonuses
```

**Stat Aliases:**
- `Damage` → `Attack`
- `Defence` → `Defense`
- `Health` → `HP`

#### Player Equipment Methods
Enhanced `Player` class with equipment functionality:

```java
// Total stat calculation (base + equipment bonuses)
public int getTotalAttack(World world)
public int getTotalDefense(World world) 
public int getTotalMaxHealth(World world)

// Equipment management
public String equipItem(EquipmentSlot slot, String itemId)
public String unequipItem(EquipmentSlot slot)
public Map<EquipmentSlot, String> getEquippedItems()
```

#### InventoryService Equipment Methods
Extended `InventoryService` with equipment operations:

```java
// Equipment commands
public CommandResult equipItem(GameContext ctx, String itemName)
public CommandResult unequipItem(GameContext ctx, String slotName)

// Helper methods
private EquipmentSlot determineEquipmentSlot(Item item)
```

#### InventoryController Integration
Updated `InventoryController` to handle equipment commands:

- Supports `Verb.EQUIP` and `Verb.UNEQUIP`
- Routes commands to appropriate service methods
- Provides user-friendly error messages

### Equipment Slot Assignment

Items are automatically assigned to slots based on their category:

| Item Category | Equipment Slot |
|---------------|----------------|
| Weapon        | WEAPON         |
| Armor         | ARMOR          |
| Artifact      | ARTIFACT       |

### Slot Conflict Handling

When equipping an item to an occupied slot:
1. Current item is automatically unequipped
2. Current item returns to inventory
3. New item is equipped to the slot
4. User receives confirmation message about the swap

### Error Handling

The system provides comprehensive error handling:

- **Item not found**: "Item 'X' not found in inventory"
- **Non-equippable item**: "Item 'X' cannot be equipped"
- **Empty slot unequip**: "No item equipped in WEAPON slot"
- **Invalid slot name**: "Invalid equipment slot: 'X'"

## Usage Examples

### Equipping Items
```
> equip iron sword
You equipped Iron Sword to your WEAPON slot.

> equip leather armor  
You equipped Leather Armor to your ARMOR slot.

> equip magic ring
You equipped Magic Ring to your ARTIFACT slot.
```

### Slot Conflicts
```
> equip battle axe
You swapped Iron Sword with Battle Axe in your WEAPON slot.
Iron Sword has been returned to your inventory.
```

### Unequipping Items
```
> unequip weapon
You unequipped Battle Axe from your WEAPON slot.
Battle Axe has been added to your inventory.
```

### Viewing Equipment
```
> inventory

** EQUIPPED ITEMS **
WEAPON: Battle Axe (+20 Attack)
ARMOR: Leather Armor (+10 Defense)
ARTIFACT: Magic Ring (+5 Attack, +5 Defense)

** INVENTORY **
Consumables:
- Health Potion (+20 HP)
- Mana Potion

Weapons:
- Iron Sword (+15 Attack)
```

## Data Format

### Item JSON Structure
Items in `items.json` follow this standardized format:

```json
{
  "id": "IT-03",
  "name": "Iron Sword",
  "description": "A sharp iron sword",
  "category": "Weapon",
  "effect": "+25 Attack",
  "specialEffect": "Increases critical hit chance slightly."
}
```

**Key Points:**
- `effect` field contains parseable stat bonuses
- `category` determines equipment slot assignment
- `specialEffect` contains non-stat special abilities

### Standardized Effect Format
All numeric bonuses use the format: `"+<value> <StatName>"`

**Examples:**
- `"+25 Attack"` (weapons)
- `"+10 Defense"` (armor)
- `"+50 HP"` (armor/artifacts)
- `"+15% all stats"` (special artifacts)

## Testing

### Unit Tests
- **StatBonusTest**: 18 tests covering parsing, aliases, percentage bonuses
- **PlayerEquipmentTest**: Equipment mechanics and stat calculations
- **InventoryServiceEquipmentTest**: Service layer functionality

### Test Coverage
- Effect parsing with various formats
- Stat alias handling (Damage→Attack, etc.)
- Percentage bonus support
- Equipment slot assignment
- Slot conflict resolution
- Error handling scenarios

## Integration Points

### Command Flow
```
User Input → CommandParser → InventoryController → InventoryService → Player
```

### Stat Calculation Flow
```
Player.getTotalAttack() → calculateEquipmentBonuses() → StatBonus.parseEffect()
```

### Inventory Integration
- Equipped items are removed from inventory
- Unequipped items are returned to inventory
- Inventory display shows both equipped and unequipped items

## Future Enhancements

### Planned Features
1. **Set Bonuses**: Bonuses for wearing complete armor sets
2. **Durability System**: Equipment degradation over time
3. **Enhancement System**: Upgrading equipment with materials
4. **Conditional Bonuses**: Bonuses that activate under certain conditions

### Extensibility
The system is designed for easy extension:
- Add new equipment slots by updating `EquipmentSlot` enum
- Add new stat types by updating `StatBonus` parsing
- Add new item categories with automatic slot assignment
- Implement percentage bonuses for any stat type

## Performance Considerations

- **Lazy Calculation**: Stats are calculated on-demand, not cached
- **Efficient Parsing**: Effect strings are parsed once when needed
- **Memory Efficient**: Equipment bonuses don't duplicate item data
- **Fast Lookups**: HashMap-based equipment slot storage

## Compatibility

The Equipment Handling system is fully compatible with:
- Existing inventory management
- Save/load functionality (equipment state is part of Player)
- Command parsing infrastructure
- JSON data format
- Testing framework

## Conclusion

The Equipment Handling system provides a robust, extensible foundation for character progression through equipment. It seamlessly integrates with existing game systems while providing room for future enhancements and customization.
