package com.quocdev.capybackpack.listener;

import com.quocdev.capybackpack.gui.BackpackHolder;
import com.quocdev.capybackpack.manager.BackpackManager;
import com.quocdev.capybackpack.model.BackpackData;
import com.quocdev.capybackpack.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class BackpackInventoryListener implements Listener {

    private final JavaPlugin plugin;
    private final BackpackManager manager;

    public BackpackInventoryListener(JavaPlugin plugin, BackpackManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BackpackHolder holder)) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int rawSlot = event.getRawSlot();
        int topSize = event.getView().getTopInventory().getSize();
        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        // 1. Prevent interacting with the currently opened backpack in bottom inventory
        if (rawSlot >= topSize) {
            // Check if current clicked item is the opened backpack
            if (manager.isBackpack(current)) {
                UUID id = manager.getBackpackId(current);
                if (holder.getBackpackId().equals(id)) {
                    event.setCancelled(true);
                    return;
                }
            }

            // Check number-key hotbar swap (1-9)
            if (event.getClick() == ClickType.NUMBER_KEY) {
                int hotbarSlot = event.getHotbarButton();
                ItemStack hotbarItem = player.getInventory().getItem(hotbarSlot);
                if (manager.isBackpack(hotbarItem)) {
                    UUID id = manager.getBackpackId(hotbarItem);
                    if (holder.getBackpackId().equals(id)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }

        // 2. Anti-Nesting: strictly forbid putting any backpack inside a backpack
        // Case A: Clicking inside backpack with a backpack cursor
        if (rawSlot < topSize && manager.isBackpack(cursor)) {
            event.setCancelled(true);
            player.sendMessage(TextUtil.parse("<#E57373>ᴄᴀɴɴᴏᴛ ɴᴇsᴛ ʙᴀᴄᴋᴘᴀᴄᴋs"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.6f, 0.8f);
            return;
        }

        // Case B: Shift-clicking a backpack from player inventory into the backpack
        if (rawSlot >= topSize && event.isShiftClick() && manager.isBackpack(current)) {
            event.setCancelled(true);
            player.sendMessage(TextUtil.parse("<#E57373>ᴄᴀɴɴᴏᴛ ɴᴇsᴛ ʙᴀᴄᴋᴘᴀᴄᴋs"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.6f, 0.8f);
            return;
        }

        // 3. Hotbar swap into top inventory with a backpack
        if (rawSlot < topSize && event.getClick() == ClickType.NUMBER_KEY) {
            int hotbarSlot = event.getHotbarButton();
            ItemStack hotbarItem = player.getInventory().getItem(hotbarSlot);
            if (manager.isBackpack(hotbarItem)) {
                event.setCancelled(true);
                player.sendMessage(TextUtil.parse("<#E57373>ᴄᴀɴɴᴏᴛ ɴᴇsᴛ ʙᴀᴄᴋᴘᴀᴄᴋs"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.6f, 0.8f);
                return;
            }
        }

        // Live-save on valid clicks (scheduled 1 tick after modification)
        Bukkit.getScheduler().runTask(plugin, () -> saveHolderData(player, holder, event.getView().getTopInventory()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof BackpackHolder holder)) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int topSize = event.getView().getTopInventory().getSize();
        ItemStack oldCursor = event.getOldCursor();

        // Anti-nesting: dragged item is a backpack into top slots
        if (manager.isBackpack(oldCursor)) {
            for (int slot : event.getRawSlots()) {
                if (slot < topSize) {
                    event.setCancelled(true);
                    player.sendMessage(TextUtil.parse("<#E57373>ᴄᴀɴɴᴏᴛ ɴᴇsᴛ ʙᴀᴄᴋᴘᴀᴄᴋs"));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.6f, 0.8f);
                    return;
                }
            }
        }

        // Live-save on valid drags
        Bukkit.getScheduler().runTask(plugin, () -> saveHolderData(player, holder, event.getView().getTopInventory()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof BackpackHolder holder)) {
            return;
        }

        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        saveHolderData(player, holder, event.getInventory());
        manager.unregisterViewer(holder.getBackpackId());
        player.playSound(player.getLocation(), Sound.ITEM_BUNDLE_DROP_CONTENTS, 0.4f, 0.9f);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        if (player.getOpenInventory().getTopInventory().getHolder() instanceof BackpackHolder holder) {
            saveHolderData(player, holder, player.getOpenInventory().getTopInventory());
            manager.unregisterViewer(holder.getBackpackId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.getOpenInventory().getTopInventory().getHolder() instanceof BackpackHolder holder) {
            saveHolderData(player, holder, player.getOpenInventory().getTopInventory());
            manager.unregisterViewer(holder.getBackpackId());
        }
    }

    private void saveHolderData(Player player, BackpackHolder holder, Inventory inv) {
        ItemStack backpackItem = findBackpackItem(player, holder);
        if (backpackItem == null) {
            return;
        }

        BackpackData data = manager.loadBackpackData(backpackItem);
        if (data == null) {
            return;
        }

        ItemStack[] contents = new ItemStack[data.getTier().getSlots()];
        for (int i = 0; i < Math.min(inv.getSize(), contents.length); i++) {
            ItemStack slotItem = inv.getItem(i);
            contents[i] = slotItem != null ? slotItem.clone() : null;
        }

        data.setContents(contents);
        manager.saveBackpackData(backpackItem, data);
    }

    private ItemStack findBackpackItem(Player player, BackpackHolder holder) {
        UUID targetId = holder.getBackpackId();

        // 1. Check source slot
        if (holder.getSourceSlot() >= 0 && holder.getSourceSlot() < player.getInventory().getSize()) {
            ItemStack item = player.getInventory().getItem(holder.getSourceSlot());
            if (item != null && manager.isBackpack(item) && targetId.equals(manager.getBackpackId(item))) {
                return item;
            }
        }

        // 2. Check offhand
        if (holder.isOffhand()) {
            ItemStack offhand = player.getInventory().getItemInOffHand();
            if (manager.isBackpack(offhand) && targetId.equals(manager.getBackpackId(offhand))) {
                return offhand;
            }
        }

        // 3. Fallback scan player inventory
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && manager.isBackpack(item) && targetId.equals(manager.getBackpackId(item))) {
                return item;
            }
        }

        // 4. Check cursor
        ItemStack cursor = player.getItemOnCursor();
        if (manager.isBackpack(cursor) && targetId.equals(manager.getBackpackId(cursor))) {
            return cursor;
        }

        return null;
    }
}
