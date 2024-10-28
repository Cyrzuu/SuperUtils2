package me.cyrzu.git.superutils2.messages;

import lombok.Getter;
import me.cyrzu.git.superutils2.collection.CollectionUtils;
import me.cyrzu.git.superutils2.color.ColorUtils;
import me.cyrzu.git.superutils2.config.Configurable;
import me.cyrzu.git.superutils2.replace.ReplaceBuilder;
import me.cyrzu.git.superutils2.sound.PlaySound;
import me.cyrzu.git.superutils2.utils.NumberUtils;
import me.cyrzu.git.superutils2.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class Message extends Configurable {

    @NotNull
    public final static Message EMPTY_MESSAGE = new Message("");

    @Getter
    @Nullable
    private final String message;

    @Getter
    @Nullable
    private PlaySound playSound;

    @Getter
    @Nullable
    private Title title;

    @Getter
    @Nullable
    private String actionBar;

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' + ChatColor.RESET +
                ", actionBar='" + actionBar + '\'' + ChatColor.RESET +
                ", playSound=" + playSound + ChatColor.RESET +
                ", title=" + title +
                '}';
    }

    public Message(Object object) {
        if(!(object instanceof String string)) {
            this.message = Objects.toString(object);
            return;
        }

        for (var entry : StringUtils.parseKeyValueDefinition(string).entrySet()) {
            String value = ColorUtils.parseText(entry.getValue());
            switch (entry.getKey().toLowerCase()) {
                case "sound","s" -> {
                    Map<String, String> soundMap = StringUtils.parseKeyValue(value);
                    String sound = CollectionUtils.getFirstPresemt("minecraft:ui.button.click", soundMap, "sound", "s", "type", "name");
                    double volume = NumberUtils.parseDouble(CollectionUtils.getFirstPresemt("0.5", soundMap, "volume", "v", "vol"));
                    double pitch = NumberUtils.parseDouble(CollectionUtils.getFirstPresemt("1.0", soundMap, "pitch", "p", "pit"));
                    this.playSound = new PlaySound(sound, volume, pitch);
                }
                case "playsound","ps" -> {
                    PlaySound registered = PlaySound.getRegistered(value);
                    this.playSound = registered != null ? registered : PlaySound.getRegistered(value.toUpperCase());
                }
                case "title","t" -> this.title = new Title(StringUtils.parseKeyValue(value));
                case "actionbar","actionmessage","ab","am" -> {
                    Map<String, String> actionBarMap = StringUtils.parseKeyValue(value);
                    this.actionBar = CollectionUtils.getFirstPresemt(actionBarMap, "message", "m", "text", "actionbar", "ab");
                }
                default -> {
                    continue;
                }
            }

            String text = "%s={%s}".formatted(entry.getKey(), entry.getValue());
            string = string.replace(text, "");
            text = "%s:{%s}".formatted(entry.getKey(), entry.getValue());
            string = string.replace(text, "");
        }

        for (Map.Entry<String, String> entry : StringUtils.parseKeyValue(string).entrySet()) {
            String value = ColorUtils.parseText(entry.getValue());
            switch (entry.getKey().toLowerCase()) {
                case "actionbar", "actionmessage", "ab", "am" -> this.actionBar = value;
                case "title", "t" -> {
                    String[] split = value.split("(\n|\\\\n)", 2);
                    this.title = split.length == 1 ? new Title(split[0], "") : new Title(split[0], split[1]);
                }
                case "playsound","ps" -> {
                    PlaySound registered = PlaySound.getRegistered(value);
                    this.playSound = registered != null ? registered : PlaySound.getRegistered(value.toUpperCase());
                }
                default -> {
                    continue;
                }
            }

            String text = "%s=\"%s\"".formatted(entry.getKey(), entry.getValue());
            string = string.replace(text, "");
            text = "%s:\"%s\"".formatted(entry.getKey(), entry.getValue());
            string = string.replace(text, "");
        }

        String trim = string.trim();
        this.message = !trim.isEmpty() ? ColorUtils.parseText(trim) : null;
    }

    public void send(@NotNull CommandSender sender) {
        if(sender instanceof Player player) {
            this.send(player);
            return;
        }

        if(message != null) {
            MessageUtils.send(sender, message);
        }
    }

    public void send(@NotNull Player player) {
        if(message != null) {
            player.sendMessage(message);
        }

        if(actionBar != null) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionBar));
        }

        if(title != null) {
            title.send(player);
        }

        if(playSound != null) {
            playSound.play(player);
        }
    }

    @Nullable
    public String sendWithoutActionBar(@NotNull Player player) {
        if(message != null) {
            player.sendMessage(message);
        }

        if(title != null) {
            title.send(player);
        }

        if(playSound != null) {
            playSound.play(player);
        }

        return actionBar;
    }

    public void send(@NotNull CommandSender sender, @NotNull ReplaceBuilder replacer, @NotNull Object... objects) {
        if(sender instanceof Player player) {
            this.send(player, replacer, objects);
            return;
        }

        if(message != null) {
            sender.sendMessage(replacer.replace(message, objects));
        }
    }

    public void send(@NotNull Player player, @NotNull ReplaceBuilder replacer, @NotNull Object... objects) {
        if(message != null) {
            player.sendMessage(replacer.replace(message, objects));
        }

        if(actionBar != null) {
            String actionbar = replacer.replace(this.actionBar, objects);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionbar));
        }

        if(title != null) {
            title.send(player, replacer, objects);
        }

        if(playSound != null) {
            playSound.play(player);
        }
    }

    @Nullable
    public String sendWithoutActionBar(@NotNull Player player, @NotNull ReplaceBuilder replacer, @NotNull Object... objects) {
        if(message != null) {
            player.sendMessage(replacer.replace(message, objects));
        }

        if(title != null) {
            title.send(player, replacer, objects);
        }

        if(playSound != null) {
            playSound.play(player);
        }

        return actionBar != null ? replacer.replace(this.actionBar, objects) : null;
    }

    @Getter
    private static class Title {

        @NotNull
        private final String title;

        @NotNull
        private final String subtitle;

        int fadeIn, duration, fadeOut;

        public Title(@NotNull Map<String, String> map) {
            this.title = CollectionUtils.getFirstPresemt("", map, "title", "t", "first", "1");
            this.subtitle = CollectionUtils.getFirstPresemt("", map, "subtitle", "st", "second", "2");

            this.fadeIn = NumberUtils.parseInteger(CollectionUtils.getFirstPresemt("10", map, "fadein", "fi", "in", "from"));
            this.duration = NumberUtils.parseInteger(CollectionUtils.getFirstPresemt("45", map, "duration", "d", "time", "stay"));
            this.fadeOut = NumberUtils.parseInteger(CollectionUtils.getFirstPresemt("10", map, "fadeout", "fo", "out", "to"));
        }

        public Title(@NotNull String title, @NotNull String subtitle) {
            this.title = title;
            this.subtitle = subtitle;
            this.fadeIn = 5;
            this.duration = 45;
            this.fadeOut = 5;
        }

        public void send(@NotNull Player player, @NotNull ReplaceBuilder replacer, @NotNull Object... objects) {
            player.sendTitle(replacer.replace(title, objects), replacer.replace(subtitle, objects), fadeIn, duration, fadeOut);
        }

        public void send(@NotNull Player player) {
            player.sendTitle(title, subtitle, fadeIn, duration, fadeOut);
        }

        @Override
        public String toString() {
            return "Title{" +
                    "duration=" + duration +
                    ", title='" + title + '\'' +
                    ", subtitle='" + subtitle + '\'' +
                    ", fadeIn=" + fadeIn +
                    ", fadeOut=" + fadeOut +
                    '}';
        }

    }

}
