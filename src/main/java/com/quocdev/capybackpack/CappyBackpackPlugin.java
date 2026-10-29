package com.quocdev.capybackpack;

import com.quocdev.capybackpack.command.CappyBackpackCommand;
import com.quocdev.capybackpack.gui.BackpackGUI;
import com.quocdev.capybackpack.gui.BackpackHolder;
import com.quocdev.capybackpack.listener.BackpackCraftListener;
import com.quocdev.capybackpack.listener.BackpackInteractListener;
import com.quocdev.capybackpack.listener.BackpackInventoryListener;
import com.quocdev.capybackpack.listener.PlayerJoinListener;
import com.quocdev.capybackpack.manager.BackpackManager;
import com.quocdev.capybackpack.manager.BackpackRecipeManager;
import com.quocdev.capybackpack.manager.MessageManager;
import com.quocdev.capybackpack.task.BackpackTask;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class CappyBackpackPlugin extends JavaPlugin {

    private BackpackManager backpackManager;
    private BackpackRecipeManager recipeManager;
    private MessageManager messageManager;
    private BackpackGUI backpackGUI;
    private BukkitTask backpackTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.messageManager = new MessageManager(this);
        this.backpackManager = new BackpackManager(this);
        this.backpackGUI = new BackpackGUI(backpackManager, messageManager);
        this.recipeManager = new BackpackRecipeManager(this, backpackManager);

        // Register Crafting and Smithing Recipes
        this.recipeManager.registerRecipes();

        // Discover recipes for any already-online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.discoverRecipes(this.recipeManager.getRecipeKeys());
        }

        // Register Listeners
        getServer().getPluginManager().registerEvents(new BackpackInteractListener(this, backpackManager, messageManager, backpackGUI), this);
        getServer().getPluginManager().registerEvents(new BackpackInventoryListener(this, backpackManager, messageManager), this);
        getServer().getPluginManager().registerEvents(new BackpackCraftListener(backpackManager), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(recipeManager), this);

        // Register Commands
        PluginCommand cmd = getCommand("cappybackpack");
        if (cmd != null) {
            CappyBackpackCommand executor = new CappyBackpackCommand(this, backpackManager, messageManager);
            cmd.setExecutor(executor);
            cmd.setTabCompleter(executor);
        }

        // Start Periodic Task
        this.backpackTask = new BackpackTask(this, backpackManager).runTaskTimer(this, 20L, 20L);

        getLogger().info("CappyBackpack loaded.");
    }

    @Override
    public void onDisable() {
        if (backpackTask != null) {
            backpackTask.cancel();
        }

        // Safely close and save any open backpacks on reload / shutdown
        for (Player player : Bukkit.getOnlinePlayers()) {
            Inventory open = player.getOpenInventory().getTopInventory();
            if (open.getHolder() instanceof BackpackHolder) {
                player.closeInventory();
            }
        }

        if (backpackManager != null) {
            backpackManager.clearAllViewers();
        }

        getLogger().info("CappyBackpack disabled.");
    }

    public BackpackManager getBackpackManager() {
        return backpackManager;
    }

    public BackpackRecipeManager getRecipeManager() {
        return recipeManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public BackpackGUI getBackpackGUI() {
        return backpackGUI;
    }
}
