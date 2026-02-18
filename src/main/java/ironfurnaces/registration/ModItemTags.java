package ironfurnaces.registration;

import com.tterrag.registrate.providers.ProviderType;
import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class ModItemTags {
    public static final TagKey<Item> PLAYER_WORKSTATIONS_CRAFTING_TABLES = Util.make(() -> {
        TagKey<Item> itemTagKey = bindC(
                "player_workstations/furnaces"
        );
        REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, x -> x.addTag(itemTagKey).add(Items.FURNACE));
        return itemTagKey;
    });

    public static final TagKey<Item> FURNACE_ALLTHEMODIUM = bindForge(
            "furnaces/allthemodium"
    );
    public static final TagKey<Item> FURNACE_COPPER = bindForge(
            "furnaces/copper"
    );
    public static final TagKey<Item> FURNACE_CRYSTAL = bindForge(
            "furnaces/crystal"
    );
    public static final TagKey<Item> FURNACE_DIAMOND = bindForge(
            "furnaces/diamond"
    );
    public static final TagKey<Item> FURNACE_EMERALD = bindForge(
            "furnaces/emerald"
    );
    public static final TagKey<Item> FURNACE_GOLD = bindForge(
            "furnaces/gold"
    );
    public static final TagKey<Item> FURNACE_IRON = bindForge(
            "furnaces/iron"
    );
    public static final TagKey<Item> FURNACE_NETHERITE = bindForge(
            "furnaces/netherite"
    );
    public static final TagKey<Item> FURNACE_OBSIDIAN = bindForge(
            "furnaces/obsidian"
    );
    public static final TagKey<Item> FURNACE_RAINBOW = bindForge(
            "furnaces/rainbow"
    );
    public static final TagKey<Item> FURNACE_SILVER = bindForge(
            "furnaces/silver"
    );
    public static final TagKey<Item> FURNACE_UNOBTAINIUM = bindForge(
            "furnaces/unobtainium"
    );
    public static final TagKey<Item> FURNACE_VIBRANIUM = bindForge(
            "furnaces/vibranium"
    );

    public static final TagKey<Item> C_FURNACE_ALLTHEMODIUM = bindC(
            "furnaces/allthemodium"
    );
    public static final TagKey<Item> C_FURNACE_COPPER = bindC(
            "furnaces/copper"
    );
    public static final TagKey<Item> C_FURNACE_CRYSTAL = bindC(
            "furnaces/crystal"
    );
    public static final TagKey<Item> C_FURNACE_DIAMOND = bindC(
            "furnaces/diamond"
    );
    public static final TagKey<Item> C_FURNACE_EMERALD = bindC(
            "furnaces/emerald"
    );
    public static final TagKey<Item> C_FURNACE_GOLD = bindC(
            "furnaces/gold"
    );
    public static final TagKey<Item> C_FURNACE_IRON = bindC(
            "furnaces/iron"
    );
    public static final TagKey<Item> C_FURNACE_NETHERITE = bindC(
            "furnaces/netherite"
    );
    public static final TagKey<Item> C_FURNACE_OBSIDIAN = bindC(
            "furnaces/obsidian"
    );
    public static final TagKey<Item> C_FURNACE_RAINBOW = bindC(
            "furnaces/rainbow"
    );
    public static final TagKey<Item> C_FURNACE_SILVER = bindC(
            "furnaces/silver"
    );
    public static final TagKey<Item> C_FURNACE_UNOBTAINIUM = bindC(
            "furnaces/unobtainium"
    );
    public static final TagKey<Item> C_FURNACE_VIBRANIUM = bindC(
            "furnaces/vibranium"
    );

    public static final TagKey<Item> NETHERITE_UPGRADE = Util.make(() -> {
        TagKey<Item> itemTagKey = TagKey.create(Registries.ITEM, IronFurnaces.id("netherite_upgrade_crafting"));
        REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, registrateItemTagsProvider -> {
            registrateItemTagsProvider.addTag(itemTagKey)
                    .add(Items.NETHERITE_INGOT, Items.NETHERITE_SCRAP);
        });
        return itemTagKey;
    });



    public static final TagKey<Item> SILVER = bindForge("ingots/silver");

    protected static <T> TagKey<T> of(ResourceKey<? extends Registry<T>> registry, ResourceLocation location){
        return TagKey.create(registry, location);
    }

    protected static TagKey<Item> bindC(String id) {
        return of(Registries.ITEM, new ResourceLocation("c", id));
    }

    protected static TagKey<Item> bindForge(String id) {
        return of(Registries.ITEM, new ResourceLocation("forge", id));
    }

    protected static TagKey<Item> bind(String id) {
        return of(Registries.ITEM, IronFurnaces.id(id));
    }
}
