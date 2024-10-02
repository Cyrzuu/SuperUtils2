package me.cyrzu.git.superutils2;

import me.cyrzu.git.superutils2.config.ConfigEntry;
import me.cyrzu.git.superutils2.item.StackBuilder;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Main extends JavaPlugin {

    @NotNull
    @ConfigEntry("itemson")
    private StackBuilder itemek = new StackBuilder(Material.STONE);

    @Override
    public void onEnable() {



    }

}