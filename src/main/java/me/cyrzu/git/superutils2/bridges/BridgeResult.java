package me.cyrzu.git.superutils2.bridges;

import me.cyrzu.git.superutils2.bridges.map.MapBridge;
import me.cyrzu.git.superutils2.utils.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.*;

public class BridgeResult {

    @NotNull
    private final MapBridge mapBridge;

    public BridgeResult(@NotNull MapBridge mapBridge) {
        this.mapBridge = mapBridge;
    }

    public boolean isEmpty() {
        return mapBridge.isEmpty();
    }

    public Set<String> keySet() {
        return mapBridge.keySet();
    }

    public Optional<String> getString(@NotNull String key) {
        return this.getObject(key, String.class);
    }

    public Optional<Integer> getInt(@NotNull String key) {
        Object value = this.getObject(key);
        return value instanceof Number number ? Optional.of(number.intValue()) : Optional.empty();
    }

    public Optional<Long> getLong(@NotNull String key) {
        Object value = this.getObject(key);
        return value instanceof Number number ? Optional.of(number.longValue()) : Optional.empty();
    }

    public Optional<Double> getDouble(@NotNull String key) {
        Object value = this.getObject(key);
        return value instanceof Number number ? Optional.of(number.doubleValue()) : Optional.empty();
    }

    public Optional<Boolean> getBoolean(@NotNull String key) {
        Object value = this.getObject(key);
        if(value instanceof Number number) {
            return Optional.of(number.intValue() == 1);
        }

        return value instanceof Boolean bool ? Optional.of(bool) : Optional.empty();
    }

    public Optional<Byte> getByte(@NotNull String key) {
        Object value = this.getObject(key);
        if(value instanceof Number number) {
            return Optional.of(number.byteValue());
        }

        return value instanceof Boolean bool ? Optional.of(bool ? (byte) 1 : 0) : Optional.empty();
    }

    public Optional<BigDecimal> getBigDecimal(@NotNull String key) {
        Optional<String> value = this.getString(key);
        try {
            return value.map(BigDecimal::new);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public Optional<UUID> getUUID(@NotNull String key) {
        Optional<String> value = this.getString(key);
        try {
            return value.map(UUID::fromString);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public Optional<byte[]> getBlob(@NotNull String key) {
        return this.getObject(key, byte[].class);
    }

    public Optional<World> getWorld(@NotNull String path) {
        Optional<String> value = this.getString(path);

        if (value.isEmpty()) {
            return Optional.empty();
        }

        World world = Bukkit.getWorld(value.get());
        return world != null ? Optional.of(world) : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(final String path, final Class<T> clazz) {
        if(!(this.getObject(path) instanceof List<?> list)) {
            return List.of();
        }

        if(Objects.equals(clazz, String.class)) {
            return (List<T>) list.stream().map(Object::toString).toList();
        }

        return list.stream()
                .filter(value -> clazz.isAssignableFrom(value.getClass()))
                .map(value -> (T) value)
                .toList();
    }

    public <T extends Enum<T>> Optional<T> getEnum(@NotNull String path, Class<T> enumType) {
        return this.getEnum(path, enumType, null);
    }

    public <T extends Enum<T>> Optional<T> getEnum(@NotNull String path, Class<T> enumType, T def) {
        Optional<String> value = this.getString(path);
        if(value.isEmpty()) {
            return Optional.of(def);
        }


        T anEnum = EnumUtils.getEnum(value.get(), enumType);
        return anEnum != null ? Optional.of(anEnum) : Optional.of(def);
    }

    @NotNull
    public BridgeResult getResult(@NotNull String path) {
        return BridgeResult.of(mapBridge.getMapBridge(path));
    }

    @NotNull
    public Map<String, BridgeResult> getKeyResults() {
        return this.getKeyResults(null);
    }

    @NotNull
    public Map<String, BridgeResult> getKeyResults(@Nullable String path) {
        Map<String, BridgeResult> map = new HashMap<>();

        BridgeResult result = path == null ? this : this.getResult(path);
        for (String key : result.keySet()) {
            map.put(key, result.getResult(key));
        }

        return map;
    }

    private <T> Optional<T> getObject(@NotNull String path, Class<T> clazz) {
        Object value = this.getObject(path);
        return Optional.ofNullable(value == null || !value.getClass().isAssignableFrom(clazz) ? null : clazz.cast(value));
    }

    @Nullable
    public Object getObject(@NotNull String path) {
        return mapBridge.get(path);
    }

    @NotNull
    private final static BridgeResult EMPTY_RESULT = new BridgeResult(MapBridge.EMPTY_MAP_BRIDGE);

    @NotNull
    public static BridgeResult of(@Nullable MapBridge mapBridge) {
        return mapBridge == null ? EMPTY_RESULT : new BridgeResult(mapBridge);
    }

}
