package me.cyrzu.git.superutils2.replace;

import me.cyrzu.git.superutils2.messages.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReplaceBuilder {

    public final static ReplaceBuilder PLAYER = new ReplaceBuilder("%player");

    public final static ReplaceBuilder PLAYER_MESSAGE = new ReplaceBuilder("%player", "%message");

    public final static ReplaceBuilder PLAYER_TARGET = new ReplaceBuilder("%player", "%target");

    private final List<String> replacment;

    public ReplaceBuilder(String... var0) {
        this.replacment = new ArrayList<>(Arrays.asList(var0));
    }

    public ReplaceBuilder add(@NotNull String... original) {
        replacment.addAll(Arrays.asList(original));
        return this;
    }

    public String replace(@NotNull String message, Object... args) {
        if(this.replacment.size() > args.length) {
            return message;
        }

        int index = 0;
        for (String s : replacment) {
            message = message.replace(s, String.valueOf(args[index++]));
        }

        return message;
    }

    public void sendMessage(@NotNull Player player, @NotNull String message, Object... args) {
        player.sendMessage(this.replace(message, args));
    }

    public void sendMessage(@NotNull CommandSender sender, @NotNull String message, Object... args) {
        sender.sendMessage(this.replace(message, args));
    }

    public void sendMessage(@NotNull Player player, @Nullable Message message) {
        if(message == null) {
            return;
        }


    }

    @NotNull
    public static ReplaceBuilder of(@NotNull String... originals) {
        return new ReplaceBuilder(originals);
    }

}
