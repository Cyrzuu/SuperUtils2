package me.cyrzu.git.superutils2.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class LocationUtils {

    @NotNull
    private final Pattern PATTERN = Pattern.compile("(\\w+):\\s*([\\w.\\-]+)");

    public double distance(@Nullable Location location, @Nullable Location secondLocation) {
        return LocationUtils.distance(location, secondLocation, false);
    }

    public double distance(@Nullable Location location, @Nullable Location secondLocation, boolean worldEqual) {
        return Math.sqrt(LocationUtils.distanceSquared(location, secondLocation, worldEqual));
    }


    public double distanceSquared(@Nullable Location location, @Nullable Location secondLocation) {
        return LocationUtils.distanceSquared(location, secondLocation, false);
    }

    public double distanceSquared(@Nullable Location location, @Nullable Location secondLocation, boolean worldEqual) {
        if(location == null || secondLocation == null) {
            return Double.MAX_VALUE;
        }

        World world = location.getWorld();
        World secondWorld = secondLocation.getWorld();
        if(worldEqual && (world == null || secondWorld == null || !world.getUID().equals(secondWorld.getUID()))) {
            return Double.MAX_VALUE;
        }

        return NumberConversions.square(location.getX() - secondLocation.getX()) +
                NumberConversions.square(location.getY() - secondLocation.getY()) +
                NumberConversions.square(location.getZ() - secondLocation.getZ());
    }


    @Nullable
    @Contract("_, _, false -> !null")
    public String serialize(@NotNull Location location, boolean block, boolean world) {
        World var0 = location.getWorld();

        if(world && var0 == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        if(world) {
            builder.append("world: ").append(var0.getName());
            builder.append(", x: ").append(block ? String.valueOf(location.getBlockX()) : NumberUtils.round(location.getX(), 3));
        } else {
            builder.append("x: ").append(block ? String.valueOf(location.getBlockX()) : NumberUtils.round(location.getX(), 3));
        }

        builder.append(", y: ").append(block ? String.valueOf(location.getBlockY()) : NumberUtils.round(location.getY(), 3));
        builder.append(", z: ").append(block ? String.valueOf(location.getBlockZ()) : NumberUtils.round(location.getZ(), 3));


        if(!block) {
            builder.append(", yaw: ").append(NumberUtils.round(location.getYaw(), 2));
            builder.append(", pitch: ").append(NumberUtils.round(location.getPitch(), 2));
        }

        return builder.toString();
    }

    @Nullable
    public Location deserialize(@NotNull String text, boolean block, boolean world) {
        Map<String, String> map = new HashMap<>();

        Matcher matcher = PATTERN.matcher(text);
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            map.put(key, value);
        }

        String x = map.get("x");
        String y = map.get("y");
        String z = map.get("z");

        if(x == null || y == null || z == null) {
            return null;
        }

        World world1 = world ? Bukkit.getWorld(map.getOrDefault("world", "")) : null;
        if(world && world1 == null) {
            return null;
        }

        return block ?
            new Location(world1, NumberConversions.toInt(x), NumberConversions.toInt(y), NumberConversions.toInt(z)) :
            new Location(world1, NumberConversions.toDouble(x), NumberConversions.toDouble(y), NumberConversions.toDouble(z), NumberConversions.toFloat(map.get("yaw")), NumberConversions.toFloat(map.get("pitch")));
    }

}
