package com.quocdev.capybackpack.model;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public class BackpackData {

    private final UUID id;
    private BackpackTier tier;
    private final Set<BackpackModule> modules;
    private ItemStack[] contents;
    private int furnaceBurnTime;

    public BackpackData(UUID id, BackpackTier tier, Set<BackpackModule> modules, ItemStack[] contents, int furnaceBurnTime) {
        this.id = id != null ? id : UUID.randomUUID();
        this.tier = tier != null ? tier : BackpackTier.BASE;
        this.modules = modules != null ? modules : EnumSet.noneOf(BackpackModule.class);
        this.contents = contents != null ? contents : new ItemStack[this.tier.getSlots()];
        this.furnaceBurnTime = furnaceBurnTime;

        if (this.contents.length != this.tier.getSlots()) {
            resizeContents(this.tier.getSlots());
        }
    }

    public UUID getId() {
        return id;
    }

    public BackpackTier getTier() {
        return tier;
    }

    public void setTier(BackpackTier tier) {
        this.tier = tier;
        resizeContents(tier.getSlots());
    }

    public Set<BackpackModule> getModules() {
        return modules;
    }

    public boolean hasModule(BackpackModule module) {
        return modules.contains(module);
    }

    public boolean addModule(BackpackModule module) {
        return modules.add(module);
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }

    public int getFurnaceBurnTime() {
        return furnaceBurnTime;
    }

    public void setFurnaceBurnTime(int furnaceBurnTime) {
        this.furnaceBurnTime = furnaceBurnTime;
    }

    public void resizeContents(int newSize) {
        ItemStack[] newContents = new ItemStack[newSize];
        if (contents != null) {
            int toCopy = Math.min(contents.length, newSize);
            System.arraycopy(contents, 0, newContents, 0, toCopy);
        }
        this.contents = newContents;
    }

    public byte[] serializeContents() {
        if (contents == null || contents.length == 0) {
            return new byte[0];
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos)) {
            boos.writeInt(contents.length);
            for (ItemStack item : contents) {
                boos.writeObject(item);
            }
            boos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static ItemStack[] deserializeContents(byte[] data, int expectedSize) {
        if (data == null || data.length == 0) {
            return new ItemStack[expectedSize];
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             BukkitObjectInputStream bois = new BukkitObjectInputStream(bais)) {
            int length = bois.readInt();
            ItemStack[] loaded = new ItemStack[expectedSize];
            int readCount = Math.min(length, expectedSize);
            for (int i = 0; i < length; i++) {
                ItemStack item = (ItemStack) bois.readObject();
                if (i < expectedSize) {
                    loaded[i] = item;
                }
            }
            return loaded;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ItemStack[expectedSize];
        }
    }
}
