package codes.snowy.dupeJS.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Config {
    private final Plugin plugin;
    private File configFile;
    private FileConfiguration config;

    public Config(Plugin plugin) {
        this.plugin = plugin;
        createConfig();
    }

    private void createConfig() {
        configFile = new File(plugin.getDataFolder(), "configuration.yml");
        if (!configFile.exists()) {
            plugin.saveResource("configuration.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public String getString(String key, String defaultValue) {
        return config.getString(key, defaultValue);
    }

    public double getDouble(String key, double defaultValue) {
        return config.getDouble(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return config.getInt(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return config.getBoolean(key, defaultValue);
    }

    public List<String> getStringList(String key) {
        return config.getStringList(key);
    }

    public Object getValue(String key) {
        return config.get(key);
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public boolean hasPermission(CommandSender sender, String perm) {
        if (sender.hasPermission(perm)) {
            return true;
        } else {
            return false;
        }
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException ex) {
            plugin.getLogger().severe("Could not save configuration.yml!");
            ex.printStackTrace();
        }
    }

    public boolean containsKeys(String... keys) {
        for (String key : keys) {
            if (!config.contains(key)) {
                return false;
            }
        }
        return true;
    }
}