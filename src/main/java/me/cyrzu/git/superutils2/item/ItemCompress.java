package me.cyrzu.git.superutils2.item;

import me.cyrzu.git.superutils2.helper.Version;
import me.cyrzu.git.superutils2.messages.MessageUtils;
import me.cyrzu.git.superutils2.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

// https://github.com/nulli0n/nightcore-spigot/blob/HEAD/src/main/java/su/nightexpress/nightcore/util/ItemNbt.java
public class ItemCompress {

    private static final Class<?> ITEM_STACK_CLASS   = ReflectionUtils.getClass("net.minecraft.world.item", "ItemStack");
    private static final Class<?> COMPOUND_TAG_CLASS = ReflectionUtils.getClass("net.minecraft.nbt", "NBTTagCompound");
    private static final Class<?> NBT_IO_CLASS       = ReflectionUtils.getClass("net.minecraft.nbt", "NBTCompressedStreamTools");

    private static final Class<?> CRAFT_ITEM_STACK_CLASS = ReflectionUtils.getClass(Version.CRAFTBUKKIT_PACKAGE + ".inventory", "CraftItemStack");

    private static final Method CRAFT_ITEM_STACK_AS_NMS_COPY    = ReflectionUtils.getMethod(CRAFT_ITEM_STACK_CLASS, "asNMSCopy", ItemStack.class);
    private static final Method CRAFT_ITEM_STACK_AS_BUKKIT_COPY = ReflectionUtils.getMethod(CRAFT_ITEM_STACK_CLASS, "asBukkitCopy", ITEM_STACK_CLASS);

    private static final Method NBT_IO_WRITE = ReflectionUtils.getMethod(NBT_IO_CLASS, "a", COMPOUND_TAG_CLASS, DataOutput.class);
    private static final Method NBT_IO_READ  = ReflectionUtils.getMethod(NBT_IO_CLASS, "a", DataInput.class);

    private static final Class<?> DATA_FIXERS_CLASS = ReflectionUtils.getClass("net.minecraft.util.datafix", "DataConverterRegistry"); // DataFixers
    private static final Method GET_DATA_FIXER = ReflectionUtils.getMethod(DATA_FIXERS_CLASS, "a");
    private static final Class<?> NBT_OPS_CLASS = ReflectionUtils.getClass("net.minecraft.nbt", "DynamicOpsNBT"); // NbtOps
    private static final Class<?> REFERENCES_CLASS = ReflectionUtils.getClass("net.minecraft.util.datafix.fixes", "DataConverterTypes"); // References

    private static final int DATA_FIXER_SOURCE_VERSION = 3700; // 1.20.4
    private static final int DATA_FXIER_TARGET_VERSION = 3953; // 1.21

    private static Object NBT_OPS_INSTANCE;
    private static Object REFERENCE_ITEM_STACK;

    // For 1.20.6+
    private static Method MINECRAFT_SERVER_REGISTRY_ACCESS;
    private static Method ITEM_STACK_PARSE_OPTIONAL;
    private static Method ITEM_STACK_SAVE_OPTIONAL;

    // For 1.20.4 and below.
    private static Constructor<?> NBT_TAG_COMPOUND_NEW;
    private static Method         NMS_ITEM_OF;
    private static Method         NMS_SAVE;

    static {
        if (NBT_OPS_CLASS != null) {
            NBT_OPS_INSTANCE = ReflectionUtils.getFieldValue(NBT_OPS_CLASS, "a");
        }
        if (REFERENCES_CLASS != null) {
            REFERENCE_ITEM_STACK = ReflectionUtils.getFieldValue(REFERENCES_CLASS, "t");
        }

        if (Version.isAtLeast(Version.v1_20_R4)) {
            Class<?> minecraftServerClass = ReflectionUtils.getClass("net.minecraft.server", "MinecraftServer");
            Class<?> holderLookupProviderClass = ReflectionUtils.getInnerClass("net.minecraft.core.HolderLookup", "a"); // Provider

            MINECRAFT_SERVER_REGISTRY_ACCESS = ReflectionUtils.getMethod(minecraftServerClass, "bc");
            ITEM_STACK_PARSE_OPTIONAL = ReflectionUtils.getMethod(ITEM_STACK_CLASS, "a", holderLookupProviderClass, COMPOUND_TAG_CLASS);
            ITEM_STACK_SAVE_OPTIONAL  = ReflectionUtils.getMethod(ITEM_STACK_CLASS, "b", holderLookupProviderClass);
        }
        else {
            NBT_TAG_COMPOUND_NEW = ReflectionUtils.getConstructor(COMPOUND_TAG_CLASS);
            NMS_ITEM_OF          = ReflectionUtils.getMethod(ITEM_STACK_CLASS, "a", COMPOUND_TAG_CLASS);
            NMS_SAVE             = ReflectionUtils.getMethod(ITEM_STACK_CLASS, "b", COMPOUND_TAG_CLASS);
        }
    }

