package me.cyrzu.git.superutils2.color;

import lombok.experimental.UtilityClass;
import me.cyrzu.git.superutils2.color.patterns.ColorPattern;
import me.cyrzu.git.superutils2.color.patterns.Hexadecimal;
import me.cyrzu.git.superutils2.color.patterns.Minecraft;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

@UtilityClass
public class ColorUtils {

    @NotNull
    public final Pattern HEX_PATTERN = Pattern.compile("([a-fA-F0-9]{6}|[a-fA-F0-9]{3})");

    @NotNull
    public final ChatColor ERROR_COLOR = ChatColor.of(new Color(204, 0, 0));

    @NotNull
    public final ChatColor INFO_COLOR = ChatColor.of(new Color(204, 163, 0));

    @NotNull
    public final ChatColor SUCCESS_COLOR = ChatColor.of(new Color(0, 204, 0));

    @NotNull
    private final static List<ColorPattern> PATTERNS = List.of(new Minecraft(), new Hexadecimal());

    @NotNull
    public String parseText(@NotNull String text) {
        for (ColorPattern pattern : PATTERNS) {
            text = pattern.parseText(text);
        }

        return text;
    }

    @NotNull
    public static String stripColor(@NotNull String text) {
        return ChatColor.stripColor(ColorUtils.parseText(text));
    }

    @Nullable
    public org.bukkit.Color getBukkitColor(@NotNull String hex) {
        return ColorUtils.getBukkitColor(hex, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public org.bukkit.Color getBukkitColor(@NotNull String hex, @Nullable org.bukkit.Color def) {
        Color color = ColorUtils.getColor(hex);

        return color != null ? org.bukkit.Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()) : def;
    }

    @Nullable
    public Color getColor(@NotNull String hex) {
        return ColorUtils.getColor(hex, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Color getColor(@NotNull String hex, @Nullable Color def) {
        hex = hex.startsWith("#") ? hex.substring(1) : hex;
        if(!HEX_PATTERN.matcher(hex).find()) {
            return def;
        }

        final int length = hex.length();
        int r = length == 3 ? Integer.valueOf(hex.substring(0, 1).repeat(2), 16) : Integer.valueOf(hex.substring(0, 2), 16);
        int g = length == 3 ? Integer.valueOf(hex.substring(1, 2).repeat(2), 16) : Integer.valueOf(hex.substring(2, 4), 16);
        int b = length == 3 ? Integer.valueOf(hex.substring(2, 3).repeat(2), 16) : Integer.valueOf(hex.substring(4, 6), 16);

        return new Color(r, g, b);
    }


}
