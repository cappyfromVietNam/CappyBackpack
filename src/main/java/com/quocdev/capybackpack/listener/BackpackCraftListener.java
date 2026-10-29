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
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;

import java.util.ArrayList;
import java.util.List;

public class BackpackCraftListener implements Listener {

    private final BackpackManager manager;

    public BackpackCraftListener(BackpackManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        SmithingInventory inv = event.getInventory();
        ItemStack template = inv.getItem(0);
        ItemStack base = inv.getItem(1);
        ItemStack addition = inv.getItem(2);

        if (base == null || !manager.isBackpack(base)) {
            return;
        }

        // Only allow upgrading Diamond Backpack with Netherite Upgrade Template and Netherite Ingot
        if (template != null && template.getType() == Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE
                && addition != null && addition.getType() == Material.NETHERITE_INGOT) {
            BackpackData data = manager.loadBackpackData(base);
            if (data != null && data.getTier() == BackpackTier.DIAMOND) {
                ItemStack result = base.clone();
                BackpackData resultData = manager.loadBackpackData(result);
                resultData.setTier(BackpackTier.NETHERITE);
                result.setType(BackpackTier.NETHERITE.getBundleMaterial());
                manager.saveBackpackData(result, resultData);
                event.setResult(result);
                return;
            }
        }

        // Disallow any other smithing involving a backpack
        event.setResult(null);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSmithItem(SmithItemEvent event) {
        ItemStack result = event.getInventory().getResult();
        if (result == null || !manager.isBackpack(result)) {
            return;
        }

        if (event.getWhoClicked() instanceof Player player) {
            player.playSound(player.getLocation(), Sound.BLOCK_SMITHING_TABLE_USE, 0.8f, 1.0f);
        }
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
                result.setType(targetTier.getBundleMaterial());
                manager.saveBackpackData(result, resultData);
                inv.setResult(result);
                return;
            }
            inv.setResult(null);
            return;
        }

        // Check 2: Backpack + 1 Module Item (Rule: Only 1 module allowed per backpack)
        if (nonBackpackItems.size() == 1 && manager.isModule(nonBackpackItems.get(0))) {
            if (data.hasAnyModule()) {
                inv.setResult(null);
                return;
            }
            BackpackModule module = manager.getModuleType(nonBackpackItems.get(0));
            if (module != null) {
                ItemStack result = foundBackpack.clone();
                BackpackData resultData = manager.loadBackpackData(result);
                resultData.setModule(module);
                manager.saveBackpackData(result, resultData);
                inv.setResult(result);
                return;
            }
            inv.setResult(null);
            return;
        }

        // Check 3: Backpack + 1 Remover Item (Remove installed module)
        if (nonBackpackItems.size() == 1 && manager.isRemover(nonBackpackItems.get(0))) {
            if (!data.hasAnyModule()) {
                inv.setResult(null);
                return;
            }
            ItemStack result = foundBackpack.clone();
            BackpackData resultData = manager.loadBackpackData(result);
            resultData.clearModule();
            manager.saveBackpackData(result, resultData);
            inv.setResult(result);
            return;
        }

        // Check 4: 3x3 Grid - Backpack in center (index 4) + 8 surrounding mineral items (Iron, Gold, Diamond)
        if (matrix.length == 9 && matrix[4] != null && manager.isBackpack(matrix[4])) {
            Material surroundMat = getSurroundingMaterial(matrix);
            if (surroundMat != null) {
                BackpackTier targetTier = getTierForMaterial(surroundMat);
                if (targetTier != null && data.getTier().getNextTier() == targetTier) {
                    ItemStack result = foundBackpack.clone();
                    BackpackData resultData = manager.loadBackpackData(result);
                    resultData.setTier(targetTier);
                    result.setType(targetTier.getBundleMaterial());
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
        return null;
    }
}
