package com.quocdev.capybackpack.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RecipeEntry {

    private final String id;
    private final String title;
    private final boolean smithing;
    private final ItemStack result;
    private final Material[] craftingMatrix; // 9 items for 3x3 crafting grid
    private final Material smithingTemplate;
    private final ItemStack smithingBase;
    private final Material smithingAddition;
    private final List<Material> shapelessIngredients;

    // 3x3 Shaped Constructor
    public RecipeEntry(String id, String title, ItemStack result, Material[] craftingMatrix) {
        this.id = id;
        this.title = title;
        this.smithing = false;
        this.result = result;
        this.craftingMatrix = craftingMatrix;
        this.smithingTemplate = null;
        this.smithingBase = null;
        this.smithingAddition = null;
        this.shapelessIngredients = null;
    }

    // Smithing Constructor
    public RecipeEntry(String id, String title, ItemStack result, Material template, ItemStack base, Material addition) {
        this.id = id;
        this.title = title;
        this.smithing = true;
        this.result = result;
        this.craftingMatrix = null;
        this.smithingTemplate = template;
        this.smithingBase = base;
        this.smithingAddition = addition;
        this.shapelessIngredients = null;
    }

    // Shapeless Constructor
    public RecipeEntry(String id, String title, ItemStack result, List<Material> shapelessIngredients) {
        this.id = id;
        this.title = title;
        this.smithing = false;
        this.result = result;
        this.craftingMatrix = null;
        this.smithingTemplate = null;
        this.smithingBase = null;
        this.smithingAddition = null;
        this.shapelessIngredients = shapelessIngredients;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSmithing() {
        return smithing;
    }

    public boolean isShapeless() {
        return shapelessIngredients != null;
    }

    public ItemStack getResult() {
        return result;
    }

    public Material[] getCraftingMatrix() {
        return craftingMatrix;
    }

    public Material getSmithingTemplate() {
        return smithingTemplate;
    }

    public ItemStack getSmithingBase() {
        return smithingBase;
    }

    public Material getSmithingAddition() {
        return smithingAddition;
    }

    public List<Material> getShapelessIngredients() {
        return shapelessIngredients;
    }
}

