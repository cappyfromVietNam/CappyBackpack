package com.quocdev.capybackpack.gui;

import com.quocdev.capybackpack.manager.BackpackManager;
import com.quocdev.capybackpack.model.BackpackModule;
import com.quocdev.capybackpack.model.BackpackTier;
import com.quocdev.capybackpack.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class RecipeGUI {

    private final BackpackManager manager;
    private final Map<String, RecipeEntry> recipeMap = new LinkedHashMap<>();

    public RecipeGUI(BackpackManager manager) {
        this.manager = manager;
        initRecipes();
    }

    private void initRecipes() {
        // 1. Base Backpack
        Material[] baseMatrix = new Material[]{
                Material.LEATHER, Material.LEATHER, Material.LEATHER,
                Material.LEATHER, Material.CHEST,   Material.LEATHER,
                Material.LEATHER, Material.LEATHER, Material.LEATHER
        };
        recipeMap.put("backpack", new RecipeEntry("backpack", "ʙᴀsᴇ ʙᴀᴄᴋᴘᴀᴄᴋ", manager.createBackpack(BackpackTier.BASE), baseMatrix));

        // 2. Iron Upgrade
        Material[] ironMatrix = new Material[]{
                Material.IRON_INGOT, Material.IRON_INGOT, Material.IRON_INGOT,
                Material.IRON_INGOT, Material.LEATHER,    Material.IRON_INGOT,
                Material.IRON_INGOT, Material.IRON_INGOT, Material.IRON_INGOT
        };
        recipeMap.put("iron_upgrade", new RecipeEntry("iron_upgrade", "ɪʀᴏɴ ᴜᴘɢʀᴀᴅᴇ", manager.createUpgrade(BackpackTier.IRON), ironMatrix));

        // 3. Gold Upgrade
        Material[] goldMatrix = new Material[]{
                Material.GOLD_INGOT, Material.GOLD_INGOT, Material.GOLD_INGOT,
                Material.GOLD_INGOT, Material.LEATHER,    Material.GOLD_INGOT,
                Material.GOLD_INGOT, Material.GOLD_INGOT, Material.GOLD_INGOT
        };
        recipeMap.put("gold_upgrade", new RecipeEntry("gold_upgrade", "ɢᴏʟᴅ ᴜᴘɢʀᴀᴅᴇ", manager.createUpgrade(BackpackTier.GOLD), goldMatrix));

        // 4. Diamond Upgrade
        Material[] diamondMatrix = new Material[]{
                Material.DIAMOND, Material.DIAMOND, Material.DIAMOND,
                Material.DIAMOND, Material.LEATHER, Material.DIAMOND,
                Material.DIAMOND, Material.DIAMOND, Material.DIAMOND
        };
        recipeMap.put("diamond_upgrade", new RecipeEntry("diamond_upgrade", "ᴅɪᴀᴍᴏɴᴅ ᴜᴘɢʀᴀᴅᴇ", manager.createUpgrade(BackpackTier.DIAMOND), diamondMatrix));

        // 5. Netherite Upgrade (Smithing Table)
        recipeMap.put("netherite_upgrade", new RecipeEntry(
                "netherite_upgrade",
                "ɴᴇᴛʜᴇʀɪᴛᴇ ʙᴀᴄᴋᴘᴀᴄᴋ",
                manager.createBackpack(BackpackTier.NETHERITE),
                Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                manager.createBackpack(BackpackTier.DIAMOND),
                Material.NETHERITE_INGOT
        ));

        // 6. Furnace Module
        Material[] furnaceMatrix = new Material[]{
                Material.IRON_INGOT, Material.IRON_INGOT, Material.IRON_INGOT,
                Material.IRON_INGOT, Material.FURNACE,    Material.IRON_INGOT,
                Material.IRON_INGOT, Material.IRON_INGOT, Material.IRON_INGOT
        };
        recipeMap.put("furnace_module", new RecipeEntry("furnace_module", "ғᴜʀɴᴀᴄᴇ ᴍᴏᴅᴜʟᴇ", manager.createModule(BackpackModule.FURNACE), furnaceMatrix));

        // 7. AutoFood Module
        Material[] autoFoodMatrix = new Material[]{
                Material.GOLD_INGOT, Material.GOLD_INGOT,     Material.GOLD_INGOT,
                Material.GOLD_INGOT, Material.GOLDEN_APPLE,   Material.GOLD_INGOT,
                Material.GOLD_INGOT, Material.GOLD_INGOT,     Material.GOLD_INGOT
        };
        recipeMap.put("autofood_module", new RecipeEntry("autofood_module", "ᴀᴜᴛᴏғᴏᴏᴅ ᴍᴏᴅᴜʟᴇ", manager.createModule(BackpackModule.AUTO_FOOD), autoFoodMatrix));

        // 8. Magnet Module
        Material[] magnetMatrix = new Material[]{
                Material.IRON_INGOT, Material.REDSTONE, Material.IRON_INGOT,
                Material.REDSTONE,   Material.COMPASS,  Material.REDSTONE,
                Material.IRON_INGOT, Material.REDSTONE, Material.IRON_INGOT
        };
        recipeMap.put("magnet_module", new RecipeEntry("magnet_module", "ᴍᴀɢɴᴇᴛ ᴍᴏᴅᴜʟᴇ", manager.createModule(BackpackModule.MAGNET), magnetMatrix));

        // 9. Restock Module
        Material[] restockMatrix = new Material[]{
                Material.IRON_INGOT, Material.OAK_PLANKS, Material.IRON_INGOT,
                Material.OAK_PLANKS, Material.HOPPER,     Material.OAK_PLANKS,
                Material.IRON_INGOT, Material.OAK_PLANKS, Material.IRON_INGOT
        };
        recipeMap.put("restock_module", new RecipeEntry("restock_module", "ʀᴇsᴛᴏᴄᴋ ᴍᴏᴅᴜʟᴇ", manager.createModule(BackpackModule.RESTOCK), restockMatrix));

        // 10. Compactor Module
        Material[] compactorMatrix = new Material[]{
                Material.COBBLESTONE, Material.IRON_INGOT, Material.COBBLESTONE,
                Material.IRON_INGOT,  Material.PISTON,     Material.IRON_INGOT,
                Material.COBBLESTONE, Material.IRON_INGOT, Material.COBBLESTONE
        };
        recipeMap.put("compactor_module", new RecipeEntry("compactor_module", "ᴄᴏᴍᴘᴀᴄᴛᴏʀ ᴍᴏᴅᴜʟᴇ", manager.createModule(BackpackModule.COMPACTOR), compactorMatrix));

        // 11. Module Remover (Shapeless)
        List<Material> removerIngredients = Arrays.asList(Material.SHEARS, Material.FLINT, Material.FLINT, Material.IRON_INGOT);
        recipeMap.put("module_remover", new RecipeEntry("module_remover", "ᴍᴏᴅᴜʟᴇ ʀᴇᴍᴏᴠᴇʀ", manager.createRemover(), removerIngredients));
    }

    public Map<String, RecipeEntry> getRecipeMap() {
        return recipeMap;
    }

    public void openCatalog(Player player) {
        RecipeCatalogHolder holder = new RecipeCatalogHolder();
        Component title = TextUtil.parse("<#E0E0E0>ʀᴇᴄɪᴘᴇs <#9E9E9E>· ᴄᴀᴛᴀʟᴏɢ");
        Inventory inv = Bukkit.createInventory(holder, 45, title);
        holder.setInventory(inv);

        // Fill background with dark gray panes
        ItemStack filler = createPane();
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler);
        }

        // Slots for 11 items
        int[] displaySlots = new int[]{10, 11, 12, 13, 14, 15, 16, 20, 21, 22, 23};
        int index = 0;

        for (RecipeEntry entry : recipeMap.values()) {
            if (index >= displaySlots.length) break;

            ItemStack display = entry.getResult().clone();
            ItemMeta meta = display.getItemMeta();
            if (meta != null) {
                List<Component> lore = meta.hasLore() ? new ArrayList<>(meta.lore()) : new ArrayList<>();
                lore.add(Component.empty());
                lore.add(TextUtil.parse("<#757575>ʟᴇғᴛ-ᴄʟɪᴄᴋ ᴛᴏ ᴠɪᴇᴡ ʀᴇᴄɪᴘᴇ"));
                meta.lore(lore);
                display.setItemMeta(meta);
            }

            inv.setItem(displaySlots[index], display);
            index++;
        }

        player.openInventory(inv);
    }

    public void openRecipe(Player player, RecipeEntry entry) {
        if (entry == null) return;

        RecipeViewHolder holder = new RecipeViewHolder();
        Component title = TextUtil.parse("<#E0E0E0>ʀᴇᴄɪᴘᴇ <#9E9E9E>· " + entry.getTitle());
        Inventory inv = Bukkit.createInventory(holder, 45, title);
        holder.setInventory(inv);

        ItemStack filler = createPane();
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler);
        }

        // Arrow and Result slots
        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta arrowMeta = arrow.getItemMeta();
        arrowMeta.displayName(TextUtil.parse("<#9E9E9E>→"));
        arrowMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        arrow.setItemMeta(arrowMeta);
        inv.setItem(23, arrow);

        inv.setItem(24, entry.getResult().clone());

        if (entry.isSmithing()) {
            // Smithing layout: slot 19 (template), 20 (base), 21 (addition), 22 (smithing table)
            inv.setItem(19, new ItemStack(entry.getSmithingTemplate()));
            inv.setItem(20, entry.getSmithingBase().clone());
            inv.setItem(21, new ItemStack(entry.getSmithingAddition()));

            ItemStack smithingIcon = new ItemStack(Material.SMITHING_TABLE);
            ItemMeta iconMeta = smithingIcon.getItemMeta();
            iconMeta.displayName(TextUtil.parse("<#B0BEC5>sᴍɪᴛʜɪɴɢ ᴛᴀʙʟᴇ"));
            smithingIcon.setItemMeta(iconMeta);
            inv.setItem(22, smithingIcon);
        } else if (entry.isShapeless()) {
            // Shapeless layout: place ingredients in grid slots
            int[] gridSlots = new int[]{11, 12, 20, 21};
            List<Material> ingredients = entry.getShapelessIngredients();
            for (int i = 0; i < Math.min(ingredients.size(), gridSlots.length); i++) {
                inv.setItem(gridSlots[i], new ItemStack(ingredients.get(i)));
            }

            ItemStack tableIcon = new ItemStack(Material.CRAFTING_TABLE);
            ItemMeta iconMeta = tableIcon.getItemMeta();
            iconMeta.displayName(TextUtil.parse("<#B0BEC5>ᴄʀᴀғᴛɪɴɢ ᴛᴀʙʟᴇ"));
            List<Component> lore = new ArrayList<>();
            lore.add(TextUtil.parse("<#757575>sʜᴀᴘᴇʟᴇss ʀᴇᴄɪᴘᴇ"));
            iconMeta.lore(lore);
            tableIcon.setItemMeta(iconMeta);
            inv.setItem(25, tableIcon);
        } else {
            // Shaped 3x3 layout
            int[] gridSlots = new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30};
            Material[] matrix = entry.getCraftingMatrix();
            for (int i = 0; i < Math.min(matrix.length, gridSlots.length); i++) {
                if (matrix[i] != null && matrix[i] != Material.AIR) {
                    inv.setItem(gridSlots[i], new ItemStack(matrix[i]));
                } else {
                    inv.setItem(gridSlots[i], null);
                }
            }

            ItemStack tableIcon = new ItemStack(Material.CRAFTING_TABLE);
            ItemMeta iconMeta = tableIcon.getItemMeta();
            iconMeta.displayName(TextUtil.parse("<#B0BEC5>ᴄʀᴀғᴛɪɴɢ ᴛᴀʙʟᴇ"));
            tableIcon.setItemMeta(iconMeta);
            inv.setItem(25, tableIcon);
        }

        // Back button at slot 40
        ItemStack back = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.displayName(TextUtil.parse("<#E0E0E0>← ʙᴀᴄᴋ"));
        List<Component> backLore = new ArrayList<>();
        backLore.add(TextUtil.parse("<#757575>ʀᴇᴛᴜʀɴ ᴛᴏ ᴄᴀᴛᴀʟᴏɢ"));
        backMeta.lore(backLore);
        backMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        back.setItemMeta(backMeta);
        inv.setItem(40, back);

        player.openInventory(inv);
    }

    private ItemStack createPane() {
        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = pane.getItemMeta();
        meta.displayName(Component.empty());
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        pane.setItemMeta(meta);
        return pane;
    }
}
