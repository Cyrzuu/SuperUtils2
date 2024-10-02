package me.cyrzu.git.superutils2.reload;

import me.cyrzu.git.superutils2.managers.Manager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReloaderManager extends Manager<JavaPlugin> {

    @NotNull
    private final List<Reloadable> reloadables;

    public ReloaderManager(@NotNull JavaPlugin plugin) {
        super(plugin);
        this.reloadables = new ArrayList<>();

        if(plugin instanceof Reloadable reloadable) {
            this.register(reloadable);
        }
    }

    public void register(@NotNull Reloadable... reloadables) {
        this.reloadables.addAll(Arrays.asList(reloadables));
    }

    public void reloadAll() {
        this.reloadables.forEach(Reloadable::reload);
    }

}
