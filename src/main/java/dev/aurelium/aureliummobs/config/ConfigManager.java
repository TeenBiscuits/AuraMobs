package dev.aurelium.aureliummobs.config;

import dev.aurelium.aureliummobs.AureliumMobs;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigManager {

    private final AureliumMobs plugin;
    private final Map<OptionKey, OptionValue> options;

    public ConfigManager(AureliumMobs plugin) {
        this.plugin = plugin;
        this.options = new HashMap<>();
    }

    public void loadConfig() {
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveDefaultConfig();
        updateConfig(); // Add new options in embedded config to config on file
        loadDefaultOptions(); // Load embedded options
        loadOptions(plugin.getConfig()); // Load options on file
    }

    public OptionValue getOption(OptionKey key) {
        return Objects.requireNonNull(options.get(key), "Option value for key " + key.getKey() + " was not found!");
    }

    private void updateConfig() {
        try {
            InputStream is = plugin.getResource("config.yml");
            if (is != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(is));
                ConfigurationSection config = defConfig.getConfigurationSection("");
                if (config != null) {
                    for (String key : config.getKeys(true)) {
                        if (config.isConfigurationSection(key)) continue;
                        if (!plugin.getConfig().contains(key)) {
                            plugin.getConfig().set(key, defConfig.get(key));
                        }
                    }
                }
                plugin.saveConfig();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadOptions(FileConfiguration config) {
        for (String path : config.getKeys(true)) {
            if (config.isConfigurationSection(path)) continue;

            OptionKey key = new OptionKey(path);
            OptionValue value = new OptionValue(config.get(path));

            options.put(key, value);
        }
    }

    private void loadDefaultOptions() {
        InputStream inputStream = plugin.getResource("config.yml");
        if (inputStream != null) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
            loadOptions(config);
        }
    }
}