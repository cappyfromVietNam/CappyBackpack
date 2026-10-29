package com.quocdev.capybackpack.listener;

import com.quocdev.capybackpack.gui.RecipeCatalogHolder;
import com.quocdev.capybackpack.gui.RecipeEntry;
import com.quocdev.capybackpack.gui.RecipeGUI;
import com.quocdev.capybackpack.gui.RecipeViewHolder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class RecipeGUIListener implements Listener {

    private final RecipeGUI recipeGUI;

    public RecipeGUIListener(RecipeGUI recipeGUI) {
        this.recipeGUI = recipeGUI;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCatalogClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof RecipeCatalogHolder)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) {
            return;
        }

        // Match against recipe entries
        for (RecipeEntry entry : recipeGUI.getRecipeMap().values()) {
            if (entry.getResult().getType() == clicked.getType()) {
                recipeGUI.openRecipe(player, entry);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRecipeViewClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof RecipeViewHolder)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        // Slot 40 is the Back button
        if (event.getRawSlot() == 40) {
            recipeGUI.openCatalog(player);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof RecipeCatalogHolder
                || event.getInventory().getHolder() instanceof RecipeViewHolder) {
            event.setCancelled(true);
        }
    }
}
