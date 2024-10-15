package me.cyrzu.git.superutils2.bridges.map;

import me.cyrzu.git.superutils2.bridges.map.DynamicMapBridge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface MapBridge extends Map<String, Object> {

    @NotNull
    MapBridge EMPTY_MAP_BRIDGE = new EmptyMapBridge();

    static MapBridge create() {
        return new DynamicMapBridge();
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
