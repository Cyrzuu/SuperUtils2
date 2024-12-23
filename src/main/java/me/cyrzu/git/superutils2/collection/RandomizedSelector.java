package me.cyrzu.git.superutils2.collection;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiPredicate;

public class RandomizedSelector<T> {

    @NotNull
    private final T defaultValue;

    @NotNull
    private Map<T, Double> elements;

    @NotNull
    private final Random random;

    @Getter
    private double total = 0D;

    public RandomizedSelector(@NotNull T defaultValue) {
        this(defaultValue, false);
    }

    public RandomizedSelector(@NotNull T defaultValue, boolean keepOrder) {
        this.defaultValue = defaultValue;
        this.elements = keepOrder ? new LinkedHashMap<>() : new HashMap<>();
        this.random = new Random();
    }

    public RandomizedSelector<T> putAll(@NotNull Map<T, Number> values) {
        values.forEach((k, v) -> put(v, k));
        return this;
    }

    public void put(@NotNull Number percent, @NotNull T value) {
        double percentage = Math.max(0D, percent.doubleValue());
        elements.put(value, percentage);

        total = elements.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public RandomizedSelector<T> remove(@NotNull T value) {
        elements.remove(value);

        total = elements.values().stream().mapToDouble(Double::doubleValue).sum();
        return this;
    }

    public boolean removeIf(BiPredicate<T, Double> filter) {
        boolean removed = false;
        for (Map.Entry<T, Double> next : elements.entrySet()) {
            if (filter.test(next.getKey(), next.getValue())) {
                elements.remove(next.getKey());
                removed = true;
            }
        }
        return removed;
    }

    public boolean contains(@NotNull T value) {
        return elements.containsKey(value);
    }

    public double get(@NotNull T value) {
        return elements.getOrDefault(value, 0D);
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public Collection<T> getValues() {
        return Collections.unmodifiableCollection(elements.keySet());
    }

    public Map<T, Double> getMapValues() {
        return Collections.unmodifiableMap(elements);
    }

    @NotNull
    public T next() {
        if (elements.isEmpty()) {
            return defaultValue;
        }

        double randomNumber = random.nextDouble() * total;
        double percent = 0D;

        for (Map.Entry<T, Double> entry : elements.entrySet()) {
            percent += entry.getValue();
            if (randomNumber < percent) {
                return entry.getKey();
            }
        }

        return defaultValue;
    }

    public void moveLeft(@NotNull T value) {
        this.elements = CollectionUtils.moveKey(this.elements, value, -1);
    }

    public void moveRight(@NotNull T value) {
        this.elements = CollectionUtils.moveKey(this.elements, value, 1);
    }

    public void moveOffSet(@NotNull T value, int offSet) {
        this.elements = CollectionUtils.moveKey(this.elements, value, offSet);
    }

}