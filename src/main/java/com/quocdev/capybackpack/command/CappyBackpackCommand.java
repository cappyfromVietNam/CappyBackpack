package com.quocdev.capybackpack.command;

import com.quocdev.capybackpack.gui.RecipeEntry;
import com.quocdev.capybackpack.gui.RecipeGUI;
import com.quocdev.capybackpack.manager.BackpackManager;
import com.quocdev.capybackpack.manager.MessageManager;
import com.quocdev.capybackpack.model.BackpackModule;
import com.quocdev.capybackpack.model.BackpackTier;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

public class CappyBackpackCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final BackpackManager manager;
    private final MessageManager messageManager;
    private final RecipeGUI recipeGUI;

    public CappyBackpackCommand(JavaPlugin plugin, BackpackManager manager, MessageManager messageManager, RecipeGUI recipeGUI) {
        this.plugin = plugin;
        this.manager = manager;
        this.messageManager = messageManager;
        this.recipeGUI = recipeGUI;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "recipe":
                if (!sender.hasPermission("cappybackpack.use")) {
                    messageManager.send(sender, "no_permission");
                    return true;
                }
                if (!(sender instanceof Player player)) {
                    messageManager.send(sender, "players_only");
                    return true;
                }
                if (args.length > 1) {
                    RecipeEntry entry = recipeGUI.getRecipeMap().get(args[1].toLowerCase());
                    if (entry != null) {
                        recipeGUI.openRecipe(player, entry);
                        return true;
                    }
                }
                recipeGUI.openCatalog(player);
                return true;

            case "reload":
                if (!sender.hasPermission("cappybackpack.admin")) {
                    messageManager.send(sender, "no_permission");
                    return true;
                }
                plugin.reloadConfig();
                messageManager.reload();
                messageManager.send(sender, "config_reloaded");
                return true;

            case "give":
                if (!sender.hasPermission("cappybackpack.admin")) {
                    messageManager.send(sender, "no_permission");
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(messageManager.get("help.give"));
                    return true;
                }
                Player targetPlayer = Bukkit.getPlayerExact(args[1]);
                if (targetPlayer == null || !targetPlayer.isOnline()) {
                    messageManager.send(sender, "player_not_found");
                    return true;
                }
                ItemStack giveItem = parseItem(sender, args, 2);
                if (giveItem == null) {
                    return true;
                }
                giveOrDrop(targetPlayer, giveItem);
                messageManager.send(sender, "given_to_player", "%player%", targetPlayer.getName(), "%amount%", String.valueOf(giveItem.getAmount()), "%item%", args[2]);
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
                if (args.length < 2) {
                    // Default /cb get gives base backpack
                    ItemStack defaultBp = manager.createBackpack(BackpackTier.BASE, 1);
                    giveOrDrop(player, defaultBp);
                    messageManager.send(player, "given_backpack", "%tier%", BackpackTier.BASE.getId(), "<color>", "<" + BackpackTier.BASE.getColorHex() + ">");
                    return true;
                }
                ItemStack getItem = parseItem(sender, args, 1);
                if (getItem == null) {
                    return true;
                }
                giveOrDrop(player, getItem);
                messageManager.send(player, "given_backpack", "%tier%", args[1], "<color>", "<#A5D6A7>");
                return true;

            // Direct Shortcuts for convenience
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
                    sender.sendMessage(messageManager.get("help.get"));
                    return true;
                }
                BackpackTier upgradeTier = BackpackTier.fromString(args[1]);
                if (upgradeTier == BackpackTier.BASE) {
                    messageManager.send(player, "base_has_no_upgrade");
                    return true;
                }
                int upQty = (args.length > 2 && isInteger(args[2])) ? Integer.parseInt(args[2]) : 1;
                ItemStack upgrade = manager.createUpgrade(upgradeTier, upQty);
                giveOrDrop(player, upgrade);
                messageManager.send(player, "given_upgrade", "%tier%", upgradeTier.getId(), "<color>", "<" + upgradeTier.getColorHex() + ">");
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
                    sender.sendMessage(messageManager.get("help.get"));
                    return true;
                }
                BackpackModule mod = BackpackModule.fromString(args[1]);
                if (mod == null) {
                    messageManager.send(player, "unknown_module");
                    return true;
                }
                int modQty = (args.length > 2 && isInteger(args[2])) ? Integer.parseInt(args[2]) : 1;
                ItemStack moduleItem = manager.createModule(mod, modQty);
                giveOrDrop(player, moduleItem);
                messageManager.send(player, "given_module", "%module%", mod.getId(), "<color>", "<" + mod.getColorHex() + ">");
                return true;

            case "remover":
                if (!sender.hasPermission("cappybackpack.admin")) {
                    messageManager.send(sender, "no_permission");
                    return true;
                }
                if (!(sender instanceof Player player)) {
                    messageManager.send(sender, "players_only");
                    return true;
                }
                int remQty = (args.length > 1 && isInteger(args[1])) ? Integer.parseInt(args[1]) : 1;
                ItemStack remover = manager.createRemover(remQty);
                giveOrDrop(player, remover);
                messageManager.send(player, "given_remover");
                return true;

            default:
                sendHelp(sender);
                return true;
        }
    }

    private ItemStack parseItem(CommandSender sender, String[] args, int typeIndex) {
        String type = args[typeIndex].toLowerCase();

        switch (type) {
            case "backpack": {
                BackpackTier tier = BackpackTier.BASE;
                int amount = 1;
                if (args.length > typeIndex + 1) {
                    String next = args[typeIndex + 1];
                    if (isInteger(next)) {
                        amount = Math.max(1, Integer.parseInt(next));
                    } else {
                        tier = BackpackTier.fromString(next);
                        if (args.length > typeIndex + 2 && isInteger(args[typeIndex + 2])) {
                            amount = Math.max(1, Integer.parseInt(args[typeIndex + 2]));
                        }
                    }
                }
                return manager.createBackpack(tier, amount);
            }
            case "upgrade": {
                if (args.length <= typeIndex + 1) {
                    sender.sendMessage(messageManager.get("help.get"));
                    return null;
                }
                BackpackTier targetTier = BackpackTier.fromString(args[typeIndex + 1]);
                if (targetTier == BackpackTier.BASE) {
                    messageManager.send(sender, "base_has_no_upgrade");
                    return null;
                }
                int amount = 1;
                if (args.length > typeIndex + 2 && isInteger(args[typeIndex + 2])) {
                    amount = Math.max(1, Integer.parseInt(args[typeIndex + 2]));
                }
                return manager.createUpgrade(targetTier, amount);
            }
            case "module": {
                if (args.length <= typeIndex + 1) {
                    sender.sendMessage(messageManager.get("help.get"));
                    return null;
                }
                BackpackModule mod = BackpackModule.fromString(args[typeIndex + 1]);
                if (mod == null) {
                    messageManager.send(sender, "unknown_module");
                    return null;
                }
                int amount = 1;
                if (args.length > typeIndex + 2 && isInteger(args[typeIndex + 2])) {
                    amount = Math.max(1, Integer.parseInt(args[typeIndex + 2]));
                }
                return manager.createModule(mod, amount);
            }
            case "remover": {
                int amount = 1;
                if (args.length > typeIndex + 1 && isInteger(args[typeIndex + 1])) {
                    amount = Math.max(1, Integer.parseInt(args[typeIndex + 1]));
                }
                return manager.createRemover(amount);
            }
            default:
                messageManager.send(sender, "unknown_item_type", "%type%", type);
                return null;
        }
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void giveOrDrop(Player player, ItemStack item) {
        for (ItemStack leftover : player.getInventory().addItem(item).values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(messageManager.get("help.header"));
        sender.sendMessage(messageManager.get("help.recipe"));
        if (sender.hasPermission("cappybackpack.admin")) {
            sender.sendMessage(messageManager.get("help.give"));
            sender.sendMessage(messageManager.get("help.get"));
            sender.sendMessage(messageManager.get("help.reload"));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("cappybackpack.use")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            List<String> subs = new ArrayList<>(Collections.singletonList("recipe"));
            if (sender.hasPermission("cappybackpack.admin")) {
                subs.addAll(Arrays.asList("give", "get", "upgrade", "module", "remover", "reload"));
            }
            return filter(subs, args[0]);
        }

        // /cb recipe <entry>
        if (args.length == 2 && "recipe".equalsIgnoreCase(args[0])) {
            return filter(new ArrayList<>(recipeGUI.getRecipeMap().keySet()), args[1]);
        }

        if (!sender.hasPermission("cappybackpack.admin")) {
            return Collections.emptyList();
        }

        // /cb give <player> ...
        if ("give".equalsIgnoreCase(args[0])) {
            if (args.length == 2) {
                return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
            }
            if (args.length == 3) {
                return filter(Arrays.asList("backpack", "upgrade", "module", "remover"), args[2]);
            }
            if (args.length == 4) {
                return completeItemArgs(args[2], args[3]);
            }
            if (args.length == 5) {
                return Arrays.asList("1", "16", "64");
            }
        }

        // /cb get <type> ...
        if ("get".equalsIgnoreCase(args[0])) {
            if (args.length == 2) {
                return filter(Arrays.asList("backpack", "upgrade", "module", "remover"), args[1]);
            }
            if (args.length == 3) {
                return completeItemArgs(args[1], args[2]);
            }
            if (args.length == 4) {
                return Arrays.asList("1", "16", "64");
            }
        }

        // Direct shortcuts
        if ("upgrade".equalsIgnoreCase(args[0])) {
            if (args.length == 2) {
                return filter(Arrays.stream(BackpackTier.values()).filter(t -> t != BackpackTier.BASE).map(BackpackTier::getId).collect(Collectors.toList()), args[1]);
            }
            if (args.length == 3) {
                return Arrays.asList("1", "16", "64");
            }
        }

        if ("module".equalsIgnoreCase(args[0])) {
            if (args.length == 2) {
                return filter(Arrays.stream(BackpackModule.values()).map(BackpackModule::getId).collect(Collectors.toList()), args[1]);
            }
            if (args.length == 3) {
                return Arrays.asList("1", "16", "64");
            }
        }

        if ("remover".equalsIgnoreCase(args[0]) && args.length == 2) {
            return Arrays.asList("1", "16", "64");
        }

        return Collections.emptyList();
    }

    private List<String> completeItemArgs(String type, String input) {
        if ("backpack".equalsIgnoreCase(type)) {
            return filter(Arrays.stream(BackpackTier.values()).map(BackpackTier::getId).collect(Collectors.toList()), input);
        }
        if ("upgrade".equalsIgnoreCase(type)) {
            return filter(Arrays.stream(BackpackTier.values()).filter(t -> t != BackpackTier.BASE).map(BackpackTier::getId).collect(Collectors.toList()), input);
        }
        if ("module".equalsIgnoreCase(type)) {
            return filter(Arrays.stream(BackpackModule.values()).map(BackpackModule::getId).collect(Collectors.toList()), input);
        }
        if ("remover".equalsIgnoreCase(type)) {
            return Arrays.asList("1", "16", "64");
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
