# CappyBackpack

A minimalist backpack plugin for Paper and Purpur (1.21+), built with Minecraft's native colored bundle items, custom container storage, drag-and-drop upgrades, automated utility modules, and a multi-layer safeguard against vanilla bundle insertion glitches.

---

## Overview

CappyBackpack replaces standard storage with an expandable, tiered backpack system:
- **Visuals**: Uses Minecraft 1.21+ colored bundles (`Material.WHITE_BUNDLE`, `GRAY_BUNDLE`, `YELLOW_BUNDLE`, `LIGHT_BLUE_BUNDLE`, `BLACK_BUNDLE`).
- **Typography**: Interface titles, lore descriptions, and feedback messages use clean Unicode Small Caps (`ᴀ ʙ ᴄ...`) without bold styling.
- **Glitch Prevention**: Strips the vanilla `BUNDLE_CONTENTS` data component and intercepts all bundle inventory actions to prevent vanilla bundle item absorption and loss.
- **Single Module Policy**: Each backpack holds at most one utility module at a time.
- **In-Game Recipe Discovery**: Automatically registers into the vanilla Recipe Book and provides an interactive visual recipe viewer GUI (`/cb recipe`).

---

## Backpack Tiers

| Tier | Base Item | Storage | GUI Rows | Crafting / Upgrade Recipe |
| :--- | :--- | :--- | :--- | :--- |
| **Leather** | White Bundle | 9 slots | 1 row | 8 Leather surrounding 1 Chest |
| **Iron** | Gray Bundle | 18 slots | 2 rows | Leather Backpack + Iron Upgrade |
| **Gold** | Yellow Bundle | 27 slots | 3 rows | Iron Backpack + Gold Upgrade |
| **Diamond** | Light Blue Bundle | 36 slots | 4 rows | Gold Backpack + Diamond Upgrade |
| **Netherite** | Black Bundle | 54 slots | 6 rows | Diamond Backpack + Netherite Upgrade (Smithing Table or Drag & Drop) |

> [!NOTE]
> Backpacks cannot be nested inside other backpacks.

---

## Upgrades

Upgrades expand backpack storage capacity. Apply an upgrade by dragging and dropping it onto a matching backpack in your inventory, or combine them in a crafting table.

| Upgrade Item | Target Tier | Capacity | Recipe (Crafting Table) |
| :--- | :--- | :--- | :--- |
| **Iron Upgrade** | Leather → Iron | 18 slots (+1 row) | 8 Iron Ingots surrounding 1 Leather |
| **Gold Upgrade** | Iron → Gold | 27 slots (+1 row) | 8 Gold Ingots surrounding 1 Leather |
| **Diamond Upgrade** | Gold → Diamond | 36 slots (+1 row) | 8 Diamonds surrounding 1 Leather |
| **Netherite Upgrade** | Diamond → Netherite | 54 slots (+2 rows) | Smithing Table: Netherite Template + Diamond Backpack + Netherite Ingot |

---

## Utility Modules

Modules add automated functions to a backpack while held in your inventory. Each backpack can have **only one active module**.

| Module | Item Material | Interval | Description | Crafting Recipe |
| :--- | :--- | :--- | :--- | :--- |
| **Feed Module** | Golden Apple | 2s | Feeds the player when hunger falls below 18 points, consuming food stored inside the backpack. | 8 Gold Ingots surrounding 1 Golden Apple |
| **Auto-Smelt Module** | Furnace | 3s | Smelts raw ores (iron, copper, gold) and cooks raw meats inside the backpack without requiring fuel. | 8 Iron Ingots surrounding 1 Furnace |
| **Magnet Module** | Compass | 1s | Draws nearby dropped items within a 5-block radius directly into empty backpack slots. | 4 Iron Ingots + 4 Redstone surrounding 1 Compass |
| **Restock Module** | Hopper | Event-driven | Automatically replenishes depleted blocks, food, or arrows in the player's hands from backpack storage. | 4 Iron Ingots + 4 Oak Planks surrounding 1 Hopper |
| **Compactor Module** | Piston | 3s | Automatically crafts stacks of 9 ingots or minerals (iron, gold, copper, diamond, emerald, lapis, coal, redstone) into storage blocks. | 4 Cobblestone + 4 Iron Ingots surrounding 1 Piston |

