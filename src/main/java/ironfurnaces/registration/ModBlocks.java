package ironfurnaces.registration;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import ironfurnaces.blocks.BlockWirelessEnergyHeater;
import ironfurnaces.blocks.furnaces.BlockItemHeater;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.BlockWirelessEnergyHeaterTile;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;
import static ironfurnaces.registration.ModItemTags.bindForge;

public class ModBlocks {

    public static final BlockEntry<BlockWirelessEnergyHeater> HEATER = REGISTRATE
            .block(BlockWirelessEnergyHeater.HEATER, BlockWirelessEnergyHeater::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .lang("Wireless Heat Transmitter")
            .item(BlockItemHeater::new)
            .build()
            .<BlockWirelessEnergyHeaterTile>blockEntity(BlockWirelessEnergyHeaterTile::new)
            .build()
            .addMiscData(ProviderType.LANG, x -> x.add("container.ironfurnaces.wireless_energy_heater", "Wireless Heater"))
            .recipe((ctx, provider) -> {
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                        .pattern("#F#")
                        .pattern("#X#")
                        .pattern("#C#")
                        .define('#', bindForge("stone"))
                        .define('C', ModItemTags.PLAYER_WORKSTATIONS_FURNACE)
                        .define('F', Items.COMPARATOR)
                        .define('X', bindForge("storage_blocks/redstone"))
                        .unlockedBy("has_comparator", RegistrateRecipeProvider.has(Items.COMPARATOR))
                        .save(provider, IronFurnaces.id(ctx.getName()));
            })
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .register();

    public static void register() {

    }

    public static BlockEntityType<? extends BlockIronFurnaceTileBase> asBlockEntityType(BlockEntry<?> entry) {
        return (BlockEntityType<? extends BlockIronFurnaceTileBase>) entry.getSibling(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES).get();
    }

    public static <T extends BlockEntity> BlockEntityType<T> asGenericBlockEntityType(BlockEntry<?> entry) {
        return (BlockEntityType<T>) entry.getSibling(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES).get();
    }
}
