package com.quocdev.capybackpack.command;

import com.quocdev.capybackpack.manager.BackpackManager;
import com.quocdev.capybackpack.manager.MessageManager;
import com.quocdev.capybackpack.model.BackpackModule;
import com.quocdev.capybackpack.model.BackpackTier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CappyBackpackCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final BackpackManager manager;
    private final MessageManager messageManager;

    public CappyBackpackCommand(JavaPlugin plugin, BackpackManager manager, MessageManager messageManager) {
        this.plugin = plugin;
        this.manager = manager;
        this.messageManager = messageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "reload":
                if (!sender.hasPermission("cappybackpack.admin")) {
                    messageManager.send(sender, "no_permission");
                    return true;
                }
                plugin.reloadConfig();
                messageManager.reload();
                messageManager.send(sender, "config_reloaded");
                return true;

            case "get":
                if (!sender.hasPermission("cappybackpack.admin")) {
                    messageManager.send(sender, "no_permission");
                    return true;
                }
                if (!(sender instanceof Player player)) {
                    messageManager.send(sender, "players_only");
                    return true;
                }
                BackpackTier tier = BackpackTier.BASE;
                if (args.length > 1) {
                    tier = BackpackTier.fromString(args[1]);
                }
                ItemStack backpack = manager.createBackpack(tier);
                giveOrDrop(player, backpack);
                messageManager.send(player, "given_backpack", "%tier%", tier.getId(), "<color>", "<" + tier.getColorHex() + ">");
                return true;

            case "upgrade":
                if (!sender.hasPermission("cappybackpack.admin")) {
                    messageManager.send(sender, "no_permission");
                    return true;
                }
                if (!(sender instanceof Player player)) {
                    messageManager.send(sender, "players_only");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(messageManager.get("help.upgrade"));
                    return true;
                }
                BackpackTier targetTier = BackpackTier.fromString(args[1]);
                if (targetTier == BackpackTier.BASE) {
                    messageManager.send(player, "base_has_no_upgrade");
                    return true;
                }
                ItemStack upgrade = manager.createUpgrade(targetTier);
                giveOrDrop(player, upgrade);
                messageManager.send(player, "given_upgrade", "%tier%", targetTier.getId(), "<color>", "<" + targetTier.getColorHex() + ">");
                return true;

            case "module":
                if (!sender.hasPermission("cappybackpack.admin")) {
                    messageManager.send(sender, "no_permission");
                    return true;
                }
                if (!(sender instanceof Player player)) {
                    messageManager.send(sender, "players_only");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(messageManager.get("help.module"));
                    return true;
                }
                BackpackModule mod = BackpackModule.fromString(args[1]);
                if (mod == null) {
                    messageManager.send(player, "unknown_module");
                    return true;
                }
                ItemStack moduleItem = manager.createModule(mod);
                giveOrDrop(player, moduleItem);
                messageManager.send(player, "given_module", "%module%", mod.getId(), "<color>", "<" + mod.getColorHex() + ">");
                return true;

            default:
                sendHelp(sender);
                return true;
        }
    }

    private void giveOrDrop(Player player, ItemStack item) {
        for (ItemStack leftover : player.getInventory().addItem(item).values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(messageManager.get("help.header"));
        sender.sendMessage(messageManager.get("help.get"));
        sender.sendMessage(messageManager.get("help.upgrade"));
        sender.sendMessage(messageManager.get("help.module"));
        sender.sendMessage(messageManager.get("help.reload"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("cappybackpack.admin")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return filter(Arrays.asList("get", "upgrade", "module", "reload"), args[0]);
        }

        if (args.length == 2) {
            if ("get".equalsIgnoreCase(args[0])) {
                return filter(Arrays.stream(BackpackTier.values()).map(BackpackTier::getId).collect(Collectors.toList()), args[1]);
            }
            if ("upgrade".equalsIgnoreCase(args[0])) {
                return filter(Arrays.stream(BackpackTier.values())
                        .filter(t -> t != BackpackTier.BASE)
                        .map(BackpackTier::getId)
                        .collect(Collectors.toList()), args[1]);
            }
            if ("module".equalsIgnoreCase(args[0])) {
                return filter(Arrays.stream(BackpackModule.values()).map(BackpackModule::getId).collect(Collectors.toList()), args[1]);
            }
        }

        return Collections.emptyList();
    }

    private List<String> filter(List<String> list, String input) {
        String lower = input.toLowerCase();
        List<String> matches = new ArrayList<>();
        for (String s : list) {
            if (s.toLowerCase().startsWith(lower)) {
                matches.add(s);
            }
        }
        return matches;
    }
}
