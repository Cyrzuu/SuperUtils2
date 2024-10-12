package me.cyrzu.git.superutils2.bridges.map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

 class EmptyMapBridge implements MapBridge {

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
