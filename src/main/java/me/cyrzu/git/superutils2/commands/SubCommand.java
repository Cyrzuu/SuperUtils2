package me.cyrzu.git.superutils2.commands;

import me.cyrzu.git.superutils2.commands.annotations.CommandName;
import me.cyrzu.git.superutils2.commands.annotations.Permission;
import me.cyrzu.git.superutils2.cooldown.PlayerCooldown;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class SubCommand {

    @Nullable
    public org.bukkit.permissions.Permission permission;

    @NotNull
    private final PlayerCooldown cooldown = new PlayerCooldown();

    public SubCommand() {
        String permission = this.getPermission();
        this.permission = permission == null ? null : new org.bukkit.permissions.Permission(permission);
    }

    @NotNull
    public String getName() {
        if(this.getClass().isAnnotationPresent(CommandName.class)) {
            CommandName value = this.getClass().getAnnotation(CommandName.class);
            return value.value();
        }

        return this.getClass().getSimpleName().toLowerCase(Locale.ENGLISH);
    }

    @Nullable
    public String getPermission() {
        if(this.getClass().isAnnotationPresent(Permission.class)) {
            Permission value = this.getClass().getAnnotation(Permission.class);
            return value.value();
        }

        return null;
    }

    public void execute(@NotNull CommandSender commandSender, @NotNull CommandContext context) { }

    public void execute(@NotNull ConsoleCommandSender console, @NotNull CommandContext context) { }

    public void execute(@NotNull Player player, @NotNull CommandContext context) { }

    @Nullable
    public Collection<String> tabComplete(@NotNull Player player, @NotNull CommandContext context) {
        return Collections.emptyList();
    }

    @Nullable
    public Collection<String> tabComplete(@NotNull CommandSender sender, @NotNull CommandContext context) {
        return Collections.emptyList();
    }

    public void usage(@NotNull CommandSender sender) { }

    public boolean hasPermission(CommandSender sender) {
        return this.permission == null || sender.hasPermission(this.permission);
    }

    @NotNull
    public String permissionMessage() {
        return "";
    }

    protected final void setCooldown(@NotNull Player player, int time, @NotNull TimeUnit unit) {
        this.setCooldown(player.getUniqueId(), time, unit);
    }

    protected final void setCooldown(@NotNull UUID uuid, int time, @NotNull TimeUnit unit) {
        cooldown.setCooldown(uuid, time, unit);
    }

    protected final boolean hasCooldown(@NotNull Player player) {
        return this.hasCooldown(player.getUniqueId());
    }

    protected final boolean hasCooldown(@NotNull UUID uuid) {
        return cooldown.hasCooldown(uuid);
    }

}
