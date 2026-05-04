package ironfurnaces.registration;

import com.clefal.nirvana_lib.utils.ResourceLocationUtils;
import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {
    public static final TagKey<Block> PLAYER_WORKSTATIONS_FURNACE = bindC(
            "player_workstations/furnaces"
    );
    public static final TagKey<Block> FURNACE_ALLTHEMODIUM = bindForge(
            "furnaces/allthemodium"
    );
    public static final TagKey<Block> FURNACE_COPPER = bindForge(
            "furnaces/copper"
    );
    public static final TagKey<Block> FURNACE_CRYSTAL = bindForge(
            "furnaces/crystal"
    );
    public static final TagKey<Block> FURNACE_DIAMOND = bindForge(
            "furnaces/diamond"
    );
    public static final TagKey<Block> FURNACE_EMERALD = bindForge(
            "furnaces/emerald"
    );
    public static final TagKey<Block> FURNACE_GOLD = bindForge(
            "furnaces/gold"
    );
    public static final TagKey<Block> FURNACE_IRON = bindForge(
            "furnaces/iron"
    );
    public static final TagKey<Block> FURNACE_NETHERITE = bindForge(
            "furnaces/netherite"
    );
    public static final TagKey<Block> FURNACE_OBSIDIAN = bindForge(
            "furnaces/obsidian"
    );
    public static final TagKey<Block> FURNACE_RAINBOW = bindForge(
            "furnaces/rainbow"
    );
    public static final TagKey<Block> FURNACE_SILVER = bindForge(
            "furnaces/silver"
    );
    public static final TagKey<Block> FURNACE_UNOBTAINIUM = bindForge(
            "furnaces/unobtainium"
    );
    public static final TagKey<Block> FURNACE_VIBRANIUM = bindForge(
            "furnaces/vibranium"
    );

    public static final TagKey<Block> C_FURNACE_ALLTHEMODIUM = bindC(
            "furnaces/allthemodium"
    );
    public static final TagKey<Block> C_FURNACE_COPPER = bindC(
            "furnaces/copper"
    );
    public static final TagKey<Block> C_FURNACE_CRYSTAL = bindC(
            "furnaces/crystal"
    );
    public static final TagKey<Block> C_FURNACE_DIAMOND = bindC(
            "furnaces/diamond"
    );
    public static final TagKey<Block> C_FURNACE_EMERALD = bindC(
            "furnaces/emerald"
    );
    public static final TagKey<Block> C_FURNACE_GOLD = bindC(
            "furnaces/gold"
    );
    public static final TagKey<Block> C_FURNACE_IRON = bindC(
            "furnaces/iron"
    );
    public static final TagKey<Block> C_FURNACE_NETHERITE = bindC(
            "furnaces/netherite"
    );
    public static final TagKey<Block> C_FURNACE_OBSIDIAN = bindC(
            "furnaces/obsidian"
    );
    public static final TagKey<Block> C_FURNACE_RAINBOW = bindC(
            "furnaces/rainbow"
    );
    public static final TagKey<Block> C_FURNACE_SILVER = bindC(
            "furnaces/silver"
    );
    public static final TagKey<Block> C_FURNACE_UNOBTAINIUM = bindC(
            "furnaces/unobtainium"
    );
    public static final TagKey<Block> C_FURNACE_VIBRANIUM = bindC(
            "furnaces/vibranium"
    );



    protected static <T> TagKey<T> of(ResourceKey<? extends Registry<T>> registry, Identifier location){
        return TagKey.create(registry, location);
    }

    protected static TagKey<Block> bindC(String id) {
        return of(Registries.BLOCK, ResourceLocationUtils.make("c", id));
    }

    protected static TagKey<Block> bindForge(String id) {
        //? 1.20.1 {
        /*return of(Registries.BLOCK, ResourceLocationUtils.make("forge", id));
        *///? } else {
        return bindC(id);
        //?}

    }

    protected static TagKey<Block> bind(String id) {
        return of(Registries.BLOCK, IronFurnaces.id(id));
    }
}
