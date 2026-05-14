package ironfurnaces.registration;

import dev.anvilcraft.lib.v2.registrum.providers.DataGenContext;
import dev.anvilcraft.lib.v2.registrum.providers.ProviderType;
import dev.anvilcraft.lib.v2.registrum.providers.generators.RegistrumRecipeProvider;
import dev.anvilcraft.lib.v2.registrum.util.entry.BlockEntry;
import ironfurnaces.blocks.BlockWirelessEnergyHeater;
import ironfurnaces.blocks.furnaces.BlockItemHeater;
import ironfurnaces.blocks.furnaces.new_furnace.FurnacePatternHolderBlock;
import ironfurnaces.blocks.furnaces.new_furnace.FurnacePatternHolderItem;
import ironfurnaces.items.upgrades.furnace_upgrade.recipe.FurnacePatternHolderRecipeBuilder;
import ironfurnaces.registration.util.ConditionRecipeUtil;
import ironfurnaces.registration.util.Constants;
import ironfurnaces.registration.util.CriterionUtil;
import ironfurnaces.registration.util.IDUtil;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.heater.BlockWirelessEnergyHeaterTile;
import ironfurnaces.tileentity.heater.WirelessEnergyHeaterRenderState;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.Optional;
import java.util.function.Consumer;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;
import static ironfurnaces.registration.ModItemTags.bindC;
import static ironfurnaces.registration.ModItemTags.bindForge;

public class ModBlocks {
    public static final BlockEntry<BlockWirelessEnergyHeater> HEATER = REGISTRATE
            .block(BlockWirelessEnergyHeater.HEATER, BlockWirelessEnergyHeater::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .lang("Wireless Heat Transmitter")
            .item(BlockItemHeater::new)
            .build()
            .<BlockWirelessEnergyHeaterTile, WirelessEnergyHeaterRenderState>blockEntity(BlockWirelessEnergyHeaterTile::new)
            .build()
            .addMiscData(ProviderType.LANG, x -> x.add("container.ironfurnaces.wireless_energy_heater", "Wireless Heater"))
            .recipe((ctx, provider) -> {
                ShapedRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get())
                        .pattern("#F#")
                        .pattern("#X#")
                        .pattern("#C#")
                        .define('#', bindForge("stone"))
                        .define('C', ModItemTags.PLAYER_WORKSTATIONS_FURNACE)
                        .define('F', Items.COMPARATOR)
                        .define('X', bindForge("storage_blocks/redstone"))
                        .unlockedBy("has_comparator", CriterionUtil.has(Items.COMPARATOR, provider))
                        .save(provider, IDUtil.makeNewFurnaceID(ctx.getName()));
            })
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .register();

