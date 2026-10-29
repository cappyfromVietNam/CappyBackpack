package com.quocdev.capybackpack.gui;

import com.quocdev.capybackpack.model.BackpackTier;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class BackpackHolder implements InventoryHolder {

    private final UUID backpackId;
    private final UUID viewerId;
    private final BackpackTier tier;
    private final int sourceSlot;
    private final boolean offhand;
    private Inventory inventory;

    public BackpackHolder(UUID backpackId, UUID viewerId, BackpackTier tier, int sourceSlot, boolean offhand) {
        this.backpackId = backpackId;
        this.viewerId = viewerId;
        this.tier = tier;
        this.sourceSlot = sourceSlot;
        this.offhand = offhand;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public UUID getBackpackId() {
        return backpackId;
    }

    public UUID getViewerId() {
        return viewerId;
    }

    public BackpackTier getTier() {
        return tier;
    }

    public int getSourceSlot() {
        return sourceSlot;
    }

    public boolean isOffhand() {
        return offhand;
    }
}
