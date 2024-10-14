package me.cyrzu.git.superutils2.config;

import lombok.SneakyThrows;
import me.cyrzu.git.superutils2.color.ColorUtils;
import me.cyrzu.git.superutils2.config.items.ItemFiles;
import me.cyrzu.git.superutils2.config.items.JsonItem;
import me.cyrzu.git.superutils2.item.StackBuilder;
import me.cyrzu.git.superutils2.messages.Message;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class SuperConfig {

    @NotNull
    private final static Pattern ITEM_PATTERN = Pattern.compile("item:.*");

    @NotNull
    private final File file;

    @NotNull
    private FileConfiguration configuration;

    @NotNull
    private final ItemFiles itemFiles;

    @NotNull
    private final Map<String, Object> configData = new ConcurrentHashMap<>();

    public SuperConfig(@NotNull Plugin plugin, @NotNull String resource) {
        resource = resource.endsWith(".yml") ? resource : (resource + ".yml");

        this.file = new File(plugin.getDataFolder(), resource);
        if(!file.exists()) {
            plugin.saveResource(resource, false);
        }

        this.configuration = YamlConfiguration.loadConfiguration(file);
        this.itemFiles = ItemFiles.getInstance(plugin);

        this.reloadConfig();
    }

    public void reload() {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        itemFiles.load();
        this.reloadConfig();
    }

    @NotNull
    public ConfigurationSection getBukkitConfig() {
        return configuration;
    }

    @Nullable
    public String getString(@NotNull String path) {
        return this.getString(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public String getString(@NotNull String path, @Nullable String def) {
        Object val = this.get(path, def);
        return val != null ? val.toString() : null;
    }

    public int getInt(@NotNull String path) {
        return this.getInt(path, 0);
    }

    public int getInt(@NotNull String path, int def) {
        Object val = this.get(path, def);
        return (val instanceof Number number) ? number.intValue() : def;
    }

    public boolean getBoolean(@NotNull String path) {
        return this.getBoolean(path, false);
    }

    public boolean getBoolean(@NotNull String path, boolean def) {
        Object val = this.get(path, def);
        return (val instanceof Boolean) ? (Boolean) val : def;
    }

    public double getDouble(@NotNull String path) {
        return this.getDouble(path, 0D);
    }

    public double getDouble(@NotNull String path, double def) {
        Object val = this.get(path, def);
        return (val instanceof Number number) ? number.doubleValue() : def;
    }

    public long getLong(@NotNull String path) {
        return this.getInt(path, 0);
    }

    public long getLong(@NotNull String path, int def) {
        Object val = this.get(path, def);
        return (val instanceof Number number) ? number.longValue() : def;
    }

    @NotNull
    public Message getMessageOrEmpty(@NotNull String path) {
        return this.getMessage(path, Message.EMPTY_MESSAGE);
    }

    @Nullable
    public Message getMessage(@NotNull String path) {
        return this.getMessage(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Message getMessage(@NotNull String path, @Nullable Message def) {
        Object val = this.get(path + ".message", def);
        if(val instanceof String string) {
            return new Message(string);
        }

        return val instanceof Message message ? message : def;
    }

    @Nullable
    public ItemStack getItemStack(@NotNull String path) {
        return this.getItemStack(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public ItemStack getItemStack(@NotNull String path, @Nullable ItemStack def) {
        Object val = this.get(path + ".itemstack", def);
        return val instanceof ItemStack itemStack ? itemStack : def;
    }

    @Nullable
    public StackBuilder getStackBuilder(@NotNull String path) {
        return this.getStackBuilder(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public StackBuilder getStackBuilder(@NotNull String path, @Nullable StackBuilder def) {
        Object val = this.get(path + ".stackbuilder", def);
        return val instanceof StackBuilder itemStack ? itemStack : def;
    }

    @Nullable
    public List<?> getList(@NotNull String path) {
        return configuration.getList(path);
    }

    @Nullable
    public List<?> getStringList(@NotNull String path) {
        return configuration.getStringList(path);
    }

    @NotNull
    public List<String> getKeys(@NotNull String path) {
        return this.getKeys(path, false);
    }

    @NotNull
    public List<String> getKeys(@NotNull String path, boolean deep) {
        ConfigurationSection section = configuration.getConfigurationSection(path);
        return section == null ? Collections.emptyList() : List.copyOf(section.getKeys(deep));
    }

    @NotNull
    public Map<String, @NotNull ConfigurationSection> getSections(@NotNull String path) {
        Map<String, @NotNull ConfigurationSection> map = new LinkedHashMap<>();

        for (String key : this.getKeys(path)) {
            ConfigurationSection section = configuration.getConfigurationSection(path + "." + key);
            if(section != null) {
                map.put(key, section);
            }
        }

        return map;
    }

    @Nullable
    public Object get(@NotNull String path) {
        return this.get(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Object get(@NotNull String path, @Nullable Object def) {
        Object object = configData.get(path);
        return object != null ? object : def;
    }

    private void reloadConfig() {
        configData.clear();
        this.loadSection(this.configuration, "");
    }

    private void loadSection(@NotNull ConfigurationSection section, @NotNull String path) {
        for (String key : section.getKeys(false)) {
            String newPath = path.isEmpty() ? key : path + "." + key;
            Object object = section.get(key);
            if(object == null) {
                continue;
            }

            if(object instanceof ConfigurationSection nextSection) {
                this.loadSection(nextSection, newPath);
                continue;
            }

            this.putData(newPath, object);
        }
    }

    private void putData(@NotNull String key, @NotNull Object object) {
        if(!(object instanceof String string)) {
            configData.put(key, object);
            return;
        }

        configData.put(key + ".message", string.isEmpty() ? Message.EMPTY_MESSAGE : new Message(string));

        JsonItem jsonItem = ITEM_PATTERN.matcher(string).matches() ? itemFiles.getJsonItem(string.split(":")[1]) : null;
        if (jsonItem != null) {
            configData.put(key + ".stackbuilder", jsonItem.getStackBuilder());
            configData.put(key + ".itemstack", jsonItem.getItemStack());
        } else {
            StackBuilder stackBuilder = StackBuilder.parseString(string);
            configData.put(key + ".stackbuilder", stackBuilder);
            configData.put(key + ".itemstack", stackBuilder.build());
        }

        configData.put(key, ColorUtils.parseText(string));
    }

    public void load(@NotNull Class<?> clazz) {
        this.load(clazz, null);
    }

    public void load(@NotNull Object object) {
        this.load(object.getClass(), object);
    }

    @SneakyThrows
    public void load(@NotNull Class<?> aClass, @Nullable Object object) {
        for (Field declaredField : aClass.getDeclaredFields()) {
            declaredField.setAccessible(true);

            if(!declaredField.isAnnotationPresent(ConfigEntry.class) || Modifier.isFinal(declaredField.getModifiers())) {
                continue;
            }

            ConfigEntry annotation = declaredField.getAnnotation(ConfigEntry.class);
            String path = annotation.value();

            if (declaredField.getType().equals(String.class)) {
                declaredField.set(object, configuration.getString(path, (String) declaredField.get(object)));
            }

            else if (declaredField.getType().equals(Integer.class)) {
                declaredField.set(object, configuration.getInt(path, declaredField.getInt(object)));
            }

            else if(declaredField.getType().equals(Boolean.class)) {
                declaredField.set(object, configuration.getBoolean(path, declaredField.getBoolean(object)));
            }

            else if(declaredField.getType().equals(Double.class)) {
                declaredField.set(object, configuration.getDouble(path, declaredField.getDouble(object)));
            }

            else if(declaredField.getType().equals(Long.class)) {
                declaredField.set(object, configuration.getLong(path, declaredField.getLong(object)));
            }

            else if(declaredField.getType().equals(ItemStack.class) || declaredField.getType().equals(StackBuilder.class)) {
                String string = configuration.getString(path, "").trim();
                if(string.isBlank()) {
                    declaredField.set(object, declaredField.get(object));
                    continue;
                }

                if(ITEM_PATTERN.matcher(string).matches()) {
                    String id = string.split(":")[1];
                    JsonItem jsonItem = id != null? itemFiles.getJsonItem(id) : null;
                    if(jsonItem != null) {
                        declaredField.set(object, declaredField.getType().equals(ItemStack.class) ? jsonItem.getItemStack() : jsonItem.getStackBuilder());
                        continue;
                    }
                }

                StackBuilder stackBuilder = StackBuilder.parseString(string);
                declaredField.set(object, declaredField.getType().equals(ItemStack.class) ? stackBuilder.build() : stackBuilder);
            }

            else if(declaredField.get(object) instanceof Configurable) {
                Class<?> clazz = declaredField.getType();
                if(!this.constructorExist(clazz)) {
                    continue;
                }

                Object instance = this.newInstance(clazz.getConstructor(Object.class), configuration.get(path));
                declaredField.set(object, instance != null ? instance : declaredField.get(object));
            }

            else if(declaredField.get(object) instanceof List<?> && String.class.equals(this.getGenericType(declaredField))) {
                declaredField.set(object, List.copyOf(configuration.getStringList(path)));
            }

            else if(declaredField.get(object) instanceof Set<?> && String.class.equals(this.getGenericType(declaredField))) {
                declaredField.set(object, Set.copyOf(configuration.getStringList(path)));
            }

            declaredField.setAccessible(false);
        }
    }

    @SneakyThrows
    private boolean constructorExist(@NotNull Class<?> clazz) {
        try {
            clazz.getConstructor(Object.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Nullable
    private Object newInstance(@NotNull Constructor<?> constructor, @Nullable Object object) {
        try {
            return constructor.newInstance(object);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            return null;
        }
    }

    @Nullable
    private Class<?> getGenericType(Field field) {
        if(field.getGenericType() instanceof ParameterizedType parameterizedType) {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

            try {
                return actualTypeArguments.length > 0 ? Class.forName(actualTypeArguments[0].getTypeName()) : null;
            } catch (ClassNotFoundException ignored) { }
        }

        return null;
    }

}