    public static final BlockEntry<FurnacePatternHolderBlock> PATTERN_HOLDER = REGISTRATE
            .block(FurnacePatternHolderBlock.ID, FurnacePatternHolderBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.noOcclusion().requiresCorrectToolForDrops())
            .lang("Pattern Furnace")
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.PLAYER_WORKSTATIONS_FURNACE)
            .blockstate(() -> (ctx, provider) -> {
                provider.createNonTemplateModelBlock(Blocks.AIR);
            })
            .loot((ctx, furnace) -> {
                LootTable.Builder builder = LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0f))
                                        .add(
                                                LootItem.lootTableItem(furnace)
                                                        .apply(
                                                                CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                                                                        .include(DataComponents.CUSTOM_DATA)
                                                        )
                                                        .apply(
                                                                CopyBlockState.copyState(furnace)
                                                                        .copy(ModBlockState.JOVIAL_STATE)
                                                        )

                                                        .apply(CopyNameFunction.copyName(LootContext.BlockEntityTarget.BLOCK_ENTITY))
                                        )
                        );

                ctx.add(furnace, builder);
            })
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item(FurnacePatternHolderItem::new)
            .recipe((ctx, provider) -> {
                FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.IRON_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("###")
                        .define('#', bindForge("ingots/iron"))
                        .define('X', bindC("player_workstations/furnaces"))
                        .unlockedBy("has_iron", CriterionUtil.has(Items.IRON_INGOT, provider))
                        .save(provider, IDUtil.makeNewFurnaceID(Constants.IRON_PATTERN_ID.getPath()));

                FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.IRON_PATTERN_ID)
                        .pattern("YYY")
                        .pattern("#X#")
                        .pattern("YYY")
                        .define('#', bindForge("glass"))
                        .define('X', bindForge("furnaces/copper"))
                        .define('Y', bindForge("ingots/iron"))
                        .unlockedBy("has_iron_ingot", CriterionUtil.has(Items.IRON_INGOT, provider))
                        .save(provider, IDUtil.makeNewFurnaceID(Constants.IRON_PATTERN_ID.getPath() + "2"));

                FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.IRON_PATTERN_ID)
                        .pattern("YYY")
                        .pattern("#X#")
                        .pattern("YYY")
                        .define('#', bindForge("glass"))
                        .define('X', bindPatternHolder(ctx.get(), Constants.COPPER_PATTERN_ID))
                        .define('Y', bindForge("ingots/iron"))
                        .unlockedBy("has_iron_ingot", CriterionUtil.has(Items.IRON_INGOT, provider))
                        .save(provider, IDUtil.makeNewFurnaceID(Constants.IRON_PATTERN_ID.getPath() + "2_nbt"));


                FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.GOLD_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("#Y#")
                        .define('#', bindForge("ingots/gold"))
                        .define('X', bindForge("furnaces/iron"))
                        .define('Y', bindForge("storage_blocks/gold"))
                        .unlockedBy("has_gold_ingot", CriterionUtil.has(Items.GOLD_INGOT, provider))
                        .save(provider, IDUtil.makeNewFurnaceID(Constants.GOLD_PATTERN_ID.getPath()));

                FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.GOLD_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("#Y#")
                        .define('#', bindForge("ingots/gold"))
                        .define('X', bindPatternHolder(ctx.get(), Constants.IRON_PATTERN_ID))
                        .define('Y', bindForge("storage_blocks/gold"))
                        .unlockedBy("has_gold_ingot", CriterionUtil.has(Items.GOLD_INGOT, provider))
                        .save(provider, IDUtil.makeNewFurnaceID(Constants.GOLD_PATTERN_ID.getPath() + "_nbt"));

                FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.GOLD_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("#Y#")
                        .define('#', bindForge("glass"))
                        .define('X', bindForge("furnaces/silver"))
                        .define('Y', bindForge("storage_blocks/gold"))
                        .unlockedBy("has_gold_block", CriterionUtil.has(Items.GOLD_BLOCK, provider))
                        .save(provider, IDUtil.makeNewFurnaceID(Constants.GOLD_PATTERN_ID.getPath() + "2"));

                FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.GOLD_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("#Y#")
                        .define('#', bindForge("glass"))
                        .define('X', bindPatternHolder(ctx.get(), Constants.SILVER_PATTERN_ID))
                        .define('Y', bindForge("storage_blocks/gold"))
                        .unlockedBy("has_gold_block", CriterionUtil.has(Items.GOLD_BLOCK, provider))
                        .save(provider, IDUtil.makeNewFurnaceID(Constants.GOLD_PATTERN_ID.getPath() + "2_nbt"));

                FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.DIAMOND_PATTERN_ID)
                        .pattern("###")
                        .pattern("GXG")
                        .pattern("###")
                        .define('#', bindForge("gems/diamond"))
                        .define('X', bindForge("furnaces/gold"))
                        .define('G', bindForge("glass"))
                        .unlockedBy("has_diamond", CriterionUtil.has(Items.DIAMOND, provider))
                        .save(provider, IDUtil.makeNewFurnaceID(Constants.DIAMOND_PATTERN_ID.getPath()));

                FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.DIAMOND_PATTERN_ID)
                        .pattern("###")
                        .pattern("GXG")
                        .pattern("###")
                        .define('#', bindForge("gems/diamond"))
                        .define('X', bindPatternHolder(ctx.get(), Constants.GOLD_PATTERN_ID))
                        .define('G', bindForge("glass"))
                        .unlockedBy("has_diamond", CriterionUtil.has(Items.DIAMOND, provider))
                        .save(provider, IDUtil.makeNewFurnaceID(Constants.DIAMOND_PATTERN_ID.getPath() + "_nbt"));

                FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.EMERALD_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("###")
                        .define('#', bindForge("gems/emerald"))
                        .define('X', bindForge("furnaces/diamond"))
                        .unlockedBy("has_emerald", CriterionUtil.has(Items.EMERALD, provider))
                        .save(provider, IDUtil.makeNewFurnaceID(Constants.EMERALD_PATTERN_ID.getPath()));

                FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.EMERALD_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("###")
                        .define('#', bindForge("gems/emerald"))
                        .define('X', bindPatternHolder(ctx.get(), Constants.DIAMOND_PATTERN_ID))
                        .unlockedBy("has_emerald", CriterionUtil.has(Items.EMERALD, provider))
                        .save(provider, IDUtil.makeNewFurnaceID(Constants.EMERALD_PATTERN_ID.getPath() + "_nbt"));

                FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.COPPER_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("###")
                        .define('#', bindForge("ingots/copper"))
                        .define('X', ModItemTags.PLAYER_WORKSTATIONS_FURNACE)
                        .unlockedBy("has_copper_ingot", CriterionUtil.has(Items.COPPER_INGOT, provider))
                        .save(provider, IDUtil.makeNewFurnaceID(Constants.COPPER_PATTERN_ID.getPath()));

                whenHasTags(x -> FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.SILVER_PATTERN_ID)
                        .pattern("#G#")
                        .pattern("GXG")
                        .pattern("#G#")
                        .define('#', ModItemTags.SILVER)
                        .define('X', bindForge("furnaces/iron"))
                        .define('G', bindForge("glass"))
                        .unlockedBy("has_silver_ingot", CriterionUtil.has(ModItemTags.SILVER, provider))
                        .save(x, IDUtil.makeNewFurnaceID(Constants.SILVER_PATTERN_ID.getPath())), ctx, provider, Constants.SILVER_PATTERN_ID.getPath(), ModItemTags.SILVER);

                whenHasTags(x -> FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.SILVER_PATTERN_ID)
                        .pattern("#G#")
                        .pattern("GXG")
                        .pattern("#G#")
                        .define('#', ModItemTags.SILVER)
                        .define('X', bindPatternHolder(ctx.get(), Constants.IRON_PATTERN_ID))
                        .define('G', bindForge("glass"))
                        .unlockedBy("has_silver_ingot", CriterionUtil.has(ModItemTags.SILVER, provider))
                        .save(x, IDUtil.makeNewFurnaceID(Constants.SILVER_PATTERN_ID.getPath() + "_nbt")), ctx, provider, Constants.SILVER_PATTERN_ID.getPath() + "_nbt", ModItemTags.SILVER);

                whenHasTags(x -> FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.SILVER_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("###")
                        .define('#', ModItemTags.SILVER)
                        .define('X', bindForge("furnaces/copper"))
                        .unlockedBy("has_silver_ingot", CriterionUtil.has(ModItemTags.SILVER, provider))
                        .save(x, IDUtil.makeNewFurnaceID(Constants.SILVER_PATTERN_ID.getPath() + "2")), ctx, provider, Constants.SILVER_PATTERN_ID.getPath() + "2", ModItemTags.SILVER);

                whenHasTags(x -> FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.SILVER_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("###")
                        .define('#', ModItemTags.SILVER)
                        .define('X', bindPatternHolder(ctx.get(), Constants.COPPER_PATTERN_ID))
                        .unlockedBy("has_silver_ingot", CriterionUtil.has(ModItemTags.SILVER, provider))
                        .save(x, IDUtil.makeNewFurnaceID(Constants.SILVER_PATTERN_ID.getPath() + "2_nbt")), ctx, provider, Constants.SILVER_PATTERN_ID.getPath() + "2_nbt", ModItemTags.SILVER);

                FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.OBSIDIAN_PATTERN_ID)
                        .pattern("#Y#")
                        .pattern("YXY")
                        .pattern("#Y#")
                        .define('#', bindForge("obsidian"))
                        .define('X', bindForge("furnaces/emerald"))
                        .define('Y', bindForge("rods/blaze"))
                        .unlockedBy("has_obsidian", CriterionUtil.has(Blocks.OBSIDIAN, provider))
                        .save(provider, IDUtil.makeNewFurnaceID(Constants.OBSIDIAN_PATTERN_ID.getPath()));

                FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.OBSIDIAN_PATTERN_ID)
                        .pattern("#Y#")
                        .pattern("YXY")
                        .pattern("#Y#")
                        .define('#', bindForge("obsidian"))
                        .define('X', bindPatternHolder(ctx.get(), Constants.EMERALD_PATTERN_ID))
                        .define('Y', bindForge("rods/blaze"))
                        .unlockedBy("has_obsidian", CriterionUtil.has(Blocks.OBSIDIAN, provider))
                        .save(provider, IDUtil.makeNewFurnaceID(Constants.OBSIDIAN_PATTERN_ID.getPath() + "_nbt"));

                FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.OBSIDIAN_PATTERN_ID)
                        .pattern("#Y#")
                        .pattern("YXY")
                        .pattern("#Y#")
                        .define('#', bindForge("obsidian"))
                        .define('X', bindForge("furnaces/crystal"))
                        .define('Y', bindForge("rods/blaze"))
                        .unlockedBy("has_obsidian", CriterionUtil.has(Blocks.OBSIDIAN, provider))
                        .save(provider, IDUtil.makeNewFurnaceID(Constants.OBSIDIAN_PATTERN_ID.getPath() + "2"));

                FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.OBSIDIAN_PATTERN_ID)
                        .pattern("#Y#")
                        .pattern("YXY")
                        .pattern("#Y#")
                        .define('#', bindForge("obsidian"))
                        .define('X', bindPatternHolder(ctx.get(), Constants.CRYSTAL_PATTERN_ID))
                        .define('Y', bindForge("rods/blaze"))
                        .unlockedBy("has_obsidian", CriterionUtil.has(Blocks.OBSIDIAN, provider))
                        .save(provider, IDUtil.makeNewFurnaceID(Constants.OBSIDIAN_PATTERN_ID.getPath() + "2_nbt"));

                FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.CRYSTAL_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("#E#")
                        .define('#', bindForge("glass"))
                        .define('X', bindForge("furnaces/diamond"))
                        .define('E', Items.ENDER_EYE)
                        .unlockedBy("has_diamond_furnace", CriterionUtil.has(Items.DIAMOND, provider))
                        .save(provider, IDUtil.makeNewFurnaceID(Constants.CRYSTAL_PATTERN_ID.getPath()));

                FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.CRYSTAL_PATTERN_ID)
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("#E#")
                        .define('#', bindForge("glass"))
                        .define('X', bindPatternHolder(ctx.get(), Constants.DIAMOND_PATTERN_ID))
                        .define('E', Items.ENDER_EYE)
                        .unlockedBy("has_diamond_furnace", CriterionUtil.has(Items.DIAMOND, provider))
                        .save(provider, IDUtil.makeNewFurnaceID(Constants.CRYSTAL_PATTERN_ID.getPath() + "_nbt"));

                whenAllthemodium(x ->
                        FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.ALLTHEMODIUM_PATTERN_ID)
                                .pattern("B#B")
                                .pattern("#X#")
                                .pattern("B#B")
                                .define('#', bindForge("ingots/allthemodium"))
                                .define('X', bindForge("furnaces/netherite"))
                                .define('B', bindForge("storage_blocks/allthemodium"))
                                .unlockedBy("has_allthemodium_ingot",
                                        CriterionUtil.has(bindForge("ingots/allthemodium"), provider))
                                .save(x, IDUtil.makeNewFurnaceID(Constants.ALLTHEMODIUM_PATTERN_ID.getPath())), ctx, Constants.ALLTHEMODIUM_PATTERN_ID.getPath(), provider);

                whenAllthemodium(x ->
                        FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.ALLTHEMODIUM_PATTERN_ID)
                                .pattern("B#B")
                                .pattern("#X#")
                                .pattern("B#B")
                                .define('#', bindForge("ingots/allthemodium"))
                                .define('X', bindPatternHolder(ctx.get(), Constants.NETHERITE_PATTERN_ID))
                                .define('B', bindForge("storage_blocks/allthemodium"))
                                .unlockedBy("has_allthemodium_ingot",
                                        CriterionUtil.has(bindForge("ingots/allthemodium"), provider))
                                .save(x, IDUtil.makeNewFurnaceID(Constants.ALLTHEMODIUM_PATTERN_ID.getPath() + "_nbt")), ctx, Constants.ALLTHEMODIUM_PATTERN_ID.getPath() + "_nbt", provider);

                whenAllthemodium(x ->
                        FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.VIBRANIUM_PATTERN_ID)
                                .pattern("B#B")
                                .pattern("#X#")
                                .pattern("B#B")
                                .define('#', bindForge("ingots/vibranium"))
                                .define('X', bindForge("furnaces/allthemodium"))
                                .define('B', bindForge("storage_blocks/vibranium"))
                                .unlockedBy("has_vibranium_ingot",
                                        CriterionUtil.has(bindForge("ingots/vibranium"), provider))
                                .save(x, IDUtil.makeNewFurnaceID(Constants.VIBRANIUM_PATTERN_ID.getPath())), ctx, Constants.VIBRANIUM_PATTERN_ID.getPath(), provider);

                whenAllthemodium(x ->
                        FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.VIBRANIUM_PATTERN_ID)
                                .pattern("B#B")
                                .pattern("#X#")
                                .pattern("B#B")
                                .define('#', bindForge("ingots/vibranium"))
                                .define('X', bindPatternHolder(ctx.get(), Constants.ALLTHEMODIUM_PATTERN_ID))
                                .define('B', bindForge("storage_blocks/vibranium"))
                                .unlockedBy("has_vibranium_ingot",
                                        CriterionUtil.has(bindForge("ingots/vibranium"), provider))
                                .save(x, IDUtil.makeNewFurnaceID(Constants.VIBRANIUM_PATTERN_ID.getPath() + "_nbt")), ctx, Constants.VIBRANIUM_PATTERN_ID.getPath() + "_nbt", provider);

                whenAllthemodium(x ->
                        FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.UNOBTAINIUM_PATTERN_ID)
                                .pattern("B#B")
                                .pattern("#X#")
                                .pattern("B#B")
                                .define('#', bindForge("ingots/unobtainium"))
                                .define('X', bindForge("furnaces/vibranium"))
                                .define('B', bindForge("storage_blocks/unobtainium"))
                                .unlockedBy("has_unobtainium_ingot",
                                        CriterionUtil.has(bindForge("ingots/unobtainium"), provider))
                                .save(x, IDUtil.makeNewFurnaceID(Constants.UNOBTAINIUM_PATTERN_ID.getPath())), ctx, Constants.UNOBTAINIUM_PATTERN_ID.getPath(), provider);

                whenAllthemodium(x ->
                        FurnacePatternHolderRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), Constants.UNOBTAINIUM_PATTERN_ID)
                                .pattern("B#B")
                                .pattern("#X#")
                                .pattern("B#B")
                                .define('#', bindForge("ingots/unobtainium"))
                                .define('X', bindPatternHolder(ctx.get(), Constants.VIBRANIUM_PATTERN_ID))
                                .define('B', bindForge("storage_blocks/unobtainium"))
                                .unlockedBy("has_unobtainium_ingot",
                                        CriterionUtil.has(bindForge("ingots/unobtainium"), provider))
                                .save(x, IDUtil.makeNewFurnaceID(Constants.UNOBTAINIUM_PATTERN_ID.getPath() + "_nbt")), ctx, Constants.UNOBTAINIUM_PATTERN_ID.getPath() + "_nbt", provider);


            })
            .model(() -> (ctx, prov) -> {
                ModModelTemplate.createPatternHolderBaseModel(ctx.get(), prov);
            })
            .tag(ModItemTags.PLAYER_WORKSTATIONS_FURNACE)
            .build()
            .clientExtension(() -> () -> {
                return new IClientBlockExtensions() {
                    @Override
                    public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
                        if (!(target instanceof BlockHitResult blockHit)) {
                            return false;
                        }
                        if (!(level instanceof ClientLevel clientLevel)) return false;

                        BlockPos pos = blockHit.getBlockPos();
                        BlockState reference = getReferenceStateOrNull(level, pos);
                        if (reference == null) {
                            return false;
                        }

                        Direction face = blockHit.getDirection();
                        VoxelShape shape = reference.getShape(level, pos);
                        if (shape.isEmpty()) {
                            shape = Shapes.block();
                        }

                        AABB aabb = shape.bounds();

                        RandomSource random = level.getRandom();

                        double x = pos.getX() + random.nextDouble() * (aabb.maxX - aabb.minX - 0.2D) + 0.1D + aabb.minX;
                        double y = pos.getY() + random.nextDouble() * (aabb.maxY - aabb.minY - 0.2D) + 0.1D + aabb.minY;
                        double z = pos.getZ() + random.nextDouble() * (aabb.maxZ - aabb.minZ - 0.2D) + 0.1D + aabb.minZ;

                        switch (face) {
                            case DOWN -> y = pos.getY() + aabb.minY - 0.1D;
                            case UP -> y = pos.getY() + aabb.maxY + 0.1D;
                            case NORTH -> z = pos.getZ() + aabb.minZ - 0.1D;
                            case SOUTH -> z = pos.getZ() + aabb.maxZ + 0.1D;
                            case WEST -> x = pos.getX() + aabb.minX - 0.1D;
                            case EAST -> x = pos.getX() + aabb.maxX + 0.1D;
                        }

                        manager.add(
                                (new TerrainParticle(clientLevel, x, y, z, 0.0D, 0.0D, 0.0D, reference, pos))
                                        .setPower(0.2F)
                                        .scale(0.6F)
                        );
                        return true;
                    }
                };
            })
            .register();


    private static FurnacePatternBlockEntity getPatternHolder(BlockGetter level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof FurnacePatternBlockEntity holder ? holder : null;
    }

    private static Optional<Block> getReferenceBlock(BlockGetter level, BlockPos pos) {
        FurnacePatternBlockEntity holder = getPatternHolder(level, pos);
        if (holder == null) {
            return Optional.empty();
        }

        FurnacePattern pattern = holder.getPattern();
        if (pattern == null) {
            return Optional.empty();
        }

        return pattern.referenceBlock()
                .map(BuiltInRegistries.BLOCK::get)
                .filter(block -> block.isPresent())
                .map(x -> x.get().value());
    }

    public static BlockState getReferenceStateOrNull(BlockGetter level, BlockPos pos) {
        return getReferenceBlock(level, pos)
                .map(Block::defaultBlockState)
                .orElse(null);
    }


    private static void whenAllthemodium(
            Consumer<RecipeOutput> consumerConsumer,
            DataGenContext<Item, FurnacePatternHolderItem> ctx,
            String id,
            RegistrumRecipeProvider provider
    ) {
        ConditionRecipeUtil.whenHasMod(consumerConsumer, ctx, "new_furnaces", id, "allthemodium", provider);
    }

    @SafeVarargs
    private static void whenHasTags(Consumer<RecipeOutput> consumerConsumer, DataGenContext<Item, FurnacePatternHolderItem> ctx, RegistrumRecipeProvider provider, String id, TagKey<Item>... tags) {
        ConditionRecipeUtil.whenHasTags(consumerConsumer, ctx, provider, "new_furnaces", id, tags);
    }


    private static Ingredient bindPatternHolder(Item item, Identifier patternId) {
        return DataComponentIngredient.of(false, ModDataComponents.FURNACE_PATTERN_COMPONENT.get(), patternId, item);
    }


    public static <T extends BlockEntity> BlockEntityType<T> asGenericBlockEntityType(BlockEntry<?> entry) {
        return (BlockEntityType<T>) entry.getSibling(BuiltInRegistries.BLOCK_ENTITY_TYPE).get();
    }


    public static void register() {

    }

}
