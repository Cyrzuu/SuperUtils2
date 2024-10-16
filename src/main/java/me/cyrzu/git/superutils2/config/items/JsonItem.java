package me.cyrzu.git.superutils2.config.items;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import me.cyrzu.git.superutils2.helper.Version;
import me.cyrzu.git.superutils2.item.Rarity;
import me.cyrzu.git.superutils2.item.StackBuilder;
import me.cyrzu.git.superutils2.json.JsonReader;
import me.cyrzu.git.superutils2.json.JsonWriter;
import me.cyrzu.git.superutils2.utils.EnumUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@Getter
public class JsonItem {

    @NotNull
    private final StackBuilder stackBuilder;

    @NotNull
    private final ItemStack itemStack;

    public JsonItem(@NotNull JsonObject jsonObject) {
        this(JsonReader.parseObject(jsonObject));
    }

    public JsonItem(@NotNull JsonReader reader) {
        String headTexture = reader.getFirstString(List.of("head_texture", "headtexture", "head", "skull", "texture"));
        Material material = headTexture == null ? reader.getFirstEnum(List.of("type", "t", "material", "m", "item", "i"), Material.class, Material.STONE) : Material.PLAYER_HEAD;

        StackBuilder stackBuilder = new StackBuilder(material);

        if(headTexture != null) {
            stackBuilder.setHeadTexture(headTexture);
        }

        stackBuilder.setAmount(reader.getFirstInt(List.of("amount", "a", "count", "c"), 1));
        stackBuilder.setName(reader.getFirstString(List.of("displayname", "dn", "name", "n")));
        stackBuilder.setLore(reader.getFirstListString(List.of("lore", "l")).toArray(String[]::new));
        reader.getAndRun(List.of("custom_model_data", "custommodeldata", "model", "cmd"), Integer.class, stackBuilder::setCustomModelData);
        reader.getAndRun(List.of("damage", "dmg"), Integer.class, stackBuilder::setDamage);
        reader.getAndRun(List.of("unbreakable", "unbr", "unb", "u"), Boolean.class, stackBuilder::setUnbreakable);


        reader.getAndRun(List.of("all_flags", "allflags"), Boolean.class, stackBuilder::allFlags);
        ItemFlag[] flags = reader.getListString("flags").stream()
                .map(value -> EnumUtils.getEnum(value, ItemFlag.class))
                .filter(Objects::nonNull).toArray(ItemFlag[]::new);
        stackBuilder.setFlags(flags);

        reader.getReader(List.of("enchantments", "enchants", "enchant", "ench", "e"), enchants -> {
            for (String enchant : enchants.keySet()) {
                stackBuilder.addEnchantment(enchant, enchants.getInt(enchant, 1));
            }
        });

        if(Version.isAtLeast(Version.v1_20_R4)) {
            stackBuilder.setRarity(reader.getFirstEnum(List.of("rarity", "rare"), Rarity.class));
            reader.getAndRun(List.of("hide_tool_tip", "hidetooltip", "hidetip", "htt"), Boolean.class, stackBuilder::setHideToolTip);
            reader.getAndRun(List.of("max_stack_size", "maxstacksize", "maxstack", "mss"), Integer.class, stackBuilder::setMaxStackSize);
        }

        this.stackBuilder = stackBuilder;
        this.itemStack = stackBuilder.build();
    }

    public static JsonWriter getExample() {
        return new JsonWriter()
                .set("example1.type", "stone")
                .set("example1.amount", 1)
                .set("example1.name", "&7Displayed Name")
                .set("example1.lore", JsonWriter.createArray(List.of("&3First line", "&#fff000Second line"), JsonArray::add))
                .set("example1.custom_model_data", 1)
                .set("example1.damage", 20)
                .set("example1.unbreakable", true)
                .set("example1.flags", JsonWriter.createArray(List.of("HIDE_ENCHANTS", "HIDE_UNBREAKABLE"), JsonArray::add))
                .set("example1.enchants", new JsonWriter().set("sharpness", 6).set("unbreaking", 3))

                .set("example2.type", "player_head")
                .set("example2.head_texture", "95f7fa5de933e26bdc36800099f752f65bce135a003cb050b1537b75026f816c")
                .set("example2.max_stack_size", 1)
                .set("example2.rarity", "EPIC")
                .set("example2.hide_tool_tip", true)
                .set("example2.all_flags", true);

    }

}
