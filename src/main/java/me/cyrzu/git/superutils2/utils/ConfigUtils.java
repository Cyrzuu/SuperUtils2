package me.cyrzu.git.superutils2.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@UtilityClass
public class ConfigUtils {

    @NotNull
    public YamlConfiguration createConfiguration(@NotNull Map<String, ?> map) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        ConfigUtils.putConfig(yamlConfiguration, map);

        return yamlConfiguration;
    }

    public void putConfig(@NotNull ConfigurationSection section, @NotNull Map<String, ?> map) {
        ConfigUtils.putConfig(section, map, null);
    }

    private void putConfig(@NotNull ConfigurationSection section, @NotNull Map<?, ?> map, @Nullable String path) {
        map.forEach((k, v) -> {
            String key = k != null ? k.toString() : "";

            String newPath = path == null ? key : (path + "." + key);
            if(v instanceof Map<?, ?> var0) {
                ConfigUtils.putConfig(section, var0, newPath);
                return;
            }

            section.set(newPath, v);
        });
    }

}
