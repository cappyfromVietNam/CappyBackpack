package com.quocdev.capybackpack.manager;

import com.quocdev.capybackpack.model.BackpackModule;
import com.quocdev.capybackpack.model.BackpackTier;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class BackpackRecipeManager {

    private final JavaPlugin plugin;
    private final BackpackManager manager;

    public BackpackRecipeManager(JavaPlugin plugin, BackpackManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public void registerRecipes() {
        registerBaseBackpackRecipe();
        registerUpgradeRecipes();
        registerModuleRecipes();
    }

    private void registerBaseBackpackRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "backpack_base");
        ItemStack result = manager.createBackpack(BackpackTier.BASE);
        ShapedRecipe recipe = new ShapedRecipe(key, result);

        recipe.shape("LLL", "LCL", "LLL");
        recipe.setIngredient('L', Material.LEATHER);
        recipe.setIngredient('C', Material.CHEST);

        Bukkit.addRecipe(recipe);
    }

    private void registerUpgradeRecipes() {
        // 1. Iron Upgrade
        NamespacedKey ironKey = new NamespacedKey(plugin, "upgrade_iron");
        ShapedRecipe ironRecipe = new ShapedRecipe(ironKey, manager.createUpgrade(BackpackTier.IRON));
        ironRecipe.shape("III", "ILI", "III");
        ironRecipe.setIngredient('I', Material.IRON_INGOT);
        ironRecipe.setIngredient('L', Material.LEATHER);
        Bukkit.addRecipe(ironRecipe);

        // 2. Gold Upgrade
        NamespacedKey goldKey = new NamespacedKey(plugin, "upgrade_gold");
        ShapedRecipe goldRecipe = new ShapedRecipe(goldKey, manager.createUpgrade(BackpackTier.GOLD));
        goldRecipe.shape("GGG", "GLG", "GGG");
        goldRecipe.setIngredient('G', Material.GOLD_INGOT);
        goldRecipe.setIngredient('L', Material.LEATHER);
        Bukkit.addRecipe(goldRecipe);

        // 3. Diamond Upgrade
        NamespacedKey diamondKey = new NamespacedKey(plugin, "upgrade_diamond");
        ShapedRecipe diamondRecipe = new ShapedRecipe(diamondKey, manager.createUpgrade(BackpackTier.DIAMOND));
        diamondRecipe.shape("DDD", "DLD", "DDD");
        diamondRecipe.setIngredient('D', Material.DIAMOND);
        diamondRecipe.setIngredient('L', Material.LEATHER);
        Bukkit.addRecipe(diamondRecipe);

        // 4. Netherite Upgrade
        NamespacedKey netheriteKey = new NamespacedKey(plugin, "upgrade_netherite");
        ShapedRecipe netheriteRecipe = new ShapedRecipe(netheriteKey, manager.createUpgrade(BackpackTier.NETHERITE));
        netheriteRecipe.shape("NNN", "NLN", "NNN");
        netheriteRecipe.setIngredient('N', Material.NETHERITE_INGOT);
        netheriteRecipe.setIngredient('L', Material.LEATHER);
        Bukkit.addRecipe(netheriteRecipe);
    }

    private void registerModuleRecipes() {
        // 1. Furnace Module
        NamespacedKey furnaceKey = new NamespacedKey(plugin, "module_furnace");
        ShapedRecipe furnaceRecipe = new ShapedRecipe(furnaceKey, manager.createModule(BackpackModule.FURNACE));
        furnaceRecipe.shape("III", "IFI", "III");
        furnaceRecipe.setIngredient('I', Material.IRON_INGOT);
        furnaceRecipe.setIngredient('F', Material.FURNACE);
        Bukkit.addRecipe(furnaceRecipe);

        // 2. AutoFood Module
        NamespacedKey autoFoodKey = new NamespacedKey(plugin, "module_autofood");
        ShapedRecipe autoFoodRecipe = new ShapedRecipe(autoFoodKey, manager.createModule(BackpackModule.AUTO_FOOD));
        autoFoodRecipe.shape("GGG", "GAG", "GGG");
        autoFoodRecipe.setIngredient('G', Material.GOLD_INGOT);
        autoFoodRecipe.setIngredient('A', Material.GOLDEN_APPLE);
        Bukkit.addRecipe(autoFoodRecipe);
    }
}
