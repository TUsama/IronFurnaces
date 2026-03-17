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
                                                                        .copy(FurnaceSettingsV2.NBT_KEY, "BlockEntityTag.settingsV2")
                                                                        .copy(FurnacePattern.NBT_KEY, "BlockEntityTag.pattern")
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
            /*.blockstate((ctx, prov) ->
                    prov.getVariantBuilder(ctx.get())
                            .forAllStates(state -> {

                                Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
                                boolean lit = state.getValue(BlockStateProperties.LIT);

                                // 新属性
                                JovialState jovialState = state.getValue(ModBlockState.JOVIAL_STATE);
                                FurnaceMode mode = state.getValue(ModBlockState.FURNACE_MODE);
                                FurnacePattern pattern = state.getValue(ModBlockState.FURNACE_PATTERN);

                                // 旧逻辑需要的“序号”，你已保证 ordinal 对齐旧 int
                                int jovial = jovialState.ordinal();
                                int type = mode.ordinal();

                                // 1) 关键：pattern 的 path 直接等同旧 block 注册名（你已确认）
                                //    因此默认 prefix 就是旧资源前缀
                                String prefix = pattern.id().getPath();

                                // 2) jovial 完全覆盖（你已确认资源未改动，仍是 spooky_furnace / xmas_furnace）
                                prefix = switch (jovial) {
                                    case 1 -> "spooky_furnace";
                                    case 2 -> "xmas_furnace";
                                    default -> prefix;
                                };

                                String litPart = lit ? "_on" : "";

                                String typePart = switch (type) {
                                    case 1 -> "_smoke";
                                    case 2 -> "_blast";
                                    default -> "";
                                };

                                String modelName = "block/" + prefix + litPart + typePart;

                                int yRot = switch (facing) {
                                    case SOUTH -> 180;
                                    case WEST -> 270;
                                    case EAST -> 90;
                                    default -> 0;
                                };

                                ConfiguredModel.Builder<?> builder = ConfiguredModel.builder()
                                        .rotationY(yRot);

                                if (type == 1) {
                                    builder.modelFile(prov.models()
                                            .orientableWithBottom(modelName,
                                                    IronFurnaces.id("block/" + prefix + "_side"),
                                                    IronFurnaces.id("block/" + prefix + "_front" + litPart + typePart),
                                                    IronFurnaces.id("block/" + prefix + "_side"),
                                                    IronFurnaces.id("block/" + prefix + "_top_smoke")
                                            )
                                    );
                                } else {
                                    builder.modelFile(prov.models()
                                            .orientable(modelName,
                                                    IronFurnaces.id("block/" + prefix + "_side"),
                                                    IronFurnaces.id("block/" + prefix + "_front" + litPart + typePart),
                                                    IronFurnaces.id("block/" + prefix + "_side")
                                            )
                                    );
                                }

                                return builder.build();
                            })
            )*/
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item(FurnacePatternHolderItem::new)
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
