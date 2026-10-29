package com.quocdev.capybackpack.listener;

import com.quocdev.capybackpack.manager.BackpackManager;
import com.quocdev.capybackpack.model.BackpackData;
import com.quocdev.capybackpack.model.BackpackModule;
import com.quocdev.capybackpack.model.BackpackTier;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BackpackCraftListener implements Listener {

    private final BackpackManager manager;

    public BackpackCraftListener(BackpackManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        CraftingInventory inv = event.getInventory();
        ItemStack[] matrix = inv.getMatrix();

        ItemStack foundBackpack = null;
        int backpackCount = 0;
        List<ItemStack> nonBackpackItems = new ArrayList<>();

        for (ItemStack item : matrix) {
            if (item == null || item.getType().isAir()) {
                continue;
            }
            if (manager.isBackpack(item)) {
                foundBackpack = item;
                backpackCount++;
            } else {
                nonBackpackItems.add(item);
            }
        }

        // If no backpack involved, let vanilla/custom recipes proceed normally
        if (backpackCount == 0) {
            return;
        }

        // Only 1 backpack allowed in crafting grid
        if (backpackCount != 1) {
            inv.setResult(null);
            return;
        }

        BackpackData data = manager.loadBackpackData(foundBackpack);
        if (data == null) {
            inv.setResult(null);
            return;
        }

        // Check 1: Backpack + 1 Upgrade Item (Shapeless crafting)
        if (nonBackpackItems.size() == 1 && manager.isUpgrade(nonBackpackItems.get(0))) {
            BackpackTier targetTier = manager.getUpgradeTarget(nonBackpackItems.get(0));
            if (targetTier != null && data.getTier().getNextTier() == targetTier) {
                ItemStack result = foundBackpack.clone();
                BackpackData resultData = manager.loadBackpackData(result);
                resultData.setTier(targetTier);
                manager.saveBackpackData(result, resultData);
                inv.setResult(result);
                return;
            }
            inv.setResult(null);
            return;
        }

        // Check 2: Backpack + 1 Module Item (Shapeless crafting)
        if (nonBackpackItems.size() == 1 && manager.isModule(nonBackpackItems.get(0))) {
            BackpackModule module = manager.getModuleType(nonBackpackItems.get(0));
            if (module != null && !data.hasModule(module)) {
                ItemStack result = foundBackpack.clone();
                BackpackData resultData = manager.loadBackpackData(result);
                resultData.addModule(module);
                manager.saveBackpackData(result, resultData);
                inv.setResult(result);
                return;
            }
            inv.setResult(null);
            return;
        }

        // Check 3: 3x3 Grid - Backpack in center (index 4) + 8 surrounding mineral items
        if (matrix.length == 9 && matrix[4] != null && manager.isBackpack(matrix[4])) {
            Material surroundMat = getSurroundingMaterial(matrix);
            if (surroundMat != null) {
                BackpackTier targetTier = getTierForMaterial(surroundMat);
                if (targetTier != null && data.getTier().getNextTier() == targetTier) {
                    ItemStack result = foundBackpack.clone();
                    BackpackData resultData = manager.loadBackpackData(result);
                    resultData.setTier(targetTier);
                    manager.saveBackpackData(result, resultData);
                    inv.setResult(result);
                    return;
                }
            }
        }

        // Block any other crafting involving a backpack (e.g. vanilla bundle dyeing wiping data)
        inv.setResult(null);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCraftItem(CraftItemEvent event) {
        ItemStack result = event.getInventory().getResult();
        if (result == null || !manager.isBackpack(result)) {
            return;
        }

        if (event.getWhoClicked() instanceof Player player) {
            player.playSound(player.getLocation(), Sound.BLOCK_SMITHING_TABLE_USE, 0.7f, 1.1f);
        }
    }

    private Material getSurroundingMaterial(ItemStack[] matrix) {
        Material target = null;
        for (int i = 0; i < 9; i++) {
            if (i == 4) continue; // Skip center backpack
            ItemStack item = matrix[i];
            if (item == null || item.getType().isAir()) {
                return null;
            }
            if (target == null) {
                target = item.getType();
            } else if (target != item.getType()) {
                return null; // Must be identical surround materials
            }
        }
        return target;
    }

    private BackpackTier getTierForMaterial(Material mat) {
        if (mat == Material.IRON_INGOT) return BackpackTier.IRON;
        if (mat == Material.GOLD_INGOT) return BackpackTier.GOLD;
        if (mat == Material.DIAMOND) return BackpackTier.DIAMOND;
        if (mat == Material.NETHERITE_INGOT) return BackpackTier.NETHERITE;
        return null;
    }
}
