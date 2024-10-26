package me.cyrzu.git.superutils2.nbt;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemNBT {

    @NotNull
    NamespacedKey getKey(@NotNull String key);

    <P, C> void set(@NotNull ItemStack itemStack, @NotNull PersistentDataType<P, C> type, @NotNull String key, @NotNull C value);

    @Nullable
    <P, C> C get(@NotNull ItemStack itemStack, @NotNull PersistentDataType<P, C> type, @NotNull String key);

    @Nullable
    @Contract("_, _, _, !null -> !null")
    <P, C> C get(@NotNull ItemStack itemStack, @NotNull PersistentDataType<P, C> type, @NotNull String key, @Nullable C def);

    boolean has(@NotNull ItemStack itemStack, @NotNull String key);

    <P, C> boolean has(@NotNull ItemStack itemStack, @NotNull PersistentDataType<P, C> type, @NotNull String key);

}
