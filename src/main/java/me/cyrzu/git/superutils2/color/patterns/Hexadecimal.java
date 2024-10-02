package me.cyrzu.git.superutils2.color.patterns;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hexadecimal implements ColorPattern {

    @NotNull
    public final Pattern hexPattern;

    public Hexadecimal() {
        this.hexPattern = Pattern.compile("&#[a-fA-F0-9]{6}");
    }

    @Override
    public @NotNull String parseText(@NotNull String text) {
        Matcher matcher = hexPattern.matcher(text);
        while (matcher.find()) {
            String group = matcher.group();
            text = text.replace(group, ChatColor.of(group.substring(1)).toString());
        }

        return text;
    }
    
}
