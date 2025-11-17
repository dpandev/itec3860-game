# Equipment System Demo

## Quick Start Guide

This demo shows how to use the Equipment Handling system in the Solo Leveling game.

## Basic Equipment Commands

### Equipping Items

```bash
# Equip a weapon
> equip iron sword
You equipped Iron Sword to your WEAPON slot.
Your attack increased from 100 to 115!

# Equip armor
> equip leather armor
You equipped Leather Armor to your ARMOR slot.
Your defense increased from 100 to 110!

# Equip an artifact
> equip magic ring
You equipped Magic Ring to your ARTIFACT slot.
Your attack increased to 120 and defense to 115!
```

### Viewing Your Equipment

```bash
> inventory

** EQUIPPED ITEMS **
WEAPON: Iron Sword (+15 Attack)
ARMOR: Leather Armor (+10 Defense)
ARTIFACT: Magic Ring (+5 Attack, +5 Defense)

** INVENTORY **
Consumables:
- Health Potion (+20 HP)
- Mana Potion

Key Items:
- Fire Sigil
- Water Sigil
```

### Handling Slot Conflicts

```bash
# Try to equip another weapon
> equip battle axe
You swapped Iron Sword with Battle Axe in your WEAPON slot.
Iron Sword has been returned to your inventory.
Your attack changed from 120 to 125!
```

### Unequipping Items

```bash
# Unequip by slot name
> unequip weapon
You unequipped Battle Axe from your WEAPON slot.
Battle Axe has been added to your inventory.
Your attack decreased from 125 to 105!

# Unequip armor
> unequip armor
You unequipped Leather Armor from your ARMOR slot.
Leather Armor has been added to your inventory.
Your defense decreased from 115 to 105!
```

## Advanced Features

### Percentage Bonuses

```bash
# Equip an item with percentage bonus
> equip demon king's crown
You equipped Demon King's Crown to your ARTIFACT slot.
All your stats increased by 15%!
```

### Complex Item Effects

```bash
# Items can have multiple stat bonuses
> inspect dragonbone armor
Dragonbone Armor
Description: Forged from fallen dragon remains.
Effect: +50 HP
Special: Increases Max HP from 200 → 250. Also reduces fire damage by 10%.
Status: In inventory

> equip dragonbone armor
You equipped Dragonbone Armor to your ARMOR slot.
Your max health increased from 200 to 250!
```

### Error Handling

```bash
# Try to equip non-existent item
> equip nonexistent sword
Error: Item 'nonexistent sword' not found in inventory.

# Try to equip non-equippable item
> equip health potion
Error: Item 'Health Potion' cannot be equipped.

# Try to unequip empty slot
> unequip weapon
Error: No item equipped in WEAPON slot.
```

## Stat Calculation Examples

### Base Stats
- **Attack**: 100
- **Defense**: 100  
- **Max Health**: 200

### With Equipment
```bash
# Equipped: Iron Sword (+15 Attack), Leather Armor (+10 Defense), Magic Ring (+5 Attack, +5 Defense)
Total Attack: 100 + 15 + 5 = 120
Total Defense: 100 + 10 + 5 = 115
Total Health: 200 (no HP bonuses equipped)
```

### With Percentage Bonuses
```bash
# Equipped: Battle Axe (+20 Attack), Steel Plate Mail (+50 HP), Demon King's Crown (+15% all stats)
Base totals: Attack 120, Defense 100, Health 250
With 15% bonus: Attack 138, Defense 115, Health 287
```

## Item Categories and Slots

| Item Type | Category | Equipment Slot | Example Items |
|-----------|----------|----------------|---------------|
| Swords, Axes, Staffs | Weapon | WEAPON | Iron Sword, Battle Axe, Lich's Staff |
| Protective Gear | Armor | ARMOR | Leather Armor, Steel Plate Mail, Dragonbone Armor |
| Special Items | Artifact | ARTIFACT | Magic Ring, Demon King's Crown, Shadow Monarch's Cloak |

## Tips and Best Practices

### Equipment Strategy
1. **Weapons**: Focus on attack bonuses for damage output
2. **Armor**: Prioritize defense and HP bonuses for survivability  
3. **Artifacts**: Look for unique effects and percentage bonuses

### Inventory Management
- Keep backup equipment for different situations
- Unequip items before major battles to free inventory space
- Use `inspect` command to compare item stats before equipping

### Stat Optimization
- Percentage bonuses are calculated after flat bonuses
- Multiple items can provide the same stat type (bonuses stack)
- Special effects don't stack (only the equipped item's effect applies)

## Common Commands Reference

| Command | Description | Example |
|---------|-------------|---------|
| `equip <item>` | Equip item from inventory | `equip iron sword` |
| `unequip <slot>` | Unequip item from slot | `unequip weapon` |
| `inventory` | Show inventory and equipment | `inventory` |
| `inspect <item>` | View item details | `inspect battle axe` |

## Troubleshooting

### Item Not Found
- Check spelling of item name
- Ensure item is in your inventory
- Use `inventory` to see available items

### Cannot Equip Item
- Verify item is equippable (Weapon/Armor/Artifact category)
- Some items are consumables or key items (not equippable)

### Slot Already Occupied
- System automatically handles slot conflicts
- Previous item returns to inventory
- No need to unequip manually before equipping

This equipment system provides a solid foundation for character progression and tactical gameplay in Solo Leveling!
