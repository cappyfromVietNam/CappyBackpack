package com.quocdev.capybackpack.manager;

import com.quocdev.capybackpack.model.BackpackData;
import com.quocdev.capybackpack.model.BackpackModule;
import com.quocdev.capybackpack.model.BackpackTier;
import com.quocdev.capybackpack.util.SmallCaps;
import com.quocdev.capybackpack.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BackpackManager {

    private final JavaPlugin plugin;
    private final NamespacedKey keyItemType;
    private final NamespacedKey keyBackpackId;
    private final NamespacedKey keyTier;
    private final NamespacedKey keyModules;
    private final NamespacedKey keyContents;
    private final NamespacedKey keyFurnaceBurn;
    private final NamespacedKey keyUpgradeTarget;
    private final NamespacedKey keyModuleType;

    // Track active viewers: backpackId -> playerId
    private final Map<UUID, UUID> activeViewers = new ConcurrentHashMap<>();

    public BackpackManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.keyItemType = new NamespacedKey(plugin, "item_type");
        this.keyBackpackId = new NamespacedKey(plugin, "backpack_id");
        this.keyTier = new NamespacedKey(plugin, "tier");
        this.keyModules = new NamespacedKey(plugin, "modules");
        this.keyContents = new NamespacedKey(plugin, "contents");
        this.keyFurnaceBurn = new NamespacedKey(plugin, "furnace_burn");
        this.keyUpgradeTarget = new NamespacedKey(plugin, "upgrade_target");
        this.keyModuleType = new NamespacedKey(plugin, "module_type");
    }

    public static boolean isBundleMaterial(Material material) {
        return material != null && (material == Material.BUNDLE || material.name().endsWith("_BUNDLE"));
    }

    public boolean isBackpack(ItemStack item) {
        if (item == null || !isBundleMaterial(item.getType()) || !item.hasItemMeta()) {
            return false;
        }
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return "backpack".equals(pdc.get(keyItemType, PersistentDataType.STRING));
    }

    public boolean isUpgrade(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return "upgrade".equals(pdc.get(keyItemType, PersistentDataType.STRING));
    }

    public boolean isModule(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return "module".equals(pdc.get(keyItemType, PersistentDataType.STRING));
    }

    public BackpackTier getUpgradeTarget(ItemStack item) {
        if (!isUpgrade(item)) return null;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        String target = pdc.get(keyUpgradeTarget, PersistentDataType.STRING);
        return BackpackTier.fromString(target);
    }

    public BackpackModule getModuleType(ItemStack item) {
        if (!isModule(item)) return null;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        String mod = pdc.get(keyModuleType, PersistentDataType.STRING);
        return BackpackModule.fromString(mod);
    }

    public UUID getBackpackId(ItemStack item) {
        if (!isBackpack(item)) return null;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        String idStr = pdc.get(keyBackpackId, PersistentDataType.STRING);
        if (idStr == null) return null;
        try {
            return UUID.fromString(idStr);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public BackpackData loadBackpackData(ItemStack item) {
        if (!isBackpack(item)) return null;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        String idStr = pdc.get(keyBackpackId, PersistentDataType.STRING);
        UUID id = idStr != null ? UUID.fromString(idStr) : UUID.randomUUID();

        String tierStr = pdc.get(keyTier, PersistentDataType.STRING);
        BackpackTier tier = BackpackTier.fromString(tierStr);

        Set<BackpackModule> modules = EnumSet.noneOf(BackpackModule.class);
        String modStr = pdc.get(keyModules, PersistentDataType.STRING);
        if (modStr != null && !modStr.isEmpty()) {
            for (String part : modStr.split(",")) {
                BackpackModule mod = BackpackModule.fromString(part.trim());
                if (mod != null) {
                    modules.add(mod);
                }
            }
        }

        byte[] rawContents = pdc.get(keyContents, PersistentDataType.BYTE_ARRAY);
        ItemStack[] contents = BackpackData.deserializeContents(rawContents, tier.getSlots());

        Integer burnTime = pdc.get(keyFurnaceBurn, PersistentDataType.INTEGER);
        int furnaceBurn = burnTime != null ? burnTime : 0;

        return new BackpackData(id, tier, modules, contents, furnaceBurn);
    }

    public void saveBackpackData(ItemStack item, BackpackData data) {
        if (item == null || data == null) return;

        // Ensure the bundle item matches the tier color
        item.setType(data.getTier().getBundleMaterial());

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(keyItemType, PersistentDataType.STRING, "backpack");
        pdc.set(keyBackpackId, PersistentDataType.STRING, data.getId().toString());
        pdc.set(keyTier, PersistentDataType.STRING, data.getTier().getId());

        if (data.getModules().isEmpty()) {
            pdc.remove(keyModules);
        } else {
            StringJoiner sj = new StringJoiner(",");
            for (BackpackModule mod : data.getModules()) {
                sj.add(mod.getId());
            }
            pdc.set(keyModules, PersistentDataType.STRING, sj.toString());
        }

        pdc.set(keyContents, PersistentDataType.BYTE_ARRAY, data.serializeContents());
        pdc.set(keyFurnaceBurn, PersistentDataType.INTEGER, data.getFurnaceBurnTime());

        applyBackpackVisuals(meta, data);
        item.setItemMeta(meta);
    }

    public ItemStack createBackpack(BackpackTier tier) {
        ItemStack item = new ItemStack(tier.getBundleMaterial());
        ItemMeta meta = item.getItemMeta();
        UUID id = UUID.randomUUID();

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(keyItemType, PersistentDataType.STRING, "backpack");
        pdc.set(keyBackpackId, PersistentDataType.STRING, id.toString());
        pdc.set(keyTier, PersistentDataType.STRING, tier.getId());
        pdc.set(keyContents, PersistentDataType.BYTE_ARRAY, new byte[0]);
        pdc.set(keyFurnaceBurn, PersistentDataType.INTEGER, 0);

        BackpackData data = new BackpackData(id, tier, EnumSet.noneOf(BackpackModule.class), new ItemStack[tier.getSlots()], 0);
        applyBackpackVisuals(meta, data);

        item.setItemMeta(meta);
        return item;
    }

    private void applyBackpackVisuals(ItemMeta meta, BackpackData data) {
        BackpackTier tier = data.getTier();
        Component name = TextUtil.parse("<#E0E0E0>ʙᴀᴄᴋᴘᴀᴄᴋ <" + tier.getColorHex() + ">· " + tier.getId());
        meta.displayName(name);

        List<Component> lore = new ArrayList<>();
        lore.add(TextUtil.parse("<#9E9E9E>ᴛɪᴇʀ  <" + tier.getColorHex() + ">· " + tier.getId()));
        lore.add(TextUtil.parse("<#9E9E9E>sʟᴏᴛs  <#E0E0E0>· " + tier.getSlots()));

        if (!data.getModules().isEmpty()) {
            StringJoiner sj = new StringJoiner(", ");
            for (BackpackModule mod : data.getModules()) {
                sj.add("<" + mod.getColorHex() + ">" + mod.getId() + "<reset>");
            }
            lore.add(TextUtil.parse("<#9E9E9E>ᴍᴏᴅᴜʟᴇs  <#E0E0E0>· " + sj));
        }

        lore.add(Component.empty());
        lore.add(TextUtil.parse("<#757575>ʀɪɢʜᴛ-ᴄʟɪᴄᴋ ᴛᴏ ᴏᴘᴇɴ"));
        lore.add(TextUtil.parse("<#757575>ᴅʀᴀɢ ᴜᴘɢʀᴀᴅᴇ ʜᴇʀᴇ"));

        meta.lore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
    }

    public ItemStack createUpgrade(BackpackTier targetTier) {
        Material mat;
        switch (targetTier) {
            case IRON:
                mat = Material.IRON_INGOT;
                break;
            case GOLD:
                mat = Material.GOLD_INGOT;
                break;
            case DIAMOND:
                mat = Material.DIAMOND;
                break;
            case NETHERITE:
                mat = Material.NETHERITE_INGOT;
                break;
            default:
                mat = Material.COPPER_INGOT;
                break;
        }

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(keyItemType, PersistentDataType.STRING, "upgrade");
        pdc.set(keyUpgradeTarget, PersistentDataType.STRING, targetTier.getId());

        meta.displayName(TextUtil.parse("<" + targetTier.getColorHex() + ">" + targetTier.getId() + " ᴜᴘɢʀᴀᴅᴇ"));

        List<Component> lore = new ArrayList<>();
        lore.add(TextUtil.parse("<#9E9E9E>ᴛᴀʀɢᴇᴛ  <#E0E0E0>· " + targetTier.getSlots() + " sʟᴏᴛs"));
        lore.add(TextUtil.parse("<#9E9E9E>ʀᴇǫᴜɪʀᴇs  <#E0E0E0>· " + targetTier.getPreviousTierId()));
        lore.add(Component.empty());
        lore.add(TextUtil.parse("<#757575>ᴅʀᴀɢ ᴏɴᴛᴏ ʙᴀᴄᴋᴘᴀᴄᴋ ᴛᴏ ᴀᴘᴘʟʏ"));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createModule(BackpackModule module) {
        Material mat = (module == BackpackModule.FURNACE) ? Material.BLAST_FURNACE : Material.GOLDEN_APPLE;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(keyItemType, PersistentDataType.STRING, "module");
        pdc.set(keyModuleType, PersistentDataType.STRING, module.getId());

        meta.displayName(TextUtil.parse("<" + module.getColorHex() + ">" + module.getId() + " ᴍᴏᴅᴜʟᴇ"));

        List<Component> lore = new ArrayList<>();
        lore.add(TextUtil.parse("<#9E9E9E>ᴇғғᴇᴄᴛ  <#E0E0E0>· " + module.getDescription()));
        lore.add(Component.empty());
        lore.add(TextUtil.parse("<#757575>ᴅʀᴀɢ ᴏɴᴛᴏ ʙᴀᴄᴋᴘᴀᴄᴋ ᴛᴏ ᴀᴘᴘʟʏ"));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isBackpackOpen(UUID backpackId) {
        return activeViewers.containsKey(backpackId);
    }

    public void registerViewer(UUID backpackId, UUID playerId) {
        activeViewers.put(backpackId, playerId);
    }

    public void unregisterViewer(UUID backpackId) {
        activeViewers.remove(backpackId);
    }

    public UUID getViewer(UUID backpackId) {
        return activeViewers.get(backpackId);
    }

    public void clearAllViewers() {
        activeViewers.clear();
    }
}

