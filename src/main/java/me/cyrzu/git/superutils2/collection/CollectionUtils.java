package me.cyrzu.git.superutils2.collection;

import lombok.experimental.UtilityClass;
import me.cyrzu.git.superutils2.other.NumberUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

@UtilityClass
public class CollectionUtils {

    @NotNull
    public <T> T randomValue(@NotNull Collection<T> collections) {
        int i = NumberUtils.randomInteger(0, collections.size() - 1);
        return collections.stream().skip(i).findFirst().orElseThrow();
    }

    @Nullable
    @SafeVarargs
    public static <K, V> V getFirstPresemt(@NotNull Map<K, V> map, K @NotNull ... keys) {
        return CollectionUtils.getFirstPresemt(null, map, keys);
    }

    @Nullable
    @SafeVarargs
    @Contract("!null, _, _ -> !null;")
    public static <K, V> V getFirstPresemt(@Nullable V def, @NotNull Map<K, V> map, K @NotNull ... keys) {
        for (K key : keys) {
            V v = map.get(key);
            if(v != null) {
                return v;
            }
        }

        return def;
    }

}
