package com.quocdev.capybackpack.gui;

import com.quocdev.capybackpack.manager.BackpackManager;
import com.quocdev.capybackpack.model.BackpackData;
import com.quocdev.capybackpack.model.BackpackTier;
import com.quocdev.capybackpack.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class BackpackGUI {

    private final BackpackManager manager;

    public BackpackGUI(BackpackManager manager) {
        this.manager = manager;
    }

    public boolean open(Player player, ItemStack backpackItem, int sourceSlot, boolean isOffhand) {
        if (!manager.isBackpack(backpackItem)) {
            return false;
        }

        BackpackData data = manager.loadBackpackData(backpackItem);
        if (data == null) {
            return false;
        }

        UUID existingViewer = manager.getViewer(data.getId());
        if (existingViewer != null && !existingViewer.equals(player.getUniqueId())) {
            player.sendMessage(TextUtil.parse("<#E57373>ʙᴀᴄᴋᴘᴀᴄᴋ ɪs ᴀʟʀᴇᴀᴅʏ ᴏᴘᴇɴ"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.6f, 0.8f);
            return false;
        }

        BackpackTier tier = data.getTier();
        Component title = TextUtil.parse("<#E0E0E0>ʙᴀᴄᴋᴘᴀᴄᴋ <" + tier.getColorHex() + ">· " + tier.getId());

        BackpackHolder holder = new BackpackHolder(data.getId(), player.getUniqueId(), tier, sourceSlot, isOffhand);
        Inventory inv = Bukkit.createInventory(holder, tier.getSlots(), title);
        holder.setInventory(inv);

        ItemStack[] contents = data.getContents();
        if (contents != null) {
            for (int i = 0; i < Math.min(contents.length, inv.getSize()); i++) {
                inv.setItem(i, contents[i]);
            }
        }

        manager.registerViewer(data.getId(), player.getUniqueId());
        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.ITEM_BUNDLE_DROP_CONTENTS, 0.5f, 1.2f);
        return true;
    }
}
