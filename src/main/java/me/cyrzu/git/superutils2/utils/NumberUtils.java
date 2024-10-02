package me.cyrzu.git.superutils2.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class NumberUtils {

    @NotNull
    private final Random random = ThreadLocalRandom.current();

    public double round(double value, int scale) {
        if(value <= 0) {
            return Math.round(value);
        }

        BigDecimal decimal = BigDecimal.valueOf(value);
        return decimal.setScale(scale, RoundingMode.FLOOR).doubleValue();
    }

    public boolean isDouble(@NotNull String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

    public double parseDouble(@NotNull String value) {
        return parseDouble(value, 0D);
    }

    public double parseDouble(@NotNull String value, double def) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return def;
        }
    }

    public boolean isInteger(@NotNull String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

    public int parseInteger(@NotNull String value) {
        return parseInteger(value, 0);
    }

    public int parseInteger(@NotNull String value, int def) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return def;
        }
    }

    public static boolean isLong(@NotNull String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException ignore) {
            return false;
        }
    }

    public long parseLong(@NotNull String value) {
        return parseLong(value, 0L);
    }

    public long parseLong(@NotNull String value, long def) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public int randomInteger(int min, int max) {
        return min >= max ? min : random.nextInt(max + 1 - min) + min;
    }

    public boolean chance(double chance) {
        if(chance >= 100D) {
            return true;
        }

        if(chance <= 0D) {
            return false;
        }

        double randomValue = random.nextDouble() * 100;
        return randomValue < chance;
    }

    public double getPercents(double percent, double number) {
        return (percent / 100.0) * number;
    }

    public double getPercents(double percent, double number, int round) {
        return NumberUtils.round((percent / 100.0) * number, round);
    }

}
