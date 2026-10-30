# Graph Report - CapyBackpack  (2026-10-30)

## Corpus Check
- 32 files · ~13,155 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 4 file(s) not represented in the graph (top: (none) 1, .jar 1, .properties 1)

## Summary
- 411 nodes · 1099 edges · 16 communities (11 shown, 5 thin omitted)
- Extraction: 82% EXTRACTED · 18% INFERRED · 0% AMBIGUOUS · INFERRED: 201 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `9ce2d3cd`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BackpackManager
- org.bukkit.inventory.ItemStack
- BackpackModule
- org.bukkit.Material
- messages
- BackpackManager.java
- BackpackTier
- plugin.yml
- org.junit.jupiter.api.Test
- Minecraft Plugin Development
- auto_food
- .onCommand
- AGENT.md
- BackpackRecipeManager

## God Nodes (most connected - your core abstractions)
1. `BackpackManager` - 54 edges
2. `BackpackData` - 36 edges
3. `BackpackTier` - 36 edges
4. `BackpackModule` - 27 edges
5. `MessageManager` - 25 edges
6. `messages` - 25 edges
7. `RecipeEntry` - 19 edges
8. `RecipeGUI` - 19 edges
9. `BackpackRecipeManager` - 19 edges
10. `CappyBackpackCommand` - 17 edges

## Surprising Connections (you probably didn't know these)
- `CappyBackpackPlugin` --references--> `RecipeGUI`  [EXTRACTED]
  src/main/java/com/quocdev/capybackpack/CappyBackpackPlugin.java → src/main/java/com/quocdev/capybackpack/gui/RecipeGUI.java
- `CappyBackpackPlugin` --references--> `BackpackRecipeManager`  [EXTRACTED]
  src/main/java/com/quocdev/capybackpack/CappyBackpackPlugin.java → src/main/java/com/quocdev/capybackpack/manager/BackpackRecipeManager.java
- `CappyBackpackCommand` --references--> `RecipeGUI`  [EXTRACTED]
  src/main/java/com/quocdev/capybackpack/command/CappyBackpackCommand.java → src/main/java/com/quocdev/capybackpack/gui/RecipeGUI.java
- `CappyBackpackCommand` --references--> `BackpackManager`  [EXTRACTED]
  src/main/java/com/quocdev/capybackpack/command/CappyBackpackCommand.java → src/main/java/com/quocdev/capybackpack/manager/BackpackManager.java
- `CappyBackpackCommand` --references--> `MessageManager`  [EXTRACTED]
  src/main/java/com/quocdev/capybackpack/command/CappyBackpackCommand.java → src/main/java/com/quocdev/capybackpack/manager/MessageManager.java

## Import Cycles
- None detected.

## Communities (16 total, 5 thin omitted)

### Community 0 - "BackpackManager"
Cohesion: 0.07
Nodes (7): CappyBackpackPlugin, BackpackGUI, BackpackInteractListener, BackpackInventoryListener, BackpackRestockListener, BackpackManager, MessageManager

### Community 1 - "org.bukkit.inventory.ItemStack"
Cohesion: 0.12
Nodes (3): BackpackData, BackpackTask, FoodValue

### Community 2 - "BackpackModule"
Cohesion: 0.07
Nodes (7): BackpackCraftListener, BackpackModule, AUTO_FOOD, COMPACTOR, FURNACE, MAGNET, RESTOCK

### Community 3 - "org.bukkit.Material"
Cohesion: 0.16
Nodes (3): RecipeEntry, RecipeGUI, RecipeGUIListener

### Community 4 - "messages"
Cohesion: 0.06
Nodes (30): get, give, header, recipe, reload, messages, backpack_already_open, base_has_no_upgrade (+22 more)

### Community 6 - "BackpackTier"
Cohesion: 0.10
Nodes (9): BackpackHolder, RecipeCatalogHolder, RecipeViewHolder, BackpackTier, BASE, DIAMOND, GOLD, IRON (+1 more)

### Community 7 - "plugin.yml"
Cohesion: 0.10
Nodes (19): api-version, author, default, description, aliases, description, permission, usage (+11 more)

### Community 8 - "org.junit.jupiter.api.Test"
Cohesion: 0.16
Nodes (3): SmallCaps, BackpackModelTest, SmallCapsTest

### Community 9 - "Minecraft Plugin Development"
Cohesion: 0.09
Nodes (22): Be careful with reload behavior, Commands, Common Gotchas, Core Rules, Default Working Style, Favor config-driven values, High-Risk Areas, Implementation Patterns (+14 more)

### Community 10 - "auto_food"
Cohesion: 0.17
Nodes (11): check_interval_ticks, enabled, hunger_threshold, check_interval_ticks, enabled, modules, auto_food, furnace (+3 more)

### Community 14 - "AGENT.md"
Cohesion: 0.40
Nodes (4): Mô tả Project, Nguồn, Quy tắc quan trọng, Vai trò

## Knowledge Gaps
- **10 isolated node(s):** `FURNACE`, `AUTO_FOOD`, `MAGNET`, `RESTOCK`, `COMPACTOR` (+5 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 128 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **5 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `BackpackManager` connect `BackpackManager` to `org.bukkit.inventory.ItemStack`, `BackpackModule`, `org.bukkit.Material`, `BackpackManager.java`, `.onCommand`, `BackpackRecipeManager`?**
  _High betweenness centrality (0.089) - this node is a cross-community bridge._
- **What connects `FURNACE`, `AUTO_FOOD`, `MAGNET` to the rest of the system?**
  _10 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BackpackManager` be split into smaller, more focused modules?**
  _Cohesion score 0.07287093942054433 - nodes in this community are weakly interconnected._
- **Why does `BackpackTier` connect `BackpackTier` to `BackpackManager`, `org.bukkit.inventory.ItemStack`, `BackpackModule`, `org.bukkit.Material`, `BackpackManager.java`, `org.junit.jupiter.api.Test`, `.onCommand`?**
  _High betweenness centrality (0.064) - this node is a cross-community bridge._
- **Should `org.bukkit.inventory.ItemStack` be split into smaller, more focused modules?**
  _Cohesion score 0.11582491582491583 - nodes in this community are weakly interconnected._
- **Why does `BackpackData` connect `org.bukkit.inventory.ItemStack` to `BackpackManager`, `BackpackModule`, `BackpackManager.java`, `BackpackTier`, `org.junit.jupiter.api.Test`, `.onCommand`?**
  _High betweenness centrality (0.045) - this node is a cross-community bridge._
- **Should `BackpackModule` be split into smaller, more focused modules?**
  _Cohesion score 0.06890756302521009 - nodes in this community are weakly interconnected._