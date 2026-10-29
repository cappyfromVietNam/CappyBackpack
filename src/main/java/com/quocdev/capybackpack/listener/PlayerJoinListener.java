package com.quocdev.capybackpack.listener;

import com.quocdev.capybackpack.manager.BackpackRecipeManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final BackpackRecipeManager recipeManager;

    public PlayerJoinListener(BackpackRecipeManager recipeManager) {
        this.recipeManager = recipeManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().discoverRecipes(recipeManager.getRecipeKeys());
    }
}
