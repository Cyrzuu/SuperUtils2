package me.cyrzu.git.superutils2.bridges.map;

import me.cyrzu.git.superutils2.bridges.map.MapBridge.Dynamic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface MapBridge extends Map<String, Object> {

    @NotNull
    MapBridge EMPTY_MAP_BRIDGE = new EmptyMapBridge();

    static MapBridge create() {
        return new Dynamic();
    }

    @NotNull
    MapBridge getMapBridge(@NotNull String path);

    @Nullable
    @Override
    Object get(Object key);

    @Override
    default boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    default boolean containsKey(Object key) {
        Set<String> strings = this.keySet();
        return strings.contains(key.toString());
    }

    @Override
    default boolean containsValue(Object value) {
        throw new UnsupportedOperationException("This operation is not supported on this map.");
    }

    @Nullable
    @Override
    default Object put(String key, Object value) {
        throw new UnsupportedOperationException("This operation is not supported on this map.");
    }

    @Override
    default Object remove(Object key) {
        throw new UnsupportedOperationException("This operation is not supported on this map.");
    }

    @Override
    default void putAll(@NotNull Map<? extends String, ?> m) {
        throw new UnsupportedOperationException("This operation is not supported on this map.");
    }

    @Override
    default void clear() {
        throw new UnsupportedOperationException("This operation is not supported on this map.");
    }

    @NotNull
    @Override
    default Collection<Object> values() {
        throw new UnsupportedOperationException("This operation is not supported on this map.");
    }

    class Dynamic implements MapBridge {

        private final Map<String, Object> map = new LinkedHashMap<>();

        public Dynamic() {
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

            if (next instanceof Dynamic dynamic) {
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

                if (!(next instanceof Dynamic)) {
                    next = new Dynamic();
                    map.put(currentKey, next);
                }

                ((Dynamic) next).putValueByPath(parts, index + 1, value);
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

            if (next instanceof Dynamic dynamic) {
                // Jeśli istnieje mapBridge dla obecnego klucza, przechodzimy dalej
                return dynamic.getOrCreateNestedMap(parts, index + 1);
            } else {
                // Jeśli nie istnieje mapBridge, tworzymy nowy i idziemy dalej
                Dynamic newBridge = new Dynamic();
                map.put(currentKey, newBridge);
                return newBridge.getOrCreateNestedMap(parts, index + 1);
            }
        }

        @Override
        public String toString() {
            return map.toString();
        }
    }

    class EmptyMapBridge implements MapBridge {

        private EmptyMapBridge() {
        }

        @Override
        public @NotNull MapBridge getMapBridge(@NotNull String path) {
            return this;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean containsKey(Object key) {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public Object get(Object key) {
            return null;
        }

        @Override
        public @Nullable Object put(String key, Object value) {
            return null;
        }

        @Override
        public Object remove(Object key) {
            return null;
        }

        @Override
        public void putAll(@NotNull Map<? extends String, ?> m) {

        }

        @Override
        public void clear() {
        }

        @Override
        public @NotNull Set<String> keySet() {
            return Set.of();
        }

        @Override
        public @NotNull Collection<Object> values() {
            return List.of();
        }

        @Override
        public @NotNull Set<Entry<String, Object>> entrySet() {
            return Set.of();
        }

    }

}
