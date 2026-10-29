package com.quocdev.capybackpack.task;

import com.quocdev.capybackpack.manager.BackpackManager;
import com.quocdev.capybackpack.model.BackpackData;
import com.quocdev.capybackpack.model.BackpackModule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BackpackTask extends BukkitRunnable {

    private final JavaPlugin plugin;
    private final BackpackManager manager;
    private long tickCounter = 0;

    // Standard Smelting Recipes
    private static final Map<Material, Material> SMELT_MAP = new EnumMap<>(Material.class);
    // Standard Fuel burn counts (items smelted per 1 fuel)
    private static final Map<Material, Integer> FUEL_MAP = new EnumMap<>(Material.class);
    // Standard Food nutrition values
    private static final Map<Material, FoodValue> FOOD_MAP = new EnumMap<>(Material.class);
    // Standard Compactor map (9 materials -> 1 block)
    private static final Map<Material, Material> COMPACT_MAP = new EnumMap<>(Material.class);

    // Food blacklist
    private static final Set<Material> FOOD_BLACKLIST = EnumSet.of(
            Material.ROTTEN_FLESH,
            Material.POISONOUS_POTATO,
            Material.SPIDER_EYE,
            Material.PUFFERFISH,
            Material.CHICKEN,
            Material.CHORUS_FRUIT
    );

    private record FoodValue(int nutrition, float saturation) {}

    static {
        // Raw ores & blocks
        SMELT_MAP.put(Material.RAW_IRON, Material.IRON_INGOT);
        SMELT_MAP.put(Material.RAW_GOLD, Material.GOLD_INGOT);
        SMELT_MAP.put(Material.RAW_COPPER, Material.COPPER_INGOT);
        SMELT_MAP.put(Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP);
        SMELT_MAP.put(Material.COBBLESTONE, Material.STONE);
        SMELT_MAP.put(Material.SAND, Material.GLASS);
        SMELT_MAP.put(Material.RED_SAND, Material.GLASS);
        SMELT_MAP.put(Material.CLAY_BALL, Material.BRICK);
        SMELT_MAP.put(Material.NETHERRACK, Material.NETHER_BRICK);

        // Foods
        SMELT_MAP.put(Material.BEEF, Material.COOKED_BEEF);
        SMELT_MAP.put(Material.PORKCHOP, Material.COOKED_PORKCHOP);
        SMELT_MAP.put(Material.MUTTON, Material.COOKED_MUTTON);
        SMELT_MAP.put(Material.CHICKEN, Material.COOKED_CHICKEN);
        SMELT_MAP.put(Material.RABBIT, Material.COOKED_RABBIT);
        SMELT_MAP.put(Material.COD, Material.COOKED_COD);
        SMELT_MAP.put(Material.SALMON, Material.COOKED_SALMON);
        SMELT_MAP.put(Material.POTATO, Material.BAKED_POTATO);
        SMELT_MAP.put(Material.KELP, Material.DRIED_KELP);

        // Woods -> Charcoal
        SMELT_MAP.put(Material.OAK_LOG, Material.CHARCOAL);
        SMELT_MAP.put(Material.SPRUCE_LOG, Material.CHARCOAL);
        SMELT_MAP.put(Material.BIRCH_LOG, Material.CHARCOAL);
        SMELT_MAP.put(Material.JUNGLE_LOG, Material.CHARCOAL);
        SMELT_MAP.put(Material.ACACIA_LOG, Material.CHARCOAL);
        SMELT_MAP.put(Material.DARK_OAK_LOG, Material.CHARCOAL);
        SMELT_MAP.put(Material.MANGROVE_LOG, Material.CHARCOAL);
        SMELT_MAP.put(Material.CHERRY_LOG, Material.CHARCOAL);

        // Fuels
        FUEL_MAP.put(Material.COAL, 8);
        FUEL_MAP.put(Material.CHARCOAL, 8);
        FUEL_MAP.put(Material.COAL_BLOCK, 80);
        FUEL_MAP.put(Material.BLAZE_ROD, 12);
        FUEL_MAP.put(Material.LAVA_BUCKET, 100);
        FUEL_MAP.put(Material.OAK_PLANKS, 1);
        FUEL_MAP.put(Material.SPRUCE_PLANKS, 1);
        FUEL_MAP.put(Material.BIRCH_PLANKS, 1);
        FUEL_MAP.put(Material.JUNGLE_PLANKS, 1);
        FUEL_MAP.put(Material.ACACIA_PLANKS, 1);
        FUEL_MAP.put(Material.DARK_OAK_PLANKS, 1);
        FUEL_MAP.put(Material.MANGROVE_PLANKS, 1);
        FUEL_MAP.put(Material.CHERRY_PLANKS, 1);

        // Foods
        FOOD_MAP.put(Material.COOKED_BEEF, new FoodValue(8, 12.8f));
        FOOD_MAP.put(Material.COOKED_PORKCHOP, new FoodValue(8, 12.8f));
        FOOD_MAP.put(Material.GOLDEN_CARROT, new FoodValue(6, 14.4f));
        FOOD_MAP.put(Material.COOKED_MUTTON, new FoodValue(6, 9.6f));
        FOOD_MAP.put(Material.COOKED_SALMON, new FoodValue(6, 9.6f));
        FOOD_MAP.put(Material.COOKED_CHICKEN, new FoodValue(6, 7.2f));
        FOOD_MAP.put(Material.BAKED_POTATO, new FoodValue(5, 6.0f));
        FOOD_MAP.put(Material.BREAD, new FoodValue(5, 6.0f));
        FOOD_MAP.put(Material.COOKED_COD, new FoodValue(5, 6.0f));
        FOOD_MAP.put(Material.COOKED_RABBIT, new FoodValue(5, 6.0f));
        FOOD_MAP.put(Material.APPLE, new FoodValue(4, 2.4f));
        FOOD_MAP.put(Material.CARROT, new FoodValue(3, 3.6f));
        FOOD_MAP.put(Material.SWEET_BERRIES, new FoodValue(2, 0.4f));
        FOOD_MAP.put(Material.GLOW_BERRIES, new FoodValue(2, 0.4f));
        FOOD_MAP.put(Material.MELON_SLICE, new FoodValue(2, 1.2f));
        FOOD_MAP.put(Material.DRIED_KELP, new FoodValue(1, 0.6f));

        // Compactor Recipes (9 items -> 1 block)
        COMPACT_MAP.put(Material.IRON_INGOT, Material.IRON_BLOCK);
        COMPACT_MAP.put(Material.GOLD_INGOT, Material.GOLD_BLOCK);
        COMPACT_MAP.put(Material.COPPER_INGOT, Material.COPPER_BLOCK);
        COMPACT_MAP.put(Material.COAL, Material.COAL_BLOCK);
        COMPACT_MAP.put(Material.REDSTONE, Material.REDSTONE_BLOCK);
        COMPACT_MAP.put(Material.LAPIS_LAZULI, Material.LAPIS_BLOCK);
        COMPACT_MAP.put(Material.DIAMOND, Material.DIAMOND_BLOCK);
        COMPACT_MAP.put(Material.EMERALD, Material.EMERALD_BLOCK);
        COMPACT_MAP.put(Material.RAW_IRON, Material.RAW_IRON_BLOCK);
        COMPACT_MAP.put(Material.RAW_GOLD, Material.RAW_GOLD_BLOCK);
        COMPACT_MAP.put(Material.RAW_COPPER, Material.RAW_COPPER_BLOCK);
        COMPACT_MAP.put(Material.NETHERITE_INGOT, Material.NETHERITE_BLOCK);
    }

    public BackpackTask(JavaPlugin plugin, BackpackManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public void run() {
        tickCounter += 20;

        boolean runMagnet = true;                     // Every 1s (20 ticks)
        boolean runAutoFood = (tickCounter % 40 == 0); // Every 2s (40 ticks)
        boolean runFurnace = (tickCounter % 60 == 0);  // Every 3s (60 ticks)
        boolean runCompactor = (tickCounter % 60 == 0);// Every 3s (60 ticks)

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isValid() || player.isDead()) {
                continue;
            }

            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null || !manager.isBackpack(item)) {
                    continue;
                }

                BackpackData data = manager.loadBackpackData(item);
                if (data == null || !data.hasAnyModule()) {
                    continue;
                }

                // If currently open in GUI by anyone, skip live background modification to avoid conflicts
                if (manager.isBackpackOpen(data.getId())) {
                    continue;
                }

                boolean modified = false;

                // 1. Process Magnet (Every 1s)
                if (runMagnet && data.hasModule(BackpackModule.MAGNET)) {
                    if (processMagnet(player, data)) {
                        modified = true;
                    }
                }

                // 2. Process AutoFood (Every 2s)
                if (runAutoFood && data.hasModule(BackpackModule.AUTO_FOOD)) {
                    if (processAutoFood(player, data)) {
                        modified = true;
                    }
                }

                // 3. Process Furnace (Every 3s)
                if (runFurnace && data.hasModule(BackpackModule.FURNACE)) {
                    if (processFurnace(player, data)) {
                        modified = true;
                    }
                }

                // 4. Process Compactor (Every 3s)
                if (runCompactor && data.hasModule(BackpackModule.COMPACTOR)) {
                    if (processCompactor(data)) {
                        modified = true;
                    }
                }

                if (modified) {
                    manager.saveBackpackData(item, data);
                }
            }
        }
    }

    private boolean processMagnet(Player player, BackpackData data) {
        boolean absorbedAny = false;
        ItemStack[] contents = data.getContents();

        for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), 5.0, 5.0, 5.0)) {
            if (!(entity instanceof Item itemEntity)) {
                continue;
            }

            if (!itemEntity.isValid() || itemEntity.isDead() || itemEntity.getPickupDelay() > 0) {
                continue;
            }

            ItemStack drop = itemEntity.getItemStack();
            if (drop == null || drop.getType().isAir() || manager.isBackpack(drop)) {
                continue; // Do not magnet other backpacks
            }

            if (!canStoreItem(contents, drop)) {
                continue; // No room for this item
            }

            // Store into backpack
            storeItem(contents, drop);
            itemEntity.remove();
            absorbedAny = true;
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.3f, 1.4f);
        }

        return absorbedAny;
    }

    private boolean processCompactor(BackpackData data) {
        ItemStack[] contents = data.getContents();

        for (Map.Entry<Material, Material> entry : COMPACT_MAP.entrySet()) {
            Material raw = entry.getKey();
            Material block = entry.getValue();

            // Count total raw items in backpack
            int total = 0;
            for (ItemStack slot : contents) {
                if (slot != null && slot.getType() == raw) {
                    total += slot.getAmount();
                }
            }

            if (total >= 9) {
                ItemStack blockStack = new ItemStack(block, 1);
                if (canStoreItem(contents, blockStack)) {
                    // Remove 9 raw items
                    int toRemove = 9;
                    for (int i = 0; i < contents.length; i++) {
                        ItemStack slot = contents[i];
                        if (slot != null && slot.getType() == raw) {
                            int take = Math.min(toRemove, slot.getAmount());
                            slot.setAmount(slot.getAmount() - take);
                            toRemove -= take;
                            if (slot.getAmount() <= 0) {
                                contents[i] = null;
                            }
                            if (toRemove <= 0) {
                                break;
                            }
                        }
                    }

                    // Add 1 block to backpack
                    storeItem(contents, blockStack);
                    return true; // Compact 1 batch per cycle
                }
            }
        }

        return false;
    }

    private boolean processAutoFood(Player player, BackpackData data) {
        if (player.getFoodLevel() > 14) {
            return false;
        }

        ItemStack[] contents = data.getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack stack = contents[i];
            if (stack == null || stack.getType().isAir()) {
                continue;
            }

            Material mat = stack.getType();
            if (FOOD_BLACKLIST.contains(mat)) {
                continue;
            }

            FoodValue foodVal = FOOD_MAP.get(mat);
            if (foodVal == null && !mat.isEdible()) {
                continue;
            }

            int nutrition = foodVal != null ? foodVal.nutrition : 4;
            float saturation = foodVal != null ? foodVal.saturation : 2.0f;

            // Apply food to player
            player.setFoodLevel(Math.min(20, player.getFoodLevel() + nutrition));
            player.setSaturation(Math.min((float) player.getFoodLevel(), player.getSaturation() + saturation));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 0.4f, 1.1f);

            // Decrement item
            if (stack.getAmount() > 1) {
                stack.setAmount(stack.getAmount() - 1);
            } else {
                contents[i] = null;
            }

            return true; // Eat 1 item per cycle
        }

        return false;
    }

    private boolean processFurnace(Player player, BackpackData data) {
        ItemStack[] contents = data.getContents();

        // 1. Find a smeltable item
        int smeltIndex = -1;
        Material rawMaterial = null;
        Material resultMaterial = null;

        for (int i = 0; i < contents.length; i++) {
            ItemStack stack = contents[i];
            if (stack != null && !stack.getType().isAir()) {
                Material result = SMELT_MAP.get(stack.getType());
                if (result != null) {
                    smeltIndex = i;
                    rawMaterial = stack.getType();
                    resultMaterial = result;
                    break;
                }
            }
        }

        if (smeltIndex == -1 || resultMaterial == null) {
            return false;
        }

        // 2. Check or consume fuel
        int burnPoints = data.getFurnaceBurnTime();
        if (burnPoints <= 0) {
            int fuelIndex = -1;
            int fuelYield = 0;

            for (int i = 0; i < contents.length; i++) {
                ItemStack stack = contents[i];
                if (stack != null && !stack.getType().isAir()) {
                    Integer yield = FUEL_MAP.get(stack.getType());
                    if (yield != null && yield > 0) {
                        fuelIndex = i;
                        fuelYield = yield;
                        break;
                    }
                }
            }

            if (fuelIndex == -1) {
                return false; // No fuel available
            }

            ItemStack fuelStack = contents[fuelIndex];
            if (fuelStack.getType() == Material.LAVA_BUCKET) {
                contents[fuelIndex] = new ItemStack(Material.BUCKET);
            } else if (fuelStack.getAmount() > 1) {
                fuelStack.setAmount(fuelStack.getAmount() - 1);
            } else {
                contents[fuelIndex] = null;
            }

            burnPoints += fuelYield;
        }

        // 3. Try placing result into contents
        ItemStack resultStack = new ItemStack(resultMaterial, 1);
        if (!canStoreItem(contents, resultStack)) {
            return false; // Backpack is full, cannot store output
        }

        // 4. Perform smelting
        burnPoints--;
        data.setFurnaceBurnTime(burnPoints);

        // Consume raw item
        ItemStack rawStack = contents[smeltIndex];
        if (rawStack.getAmount() > 1) {
            rawStack.setAmount(rawStack.getAmount() - 1);
        } else {
            contents[smeltIndex] = null;
        }

        // Store result item
        storeItem(contents, resultStack);

        return true;
    }

    private boolean canStoreItem(ItemStack[] contents, ItemStack item) {
        int remaining = item.getAmount();
        int maxStack = item.getMaxStackSize();

        for (ItemStack slot : contents) {
            if (slot == null || slot.getType().isAir()) {
                return true;
            }
            if (slot.isSimilar(item) && slot.getAmount() < maxStack) {
                remaining -= (maxStack - slot.getAmount());
                if (remaining <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private void storeItem(ItemStack[] contents, ItemStack item) {
        int remaining = item.getAmount();
        int maxStack = item.getMaxStackSize();

        // 1. Fill existing matching stacks
        for (ItemStack slot : contents) {
            if (slot != null && slot.isSimilar(item) && slot.getAmount() < maxStack) {
                int add = Math.min(remaining, maxStack - slot.getAmount());
                slot.setAmount(slot.getAmount() + add);
                remaining -= add;
                if (remaining <= 0) {
                    return;
                }
            }
        }

        // 2. Put into empty slot
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] == null || contents[i].getType().isAir()) {
                ItemStack clone = item.clone();
                clone.setAmount(remaining);
                contents[i] = clone;
                return;
            }
        }
    }
}
