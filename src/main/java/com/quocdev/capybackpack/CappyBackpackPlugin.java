package com.quocdev.capybackpack;

import com.quocdev.capybackpack.command.CappyBackpackCommand;
import com.quocdev.capybackpack.gui.BackpackGUI;
import com.quocdev.capybackpack.gui.BackpackHolder;
import com.quocdev.capybackpack.listener.BackpackInteractListener;
import com.quocdev.capybackpack.listener.BackpackInventoryListener;
import com.quocdev.capybackpack.manager.BackpackManager;
import com.quocdev.capybackpack.task.BackpackTask;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class CappyBackpackPlugin extends JavaPlugin {

    private BackpackManager backpackManager;
    private BackpackGUI backpackGUI;
    private BukkitTask backpackTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.backpackManager = new BackpackManager(this);
        this.backpackGUI = new BackpackGUI(backpackManager);

        // Register Listeners
        getServer().getPluginManager().registerEvents(new BackpackInteractListener(this, backpackManager, backpackGUI), this);
        getServer().getPluginManager().registerEvents(new BackpackInventoryListener(this, backpackManager), this);

        // Register Commands
        PluginCommand cmd = getCommand("cappybackpack");
        if (cmd != null) {
            CappyBackpackCommand executor = new CappyBackpackCommand(this, backpackManager);
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

    public BackpackGUI getBackpackGUI() {
        return backpackGUI;
    }
}
