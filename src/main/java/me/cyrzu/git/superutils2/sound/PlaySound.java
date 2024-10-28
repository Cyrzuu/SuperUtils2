package me.cyrzu.git.superutils2.sound;

import me.cyrzu.git.superutils2.utils.EnumUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public record PlaySound(@NotNull String sound, float volume, float pitch) {

    @NotNull
    private final static Map<String, PlaySound> REGISTERED = new HashMap<>();

    public static PlaySound ERROR_1 = new PlaySound(Sound.BLOCK_ANVIL_LAND, 0.5, 1.85, "ERROR_1");

    public static PlaySound ERROR_2 = new PlaySound(Sound.BLOCK_CHEST_OPEN, 0.5, 2, "ERROR_2");

    public static PlaySound ERROR_3 = new PlaySound(Sound.ENTITY_BLAZE_SHOOT, 0.5, 2, "ERROR_3");

    public static PlaySound ERROR_4 = new PlaySound(Sound.ENTITY_VILLAGER_NO, 0.5, 0.75, "ERROR_4");

    public static PlaySound ERROR_5 = new PlaySound(Sound.ENTITY_BAT_HURT, 0.5, 0.85, "ERROR_5");

    public static PlaySound LEVEL_UP = new PlaySound(Sound.ENTITY_PLAYER_LEVELUP, 0, 1.25, "LEVEL_UP");

    public static PlaySound XP = new PlaySound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5, 1.25, "XP");

    public static PlaySound CLICK = new PlaySound(Sound.UI_BUTTON_CLICK, 0.3, 1.25, "CLICK");

    public static PlaySound CLICK_OFF = new PlaySound(Sound.UI_BUTTON_CLICK, 0.3, 0.75, "CLICK_OFF");

    public PlaySound(@NotNull Sound sound, @NotNull Number volume, @NotNull Number pitch) {
        this(sound, volume, pitch, null);
    }

    public PlaySound(@NotNull Sound sound, @NotNull Number volume, @NotNull Number pitch, @Nullable String id) {
        this("%s:%s".formatted(sound.getKey().getNamespace(), sound.getKey().getKey()), volume, pitch, id);
    }

    public PlaySound(@NotNull String sound, @NotNull Number volume, @NotNull Number pitch) {
        this(sound, volume, pitch, null);
    }

    public PlaySound(@NotNull String sound, float volume, float pitch) {
        this.volume = volume;
        this.pitch = pitch;

        sound = sound.trim().replace(" ", "_");
        String[] split = sound.split(":");
        Sound anEnum = EnumUtils.getEnum(sound, Sound.class);
        this.sound = anEnum != null ? "%s:%s".formatted(anEnum.getKey().getNamespace(), anEnum.getKey().getKey()) : split.length == 1 ? "minecraft:%s".formatted(sound) : "%s:%s".formatted(split[0], split[1]);
    }

    public PlaySound(@NotNull String sound, @NotNull Number volume, @NotNull Number pitch, @Nullable String id) {
        this(sound, volume.floatValue(), pitch.floatValue());
        Optional.ofNullable(id).ifPresent(value -> PlaySound.REGISTERED.put(id.toLowerCase(), this));
    }

    public void play(@NotNull Player player) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public void play(@NotNull Player player, @NotNull Number volume) {
        player.playSound(player.getLocation(), sound, volume.floatValue(), pitch);
    }

    public void play(@NotNull Player player, @NotNull Number volume, @NotNull Number pitch) {
        player.playSound(player.getLocation(), sound, volume.floatValue(), pitch.floatValue());
    }

    public static void play(@NotNull Player player, @NotNull Sound sound) {
        PlaySound.play(player, sound, 1F, 0.5F);
    }

    public static void play(@NotNull Player player, @NotNull Sound sound, @NotNull Number pitch) {
        PlaySound.play(player, sound, pitch, 0.5F);
    }

    public static void play(@NotNull Player player, @NotNull Sound sound, @NotNull Number pitch, @NotNull Number volume) {
        player.playSound(player.getLocation(), sound, volume.floatValue(), pitch.floatValue());
    }

    @Nullable
    public static PlaySound getRegistered(@Nullable String soundName) {
        return soundName != null ? REGISTERED.get(soundName.toLowerCase(Locale.ROOT)) : null;
    }

}
