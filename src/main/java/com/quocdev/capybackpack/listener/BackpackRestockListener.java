package com.quocdev.capybackpack.listener;

import com.quocdev.capybackpack.manager.BackpackManager;
import com.quocdev.capybackpack.model.BackpackData;
import com.quocdev.capybackpack.model.BackpackModule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BackpackRestockListener implements Listener {

    private final JavaPlugin plugin;
    private final BackpackManager manager;

    public BackpackRestockListener(JavaPlugin plugin, BackpackManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand();
        Material placedType = event.getBlockPlaced().getType();

        Bukkit.getScheduler().runTask(plugin, () -> {
            ItemStack inHand = (hand == EquipmentSlot.HAND)
                    ? player.getInventory().getItemInMainHand()
                    : player.getInventory().getItemInOffHand();

            if (inHand == null || inHand.getType().isAir()) {
                restockItem(player, hand, placedType);
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack consumed = event.getItem();
        Material consumedType = consumed.getType();

        Bukkit.getScheduler().runTask(plugin, () -> {
            ItemStack main = player.getInventory().getItemInMainHand();
            if (main == null || main.getType().isAir()) {
                restockItem(player, EquipmentSlot.HAND, consumedType);
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!player.getInventory().contains(Material.ARROW)) {
                restockArrow(player);
            }
        });
    }

    private void restockItem(Player player, EquipmentSlot hand, Material targetMaterial) {
        for (ItemStack backpackItem : player.getInventory().getContents()) {
            if (backpackItem == null || !manager.isBackpack(backpackItem)) {
                continue;
            }

            BackpackData data = manager.loadBackpackData(backpackItem);
            if (data == null || !data.hasModule(BackpackModule.RESTOCK)) {
                continue;
            }

            // If backpack is currently open in GUI, skip
            if (manager.isBackpackOpen(data.getId())) {
                continue;
            }

            ItemStack[] contents = data.getContents();
            for (int i = 0; i < contents.length; i++) {
                ItemStack slot = contents[i];
                if (slot != null && slot.getType() == targetMaterial) {
                    // Transfer stack to hand
                    if (hand == EquipmentSlot.HAND) {
                        player.getInventory().setItemInMainHand(slot.clone());
                    } else {
                        player.getInventory().setItemInOffHand(slot.clone());
                    }

                    contents[i] = null;
                    data.setContents(contents);
                    manager.saveBackpackData(backpackItem, data);

                    player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 0.6f, 1.3f);
                    return;
                }
            }
        }
    }

    private void restockArrow(Player player) {
        for (ItemStack backpackItem : player.getInventory().getContents()) {
            if (backpackItem == null || !manager.isBackpack(backpackItem)) {
                continue;
            }

            BackpackData data = manager.loadBackpackData(backpackItem);
            if (data == null || !data.hasModule(BackpackModule.RESTOCK)) {
                continue;
            }

            if (manager.isBackpackOpen(data.getId())) {
                continue;
            }

            ItemStack[] contents = data.getContents();
            for (int i = 0; i < contents.length; i++) {
                ItemStack slot = contents[i];
                if (slot != null && slot.getType() == Material.ARROW) {
                    player.getInventory().addItem(slot.clone());
                    contents[i] = null;
                    data.setContents(contents);
                    manager.saveBackpackData(backpackItem, data);

                    player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 0.6f, 1.3f);
                    return;
                }
            }
        }
    }
}

