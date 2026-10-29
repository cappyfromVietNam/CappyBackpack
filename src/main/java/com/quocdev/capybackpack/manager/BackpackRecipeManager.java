package com.quocdev.capybackpack.manager;

import com.quocdev.capybackpack.model.BackpackModule;
import com.quocdev.capybackpack.model.BackpackTier;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BackpackRecipeManager {

    private final JavaPlugin plugin;
    private final BackpackManager manager;
    private final List<NamespacedKey> recipeKeys = new ArrayList<>();

    public BackpackRecipeManager(JavaPlugin plugin, BackpackManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public List<NamespacedKey> getRecipeKeys() {
        return Collections.unmodifiableList(recipeKeys);
    }

    public void registerRecipes() {
        registerBaseBackpackRecipe();
        registerUpgradeRecipes();
        registerModuleRecipes();
        registerRemoverRecipe();
        registerSmithingNetheriteRecipe();
    }

    private void registerBaseBackpackRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "backpack_base");
        ItemStack result = manager.createBackpack(BackpackTier.BASE);
        ShapedRecipe recipe = new ShapedRecipe(key, result);

        recipe.shape("LLL", "LCL", "LLL");
        recipe.setIngredient('L', Material.LEATHER);
        recipe.setIngredient('C', Material.CHEST);
        recipe.setGroup("capybackpack");
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);

        Bukkit.addRecipe(recipe);
        recipeKeys.add(key);
    }

    private void registerUpgradeRecipes() {
        // 1. Iron Upgrade
        NamespacedKey ironKey = new NamespacedKey(plugin, "upgrade_iron");
        ShapedRecipe ironRecipe = new ShapedRecipe(ironKey, manager.createUpgrade(BackpackTier.IRON));
        ironRecipe.shape("III", "ILI", "III");
        ironRecipe.setIngredient('I', Material.IRON_INGOT);
        ironRecipe.setIngredient('L', Material.LEATHER);
        ironRecipe.setGroup("capybackpack");
        ironRecipe.setCategory(CraftingBookCategory.EQUIPMENT);
        Bukkit.addRecipe(ironRecipe);
        recipeKeys.add(ironKey);

        // 2. Gold Upgrade
        NamespacedKey goldKey = new NamespacedKey(plugin, "upgrade_gold");
        ShapedRecipe goldRecipe = new ShapedRecipe(goldKey, manager.createUpgrade(BackpackTier.GOLD));
        goldRecipe.shape("GGG", "GLG", "GGG");
        goldRecipe.setIngredient('G', Material.GOLD_INGOT);
        goldRecipe.setIngredient('L', Material.LEATHER);
        goldRecipe.setGroup("capybackpack");
        goldRecipe.setCategory(CraftingBookCategory.EQUIPMENT);
        Bukkit.addRecipe(goldRecipe);
        recipeKeys.add(goldKey);

        // 3. Diamond Upgrade
        NamespacedKey diamondKey = new NamespacedKey(plugin, "upgrade_diamond");
        ShapedRecipe diamondRecipe = new ShapedRecipe(diamondKey, manager.createUpgrade(BackpackTier.DIAMOND));
        diamondRecipe.shape("DDD", "DLD", "DDD");
        diamondRecipe.setIngredient('D', Material.DIAMOND);
        diamondRecipe.setIngredient('L', Material.LEATHER);
        diamondRecipe.setGroup("capybackpack");
        diamondRecipe.setCategory(CraftingBookCategory.EQUIPMENT);
        Bukkit.addRecipe(diamondRecipe);
        recipeKeys.add(diamondKey);
    }

    private void registerModuleRecipes() {
        // 1. Furnace Module
        NamespacedKey furnaceKey = new NamespacedKey(plugin, "module_furnace");
        ShapedRecipe furnaceRecipe = new ShapedRecipe(furnaceKey, manager.createModule(BackpackModule.FURNACE));
        furnaceRecipe.shape("III", "IFI", "III");
        furnaceRecipe.setIngredient('I', Material.IRON_INGOT);
        furnaceRecipe.setIngredient('F', Material.FURNACE);
        furnaceRecipe.setGroup("capybackpack");
        furnaceRecipe.setCategory(CraftingBookCategory.EQUIPMENT);
        Bukkit.addRecipe(furnaceRecipe);
        recipeKeys.add(furnaceKey);

        // 2. AutoFood Module
        NamespacedKey autoFoodKey = new NamespacedKey(plugin, "module_autofood");
        ShapedRecipe autoFoodRecipe = new ShapedRecipe(autoFoodKey, manager.createModule(BackpackModule.AUTO_FOOD));
        autoFoodRecipe.shape("GGG", "GAG", "GGG");
        autoFoodRecipe.setIngredient('G', Material.GOLD_INGOT);
        autoFoodRecipe.setIngredient('A', Material.GOLDEN_APPLE);
        autoFoodRecipe.setGroup("capybackpack");
        autoFoodRecipe.setCategory(CraftingBookCategory.EQUIPMENT);
        Bukkit.addRecipe(autoFoodRecipe);
        recipeKeys.add(autoFoodKey);

        // 3. Magnet Module
        NamespacedKey magnetKey = new NamespacedKey(plugin, "module_magnet");
        ShapedRecipe magnetRecipe = new ShapedRecipe(magnetKey, manager.createModule(BackpackModule.MAGNET));
        magnetRecipe.shape("IRI", "RCR", "IRI");
        magnetRecipe.setIngredient('I', Material.IRON_INGOT);
        magnetRecipe.setIngredient('R', Material.REDSTONE);
        magnetRecipe.setIngredient('C', Material.COMPASS);
        magnetRecipe.setGroup("capybackpack");
        magnetRecipe.setCategory(CraftingBookCategory.EQUIPMENT);
        Bukkit.addRecipe(magnetRecipe);
        recipeKeys.add(magnetKey);

        // 4. Restock Module
        NamespacedKey restockKey = new NamespacedKey(plugin, "module_restock");
        ShapedRecipe restockRecipe = new ShapedRecipe(restockKey, manager.createModule(BackpackModule.RESTOCK));
        restockRecipe.shape("IPI", "PHP", "IPI");
        restockRecipe.setIngredient('I', Material.IRON_INGOT);
        restockRecipe.setIngredient('P', Material.OAK_PLANKS);
        restockRecipe.setIngredient('H', Material.HOPPER);
        restockRecipe.setGroup("capybackpack");
        restockRecipe.setCategory(CraftingBookCategory.EQUIPMENT);
        Bukkit.addRecipe(restockRecipe);
        recipeKeys.add(restockKey);

        // 5. Compactor Module
        NamespacedKey compactorKey = new NamespacedKey(plugin, "module_compactor");
        ShapedRecipe compactorRecipe = new ShapedRecipe(compactorKey, manager.createModule(BackpackModule.COMPACTOR));
        compactorRecipe.shape("CIC", "IPI", "CIC");
        compactorRecipe.setIngredient('C', Material.COBBLESTONE);
        compactorRecipe.setIngredient('I', Material.IRON_INGOT);
        compactorRecipe.setIngredient('P', Material.PISTON);
        compactorRecipe.setGroup("capybackpack");
        compactorRecipe.setCategory(CraftingBookCategory.EQUIPMENT);
        Bukkit.addRecipe(compactorRecipe);
        recipeKeys.add(compactorKey);
    }

    private void registerRemoverRecipe() {
        NamespacedKey removerKey = new NamespacedKey(plugin, "module_remover");
        ShapelessRecipe removerRecipe = new ShapelessRecipe(removerKey, manager.createRemover());
        removerRecipe.addIngredient(Material.SHEARS);
        removerRecipe.addIngredient(2, Material.FLINT);
        removerRecipe.addIngredient(Material.IRON_INGOT);
        removerRecipe.setGroup("capybackpack");
        removerRecipe.setCategory(CraftingBookCategory.EQUIPMENT);
        Bukkit.addRecipe(removerRecipe);
        recipeKeys.add(removerKey);
    }

    private void registerSmithingNetheriteRecipe() {
        NamespacedKey netheriteKey = new NamespacedKey(plugin, "backpack_netherite_smithing");
        ItemStack result = manager.createBackpack(BackpackTier.NETHERITE);

        RecipeChoice templateChoice = new RecipeChoice.MaterialChoice(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        RecipeChoice baseChoice = new RecipeChoice.MaterialChoice(Material.LIGHT_BLUE_BUNDLE);
        RecipeChoice additionChoice = new RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT);

        SmithingTransformRecipe recipe = new SmithingTransformRecipe(
                netheriteKey,
                result,
                templateChoice,
                baseChoice,
                additionChoice
        );

        Bukkit.addRecipe(recipe);
        recipeKeys.add(netheriteKey);
    }
}
