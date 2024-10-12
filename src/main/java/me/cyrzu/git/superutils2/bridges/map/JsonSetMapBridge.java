package me.cyrzu.git.superutils2.bridges.map;

import me.cyrzu.git.superutils2.json.JsonReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class JsonSetMapBridge implements MapBridge {

    private final JsonReader reader;

    private JsonSetMapBridge(@NotNull JsonReader jsonReader) {
        this.reader = jsonReader;
    }

    @Override
    public int size() {
        return this.keySet().size();
    }

    @Override
    public Object get(Object key) {
        return this.get(key.toString());
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        return reader.keySet();
    }

    @Override
    public @NotNull MapBridge getMapBridge(@NotNull String path) {
        JsonReader pathReader = reader.getReader(path);
        return pathReader != null ? new JsonSetMapBridge(pathReader) : MapBridge.EMPTY_MAP_BRIDGE;
    }

    private <T> T get(String key) {
        // noinspection all
        return (T) reader.getObject(key);
    }

    @NotNull
    public static MapBridge of(@NotNull String json) {
        return JsonSetMapBridge.of(JsonReader.parseString(json));
    }

    @NotNull
    public static MapBridge of(@Nullable JsonReader jsonReader) {
        return jsonReader == null ? MapBridge.EMPTY_MAP_BRIDGE : new JsonSetMapBridge(jsonReader);
    }

}
