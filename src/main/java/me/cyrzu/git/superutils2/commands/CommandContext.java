package me.cyrzu.git.superutils2.commands;

import me.cyrzu.git.superutils2.utils.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class CommandContext {

    @NotNull
    private final String[] args;

    public CommandContext(@NotNull String[] arguments) {
        this.args = arguments;
    }

    @NotNull
    public String joiner() {
        return String.join(" ", args);
    }

    @NotNull
    public String joiner(int start) {
        return String.join(" ", Arrays.copyOfRange(args, start, size()));
    }

    public int size() {
        return args.length;
    }

    public boolean isEmpty() {
        return args.length == 0;
    }

    public boolean isSet(int index) {
        return index < args.length;
    }

    @Nullable
    public String get(int index) {
        return get(index, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public String get(int index, @Nullable String def) {
        String value = index < args.length ? args[index] : null;
        return value == null ? def : value;
    }

    public int asInt(int index) {
        return asInt(index, 0);
    }

    public int asInt(int index, int def) {
        String value = get(index, null);
        return value != null ? NumberUtils.parseInteger(value, def) : def;
    }

    public boolean isInt(int index) {
        return asInt(index, Integer.MIN_VALUE) != Integer.MIN_VALUE;
    }

    public long asLong(int index) {
        return asLong(index, 0L);
    }

    public long asLong(int index, long def) {
        String value = get(index, null);
        return value != null ? NumberUtils.parseLong(value, def) : def;
    }

    public boolean isLong(int index) {
        return asLong(index, Long.MIN_VALUE) != Long.MIN_VALUE;
    }

    public double asDouble(int index) {
        return asDouble(index, 0);
    }

    public double asDouble(int index, double def) {
        String value = get(index, null);
        return value != null ? NumberUtils.parseDouble(value, def) : def;
    }

    public double asDouble(int index, double def, int round) {
        String value = get(index, null);
        double v = value != null ? NumberUtils.parseDouble(value, def) : def;
        return NumberUtils.round(v, round);
    }

    public boolean isDouble(int index) {
        return asDouble(index, Double.MIN_VALUE) != Double.MIN_VALUE;
    }

    public boolean asBoolean(int index) {
        return asBoolean(index, false);
    }

    public boolean asBoolean(int index, boolean def) {
        String value = get(index, null);
        return value != null ? Boolean.parseBoolean(value) : def;
    }

    public boolean isBoolean(int index) {
        String value = get(index, "").toLowerCase();
        return value.equals("true") || value.equals("false");
    }

    @Nullable
    public Player asPlayer(int index) {
        return asPlayer(index, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Player asPlayer(int index, @Nullable Player def) {
        String value = index < args.length ? args[index] : null;
        if(value == null) {
            return def;
        }

        Player player = Bukkit.getPlayer(value);
        return player != null ? player : def;
    }

    public boolean isPlayer(int index) {
        return asPlayer(index) != null;
    }

    @Nullable
    public OfflinePlayer asOfflinePlayer(int index) {
        return asOfflinePlayer(index, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public OfflinePlayer asOfflinePlayer(int index, @Nullable OfflinePlayer def) {
        String value = index < args.length ? args[index] : null;
        if(value == null) {
            return def;
        }

        return Bukkit.getOfflinePlayer(value);
    }

    public boolean isOfflinePlayer(int index) {
        return asOfflinePlayer(index) != null;
    }

    @Nullable
    public <T extends Enum<T>> T getEnum(int index, @NotNull Class<T> clazz) {
        return getEnum(index, clazz, null);
    }

    @Nullable
    @Contract("_, _, !null -> !null")
    public <T extends Enum<T>> T getEnum(int index, @NotNull Class<T> clazz, @Nullable T def) {
        try {
            return Enum.valueOf(clazz, this.args[index].toUpperCase());
        } catch (final Exception exception) {
            return def;
        }
    }

    public <T extends Enum<T>> boolean isEnum(final int index, final Class<T> clazz) {
        return this.getEnum(index, clazz) != null;
    }

    @NotNull
    public String[] asArray() {
        return Arrays.copyOf(args, args.length);
    }

    @Override
    public String toString() {
        return "CommandContext{" +
                "args=" + Arrays.toString(args) +
                '}';
    }

}
