package ironfurnaces.registration;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import ironfurnaces.blocks.BlockWirelessEnergyHeater;
import ironfurnaces.blocks.furnaces.BlockItemHeater;
import ironfurnaces.blocks.furnaces.new_furnace.FurnacePatternHolderBlock;
import ironfurnaces.blocks.furnaces.new_furnace.FurnacePatternHolderItem;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.BlockWirelessEnergyHeaterTile;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.client.model.generators.ModelFile;
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

    public static final BlockEntry<FurnacePatternHolderBlock> PATTERN_HOLDER = REGISTRATE
            .block(FurnacePatternHolderBlock.ID, FurnacePatternHolderBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.noOcclusion().requiresCorrectToolForDrops())
            .lang("Furnace")
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.PLAYER_WORKSTATIONS_FURNACE)
            .loot((ctx, furnace) -> {
                LootTable.Builder builder = LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0f))
                                        .add(
                                                LootItem.lootTableItem(furnace)

                                                        .apply(
                                                                CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                                                        .copy(FurnaceSettingsV2.NBT_KEY, "BlockEntityTag." + FurnaceSettingsV2.NBT_KEY)
                                                                        .copy(FurnacePattern.NBT_KEY, "BlockEntityTag." + FurnacePattern.NBT_KEY)
                                                        )
                                                        .apply(
                                                                CopyBlockState.copyState(furnace)
                                                                        .copy(ModBlockState.JOVIAL_STATE)
                                                        )
                                                        .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                                        )
                        );

                ctx.add(furnace, builder);
            })
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item(FurnacePatternHolderItem::new)
            .model((ctx, prov) -> prov.getBuilder(ctx.getName())
                    .parent(new ModelFile.UncheckedModelFile("minecraft:builtin/entity")))
            .tag(ModItemTags.PLAYER_WORKSTATIONS_FURNACE)
            .build()
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
