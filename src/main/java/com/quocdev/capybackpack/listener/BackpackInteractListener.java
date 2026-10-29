package com.quocdev.capybackpack.listener;

import com.quocdev.capybackpack.gui.BackpackGUI;
import com.quocdev.capybackpack.manager.BackpackManager;
import com.quocdev.capybackpack.manager.MessageManager;
import com.quocdev.capybackpack.model.BackpackData;
import com.quocdev.capybackpack.model.BackpackModule;
import com.quocdev.capybackpack.model.BackpackTier;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BackpackInteractListener implements Listener {

    private final JavaPlugin plugin;
    private final BackpackManager manager;
    private final MessageManager messageManager;
    private final BackpackGUI gui;

    public BackpackInteractListener(JavaPlugin plugin, BackpackManager manager, MessageManager messageManager, BackpackGUI gui) {
        this.plugin = plugin;
        this.manager = manager;
        this.messageManager = messageManager;
        this.gui = gui;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreventVanillaBundleInteractions(InventoryClickEvent event) {
        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        boolean currentIsBackpack = manager.isBackpack(current);
        boolean cursorIsBackpack = manager.isBackpack(cursor);

        if (!currentIsBackpack && !cursorIsBackpack) {
            return;
        }

        // 1. Cancel all vanilla bundle inventory actions
        InventoryAction action = event.getAction();
        switch (action) {
            case PICKUP_FROM_BUNDLE:
            case PICKUP_ALL_INTO_BUNDLE:
            case PICKUP_SOME_INTO_BUNDLE:
            case PLACE_FROM_BUNDLE:
            case PLACE_ALL_INTO_BUNDLE:
            case PLACE_SOME_INTO_BUNDLE:
                event.setCancelled(true);
                return;
            default:
                break;
        }

        // 2. Prevent right-clicking a non-upgrade, non-module item onto a backpack
        if (currentIsBackpack && cursor != null && !cursor.getType().isAir()) {
            if (!manager.isUpgrade(cursor) && !manager.isModule(cursor)) {
                if (event.getClick() == ClickType.RIGHT) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        // 3. Prevent holding a backpack on cursor and right-clicking any item or slot
        if (cursorIsBackpack && event.getClick() == ClickType.RIGHT) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || !manager.isBackpack(item)) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();

        if (!player.hasPermission("cappybackpack.use")) {
            messageManager.send(player, "no_permission");
            return;
        }

        boolean isOffhand = event.getHand() == EquipmentSlot.OFF_HAND;
        int slot = isOffhand ? -1 : player.getInventory().getHeldItemSlot();

        gui.open(player, item, slot, isOffhand);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();

        // 1. Drag & Drop Upgrade or Module onto a Backpack
        if (manager.isBackpack(current) && (manager.isUpgrade(cursor) || manager.isModule(cursor))) {
            event.setCancelled(true);

            if (!player.hasPermission("cappybackpack.use")) {
                messageManager.send(player, "no_permission");
                return;
            }

            BackpackData data = manager.loadBackpackData(current);
            if (data == null) return;

            if (manager.isUpgrade(cursor)) {
                BackpackTier targetTier = manager.getUpgradeTarget(cursor);
                if (targetTier == null) return;

                if (data.getTier().getNextTier() == targetTier) {
                    // Valid upgrade!
                    consumeCursor(event, cursor);
                    data.setTier(targetTier);
                    current.setType(targetTier.getBundleMaterial());
                    manager.saveBackpackData(current, data);

                    player.playSound(player.getLocation(), Sound.BLOCK_SMITHING_TABLE_USE, 0.7f, 1.1f);
                    messageManager.send(player, "upgrade_applied", "%tier%", targetTier.getId(), "%slots%", String.valueOf(targetTier.getSlots()), "<color>", "<" + targetTier.getColorHex() + ">");
                } else {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.6f, 0.8f);
                    if (data.getTier() == targetTier || data.getTier().ordinal() >= targetTier.ordinal()) {
                        messageManager.send(player, "upgrade_already_max");
                    } else {
                        messageManager.send(player, "upgrade_invalid", "%required%", targetTier.getPreviousTierId());
                    }
                }
                return;
            }

            if (manager.isModule(cursor)) {
                BackpackModule module = manager.getModuleType(cursor);
                if (module == null) return;

                if (data.hasModule(module)) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.6f, 0.8f);
                    messageManager.send(player, "module_already_installed");
                } else {
                    consumeCursor(event, cursor);
                    data.addModule(module);
                    manager.saveBackpackData(current, data);

                    player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.7f, 1.2f);
                    messageManager.send(player, "module_installed", "%module%", module.getId(), "<color>", "<" + module.getColorHex() + ">");
                }
                return;
            }
        }

        // 2. Right-click Backpack inside inventory to open GUI
        if (event.getClick() == ClickType.RIGHT && (cursor == null || cursor.getType().isAir())) {
            if (manager.isBackpack(current) && event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.PLAYER) {
                // Check if not already in custom backpack inventory
                if (!(event.getView().getTopInventory().getHolder() instanceof com.quocdev.capybackpack.gui.BackpackHolder)) {
                    event.setCancelled(true);
                    int slot = event.getSlot();
                    // Schedule next tick to avoid inventory client desync
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        ItemStack backpack = player.getInventory().getItem(slot);
                        if (backpack != null && manager.isBackpack(backpack)) {
                            gui.open(player, backpack, slot, false);
                        }
                    });
                }
            }
        }
    }

    private void consumeCursor(InventoryClickEvent event, ItemStack cursor) {
        if (cursor.getAmount() > 1) {
            cursor.setAmount(cursor.getAmount() - 1);
            event.getView().setCursor(cursor);
        } else {
            event.getView().setCursor(null);
        }
    }
}
