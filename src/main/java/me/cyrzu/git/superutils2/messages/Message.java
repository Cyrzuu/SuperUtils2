package me.cyrzu.git.superutils2.messages;

import me.cyrzu.git.superutils2.collection.CollectionUtils;
import me.cyrzu.git.superutils2.color.ColorUtils;
import me.cyrzu.git.superutils2.config.Configurable;
import me.cyrzu.git.superutils2.utils.NumberUtils;
import me.cyrzu.git.superutils2.utils.StringUtils;
import me.cyrzu.git.superutils2.replace.ReplaceBuilder;
import me.cyrzu.git.superutils2.sound.PlaySound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class Message extends Configurable {

    @NotNull
    public final static Message EMPTY_MESSAGE = new Message("");

    @Nullable
    private final String message;

    @Nullable
    private PlaySound playSound;

    @Nullable
    private Title title;

    @Nullable
    private String actionBar;

    public Message(Object object) {
        if(!(object instanceof String string)) {
            this.message = Objects.toString(object);
            return;
        }

        string = ColorUtils.parseText(string);
        for (var entry : StringUtils.parseKeyValueDefinition(string).entrySet()) {
            switch (entry.getKey().toLowerCase()) {
                case "sound","s" -> {
                    Map<String, String> soundMap = StringUtils.parseKeyValue(entry.getValue());
                    String sound = CollectionUtils.getFirstPresemt("minecraft:ui.button.click", soundMap, "sound", "s", "type", "name");
                    double volume = NumberUtils.parseDouble(CollectionUtils.getFirstPresemt("0.5", soundMap, "volume", "v", "vol"));
                    double pitch = NumberUtils.parseDouble(CollectionUtils.getFirstPresemt("0.5", soundMap, "pitch", "p", "pit"));
                    this.playSound = new PlaySound(sound, volume, pitch);
                }
                case "title","t" -> this.title = new Title(StringUtils.parseKeyValue(entry.getValue()));
                case "actionbar","actionmessage","ab","am" -> {
                    Map<String, String> actionBarMap = StringUtils.parseKeyValue(entry.getValue());
                    this.actionBar = CollectionUtils.getFirstPresemt(actionBarMap, "message", "m", "text");
                }
                default -> {
                    continue;
                }
            }

            String text = "(%1$s={%2$s}|%1$s:{%2$s})".formatted(entry.getKey(), entry.getValue());
            string = string.replaceAll(text, "");
        }

        String trim = string.trim();
        this.message = !trim.isEmpty() ? trim : null;
    }

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

        public void send(@NotNull Player player, @NotNull ReplaceBuilder replacer, @NotNull Object... objects) {
            player.sendTitle(replacer.replace(title, objects), replacer.replace(subtitle, objects), fadeIn, duration, fadeOut);
        }

        public void send(@NotNull Player player) {
            player.sendTitle(title, subtitle, fadeIn, duration, fadeOut);
        }

    }

}
