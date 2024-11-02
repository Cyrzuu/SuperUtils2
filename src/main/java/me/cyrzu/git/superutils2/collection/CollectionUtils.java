package me.cyrzu.git.superutils2.collection;

import lombok.experimental.UtilityClass;
import me.cyrzu.git.superutils2.utils.NumberUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@UtilityClass
public class CollectionUtils {

    @NotNull
    public <T> T randomValue(@NotNull Collection<T> collections) {
        int i = NumberUtils.randomInteger(0, collections.size() - 1);
        return collections.stream().skip(i).findFirst().orElseThrow();
    }

    @Nullable
    @SafeVarargs
    public <K, V> V getFirstPresemt(@NotNull Map<K, V> map, K @NotNull ... keys) {
        return CollectionUtils.getFirstPresemt(null, map, keys);
    }

    @Nullable
    @SafeVarargs
    @Contract("!null, _, _ -> !null;")
    public <K, V> V getFirstPresemt(@Nullable V def, @NotNull Map<K, V> map, K @NotNull ... keys) {
        for (K key : keys) {
            V v = map.get(key);
            if(v != null) {
                return v;
            }
        }

        return def;
    }

    @NotNull
    public <K, V> Map<K, V> moveKey(@NotNull Map<K, V> map, K key, int offset) {
        if(!(map instanceof LinkedHashMap<K,V>)) {
            return map;
        }

        List<K> keys = new ArrayList<>(map.keySet());
        int index = keys.indexOf(key);

        if (index == -1 || offset == 0) {
            return map;
        }

        int newIndex = Math.max(0, Math.min(keys.size() - 1, index + offset));
        keys.remove(index);
        keys.add(newIndex, key);

        Map<K, V> result = new LinkedHashMap<>();
        for (K k : keys) {
            result.put(k, map.get(k));
        }

        return result;
    }

}
