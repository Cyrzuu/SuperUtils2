package me.cyrzu.git.superutils2.bridges.map;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class ConfigSectionMapBridge<T extends ConfigurationSection> implements MapBridge {

    @Getter
    private final T config;

    @Override
    public String toString() {
        return "ConfigSectionMapBridge" + this.entrySet();
    }

    private ConfigSectionMapBridge(@NotNull T config) {
        this.config = config;
    }

    @Override
    public int size() {
        return this.keySet().size();
    }

    @Override
    public Object get(Object key) {
        return key != null ? config.get(key.toString()) : null;
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        return config.getKeys(false);
    }

    @Override
    public @NotNull MapBridge getMapBridge(@NotNull String path) {
        ConfigurationSection section = config.getConfigurationSection(path);
        return section != null ? new ConfigSectionMapBridge<>(section) : MapBridge.EMPTY_MAP_BRIDGE;
    }

    @Override
    public @NotNull Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> entrySet = new HashSet<>();

        for (String key : this.keySet()) {
            Object rawValue = config.get(key);
            Object convertedValue = this.convertValue(rawValue);
            entrySet.add(new AbstractMap.SimpleEntry<>(key, convertedValue));
        }

        return entrySet;
    }

    private Object convertValue(Object value) {
        if (value instanceof ConfigurationSection section) {
            Map<String, Object> mapValue = new HashMap<>();
            for (String key : section.getKeys(false)) {
                mapValue.put(key, this.convertValue(section.get(key)));
            }

            return mapValue;
        }

        return value;
    }

    private <O> O get(String key) {
        // noinspection all
        return (O) config.get(key);
    }

    @NotNull
    public static <O extends ConfigurationSection> ConfigSectionMapBridge<O> of(@NotNull O config) {
        return new ConfigSectionMapBridge<>(config);
    }

    public static class Yaml extends ConfigSectionMapBridge<YamlConfiguration> {

        public Yaml() {
            super(new YamlConfiguration());
        }

        public void save(@NotNull File file) {
            try {
                this.getConfig().save(file);
            } catch (Exception ignored) {}
        }

    }

}