### Module Remover
- **Item**: Shears (`ᴍᴏᴅᴜʟᴇ ʀᴇᴍᴏᴠᴇʀ`)
- **Usage**: Drag and drop onto any backpack with an installed module to dismantle and destroy the active module, freeing the slot for a new module.
- **Recipe (Shapeless)**: 1 Shears + 2 Flint + 1 Iron Ingot.

---

## In-Game Recipe Catalog

Use `/cb recipe` to open an interactive GUI browsing all custom recipes:
- Filter through categories: **Backpacks**, **Upgrades**, **Modules**, and **Remover**.
- Click any entry to inspect its exact 3x3 crafting grid or smithing table layout with animated cycling ingredients.

---

## Commands & Permissions

### Commands

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/cb give <player> <type> [args] [amount]` | Grant items to a specific player | `capybackpack.admin` |
| `/cb get <type> [args] [amount]` | Obtain items directly for yourself | `capybackpack.admin` |
| `/cb recipe [type] [args]` | Open the recipe catalog GUI | `capybackpack.recipe` |
| `/cb reload` | Reload configuration and `messages.yml` | `capybackpack.admin` |

#### Item Arguments (`<type> [args]`)
- `backpack <leather|iron|gold|diamond|netherite> [amount]`
- `upgrade <iron|gold|diamond|netherite> [amount]`
- `module <feed|auto_smelt|magnet|restock|compactor> [amount]`
- `remover [amount]`

### Permissions

| Permission Node | Description | Default |
| :--- | :--- | :--- |
| `capybackpack.open` | Open and manage personal backpacks | Everyone |
| `capybackpack.recipe` | Open the recipe catalog via `/cb recipe` | Everyone |
| `capybackpack.admin` | Access admin commands (`/cb give`, `/cb get`, `/cb reload`) | OP |

---

## Technical: Vanilla Bundle Mitigation

In Minecraft 1.21+, the vanilla `Bundle` item introduces native right-click insertion and vacuum mechanics that interfere with custom backpack inventories. CappyBackpack implements a four-tier mitigation architecture:

1. **Component Stripping**: Calls `unsetData(DataComponentTypes.BUNDLE_CONTENTS)` during item creation, load, and save cycles. Without this component, the vanilla client does not display the bundle fullness bar ($0/64$) and the server rejects native bundle insertions.
2. **Data Rescue**: On backpack load, any items trapped in vanilla `BUNDLE_CONTENTS` or `BundleMeta` are automatically transferred into the backpack's PersistentDataContainer before clearing the bundle component.
3. **Action Interception**: Cancels all six bundle-related inventory actions (`PICKUP_FROM_BUNDLE`, `PICKUP_ALL_INTO_BUNDLE`, `PICKUP_SOME_INTO_BUNDLE`, `PLACE_FROM_BUNDLE`, `PLACE_ALL_INTO_BUNDLE`, `PLACE_SOME_INTO_BUNDLE`) at `EventPriority.LOWEST`.
4. **Auto-Pickup Suppression**: Strips bundle contents upon `EntityPickupItemEvent` to prevent ground items from bypassing player inventory into vanilla bundle storage.

---

## Building and Installation

### Requirements
- **Server**: Paper, Purpur, or compatible 1.21.x server (tested on 1.21.4, supports up to 26.x).
- **Java**: Java 21 or newer.

### Build from Source
```bash
git clone https://github.com/cappyfromVietNam/CappyBackpack.git
cd CappyBackpack
./gradlew build
```
The compiled jar file will be located at `build/libs/CapyBackpack-1.0.0.jar`.

### Installation
1. Place `CapyBackpack-1.0.0.jar` into your server's `plugins/` folder.
2. Start or reload the server.
3. Configure messages in `plugins/CapyBackpack/messages.yml` if needed.

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
