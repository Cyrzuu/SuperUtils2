package me.cyrzu.git.superutils2.bridges.map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class DynamicMapBridge implements MapBridge {

    private final Map<String, Object> map = new LinkedHashMap<>();

    public DynamicMapBridge() {
    }

    @Override
    public int size() {
        return map.size();
    }

    @Nullable
    @Override
    public Object get(Object key) {
        if (key instanceof String) {
            String[] parts = ((String) key).split("\\.");
            return getValueByPath(parts, 0);
        }
        return null;
    }

    private Object getValueByPath(String[] parts, int index) {
        if (index >= parts.length) {
            return null;
        }

        String currentKey = parts[index];
        Object next = map.get(currentKey);

        if (index == parts.length - 1) {
            return next;
        }

        if (next instanceof DynamicMapBridge dynamic) {
            return dynamic.getValueByPath(parts, index + 1);
        }

        return null;
    }

    @Override
    public Object put(String key, Object value) {
        if (key != null) {
            String[] parts = key.split("\\.");
            putValueByPath(parts, 0, value);
        }
        return value;
    }

    private void putValueByPath(String[] parts, int index, Object value) {
        if (index >= parts.length) {
            return;
        }

        String currentKey = parts[index];
        if (index == parts.length - 1) {
            map.put(currentKey, value);
        } else {
            Object next = map.get(currentKey);

            if (!(next instanceof DynamicMapBridge)) {
                next = new DynamicMapBridge();
                map.put(currentKey, next);
            }

            ((DynamicMapBridge) next).putValueByPath(parts, index + 1, value);
        }
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @NotNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    @NotNull
    @Override
    public MapBridge getMapBridge(@NotNull String path) {
        String[] parts = path.split("\\.");
        return this.getOrCreateNestedMap(parts, 0);
    }

    private MapBridge getOrCreateNestedMap(String[] parts, int index) {
        if (index >= parts.length) {
            return this;
        }

        String currentKey = parts[index];
        Object next = map.get(currentKey);

        if (next instanceof DynamicMapBridge dynamic) {
            // Jeśli istnieje mapBridge dla obecnego klucza, przechodzimy dalej
            return dynamic.getOrCreateNestedMap(parts, index + 1);
        } else {
            // Jeśli nie istnieje mapBridge, tworzymy nowy i idziemy dalej
            DynamicMapBridge newBridge = new DynamicMapBridge();
            map.put(currentKey, newBridge);
            return newBridge.getOrCreateNestedMap(parts, index + 1);
        }
    }

    @Override
    public String toString() {
        return map.toString();
    }
}