package me.cyrzu.git.superutils2.config.items;

import me.cyrzu.git.superutils2.item.StackBuilder;
import me.cyrzu.git.superutils2.json.JsonReader;
import me.cyrzu.git.superutils2.other.FileUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemFiles {

    @NotNull
    private static final Map<Class<? extends Plugin>, ItemFiles> INSTANCES = new ConcurrentHashMap<>();

    @NotNull
    public static ItemFiles getInstance(@NotNull Plugin plugin) {
        ItemFiles.INSTANCES.putIfAbsent(plugin.getClass(), new ItemFiles(plugin));
        return ItemFiles.INSTANCES.get(plugin.getClass());
    }

    @NotNull
    private final Plugin plugin;

    @NotNull
    private Map<String, JsonItem> items = Collections.emptyMap();

    private ItemFiles(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.load();
    }

    public void load() {
        File direction = new File(plugin.getDataFolder() + File.separator + "item_configs");
        if(!direction.exists()) {
            File file = new File(direction, "examples.json");
            JsonItem.getExample().saveToFile(file, true);
        }

        File[] files = direction.listFiles(FileUtils.JSON_FILTER);
        if(files == null) {
            return;
        }

        Map<String, JsonItem> items = new ConcurrentHashMap<>();
        for (File file : files) {
            JsonReader fileReader = JsonReader.parseFile(file);
            if(fileReader == null) {
                continue;
            }

            fileReader.getKeysWithReader().forEach((id, itemReader) -> {
                JsonItem jsonItem = new JsonItem(itemReader);
                items.put(id, jsonItem);
            });
        }

        this.items = Map.copyOf(items);
    }

    @Nullable
    public JsonItem getJsonItem(@NotNull String id) {
        return items.get(id);
    }

    @Nullable
    public StackBuilder getStackBuilder(@NotNull String id) {
        JsonItem jsonItem = items.get(id);
        return jsonItem != null ? jsonItem.getStackBuilder() : null;
    }

    @Nullable
    public ItemStack getItemStack(@NotNull String id) {
        JsonItem jsonItem = items.get(id);
        return jsonItem != null ? jsonItem.getItemStack() : null;
    }

}
