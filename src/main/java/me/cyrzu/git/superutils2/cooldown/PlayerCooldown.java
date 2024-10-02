package me.cyrzu.git.superutils2.cooldown;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerCooldown extends Cooldown<UUID> {

    public boolean hasCooldown(@NotNull OfflinePlayer player) {
        return this.hasCooldown(player.getUniqueId());
    }

    public void setCooldown(@NotNull OfflinePlayer player, Duration duration) {
        this.setCooldown(player.getUniqueId(), duration);
    }

    public void setCooldown(@NotNull OfflinePlayer player, int time, @NotNull TimeUnit unit) {
        this.setCooldown(player.getUniqueId(), time, unit);
    }

    @NotNull
    public Duration getRemainingCooldown(@NotNull OfflinePlayer player) {
        return this.getRemainingCooldown(player.getUniqueId());
    }

    public boolean checkAndSetCooldown(@NotNull OfflinePlayer player, Number time, @NotNull TimeUnit unit) {
        return this.checkAndSetCooldown(player.getUniqueId(), time, unit);
    }

    public void addCooldown(@NotNull OfflinePlayer player, int time, @NotNull TimeUnit unit) {
        this.addCooldown(player.getUniqueId(), time, unit);
    }

    public boolean contains(@NotNull OfflinePlayer player) {
        return this.contains(player.getUniqueId());
    }

    public void removeCooldown(@NotNull OfflinePlayer player) {
        this.removeCooldown(player.getUniqueId());
    }

}
