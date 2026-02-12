package ironfurnaces.registration;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import ironfurnaces.blocks.furnaces.*;
import ironfurnaces.blocks.furnaces.other.BlockAllthemodiumFurnace;
import ironfurnaces.blocks.furnaces.other.BlockUnobtainiumFurnace;
import ironfurnaces.blocks.furnaces.other.BlockVibraniumFurnace;
import ironfurnaces.init.Registration;
import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.Consumer;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class LegacyFurnaceBlocks{


    public static final BlockEntry<? extends Block> IRON_FURNACE = furnace(BlockIronFurnace.IRON_FURNACE, BlockIronFurnace::new, () -> Blocks.IRON_BLOCK)
            .recipe((ctx, provider) -> {
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("###")
                        .define('#', bindForge("ingots/iron"))
                        .define('X', bindC("player_workstations/furnaces"))
                        .unlockedBy("has_iron", RegistrateRecipeProvider.has(Items.IRON_INGOT))
                        .save(provider, BlockIronFurnace.IRON_FURNACE);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                        .pattern("YYY")
                        .pattern("#X#")
                        .pattern("YYY")
                        .define('#', bindForge("glass"))
                        .define('X', bindForge("furnaces/copper"))
                        .define('Y', bindForge("ingots/iron"))
                        .unlockedBy("has_iron_ingot",
                                RegistrateRecipeProvider.has(Items.IRON_INGOT))
                        .save(provider, BlockIronFurnace.IRON_FURNACE + "2");


            })
            .tag(ModBlockTags.FURNACE_IRON, ModBlockTags.C_FURNACE_IRON)
            .register();

    public static final BlockEntry<BlockGoldFurnace> GOLD_FURNACE =
            furnace(BlockGoldFurnace.GOLD_FURNACE, BlockGoldFurnace::new, () -> Blocks.GOLD_BLOCK)
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
                                .save(provider, BlockGoldFurnace.GOLD_FURNACE);


                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("#Y#")
                                .define('#', bindForge("glass"))
                                .define('X', bindForge("furnaces/silver"))
                                .define('Y', bindForge("storage_blocks/gold"))
                                .unlockedBy("has_gold_block",
                                        RegistrateRecipeProvider.has(Items.GOLD_BLOCK))
                                .save(provider, BlockGoldFurnace.GOLD_FURNACE + "2");

                    })
                    .tag(ModBlockTags.FURNACE_GOLD, ModBlockTags.C_FURNACE_GOLD)
                    .register();


    public static final BlockEntry<BlockDiamondFurnace> DIAMOND_FURNACE =
            furnace(BlockDiamondFurnace.DIAMOND_FURNACE, BlockDiamondFurnace::new, () -> Blocks.DIAMOND_BLOCK)
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
                                .save(provider, BlockDiamondFurnace.DIAMOND_FURNACE);

                    })
                    .tag(ModBlockTags.FURNACE_DIAMOND, ModBlockTags.C_FURNACE_DIAMOND)
                    .register();


    public static final BlockEntry<BlockEmeraldFurnace> EMERALD_FURNACE =
            furnace(BlockEmeraldFurnace.EMERALD_FURNACE, BlockEmeraldFurnace::new, () -> Blocks.EMERALD_BLOCK)            .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', bindForge("gems/emerald"))
                                .define('X', bindForge("furnaces/diamond"))
                                .unlockedBy("has_emerald",
                                        RegistrateRecipeProvider.has(Items.EMERALD))
                                .save(provider, BlockEmeraldFurnace.EMERALD_FURNACE);



                    })
                    .tag(ModBlockTags.FURNACE_EMERALD, ModBlockTags.C_FURNACE_EMERALD)
                    .register();


    public static final BlockEntry<BlockCopperFurnace> COPPER_FURNACE =
            furnace(BlockCopperFurnace.COPPER_FURNACE, BlockCopperFurnace::new, () -> Blocks.COPPER_BLOCK)            .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', bindForge("ingots/copper"))
                                .define('X', Items.FURNACE)
                                .unlockedBy("has_copper_ingot",
                                        RegistrateRecipeProvider.has(Items.COPPER_INGOT))
                                .save(provider, BlockCopperFurnace.COPPER_FURNACE);
                    })
                    .tag(ModBlockTags.FURNACE_COPPER, ModBlockTags.C_FURNACE_COPPER)
                    .register();


    public static final BlockEntry<BlockSilverFurnace> SILVER_FURNACE =
            furnace(BlockSilverFurnace.SILVER_FURNACE, BlockSilverFurnace::new, () -> Blocks.IRON_BLOCK)            .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("#G#")
                                .pattern("GXG")
                                .pattern("#G#")
                                .define('#', bindForge("ingots/silver"))
                                .define('X', bindForge("furnaces/iron"))
                                .define('G', bindForge("glass"))
                                .unlockedBy("has_silver_ingot",
                                        RegistrateRecipeProvider.has(bindForge("ingots/silver")))
                                .save(provider, BlockSilverFurnace.SILVER_FURNACE);


                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', bindForge("ingots/silver"))
                                .define('X', bindForge("furnaces/copper"))
                                .unlockedBy("has_silver_ingot",
                                        RegistrateRecipeProvider.has(bindForge("ingots/silver")))
                                .save(provider,
                                        BlockSilverFurnace.SILVER_FURNACE + "2");



                    })
                    .tag(ModBlockTags.FURNACE_SILVER, ModBlockTags.C_FURNACE_SILVER)
                    .register();

    public static final BlockEntry<BlockMillionFurnace> MILLION_FURNACE =
            furnace(BlockMillionFurnace.MILLION_FURNACE,
                    BlockMillionFurnace::new,
                    () -> Blocks.IRON_BLOCK)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#C#")
                                .pattern("###")
                                .define('C', Registration.RAINBOW_CORE.get())
                                .define('#', Registration.RAINBOW_PLATING.get())
                                .unlockedBy("has_rainbow_core",
                                        RegistrateRecipeProvider.has(Registration.RAINBOW_CORE.get()))
                                .save(provider,
                                        BlockMillionFurnace.MILLION_FURNACE);

                    })
                    .tag(ModBlockTags.FURNACE_RAINBOW, ModBlockTags.C_FURNACE_RAINBOW)
                    .register();


    public static final BlockEntry<BlockAllthemodiumFurnace> ALLTHEMODIUM_FURNACE =
            furnace(BlockAllthemodiumFurnace.ALLTHEMODIUM_FURNACE, BlockAllthemodiumFurnace::new, () -> Blocks.GOLD_BLOCK)
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
                                .save(provider, BlockAllthemodiumFurnace.ALLTHEMODIUM_FURNACE);


                    })
                    .tag(ModBlockTags.FURNACE_ALLTHEMODIUM, ModBlockTags.C_FURNACE_ALLTHEMODIUM)
                    .register();


    public static final BlockEntry<BlockVibraniumFurnace> VIBRANIUM_FURNACE =
            furnace(BlockVibraniumFurnace.VIBRANIUM_FURNACE, BlockVibraniumFurnace::new, () -> Blocks.DIAMOND_BLOCK)
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
                                .save(provider, "vibranium_furnace_from_allthemodium_furnace");



                    })
                    .tag(ModBlockTags.FURNACE_VIBRANIUM, ModBlockTags.C_FURNACE_VIBRANIUM)
                    .register();


    public static final BlockEntry<BlockUnobtainiumFurnace> UNOBTAINIUM_FURNACE =
            furnace(BlockUnobtainiumFurnace.UNOBTAINIUM_FURNACE, BlockUnobtainiumFurnace::new, () -> Blocks.NETHERITE_BLOCK)
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
                                .save(provider, BlockUnobtainiumFurnace.UNOBTAINIUM_FURNACE);



                    })
                    .tag(ModBlockTags.FURNACE_UNOBTAINIUM, ModBlockTags.C_FURNACE_UNOBTAINIUM)
                    .register();

    public static final BlockEntry<BlockObsidianFurnace> OBSIDIAN_FURNACE =
            furnaceWithProps(
                    BlockObsidianFurnace.OBSIDIAN_FURNACE,
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
                                .save(provider, BlockObsidianFurnace.OBSIDIAN_FURNACE);

                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                                .pattern("#Y#")
                                .pattern("YXY")
                                .pattern("#Y#")
                                .define('#', bindForge("obsidian"))
                                .define('X', bindForge("furnaces/crystal"))
                                .define('Y', bindForge("rods/blaze"))
                                .unlockedBy("has_obsidian",
                                        RegistrateRecipeProvider.has(Blocks.OBSIDIAN))
                                .save(provider, BlockObsidianFurnace.OBSIDIAN_FURNACE + "2");

                    })
                    .tag(ModBlockTags.FURNACE_OBSIDIAN, ModBlockTags.C_FURNACE_OBSIDIAN)
                    .register();

    public static final BlockEntry<BlockCrystalFurnace> CRYSTAL_FURNACE =
            furnaceWithProps(
                    BlockCrystalFurnace.CRYSTAL_FURNACE,
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
                                .save(provider, BlockCrystalFurnace.CRYSTAL_FURNACE);



                    })
                    .tag(ModBlockTags.FURNACE_CRYSTAL, ModBlockTags.C_FURNACE_CRYSTAL)
                    .register();

    public static final BlockEntry<BlockNetheriteFurnace> NETHERITE_FURNACE =
            furnaceWithProps(
                    BlockNetheriteFurnace.NETHERITE_FURNACE,
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
                                .save(provider, BlockNetheriteFurnace.NETHERITE_FURNACE);



                    })
                    .tag(ModBlockTags.FURNACE_NETHERITE, ModBlockTags.C_FURNACE_NETHERITE)
                    .register();



    private static <T extends Block> BlockBuilder<T, Registrate> furnace(
            String name,
            NonNullFunction<BlockBehaviour.Properties, T> factory,
            NonNullSupplier<Block> baseBlock
    ) {
        return REGISTRATE
                .block(name, factory)
                .initialProperties(baseBlock)
                .blockstate((ctx, provider) -> {
                })
                .tag(BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.PLAYER_WORKSTATIONS_CRAFTING_TABLES)
                .loot((ctx, furnace) -> {
                    LootTable.Builder builder = LootTable.lootTable()
                            .withPool(
                                    LootPool.lootPool()
                                            .setRolls(ConstantValue.exactly(1.0f))
                                            .add(LootItem.lootTableItem(furnace))
                                            .apply(
                                                    CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                                            .copy("Augment", "BlockEntityTag.Augment")
                                                            .copy("Jovial", "BlockEntityTag.Jovial")
                                                            .copy("Tag", "BlockEntityTag.Tag")
                                            )
                                            .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                            );
                    ctx.add(furnace, builder);
                })
                .simpleItem();
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


    public static void register(){

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