    private static boolean useRegistry;
    private static Object registryAccess;

    public static boolean setup() {
        if (!Version.isAtLeast(Version.v1_20_R4)) return true;

        useRegistry = true;

        Class<?> craftServerClass = ReflectionUtils.getClass(Version.CRAFTBUKKIT_PACKAGE, "CraftServer");
        if (craftServerClass == null) {
            MessageUtils.sendWarning(Bukkit.getConsoleSender(), "Could not find 'CraftServer' class in craftbukkit package: '" + Version.CRAFTBUKKIT_PACKAGE + "'.");
            return false;
        }

        Method getServer = ReflectionUtils.getMethod(craftServerClass, "getServer");
        if (getServer == null || MINECRAFT_SERVER_REGISTRY_ACCESS == null) {
            MessageUtils.sendWarning(Bukkit.getConsoleSender(), "Could not find proper class(es) for ItemStack compression util.");
            return false;
        }

        try {
            Object craftServer = craftServerClass.cast(Bukkit.getServer());
            Object minecraftServer = getServer.invoke(craftServer);
            registryAccess = MINECRAFT_SERVER_REGISTRY_ACCESS.invoke(minecraftServer);
            return true;
        }
        catch (ReflectiveOperationException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Nullable
    public static String compress(@NotNull ItemStack item) {
        if (CRAFT_ITEM_STACK_AS_NMS_COPY == null || NBT_IO_WRITE == null) {
            return null;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(outputStream);
        try {
            Object compoundTag;
            Object itemStack = CRAFT_ITEM_STACK_AS_NMS_COPY.invoke(null, item);

            if (useRegistry) {
                if (ITEM_STACK_SAVE_OPTIONAL == null) return null;

                compoundTag = ITEM_STACK_SAVE_OPTIONAL.invoke(itemStack, registryAccess);
            }
            else {
                if (NBT_TAG_COMPOUND_NEW == null || NMS_SAVE == null) return null;

                compoundTag = NBT_TAG_COMPOUND_NEW.newInstance();
                NMS_SAVE.invoke(itemStack, compoundTag);
            }

            NBT_IO_WRITE.invoke(null, compoundTag, dataOutput);

            return new BigInteger(1, outputStream.toByteArray()).toString(32);
        }
        catch (ReflectiveOperationException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static ItemStack decompress(@NotNull String compressed) {
        if (NBT_IO_READ == null || CRAFT_ITEM_STACK_AS_BUKKIT_COPY == null) {
            throw new UnsupportedOperationException("Unsupported server version!");
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(compressed, 32).toByteArray());
        try {
            Object compoundTag = NBT_IO_READ.invoke(null, new DataInputStream(inputStream));
            Object itemStack;

            if (useRegistry) {
                if (ITEM_STACK_PARSE_OPTIONAL == null) return null;

                itemStack = ITEM_STACK_PARSE_OPTIONAL.invoke(null, registryAccess, compoundTag);
            }
            else {
                if (NMS_ITEM_OF == null) return null;

                itemStack = NMS_ITEM_OF.invoke(null, compoundTag);
            }

            return (ItemStack) CRAFT_ITEM_STACK_AS_BUKKIT_COPY.invoke(null, itemStack);
        }
        catch (ReflectiveOperationException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @NotNull
    public static List<String> compress(@NotNull ItemStack[] items) {
        return compress(Arrays.asList(items));
    }

    @NotNull
    public static List<String> compress(@NotNull List<ItemStack> items) {
        return new ArrayList<>(items.stream().map(ItemCompress::compress).filter(Objects::nonNull).toList());
    }

    public static ItemStack[] decompress(@NotNull List<String> list) {
        List<ItemStack> items = list.stream().map(ItemCompress::decompress).filter(Objects::nonNull).toList();
        return items.toArray(new ItemStack[list.size()]);
    }

}
