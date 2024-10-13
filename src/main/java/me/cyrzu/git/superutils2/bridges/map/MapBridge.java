package me.cyrzu.git.superutils2.bridges.map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface MapBridge extends Map<String, Object> {

    @NotNull
    MapBridge EMPTY_MAP_BRIDGE = new EmptyMapBridge();

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

}
