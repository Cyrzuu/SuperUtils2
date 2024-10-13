package me.cyrzu.git.superutils2.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class LocationUtils {

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

}
