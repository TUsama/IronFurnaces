package ironfurnaces.registration;

import com.google.common.collect.Lists;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import ironfurnaces.Config;
import ironfurnaces.blocks.furnaces.*;
import ironfurnaces.blocks.furnaces.other.BlockAllthemodiumFurnace;
import ironfurnaces.blocks.furnaces.other.BlockUnobtainiumFurnace;
import ironfurnaces.blocks.furnaces.other.BlockVibraniumFurnace;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.registration.util.LangUtils;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import ironfurnaces.tileentity.furnaces.LegacyUnifiedTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.client.model.generators.ConfiguredModel;

import java.util.List;
import java.util.function.Consumer;

import static ironfurnaces.blocks.furnaces.BlockIronFurnaceBase.JOVIAL;
import static ironfurnaces.blocks.furnaces.BlockIronFurnaceBase.TYPE;
import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class LegacyFurnaceBlocks {


    public static final BlockEntry<? extends Block> IRON_FURNACE = furnace(BlockIronFurnace.ID, BlockIronFurnace::new, () -> Blocks.IRON_BLOCK)
            .recipe((ctx, provider) -> {
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("###")
                        .define('#', bindForge("ingots/iron"))
                        .define('X', bindC("player_workstations/furnaces"))
                        .unlockedBy("has_iron", RegistrateRecipeProvider.has(Items.IRON_INGOT))
                        .save(provider, BlockIronFurnace.ID);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                        .pattern("YYY")
                        .pattern("#X#")
                        .pattern("YYY")
                        .define('#', bindForge("glass"))
                        .define('X', bindForge("furnaces/copper"))
                        .define('Y', bindForge("ingots/iron"))
                        .unlockedBy("has_iron_ingot",
                                RegistrateRecipeProvider.has(Items.IRON_INGOT))
                        .save(provider, BlockIronFurnace.ID + "2");


            })
            .tag(ModBlockTags.FURNACE_IRON, ModBlockTags.C_FURNACE_IRON)
            .item()
            .tag(ModItemTags.FURNACE_IRON, ModItemTags.C_FURNACE_IRON)
            .build()
            .blockEntity((type, pos, state) -> new LegacyUnifiedTileEntity(type, pos, state, Config.ironFurnaceSpeed, Config.ironFurnaceTier, Config.ironFurnaceGeneration, BlockIronFurnace.ID))
            .build()
            .register();

    public static final BlockEntry<BlockGoldFurnace> GOLD_FURNACE =
            furnace(BlockGoldFurnace.ID, BlockGoldFurnace::new, () -> Blocks.GOLD_BLOCK)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("#Y#")
                                .define('#', bindForge("ingots/gold"))
                                .define('X', bindForge("furnaces/iron"))
                                .define('Y', bindForge("storage_blocks/gold"))
                                .unlockedBy("has_gold_ingot",
                                        RegistrateRecipeProvider.has(Items.GOLD_INGOT))
                                .save(provider, BlockGoldFurnace.ID);


                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("#Y#")
                                .define('#', bindForge("glass"))
                                .define('X', bindForge("furnaces/silver"))
                                .define('Y', bindForge("storage_blocks/gold"))
                                .unlockedBy("has_gold_block",
                                        RegistrateRecipeProvider.has(Items.GOLD_BLOCK))
                                .save(provider, BlockGoldFurnace.ID + "2");

                    })
                    .tag(ModBlockTags.FURNACE_GOLD, ModBlockTags.C_FURNACE_GOLD)
                    .item()
                    .tag(ModItemTags.FURNACE_GOLD, ModItemTags.C_FURNACE_GOLD)
                    .build()
                    .blockEntity((type, pos, state) -> new LegacyUnifiedTileEntity(type, pos, state, Config.goldFurnaceSpeed, Config.goldFurnaceTier, Config.goldFurnaceGeneration, BlockGoldFurnace.ID))
                    .build()
                    .register();


    public static final BlockEntry<BlockDiamondFurnace> DIAMOND_FURNACE =
            furnace(BlockDiamondFurnace.ID, BlockDiamondFurnace::new, () -> Blocks.DIAMOND_BLOCK)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("GXG")
                                .pattern("###")
                                .define('#', bindForge("gems/diamond"))
                                .define('X', bindForge("furnaces/gold"))
                                .define('G', bindForge("glass"))
                                .unlockedBy("has_diamond",
                                        RegistrateRecipeProvider.has(Items.DIAMOND))
                                .save(provider, BlockDiamondFurnace.ID);

                    })
                    .tag(ModBlockTags.FURNACE_DIAMOND, ModBlockTags.C_FURNACE_DIAMOND)
                    .item()
                    .tag(ModItemTags.FURNACE_DIAMOND, ModItemTags.C_FURNACE_DIAMOND)
                    .build()
                    .blockEntity((type, pos, state) -> new LegacyUnifiedTileEntity(type, pos, state, Config.diamondFurnaceSpeed, Config.diamondFurnaceTier, Config.diamondFurnaceGeneration, BlockDiamondFurnace.ID))
                    .build()
                    .register();


    public static final BlockEntry<BlockEmeraldFurnace> EMERALD_FURNACE =
            furnace(BlockEmeraldFurnace.ID, BlockEmeraldFurnace::new, () -> Blocks.EMERALD_BLOCK).recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', bindForge("gems/emerald"))
                                .define('X', bindForge("furnaces/diamond"))
                                .unlockedBy("has_emerald",
                                        RegistrateRecipeProvider.has(Items.EMERALD))
                                .save(provider, BlockEmeraldFurnace.ID);


                    })
                    .tag(ModBlockTags.FURNACE_EMERALD, ModBlockTags.C_FURNACE_EMERALD)
                    .item()
                    .tag(ModItemTags.FURNACE_EMERALD, ModItemTags.C_FURNACE_EMERALD)
                    .build()
                    .blockEntity((type, pos, state) -> new LegacyUnifiedTileEntity(type, pos, state, Config.emeraldFurnaceSpeed, Config.emeraldFurnaceTier, Config.emeraldFurnaceGeneration, BlockEmeraldFurnace.ID))
                    .build()
                    .register();


    public static final BlockEntry<BlockCopperFurnace> COPPER_FURNACE =
            furnace(BlockCopperFurnace.ID, BlockCopperFurnace::new, () -> Blocks.COPPER_BLOCK).recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', bindForge("ingots/copper"))
                                .define('X', Items.FURNACE)
                                .unlockedBy("has_copper_ingot",
                                        RegistrateRecipeProvider.has(Items.COPPER_INGOT))
                                .save(provider, BlockCopperFurnace.ID);
                    })
                    .tag(ModBlockTags.FURNACE_COPPER, ModBlockTags.C_FURNACE_COPPER)
                    .item()
                    .tag(ModItemTags.FURNACE_COPPER, ModItemTags.C_FURNACE_COPPER)
                    .build()
                    .blockEntity((type, pos, state) -> new LegacyUnifiedTileEntity(type, pos, state, Config.copperFurnaceSpeed, Config.copperFurnaceTier, Config.copperFurnaceGeneration, BlockCopperFurnace.ID))
                    .build()
                    .register();


    public static final BlockEntry<BlockSilverFurnace> SILVER_FURNACE =
            furnace(BlockSilverFurnace.ID, BlockSilverFurnace::new, () -> Blocks.IRON_BLOCK).recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("#G#")
                                .pattern("GXG")
                                .pattern("#G#")
                                .define('#', bindForge("ingots/silver"))
                                .define('X', bindForge("furnaces/iron"))
                                .define('G', bindForge("glass"))
                                .unlockedBy("has_silver_ingot",
                                        RegistrateRecipeProvider.has(bindForge("ingots/silver")))
                                .save(provider, BlockSilverFurnace.ID);


                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', bindForge("ingots/silver"))
                                .define('X', bindForge("furnaces/copper"))
                                .unlockedBy("has_silver_ingot",
                                        RegistrateRecipeProvider.has(bindForge("ingots/silver")))
                                .save(provider,
                                        BlockSilverFurnace.ID + "2");


                    })
                    .tag(ModBlockTags.FURNACE_SILVER, ModBlockTags.C_FURNACE_SILVER)
                    .item()
                    .tag(ModItemTags.FURNACE_SILVER, ModItemTags.C_FURNACE_SILVER)
                    .build()
                    .blockEntity((type, pos, state) -> new LegacyUnifiedTileEntity(type, pos, state, Config.silverFurnaceSpeed, Config.silverFurnaceTier, Config.silverFurnaceGeneration, BlockSilverFurnace.ID))
                    .build()
                    .register();

    public static final BlockEntry<BlockMillionFurnace> MILLION_FURNACE =
            furnace(BlockMillionFurnace.ID,
                    BlockMillionFurnace::new,
                    () -> Blocks.IRON_BLOCK)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#C#")
                                .pattern("###")
                                .define('C', ModItems.RAINBOW_CORE.get())
                                .define('#', ModItems.RAINBOW_PLATING.get())
                                .unlockedBy("has_rainbow_core",
                                        RegistrateRecipeProvider.has(ModItems.RAINBOW_CORE.get()))
                                .save(provider,
                                        BlockMillionFurnace.ID);

                    })
                    .tag(ModBlockTags.FURNACE_RAINBOW, ModBlockTags.C_FURNACE_RAINBOW)
                    .item()
                    .tag(ModItemTags.FURNACE_RAINBOW, ModItemTags.C_FURNACE_RAINBOW)
                    .build()
                    .blockEntity((type, pos, state) -> new LegacyUnifiedTileEntity(type, pos, state, Config.millionFurnaceSpeed, Config.millionFurnaceTier, Config.millionFurnaceGeneration, BlockMillionFurnace.ID) {
                                public List<BlockIronFurnaceTileBase> furnaces = Lists.newArrayList();
                                public List<BlockPos> furnaces_to_load = Lists.newArrayList();

                                @Override
                                public void saveAdditional(CompoundTag tag) {
                                    super.saveAdditional(tag);
                                    CompoundTag furnaces = new CompoundTag();
                                    for (int i = 0; i < this.furnaces.size(); i++) {
                                        CompoundTag tag2 = new CompoundTag();
                                        tag2.putInt("X", this.furnaces.get(i).getBlockPos().getX());
                                        tag2.putInt("Y", this.furnaces.get(i).getBlockPos().getY());
                                        tag2.putInt("Z", this.furnaces.get(i).getBlockPos().getZ());
                                        furnaces.put("Furnace" + i, tag2);
                                    }
                                    tag.put("Furnaces", furnaces);

                                }

                                @Override
                                public void load(CompoundTag tag) {
                                    super.load(tag);
                                    CompoundTag furnaces = tag.getCompound("Furnaces");
                                    for (int i = 0; i < furnaces.size(); i++) {
                                        CompoundTag furnace = furnaces.getCompound("Furnace" + i);
                                        furnaces_to_load.add(new BlockPos(furnace.getInt("X"), furnace.getInt("Y"), furnace.getInt("Z")));
                                    }
                                }

                                @Override
                                public int getMaxSmeltItemNumberOnSingleOp() {
                                    return 64;
                                }
                            }
                    )
                    .build()
                    .register();


    public static final BlockEntry<BlockAllthemodiumFurnace> ALLTHEMODIUM_FURNACE =
            furnace(BlockAllthemodiumFurnace.ID, BlockAllthemodiumFurnace::new, () -> Blocks.GOLD_BLOCK)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("B#B")
                                .pattern("#X#")
                                .pattern("B#B")
                                .define('#', bindForge("ingots/allthemodium"))
                                .define('X', bindForge("furnaces/netherite"))
                                .define('B', bindForge("storage_blocks/allthemodium"))
                                .unlockedBy("has_allthemodium_ingot",
                                        RegistrateRecipeProvider.has(bindForge("ingots/allthemodium")))
                                .save(provider, BlockAllthemodiumFurnace.ID);


                    })
                    .tag(ModBlockTags.FURNACE_ALLTHEMODIUM, ModBlockTags.C_FURNACE_ALLTHEMODIUM)
                    .item()
                    .tag(ModItemTags.FURNACE_ALLTHEMODIUM, ModItemTags.C_FURNACE_ALLTHEMODIUM)
                    .build()
                    .blockEntity((type, pos, state) -> new LegacyUnifiedTileEntity(type, pos, state, Config.allthemodiumFurnaceSpeed, Config.allthemodiumFurnaceTier, Config.allthemodiumGeneration, BlockAllthemodiumFurnace.ID){
                        @Override
                        public int getMaxSmeltItemNumberOnSingleOp() {
                            return Config.allthemodiumFurnaceSmeltMult.get();
                        }
                    })
                    .build()
                    .register();


    public static final BlockEntry<BlockVibraniumFurnace> VIBRANIUM_FURNACE =
            furnace(BlockVibraniumFurnace.ID, BlockVibraniumFurnace::new, () -> Blocks.DIAMOND_BLOCK)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("B#B")
                                .pattern("#X#")
                                .pattern("B#B")
                                .define('#', bindForge("ingots/vibranium"))
                                .define('X', bindForge("furnaces/allthemodium"))
                                .define('B', bindForge("storage_blocks/vibranium"))
                                .unlockedBy("has_vibranium_ingot",
                                        RegistrateRecipeProvider.has(bindForge("ingots/vibranium")))
                                .save(provider, BlockVibraniumFurnace.ID);


                    })
                    .tag(ModBlockTags.FURNACE_VIBRANIUM, ModBlockTags.C_FURNACE_VIBRANIUM)
                    .item()
                    .tag(ModItemTags.FURNACE_VIBRANIUM, ModItemTags.C_FURNACE_VIBRANIUM)
                    .build()
                    .blockEntity((type, pos, state) -> new LegacyUnifiedTileEntity(type, pos, state, Config.vibraniumFurnaceSpeed, Config.vibraniumFurnaceTier, Config.vibraniumGeneration, BlockVibraniumFurnace.ID){
                        @Override
                        public int getMaxSmeltItemNumberOnSingleOp() {
                            return Config.vibraniumFurnaceSmeltMult.get();
                        }
                    })
                    .build()
                    .register();


    public static final BlockEntry<BlockUnobtainiumFurnace> UNOBTAINIUM_FURNACE =
            furnace(BlockUnobtainiumFurnace.ID, BlockUnobtainiumFurnace::new, () -> Blocks.NETHERITE_BLOCK)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("B#B")
                                .pattern("#X#")
                                .pattern("B#B")
                                .define('#', bindForge("ingots/unobtainium"))
                                .define('X', bindForge("furnaces/vibranium"))
                                .define('B', bindForge("storage_blocks/unobtainium"))
                                .unlockedBy("has_unobtainium_ingot",
                                        RegistrateRecipeProvider.has(bindForge("ingots/unobtainium")))
                                .save(provider, BlockUnobtainiumFurnace.ID);


                    })
                    .tag(ModBlockTags.FURNACE_UNOBTAINIUM, ModBlockTags.C_FURNACE_UNOBTAINIUM)
                    .item()
                    .tag(ModItemTags.FURNACE_UNOBTAINIUM, ModItemTags.C_FURNACE_UNOBTAINIUM)
                    .build()
                    .blockEntity((type, pos, state) -> new LegacyUnifiedTileEntity(type, pos, state, Config.unobtainiumFurnaceSpeed, Config.unobtainiumFurnaceTier, Config.unobtainiumGeneration, BlockUnobtainiumFurnace.ID){
                        @Override
                        public int getMaxSmeltItemNumberOnSingleOp() {
                            return Config.unobtainiumFurnaceSmeltMult.get();
                        }
                    })
                    .build()
                    .register();

    public static final BlockEntry<BlockObsidianFurnace> OBSIDIAN_FURNACE =
            furnaceWithProps(
                    BlockObsidianFurnace.ID,
                    BlockObsidianFurnace::new,
                    () -> Blocks.OBSIDIAN,
                    p -> p.strength(40.0F, 6000.0F)
            )
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("#Y#")
                                .pattern("YXY")
                                .pattern("#Y#")
                                .define('#', bindForge("obsidian"))
                                .define('X', bindForge("furnaces/emerald"))
                                .define('Y', bindForge("rods/blaze"))
                                .unlockedBy("has_obsidian",
                                        RegistrateRecipeProvider.has(Blocks.OBSIDIAN))
                                .save(provider, BlockObsidianFurnace.ID);

                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("#Y#")
                                .pattern("YXY")
                                .pattern("#Y#")
                                .define('#', bindForge("obsidian"))
                                .define('X', bindForge("furnaces/crystal"))
                                .define('Y', bindForge("rods/blaze"))
                                .unlockedBy("has_obsidian",
                                        RegistrateRecipeProvider.has(Blocks.OBSIDIAN))
                                .save(provider, BlockObsidianFurnace.ID + "2");

                    })
                    .tag(ModBlockTags.FURNACE_OBSIDIAN, ModBlockTags.C_FURNACE_OBSIDIAN)
                    .item()
                    .tag(ModItemTags.FURNACE_OBSIDIAN, ModItemTags.C_FURNACE_OBSIDIAN)
                    .build()
                    .blockEntity((type, pos, state) -> new LegacyUnifiedTileEntity(type, pos, state, Config.obsidianFurnaceSpeed, Config.obsidianFurnaceTier, Config.obsidianFurnaceGeneration, BlockObsidianFurnace.ID))
                    .build()
                    .register();

    public static final BlockEntry<BlockCrystalFurnace> CRYSTAL_FURNACE =
            furnaceWithProps(
                    BlockCrystalFurnace.ID,
                    BlockCrystalFurnace::new,
                    () -> Blocks.PRISMARINE,
                    p -> p
                            .noOcclusion()
                            .isValidSpawn((a, b, c, d) -> false)
                            .isSuffocating((x, y, z) -> false)
                            .isViewBlocking((x, y, z) -> false)
            )
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("#E#")
                                .define('#', bindForge("glass"))
                                .define('X', bindForge("furnaces/diamond"))
                                .define('E', Items.ENDER_EYE)
                                .unlockedBy("has_diamond_furnace",
                                        RegistrateRecipeProvider.has(Items.DIAMOND))
                                .save(provider, BlockCrystalFurnace.ID);


                    })
                    .tag(ModBlockTags.FURNACE_CRYSTAL, ModBlockTags.C_FURNACE_CRYSTAL)
                    .item()
                    .tag(ModItemTags.FURNACE_CRYSTAL, ModItemTags.C_FURNACE_CRYSTAL)
                    .build()
                    .blockEntity((type, pos, state) -> new LegacyUnifiedTileEntity(type, pos, state, Config.crystalFurnaceSpeed, Config.crystalFurnaceTier, Config.crystalFurnaceGeneration, BlockCrystalFurnace.ID))
                    .build()
                    .register();

    public static final BlockEntry<BlockNetheriteFurnace> NETHERITE_FURNACE =
            furnaceWithProps(
                    BlockNetheriteFurnace.ID,
                    BlockNetheriteFurnace::new,
                    () -> Blocks.NETHERITE_BLOCK,
                    p -> p.strength(40.0F, 6000.0F)
            )
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("N#N")
                                .pattern("#X#")
                                .pattern("NSN")
                                .define('#', Items.MAGMA_CREAM)
                                .define('X', bindForge("furnaces/obsidian"))
                                .define('S', bind("soul_fire_base_blocks"))
                                .define('N', Items.NETHERITE_INGOT)
                                .unlockedBy("has_obsidian_furnace",
                                        RegistrateRecipeProvider.has(LegacyFurnaceBlocks.OBSIDIAN_FURNACE))
                                .save(provider, BlockNetheriteFurnace.ID);


                    })
                    .tag(ModBlockTags.FURNACE_NETHERITE, ModBlockTags.C_FURNACE_NETHERITE)
                    .item()
                    .tag(ModItemTags.FURNACE_NETHERITE, ModItemTags.C_FURNACE_NETHERITE)
                    .build()
                    .blockEntity((type, pos, state) -> new LegacyUnifiedTileEntity(type, pos, state, Config.netheriteFurnaceSpeed, Config.netheriteFurnaceTier, Config.netheriteFurnaceGeneration, BlockNetheriteFurnace.ID))
                    .build()
                    .register();


    private static <T extends Block> BlockBuilder<T, Registrate> furnace(
            String name,
            NonNullFunction<BlockBehaviour.Properties, T> factory,
            NonNullSupplier<Block> baseBlock
    ) {
        return REGISTRATE
                .block(name, factory)
                .initialProperties(baseBlock)
                .blockstate((ctx, prov) ->
                        prov.getVariantBuilder(ctx.get())
                                .forAllStates(state -> {

                                    Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
                                    int jovial = state.getValue(JOVIAL);
                                    boolean lit = state.getValue(BlockStateProperties.LIT);
                                    int type = state.getValue(TYPE);

                                    String prefix = switch (jovial) {
                                        case 1 -> "spooky_furnace";
                                        case 2 -> "xmas_furnace";
                                        default -> ctx.getName();
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
                                    if (type == 1){
                                        builder.modelFile(prov.models()
                                                .orientableWithBottom(modelName,
                                                        IronFurnaces.id("block/" + prefix + "_side"),
                                                        IronFurnaces.id("block/" + prefix + "_front" + litPart + typePart),
                                                        IronFurnaces.id("block/" + prefix + "_side"),
                                                        IronFurnaces.id("block/" + prefix + "_top_smoke"))
                                        );

                                    } else {
                                        builder.modelFile(prov.models()
                                                .orientable(modelName,
                                                        IronFurnaces.id("block/" + prefix + "_side"),
                                                        IronFurnaces.id("block/" + prefix + "_front" + litPart + typePart),
                                                        IronFurnaces.id("block/" + prefix + "_side"))
                                        );
                                    }

                                    return builder.build();
                                })

                )
                .addMiscData(ProviderType.LANG, x -> {
                    x.add("container.ironfurnaces." + name, LangUtils.snakeToTitleWithSpace(name).replace("Million", "Rainbow"));
                })
                .lang(Block::getDescriptionId, LangUtils.snakeToTitleWithSpace(name).replace("Million", "Rainbow"))
                .tag(BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.PLAYER_WORKSTATIONS_CRAFTING_TABLES)
                .loot((ctx, furnace) -> {
                    LootTable.Builder builder = LootTable.lootTable()
                            .withPool(
                                    LootPool.lootPool()
                                            .setRolls(ConstantValue.exactly(1.0f))
                                            .add(LootItem.lootTableItem(furnace)
                                                    .apply(
                                                            CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                                                    .copy("Augment", "BlockEntityTag.Augment")
                                                                    .copy("Jovial", "BlockEntityTag.Jovial")
                                                                    .copy("Tag", "BlockEntityTag.Tag")
                                                    )
                                                    .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                                            )

                            );
                    ctx.add(furnace, builder);
                });
    }


    private static <T extends Block> BlockBuilder<T, Registrate> furnaceWithProps(
            String name,
            NonNullFunction<BlockBehaviour.Properties, T> factory,
            NonNullSupplier<Block> baseBlock,
            Consumer<BlockBehaviour.Properties> extraProps
    ) {
        return furnace(name, factory, baseBlock).properties(p -> {
            extraProps.accept(p);
            return p;
        });
    }


    public static void register() {

    }


    private static TagKey<Item> bindC(String id) {
        return ModBlockTags.of(Registries.ITEM, new ResourceLocation("c", id));
    }

    private static TagKey<Item> bindForge(String id) {
        return ModBlockTags.of(Registries.ITEM, new ResourceLocation("forge", id));
    }

    private static TagKey<Item> bind(String id) {
        return ModBlockTags.of(Registries.ITEM, IronFurnaces.id(id));
    }


}
