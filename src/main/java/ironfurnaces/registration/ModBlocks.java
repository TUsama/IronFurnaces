package ironfurnaces.registration;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import ironfurnaces.blocks.BlockWirelessEnergyHeater;
import ironfurnaces.blocks.furnaces.BlockItemHeater;
import ironfurnaces.blocks.furnaces.new_furnace.FurnacePatternHolderBlock;
import ironfurnaces.blocks.furnaces.new_furnace.FurnacePatternHolderItem;
import ironfurnaces.items.upgrades.furnace_pattern.IPatternAccessor;
import ironfurnaces.items.upgrades.furnace_upgrade.recipe.FurnacePatternHolderRecipeBuilder;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.BlockWirelessEnergyHeaterTile;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import net.minecraft.Util;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
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
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.PartialNBTIngredient;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;
import static ironfurnaces.registration.ModItemTags.*;

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
    private static final ResourceLocation COPPER_PATTERN_ID = IronFurnaces.id("copper_furnace");
    private static final ResourceLocation IRON_PATTERN_ID = IronFurnaces.id("iron_furnace");
    private static final ResourceLocation SILVER_PATTERN_ID = IronFurnaces.id("silver_furnace");
    private static final ResourceLocation GOLD_PATTERN_ID = IronFurnaces.id("gold_furnace");
    private static final ResourceLocation DIAMOND_PATTERN_ID = IronFurnaces.id("diamond_furnace");
    private static final ResourceLocation EMERALD_PATTERN_ID = IronFurnaces.id("emerald_furnace");
    private static final ResourceLocation CRYSTAL_PATTERN_ID = IronFurnaces.id("crystal_furnace");
    private static final ResourceLocation OBSIDIAN_PATTERN_ID = IronFurnaces.id("obsidian_furnace");
    private static final ResourceLocation NETHERITE_PATTERN_ID = IronFurnaces.id("netherite_furnace");
    private static final ResourceLocation ALLTHEMODIUM_PATTERN_ID = IronFurnaces.id("allthemodium_furnace");
    private static final ResourceLocation VIBRANIUM_PATTERN_ID = IronFurnaces.id("vibranium_furnace");
    private static final ResourceLocation UNOBTAINIUM_PATTERN_ID = IronFurnaces.id("unobtainium_furnace");

    public static final BlockEntry<FurnacePatternHolderBlock> PATTERN_HOLDER = REGISTRATE
            .block(FurnacePatternHolderBlock.ID, FurnacePatternHolderBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.noOcclusion().requiresCorrectToolForDrops())
            .lang("Pattern Furnace")
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
            .recipe((ctx, provider) -> {
                FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), IRON_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("###")
                        .define('#', bindForge("ingots/iron"))
                        .define('X', bindC("player_workstations/furnaces"))
                        .unlockedBy("has_iron", RegistrateRecipeProvider.has(Items.IRON_INGOT))
                        .save(provider, makeID(IRON_PATTERN_ID.getPath()));

                FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), IRON_PATTERN_ID)
                        .pattern("YYY")
                        .pattern("#X#")
                        .pattern("YYY")
                        .define('#', bindForge("glass"))
                        .define('X', bindForge("furnaces/copper"))
                        .define('Y', bindForge("ingots/iron"))
                        .unlockedBy("has_iron_ingot", RegistrateRecipeProvider.has(Items.IRON_INGOT))
                        .save(provider, makeID(IRON_PATTERN_ID.getPath() + "2"));

                FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), IRON_PATTERN_ID)
                        .pattern("YYY")
                        .pattern("#X#")
                        .pattern("YYY")
                        .define('#', bindForge("glass"))
                        .define('X', PartialNBTIngredient.of(
                                ctx.get(),
                                Util.make(() -> {
                                    var furnacePatternHolderItem = ctx.get().getDefaultInstance();
                                    IPatternAccessor.writePatternToItemStack(furnacePatternHolderItem, COPPER_PATTERN_ID);
                                    return furnacePatternHolderItem.getShareTag();
                                })
                        ))
                        .define('Y', bindForge("ingots/iron"))
                        .unlockedBy("has_iron_ingot", RegistrateRecipeProvider.has(Items.IRON_INGOT))
                        .save(provider, makeID(IRON_PATTERN_ID.getPath() + "2_nbt"));


                FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), GOLD_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("#Y#")
                        .define('#', bindForge("ingots/gold"))
                        .define('X', bindForge("furnaces/iron"))
                        .define('Y', bindForge("storage_blocks/gold"))
                        .unlockedBy("has_gold_ingot", RegistrateRecipeProvider.has(Items.GOLD_INGOT))
                        .save(provider, makeID(GOLD_PATTERN_ID.getPath()));

                FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), GOLD_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("#Y#")
                        .define('#', bindForge("ingots/gold"))
                        .define('X', PartialNBTIngredient.of(
                                ctx.get(),
                                Util.make(() -> {
                                    var furnacePatternHolderItem = ctx.get().getDefaultInstance();
                                    IPatternAccessor.writePatternToItemStack(furnacePatternHolderItem, IRON_PATTERN_ID);
                                    return furnacePatternHolderItem.getShareTag();
                                })
                        ))
                        .define('Y', bindForge("storage_blocks/gold"))
                        .unlockedBy("has_gold_ingot", RegistrateRecipeProvider.has(Items.GOLD_INGOT))
                        .save(provider, makeID(GOLD_PATTERN_ID.getPath() + "_nbt"));

                FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), GOLD_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("#Y#")
                        .define('#', bindForge("glass"))
                        .define('X', bindForge("furnaces/silver"))
                        .define('Y', bindForge("storage_blocks/gold"))
                        .unlockedBy("has_gold_block", RegistrateRecipeProvider.has(Items.GOLD_BLOCK))
                        .save(provider, makeID(GOLD_PATTERN_ID.getPath() + "2"));

                FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), GOLD_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("#Y#")
                        .define('#', bindForge("glass"))
                        .define('X', PartialNBTIngredient.of(
                                ctx.get(),
                                Util.make(() -> {
                                    var furnacePatternHolderItem = ctx.get().getDefaultInstance();
                                    IPatternAccessor.writePatternToItemStack(furnacePatternHolderItem, SILVER_PATTERN_ID);
                                    return furnacePatternHolderItem.getShareTag();
                                })
                        ))
                        .define('Y', bindForge("storage_blocks/gold"))
                        .unlockedBy("has_gold_block", RegistrateRecipeProvider.has(Items.GOLD_BLOCK))
                        .save(provider, makeID(GOLD_PATTERN_ID.getPath() + "2_nbt"));

                FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), DIAMOND_PATTERN_ID)
                        .pattern("###")
                        .pattern("GXG")
                        .pattern("###")
                        .define('#', bindForge("gems/diamond"))
                        .define('X', bindForge("furnaces/gold"))
                        .define('G', bindForge("glass"))
                        .unlockedBy("has_diamond", RegistrateRecipeProvider.has(Items.DIAMOND))
                        .save(provider, makeID(DIAMOND_PATTERN_ID.getPath()));

                FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), DIAMOND_PATTERN_ID)
                        .pattern("###")
                        .pattern("GXG")
                        .pattern("###")
                        .define('#', bindForge("gems/diamond"))
                        .define('X', PartialNBTIngredient.of(
                                ctx.get(),
                                Util.make(() -> {
                                    var furnacePatternHolderItem = ctx.get().getDefaultInstance();
                                    IPatternAccessor.writePatternToItemStack(furnacePatternHolderItem, GOLD_PATTERN_ID);
                                    return furnacePatternHolderItem.getShareTag();
                                })
                        ))
                        .define('G', bindForge("glass"))
                        .unlockedBy("has_diamond", RegistrateRecipeProvider.has(Items.DIAMOND))
                        .save(provider, makeID(DIAMOND_PATTERN_ID.getPath() + "_nbt"));

                FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), EMERALD_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("###")
                        .define('#', bindForge("gems/emerald"))
                        .define('X', bindForge("furnaces/diamond"))
                        .unlockedBy("has_emerald", RegistrateRecipeProvider.has(Items.EMERALD))
                        .save(provider, makeID(EMERALD_PATTERN_ID.getPath()));

                FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), EMERALD_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("###")
                        .define('#', bindForge("gems/emerald"))
                        .define('X', PartialNBTIngredient.of(
                                ctx.get(),
                                Util.make(() -> {
                                    var furnacePatternHolderItem = ctx.get().getDefaultInstance();
                                    IPatternAccessor.writePatternToItemStack(furnacePatternHolderItem, DIAMOND_PATTERN_ID);
                                    return furnacePatternHolderItem.getShareTag();
                                })
                        ))
                        .unlockedBy("has_emerald", RegistrateRecipeProvider.has(Items.EMERALD))
                        .save(provider, makeID(EMERALD_PATTERN_ID.getPath() + "_nbt"));

                FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), COPPER_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("###")
                        .define('#', bindForge("ingots/copper"))
                        .define('X', ModItemTags.PLAYER_WORKSTATIONS_FURNACE)
                        .unlockedBy("has_copper_ingot", RegistrateRecipeProvider.has(Items.COPPER_INGOT))
                        .save(provider, makeID(COPPER_PATTERN_ID.getPath()));

                whenHasTags(x -> FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), SILVER_PATTERN_ID)
                        .pattern("#G#")
                        .pattern("GXG")
                        .pattern("#G#")
                        .define('#', ModItemTags.SILVER)
                        .define('X', bindForge("furnaces/iron"))
                        .define('G', bindForge("glass"))
                        .unlockedBy("has_silver_ingot", RegistrateRecipeProvider.has(ModItemTags.SILVER))
                        .save(x, makeID(SILVER_PATTERN_ID.getPath())), ctx, provider, SILVER_PATTERN_ID.getPath(), ModItemTags.SILVER);

                whenHasTags(x -> FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), SILVER_PATTERN_ID)
                        .pattern("#G#")
                        .pattern("GXG")
                        .pattern("#G#")
                        .define('#', ModItemTags.SILVER)
                        .define('X', PartialNBTIngredient.of(
                                ctx.get(),
                                Util.make(() -> {
                                    var furnacePatternHolderItem = ctx.get().getDefaultInstance();
                                    IPatternAccessor.writePatternToItemStack(furnacePatternHolderItem, IRON_PATTERN_ID);
                                    return furnacePatternHolderItem.getShareTag();
                                })
                        ))
                        .define('G', bindForge("glass"))
                        .unlockedBy("has_silver_ingot", RegistrateRecipeProvider.has(ModItemTags.SILVER))
                        .save(x, makeID(SILVER_PATTERN_ID.getPath() + "_nbt")), ctx, provider, SILVER_PATTERN_ID.getPath() + "_nbt", ModItemTags.SILVER);

                whenHasTags(x -> FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), SILVER_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("###")
                        .define('#', ModItemTags.SILVER)
                        .define('X', bindForge("furnaces/copper"))
                        .unlockedBy("has_silver_ingot", RegistrateRecipeProvider.has(ModItemTags.SILVER))
                        .save(x, makeID(SILVER_PATTERN_ID.getPath() + "2")), ctx, provider, SILVER_PATTERN_ID.getPath() + "2", ModItemTags.SILVER);

                whenHasTags(x -> FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), SILVER_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("###")
                        .define('#', ModItemTags.SILVER)
                        .define('X', PartialNBTIngredient.of(
                                ctx.get(),
                                Util.make(() -> {
                                    var furnacePatternHolderItem = ctx.get().getDefaultInstance();
                                    IPatternAccessor.writePatternToItemStack(furnacePatternHolderItem, COPPER_PATTERN_ID);
                                    return furnacePatternHolderItem.getShareTag();
                                })
                        ))
                        .unlockedBy("has_silver_ingot", RegistrateRecipeProvider.has(ModItemTags.SILVER))
                        .save(x, makeID(SILVER_PATTERN_ID.getPath() + "2_nbt")), ctx, provider, SILVER_PATTERN_ID.getPath() + "2_nbt", ModItemTags.SILVER);

                FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), OBSIDIAN_PATTERN_ID)
                        .pattern("#Y#")
                        .pattern("YXY")
                        .pattern("#Y#")
                        .define('#', bindForge("obsidian"))
                        .define('X', bindForge("furnaces/emerald"))
                        .define('Y', bindForge("rods/blaze"))
                        .unlockedBy("has_obsidian", RegistrateRecipeProvider.has(Blocks.OBSIDIAN))
                        .save(provider, makeID(OBSIDIAN_PATTERN_ID.getPath()));

                FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), OBSIDIAN_PATTERN_ID)
                        .pattern("#Y#")
                        .pattern("YXY")
                        .pattern("#Y#")
                        .define('#', bindForge("obsidian"))
                        .define('X', PartialNBTIngredient.of(
                                ctx.get(),
                                Util.make(() -> {
                                    var furnacePatternHolderItem = ctx.get().getDefaultInstance();
                                    IPatternAccessor.writePatternToItemStack(furnacePatternHolderItem, EMERALD_PATTERN_ID);
                                    return furnacePatternHolderItem.getShareTag();
                                })
                        ))
                        .define('Y', bindForge("rods/blaze"))
                        .unlockedBy("has_obsidian", RegistrateRecipeProvider.has(Blocks.OBSIDIAN))
                        .save(provider, makeID(OBSIDIAN_PATTERN_ID.getPath() + "_nbt"));

                FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), OBSIDIAN_PATTERN_ID)
                        .pattern("#Y#")
                        .pattern("YXY")
                        .pattern("#Y#")
                        .define('#', bindForge("obsidian"))
                        .define('X', bindForge("furnaces/crystal"))
                        .define('Y', bindForge("rods/blaze"))
                        .unlockedBy("has_obsidian", RegistrateRecipeProvider.has(Blocks.OBSIDIAN))
                        .save(provider, makeID(OBSIDIAN_PATTERN_ID.getPath() + "2"));

                FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), OBSIDIAN_PATTERN_ID)
                        .pattern("#Y#")
                        .pattern("YXY")
                        .pattern("#Y#")
                        .define('#', bindForge("obsidian"))
                        .define('X', PartialNBTIngredient.of(
                                ctx.get(),
                                Util.make(() -> {
                                    var furnacePatternHolderItem = ctx.get().getDefaultInstance();
                                    IPatternAccessor.writePatternToItemStack(furnacePatternHolderItem, CRYSTAL_PATTERN_ID);
                                    return furnacePatternHolderItem.getShareTag();
                                })
                        ))
                        .define('Y', bindForge("rods/blaze"))
                        .unlockedBy("has_obsidian", RegistrateRecipeProvider.has(Blocks.OBSIDIAN))
                        .save(provider, makeID(OBSIDIAN_PATTERN_ID.getPath() + "2_nbt"));

                FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), CRYSTAL_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("#E#")
                        .define('#', bindForge("glass"))
                        .define('X', bindForge("furnaces/diamond"))
                        .define('E', Items.ENDER_EYE)
                        .unlockedBy("has_diamond_furnace", RegistrateRecipeProvider.has(Items.DIAMOND))
                        .save(provider, makeID(CRYSTAL_PATTERN_ID.getPath()));

                FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), CRYSTAL_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("#E#")
                        .define('#', bindForge("glass"))
                        .define('X', PartialNBTIngredient.of(
                                ctx.get(),
                                Util.make(() -> {
                                    var furnacePatternHolderItem = ctx.get().getDefaultInstance();
                                    IPatternAccessor.writePatternToItemStack(furnacePatternHolderItem, DIAMOND_PATTERN_ID);
                                    return furnacePatternHolderItem.getShareTag();
                                })
                        ))
                        .define('E', Items.ENDER_EYE)
                        .unlockedBy("has_diamond_furnace", RegistrateRecipeProvider.has(Items.DIAMOND))
                        .save(provider, makeID(CRYSTAL_PATTERN_ID.getPath() + "_nbt"));

                FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), NETHERITE_PATTERN_ID)
                        .pattern("N#N")
                        .pattern("#X#")
                        .pattern("NSN")
                        .define('#', Items.MAGMA_CREAM)
                        .define('X', bindForge("furnaces/obsidian"))
                        .define('S', bindVanilla("soul_fire_base_blocks"))
                        .define('N', Items.NETHERITE_INGOT)
                        .unlockedBy("has_obsidian_furnace", RegistrateRecipeProvider.has(LegacyFurnaceBlocks.OBSIDIAN_FURNACE))
                        .save(provider, makeID(NETHERITE_PATTERN_ID.getPath()));

                FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), NETHERITE_PATTERN_ID)
                        .pattern("N#N")
                        .pattern("#X#")
                        .pattern("NSN")
                        .define('#', Items.MAGMA_CREAM)
                        .define('X', PartialNBTIngredient.of(
                                ctx.get(),
                                Util.make(() -> {
                                    var furnacePatternHolderItem = ctx.get().getDefaultInstance();
                                    IPatternAccessor.writePatternToItemStack(furnacePatternHolderItem, OBSIDIAN_PATTERN_ID);
                                    return furnacePatternHolderItem.getShareTag();
                                })
                        ))
                        .define('S', bindVanilla("soul_fire_base_blocks"))
                        .define('N', Items.NETHERITE_INGOT)
                        .unlockedBy("has_obsidian_furnace", RegistrateRecipeProvider.has(LegacyFurnaceBlocks.OBSIDIAN_FURNACE))
                        .save(provider, makeID(NETHERITE_PATTERN_ID.getPath() + "_nbt"));

                whenAllthemodium(x -> FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), ALLTHEMODIUM_PATTERN_ID)
                        .pattern("B#B")
                        .pattern("#X#")
                        .pattern("B#B")
                        .define('#', bindForge("ingots/allthemodium"))
                        .define('X', bindForge("furnaces/netherite"))
                        .define('B', bindForge("storage_blocks/allthemodium"))
                        .unlockedBy("has_allthemodium_ingot",
                                RegistrateRecipeProvider.has(bindForge("ingots/allthemodium")))
                        .save(x, makeID(ALLTHEMODIUM_PATTERN_ID.getPath())), ctx, ALLTHEMODIUM_PATTERN_ID.getPath(), provider);

                whenAllthemodium(x -> FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), ALLTHEMODIUM_PATTERN_ID)
                        .pattern("B#B")
                        .pattern("#X#")
                        .pattern("B#B")
                        .define('#', bindForge("ingots/allthemodium"))
                        .define('X', PartialNBTIngredient.of(
                                ctx.get(),
                                Util.make(() -> {
                                    var furnacePatternHolderItem = ctx.get().getDefaultInstance();
                                    IPatternAccessor.writePatternToItemStack(furnacePatternHolderItem, NETHERITE_PATTERN_ID);
                                    return furnacePatternHolderItem.getShareTag();
                                })
                        ))
                        .define('B', bindForge("storage_blocks/allthemodium"))
                        .unlockedBy("has_allthemodium_ingot",
                                RegistrateRecipeProvider.has(bindForge("ingots/allthemodium")))
                        .save(x, makeID(ALLTHEMODIUM_PATTERN_ID.getPath() + "_nbt")), ctx, ALLTHEMODIUM_PATTERN_ID.getPath() + "_nbt", provider);

                whenAllthemodium(x -> FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), VIBRANIUM_PATTERN_ID)
                        .pattern("B#B")
                        .pattern("#X#")
                        .pattern("B#B")
                        .define('#', bindForge("ingots/vibranium"))
                        .define('X', bindForge("furnaces/allthemodium"))
                        .define('B', bindForge("storage_blocks/vibranium"))
                        .unlockedBy("has_vibranium_ingot",
                                RegistrateRecipeProvider.has(bindForge("ingots/vibranium")))
                        .save(x, makeID(VIBRANIUM_PATTERN_ID.getPath())), ctx, VIBRANIUM_PATTERN_ID.getPath(), provider);

                whenAllthemodium(x -> FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), VIBRANIUM_PATTERN_ID)
                        .pattern("B#B")
                        .pattern("#X#")
                        .pattern("B#B")
                        .define('#', bindForge("ingots/vibranium"))
                        .define('X', PartialNBTIngredient.of(
                                ctx.get(),
                                Util.make(() -> {
                                    var furnacePatternHolderItem = ctx.get().getDefaultInstance();
                                    IPatternAccessor.writePatternToItemStack(furnacePatternHolderItem, ALLTHEMODIUM_PATTERN_ID);
                                    return furnacePatternHolderItem.getShareTag();
                                })
                        ))
                        .define('B', bindForge("storage_blocks/vibranium"))
                        .unlockedBy("has_vibranium_ingot",
                                RegistrateRecipeProvider.has(bindForge("ingots/vibranium")))
                        .save(x, makeID(VIBRANIUM_PATTERN_ID.getPath() + "_nbt")), ctx, VIBRANIUM_PATTERN_ID.getPath() + "_nbt", provider);

                whenAllthemodium(x -> FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), UNOBTAINIUM_PATTERN_ID)
                        .pattern("B#B")
                        .pattern("#X#")
                        .pattern("B#B")
                        .define('#', bindForge("ingots/unobtainium"))
                        .define('X', bindForge("furnaces/vibranium"))
                        .define('B', bindForge("storage_blocks/unobtainium"))
                        .unlockedBy("has_unobtainium_ingot",
                                RegistrateRecipeProvider.has(bindForge("ingots/unobtainium")))
                        .save(x, makeID(UNOBTAINIUM_PATTERN_ID.getPath())), ctx, UNOBTAINIUM_PATTERN_ID.getPath(), provider);

                whenAllthemodium(x -> FurnacePatternHolderRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), UNOBTAINIUM_PATTERN_ID)
                        .pattern("B#B")
                        .pattern("#X#")
                        .pattern("B#B")
                        .define('#', bindForge("ingots/unobtainium"))
                        .define('X', PartialNBTIngredient.of(
                                ctx.get(),
                                Util.make(() -> {
                                    var furnacePatternHolderItem = ctx.get().getDefaultInstance();
                                    IPatternAccessor.writePatternToItemStack(furnacePatternHolderItem, VIBRANIUM_PATTERN_ID);
                                    return furnacePatternHolderItem.getShareTag();
                                })
                        ))
                        .define('B', bindForge("storage_blocks/unobtainium"))
                        .unlockedBy("has_unobtainium_ingot",
                                RegistrateRecipeProvider.has(bindForge("ingots/unobtainium")))
                        .save(x, makeID(UNOBTAINIUM_PATTERN_ID.getPath() + "_nbt")), ctx, UNOBTAINIUM_PATTERN_ID.getPath() + "_nbt", provider);

            })
            .model((ctx, prov) -> prov.getBuilder(ctx.getName())
                    .parent(new ModelFile.UncheckedModelFile("minecraft:builtin/entity")))
            .tag(ModItemTags.PLAYER_WORKSTATIONS_FURNACE)
            .build()
            .register();

    @SafeVarargs
    private static void whenHasTags(Consumer<Consumer<FinishedRecipe>> consumerConsumer, DataGenContext<Item, FurnacePatternHolderItem> ctx, RegistrateRecipeProvider provider, String id, TagKey<Item>... tags) {
        ConditionalRecipe.Builder builder = ConditionalRecipe.builder();
        for (TagKey<Item> itemTagKey : tags) {
            builder.addCondition(new NotCondition(new TagEmptyCondition(itemTagKey.location())));
        }

        consumerConsumer.accept(x -> builder.addRecipe(x).build(provider, IronFurnaces.id("new_furnaces" + "/" + id)));
    }

    public static ResourceLocation makeID(String furnaceName) {
        return IronFurnaces.id("new_furnaces/" + furnaceName);
    }

    public static void register() {

    }
    private static void whenAllthemodium(
            Consumer<Consumer<FinishedRecipe>> consumerConsumer,
            DataGenContext<Item, FurnacePatternHolderItem> ctx,
            String id,
            RegistrateRecipeProvider provider
    ) {
        consumerConsumer.accept(x -> ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition("allthemodium"))
                .addRecipe(x)
                .build(provider, IronFurnaces.id("new_furnaces/" + id)));
    }

    public static BlockEntityType<? extends BlockIronFurnaceTileBase> asBlockEntityType(BlockEntry<?> entry) {
        return (BlockEntityType<? extends BlockIronFurnaceTileBase>) entry.getSibling(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES).get();
    }

    public static <T extends BlockEntity> BlockEntityType<T> asGenericBlockEntityType(BlockEntry<?> entry) {
        return (BlockEntityType<T>) entry.getSibling(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES).get();
    }
}
