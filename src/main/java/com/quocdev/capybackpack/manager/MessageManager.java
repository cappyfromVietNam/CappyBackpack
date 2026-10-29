package com.quocdev.capybackpack.manager;

import com.quocdev.capybackpack.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MessageManager {

    private final JavaPlugin plugin;
    private File file;
    private FileConfiguration config;

    public MessageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        this.file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);

        // Copy defaults if missing
        InputStream defStream = plugin.getResource("messages.yml");
        if (defStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defStream, StandardCharsets.UTF_8));
            config.setDefaults(defConfig);
        }
    }

    public void reload() {
        load();
    }

    public String getRaw(String key) {
        String msg = config.getString("messages." + key);
        if (msg == null) {
            msg = config.getString(key, "<red>Missing message: " + key);
        }
        return msg;
    }

    public Component get(String key, String... replacements) {
        String raw = getRaw(key);
        if (replacements != null && replacements.length > 0) {
            for (int i = 0; i < replacements.length - 1; i += 2) {
                String target = replacements[i];
                String val = replacements[i + 1];
                if (target != null && val != null) {
                    raw = raw.replace(target, val);
                }
            }
        }
        return TextUtil.parse(raw);
    }

    public void send(CommandSender sender, String key, String... replacements) {
        if (sender != null) {
            sender.sendMessage(get(key, replacements));
        }
    }
}
