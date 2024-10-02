package me.cyrzu.git.superutils2.managers;

import me.cyrzu.git.superutils2.helper.Scheduler;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class Manager<T extends JavaPlugin> {

    @NotNull
    protected final T plugin;

    @NotNull
    protected final Scheduler scheduler;

    public Manager(@NotNull T plugin) {
        this.plugin = plugin;
        this.scheduler = new Scheduler(plugin);
    }

    public void load() {
    }

    public void unload() {
    }

}
