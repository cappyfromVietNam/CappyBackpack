package com.quocdev.capybackpack.command;

import com.quocdev.capybackpack.manager.BackpackManager;
import com.quocdev.capybackpack.model.BackpackModule;
import com.quocdev.capybackpack.model.BackpackTier;
import com.quocdev.capybackpack.util.TextUtil;
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

    public CappyBackpackCommand(JavaPlugin plugin, BackpackManager manager) {
        this.plugin = plugin;
        this.manager = manager;
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
                    sender.sendMessage(TextUtil.parse("<#E57373>ɴᴏ ᴘᴇʀᴍɪssɪᴏɴ"));
                    return true;
                }
                plugin.reloadConfig();
                sender.sendMessage(TextUtil.parse("<#A5D6A7>ᴄᴏɴғɪɢ ʀᴇʟᴏᴀᴅᴇᴅ"));
                return true;

            case "get":
                if (!sender.hasPermission("cappybackpack.admin")) {
                    sender.sendMessage(TextUtil.parse("<#E57373>ɴᴏ ᴘᴇʀᴍɪssɪᴏɴ"));
                    return true;
                }
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(TextUtil.parse("<#E57373>ᴘʟᴀʏᴇʀs ᴏɴʟʏ"));
                    return true;
                }
                BackpackTier tier = BackpackTier.BASE;
                if (args.length > 1) {
                    tier = BackpackTier.fromString(args[1]);
                }
                ItemStack backpack = manager.createBackpack(tier);
                giveOrDrop(player, backpack);
                player.sendMessage(TextUtil.parse("<#A5D6A7>ɢɪᴠᴇɴ ʙᴀᴄᴋᴘᴀᴄᴋ  <" + tier.getColorHex() + ">· " + tier.getId()));
                return true;

            case "upgrade":
                if (!sender.hasPermission("cappybackpack.admin")) {
                    sender.sendMessage(TextUtil.parse("<#E57373>ɴᴏ ᴘᴇʀᴍɪssɪᴏɴ"));
                    return true;
                }
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(TextUtil.parse("<#E57373>ᴘʟᴀʏᴇʀs ᴏɴʟʏ"));
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(TextUtil.parse("<#E57373>ᴜsᴀɢᴇ: /ᴄʙ ᴜᴘɢʀᴀᴅᴇ <ɪʀᴏɴ|ɢᴏʟᴅ|ᴅɪᴀᴍᴏɴᴅ|ɴᴇᴛʜᴇʀɪᴛᴇ>"));
                    return true;
                }
                BackpackTier targetTier = BackpackTier.fromString(args[1]);
                if (targetTier == BackpackTier.BASE) {
                    player.sendMessage(TextUtil.parse("<#E57373>ʙᴀsᴇ ʜᴀs ɴᴏ ᴜᴘɢʀᴀᴅᴇ ɪᴛᴇᴍ"));
                    return true;
                }
                ItemStack upgrade = manager.createUpgrade(targetTier);
                giveOrDrop(player, upgrade);
                player.sendMessage(TextUtil.parse("<#A5D6A7>ɢɪᴠᴇɴ ᴜᴘɢʀᴀᴅᴇ  <" + targetTier.getColorHex() + ">· " + targetTier.getId()));
                return true;

            case "module":
                if (!sender.hasPermission("cappybackpack.admin")) {
                    sender.sendMessage(TextUtil.parse("<#E57373>ɴᴏ ᴘᴇʀᴍɪssɪᴏɴ"));
                    return true;
                }
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(TextUtil.parse("<#E57373>ᴘʟᴀʏᴇʀs ᴏɴʟʏ"));
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(TextUtil.parse("<#E57373>ᴜsᴀɢᴇ: /ᴄʙ ᴍᴏᴅᴜʟᴇ <ғᴜʀɴᴀᴄᴇ|ᴀᴜᴛᴏғᴏᴏᴅ>"));
                    return true;
                }
                BackpackModule mod = BackpackModule.fromString(args[1]);
                if (mod == null) {
                    player.sendMessage(TextUtil.parse("<#E57373>ᴜɴᴋɴᴏᴡɴ ᴍᴏᴅᴜʟᴇ"));
                    return true;
                }
                ItemStack moduleItem = manager.createModule(mod);
                giveOrDrop(player, moduleItem);
                player.sendMessage(TextUtil.parse("<#A5D6A7>ɢɪᴠᴇɴ ᴍᴏᴅᴜʟᴇ  <" + mod.getColorHex() + ">· " + mod.getId()));
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
        sender.sendMessage(TextUtil.parse("<#9E9E9E>— <#ECEFF1>ᴄᴀᴘᴘʏ ʙᴀᴄᴋᴘᴀᴄᴋ <#9E9E9E>—"));
        sender.sendMessage(TextUtil.parse("<#B0BEC5>/ᴄʙ ɢᴇᴛ [ᴛɪᴇʀ] <#757575>· ɢɪᴠᴇ ʙᴀᴄᴋᴘᴀᴄᴋ"));
        sender.sendMessage(TextUtil.parse("<#B0BEC5>/ᴄʙ ᴜᴘɢʀᴀᴅᴇ <ᴛɪᴇʀ> <#757575>· ɢɪᴠᴇ ᴜᴘɢʀᴀᴅᴇ ɪᴛᴇᴍ"));
        sender.sendMessage(TextUtil.parse("<#B0BEC5>/ᴄʙ ᴍᴏᴅᴜʟᴇ <ᴛʏᴘᴇ> <#757575>· ɢɪᴠᴇ ᴍᴏᴅᴜʟᴇ ɪᴛᴇᴍ"));
        sender.sendMessage(TextUtil.parse("<#B0BEC5>/ᴄʙ ʀᴇʟᴏᴀᴅ <#757575>· ʀᴇʟᴏᴀᴅ ᴄᴏɴғɪɢ"));
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
