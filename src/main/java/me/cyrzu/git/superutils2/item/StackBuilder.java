package me.cyrzu.git.superutils2.item;

import lombok.Getter;
import me.cyrzu.git.superutils2.collection.CollectionUtils;
import me.cyrzu.git.superutils2.color.ColorUtils;
import me.cyrzu.git.superutils2.config.Configurable;
import me.cyrzu.git.superutils2.helper.Version;
import me.cyrzu.git.superutils2.replace.ReplaceBuilder;
import me.cyrzu.git.superutils2.utils.EnumUtils;
import me.cyrzu.git.superutils2.utils.NumberUtils;
import me.cyrzu.git.superutils2.utils.StringUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StackBuilder extends Configurable {

    @NotNull
    private static final Pattern LORE_NEW_LINE_PATTERN = Pattern.compile("(\\\\n|\\n)");

    @Getter
    @NotNull
    private final Material material;

    @Nullable
    private String displayName;

    @NotNull
    private final List<String> lore = new ArrayList<>();

    @Getter
    private boolean unbreakable = false;

    @Getter
    private int amount;

    @Getter
    private Integer customModelData;

    @Getter
    @Nullable
    private Integer damage;

    @Getter
    @Nullable
    private Color color;

    @Getter
    @Nullable
    private Integer maxStackSize;

    @NotNull
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();

    @NotNull
    private final Set<ItemFlag> flags = new HashSet<>();

    @Nullable
    private PlayerProfile headTexture;

    @Nullable
    private Rarity rarity;

    @NotNull
    private final Map<NamespacedKey, String> persistentData = new HashMap<>();

    @Nullable
    private Boolean hideToolTip;

    private boolean glow;

    public StackBuilder(@NotNull Material material) {
        this(material, 1);
    }

    public StackBuilder(@NotNull Material material, int amount) {
        this.material = material.isItem() && !material.isAir() ? material : Material.STONE;
        this.amount = Math.max(1, amount);
    }

    public StackBuilder(@NotNull ItemStack item) {
        Material type = item.getType();
        this.material = type.isItem() && !type.isAir() ? type : Material.STONE;
        this.amount = item.getAmount();

        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta != null) {
            this.displayName = itemMeta.getDisplayName();

            List<String> lore = itemMeta.getLore();
            if(lore != null && !lore.isEmpty()) {
                this.lore.addAll(lore);
            }

            if(itemMeta instanceof Damageable damageable) {
                this.unbreakable = damageable.isUnbreakable();

                int damage = damageable.getDamage();
                this.damage = damage > 0 ? damage : null;
            }

            this.customModelData = itemMeta.hasCustomModelData() ? itemMeta.getCustomModelData() : null;

            if(itemMeta instanceof LeatherArmorMeta leatherArmorMeta) {
                this.color = leatherArmorMeta.getColor();
            }

            if(Version.isAtLeast(Version.v1_20_R4)) {
                if(itemMeta.hasRarity()) {
                    this.rarity = Rarity.getRarity(itemMeta.getRarity());
                }

                if(itemMeta.hasMaxStackSize()) {
                    this.maxStackSize = itemMeta.getMaxStackSize();
                }

                if(itemMeta.isHideTooltip()) {
                    this.hideToolTip = itemMeta.isHideTooltip();
                }
            }

            PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
            for (NamespacedKey key : persistentDataContainer.getKeys()) {
                String value = persistentDataContainer.get(key, PersistentDataType.STRING);
                if(!key.getNamespace().equals(NamespacedKey.MINECRAFT) || value == null) {
                    continue;
                }

               persistentData.put(key, persistentDataContainer.get(key, PersistentDataType.STRING));
            }

            if(itemMeta instanceof SkullMeta skullMeta) {
                this.headTexture = skullMeta.getOwnerProfile();
            }

            enchantments.putAll(itemMeta.getEnchants());
            flags.addAll(itemMeta.getItemFlags());
        }
    }

    public StackBuilder(Object object) {
        if(!(object instanceof String string)) {
            this.material = Material.STONE;
            return;
        }

        var entries = StringUtils.parseKeyValue(string);

        String material = CollectionUtils.getFirstPresemt(entries, "material", "m", "type", "t");
        this.material = material == null ? Material.STONE : EnumUtils.getEnum(material, Material.class, Material.STONE);

        String amount = CollectionUtils.getFirstPresemt("1", entries, "amount", "a", "count", "c");
        this.amount = NumberUtils.parseInteger(amount.trim(), 1);

        String displayName = CollectionUtils.getFirstPresemt(entries, "displayname", "dn", "name", "n");
        this.setName(displayName);

        String lore = CollectionUtils.getFirstPresemt(entries, "lore", "l");
        if(lore != null) {
            this.addLore(LORE_NEW_LINE_PATTERN.split(lore));
        }

        String damage = CollectionUtils.getFirstPresemt(entries, "damage", "dmg");
        this.damage = damage != null ? NumberUtils.parseInteger(damage.trim(), -1) : null;

        String unbreakable = CollectionUtils.getFirstPresemt(entries, "unbreakable", "unbr", "unb", "u");
        this.unbreakable = unbreakable != null;

        String customModelData = CollectionUtils.getFirstPresemt(entries, "custommodeldata", "model", "cmd");
        this.customModelData = customModelData != null ? NumberUtils.parseInteger(customModelData.trim(), -1) : null;

        String color = CollectionUtils.getFirstPresemt(entries, "color");
        this.color = color != null ? ColorUtils.getBukkitColor(color.trim()) : null;

        String rarity = CollectionUtils.getFirstPresemt(entries, "rarity", "rare");
        this.rarity = rarity != null ? EnumUtils.getEnum(rarity.trim(), Rarity.class) : null;

        int maxStackSize = NumberUtils.parseInteger(CollectionUtils.getFirstPresemt("", entries, "maxstacksize", "maxstack", "mss").trim(), -1);
        this.maxStackSize = maxStackSize > 0 ? maxStackSize : null;

        String hideToolTip = CollectionUtils.getFirstPresemt(entries, "hidetooltip", "hidetip", "htt");
        this.hideToolTip =  hideToolTip != null ? Boolean.getBoolean(hideToolTip.trim()) : null;

        String headTexture = CollectionUtils.getFirstPresemt(entries, "headtexture", "head", "skull", "texture");
        this.setHeadTexture(headTexture);

        String persistent = CollectionUtils.getFirstPresemt(entries, "persistent", "nbt", "tag", "tags");
        if(persistent != null) {
            for (String value : persistent.split(",,")) {
                String[] split = value.split(":", 2);
                if(split.length != 2) {
                    continue;
                }

                this.addPersistentData(split[0].trim(), split[1]);
            }
        }

        String enchantments = CollectionUtils.getFirstPresemt(entries, "enchantments", "enchants", "enchant", "ench", "e");
        if(enchantments != null) {
            for (String value : enchantments.replace(" ", "").split("(,,|,)")) {
                String[] split = value.split(":", 2);
                if(split.length != 2) {
                    continue;
                }

                this.addEnchantment(split[0], NumberUtils.parseInteger(split[1], 1));
            }
        }

        String flags = CollectionUtils.getFirstPresemt(entries, "flags", "flag");
        if(flags != null && flags.equalsIgnoreCase("ALL")) {
            this.allFlags();
        } else if(flags != null) {
            this.addFlags(Arrays.stream(flags.replace(" ", "").split("(,,|,)"))
                    .map(s -> EnumUtils.getEnum(s, ItemFlag.class))
                    .filter(Objects::nonNull)
                    .toArray(ItemFlag[]::new));
        }
    }

    @NotNull
    public StackBuilder setAmount(int amount) {
        this.amount = Math.max(1, amount);
        return this;
    }

    @NotNull
    public StackBuilder setName(@Nullable String displayName) {
        this.displayName = displayName != null ? ColorUtils.parseText(displayName) : null;
        return this;
    }

    @NotNull
    public StackBuilder addLore(String @NotNull ... lores) {
        lore.addAll(Arrays.stream(lores).map(ColorUtils::parseText).toList());
        return this;
    }

    @NotNull
    public StackBuilder setLore(String @NotNull ... lores) {
        this.clearLore();
        lore.addAll(Arrays.stream(lores).map(ColorUtils::parseText).toList());
        return this;
    }

    @NotNull
    public StackBuilder clearLore() {
        lore.clear();
        return this;
    }

    @NotNull
    public StackBuilder setDamage(int damage) {
        this.damage = damage <= 0 ? null : damage;
        return this;
    }

    @NotNull
    public StackBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    @NotNull
    public StackBuilder setCustomModelData(int customModelData) {
        this.customModelData = customModelData < 0 ? null : customModelData;
        return this;
    }

    @NotNull
    public StackBuilder setColor(@Nullable Color color) {
        this.color = color;
        return this;
    }

    @NotNull
    public StackBuilder setRarity(@Nullable Rarity rarity) {
        this.rarity = rarity;
        return this;
    }

    @NotNull
    public StackBuilder setMaxStackSize(@Nullable Integer maxStackSize) {
        this.maxStackSize = maxStackSize;
        return this;
    }

    @NotNull
    public StackBuilder setHideToolTip(@Nullable Boolean hideToolTip) {
        this.hideToolTip = hideToolTip;
        return this;
    }

    @NotNull
    public StackBuilder setHeadTexture(@Nullable String headTexture) {
        if(headTexture == null) {
            this.headTexture = null;
            return this;
        }

        PlayerProfile playerProfile = Bukkit.createPlayerProfile(new UUID(0, 0), "Cyrzu");
        try {
            PlayerTextures textures = playerProfile.getTextures();

            String urlString = "http://textures.minecraft.net/texture/";
            URL url = new URL(headTexture.startsWith(urlString) ? headTexture : urlString + headTexture);
            textures.setSkin(url);
            playerProfile.setTextures(textures);
        } catch (Exception ignore) { }

        this.headTexture = playerProfile;
        return this;
    }

    public StackBuilder addPersistentData(@NotNull String key, @NotNull String value) {
        persistentData.put(NamespacedKey.minecraft(key), value);
        return this;
    }

    public StackBuilder addEnchantment(@NotNull Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);
        return this;
    }

    public StackBuilder addEnchantment(@NotNull String enchantment, int level) {
        Enchantment byName = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchantment.toLowerCase()));
        return byName != null ? this.addEnchantment(byName, level) : this;
    }

    @NotNull
    public StackBuilder clearEnchantments() {
        this.enchantments.clear();
        return this;
    }

    @NotNull
    public StackBuilder addEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        this.enchantments.putAll(enchantments);
        return this;
    }

    @NotNull
    public StackBuilder setEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        this.clearEnchantments();
        return this.addEnchantments(enchantments);
    }

    @NotNull
    public StackBuilder addFlags(ItemFlag @NotNull ... flags) {
        this.flags.addAll(Arrays.asList(flags));
        return this;
    }

    public StackBuilder clearFlags() {
        this.flags.clear();
        return this;
    }

    public StackBuilder setFlags(ItemFlag @NotNull ... flags) {
        this.clearFlags();
        return this.addFlags(flags);
    }

    public StackBuilder allFlags() {
        this.addFlags(ItemFlag.values());
        return this;
    }

    public StackBuilder setGlow(boolean glow) {
        this.glow = glow;
        return this;
    }

    @NotNull
    public ItemStack build() {
        return this.build(null);
    }

    @NotNull
    public ItemStack build(@NotNull ReplaceBuilder replaceBuilder, Object... args) {
        return this.build(s -> replaceBuilder.replace(s, args));
    }

    @NotNull
    public ItemStack build(@Nullable Function<String, String> replace) {
        ItemStack itemStack = new ItemStack(this.material);
        itemStack.setAmount(this.amount);

        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) {
            return itemStack;
        }

        if(displayName != null && replace == null) {
            itemMeta.setDisplayName(displayName);
        } else if(displayName != null) {
            itemMeta.setDisplayName(replace.apply(displayName));
        }

        boolean loreEmpty = lore.isEmpty();
        if(!loreEmpty && replace == null) {
            itemMeta.setLore(lore);
        } else if(!loreEmpty) {
            List<String> list = lore.stream().map(replace).flatMap(line -> Stream.of(LORE_NEW_LINE_PATTERN.split(line))).toList();
            itemMeta.setLore(list);
        }

        if(itemMeta instanceof Damageable damageable) {
            if(this.unbreakable) {
                damageable.setUnbreakable(true);
            }

            if(damage != null) {
                damageable.setDamage(damage);
            }
        }

        if(customModelData != null) {
            itemMeta.setCustomModelData(customModelData);
        }

        if(color != null && itemMeta instanceof LeatherArmorMeta armorMeta) {
            armorMeta.setColor(color);
        }

        if(Version.isAtLeast(Version.v1_20_R4)) {
            if(rarity != null) {
                itemMeta.setRarity(rarity.getRarity());
            }

            if(maxStackSize != null) {
                itemMeta.setMaxStackSize(maxStackSize);
            }

            if(hideToolTip != null) {
                itemMeta.setHideTooltip(hideToolTip);
            }
        }

        PersistentDataContainer persistentData = itemMeta.getPersistentDataContainer();
        this.persistentData.forEach((k, v) -> persistentData.set(k, PersistentDataType.STRING, replace == null ? v : replace.apply(v)));

        if(headTexture != null && itemMeta instanceof SkullMeta skullMeta) {
            skullMeta.setOwnerProfile(headTexture);
        }

        enchantments.forEach((k, v) -> itemMeta.addEnchant(k, v, true));
        itemMeta.addItemFlags(flags.toArray(ItemFlag[]::new));

        if(glow && itemMeta.getEnchants().isEmpty()) {
            itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("StackBuilder{");

        sb.append(material).append(" x ").append(amount);

        if (displayName != null) {
            sb.append(", displayName='").append(displayName).append(ChatColor.RESET).append('\'');
        }

        if (!lore.isEmpty()) {
            String collect = lore.stream().collect(Collectors.joining(ChatColor.RESET + ", "));
            sb.append(", lore=").append("[").append(collect).append(ChatColor.RESET).append("]");
        }

        if (unbreakable) {
            sb.append(", unbreakable=").append(true);
        }

        if (customModelData != null) {
            sb.append(", customModelData=").append(customModelData);
        }

        if (damage != null) {
            sb.append(", damage=").append(damage);
        }

        if (color != null) {
            sb.append(", dyeColor=").append(color);
        }

        if (maxStackSize != null) {
            sb.append(", maxStackSize=").append(maxStackSize);
        }

        if (!enchantments.isEmpty()) {
            sb.append(", enchantments=").append(enchantments);
        }

        if (!flags.isEmpty()) {
            sb.append(", flags=").append(flags);
        }

        if (headTexture != null) {
            sb.append(", headTexture=").append(true);
        }

        if (rarity != null) {
            sb.append(", rarity=").append(rarity);
        }

        if (!persistentData.isEmpty()) {
            sb.append(", persistentData=").append(persistentData.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getKey(), Map.Entry::getValue)));
        }

        if (hideToolTip != null) {
            sb.append(", hideToolTip=").append(hideToolTip);
        }

        if (glow) {
            sb.append(", glow=").append(true);
        }

        sb.append('}');

        return sb.toString();
    }

    @NotNull
    public static StackBuilder parseString(@Nullable String string) {
        return new StackBuilder(string);
    }

}
