package me.cyrzu.git.superutils2.collection;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CircularList<T> {

    private final boolean random;
    private final List<T> elements;
    private Iterator<T> iterator;

    public CircularList() {
        this(false);
    }

    public CircularList(boolean random) {
        this.random = random;
        this.elements = new ArrayList<>();
        this.iterator = null;
    }

    public void add(T element) {
        elements.add(element);
    }

    private void startIteration() {
        if(random) {
            Collections.shuffle(elements);
        }

        iterator = elements.iterator();
    }

    public T next() {
        if (iterator == null || !iterator.hasNext()) {
            this.startIteration();
        }

        return iterator.next();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @NotNull
    @SafeVarargs
    public static <T> CircularList<T> of(@NotNull T... values) {
        return CircularList.of(Arrays.asList(values));
    }

    @NotNull
    public static <T> CircularList<T> of(@NotNull Collection<T> values) {
        CircularList<T> circularList = new CircularList<>();
        for (T value : values) {
            circularList.add(value);
        }

        return circularList;
    }

    @NotNull
    @SafeVarargs
    public static <T> CircularList<T> randomOf(@NotNull T... values) {
        return CircularList.randomOf(Arrays.asList(values));
    }

    @NotNull
    public static <T> CircularList<T> randomOf(@NotNull Collection<T> values) {
        CircularList<T> circularList = new CircularList<>(true);
        for (T value : values) {
            circularList.add(value);
        }

        return circularList;
    }

}
