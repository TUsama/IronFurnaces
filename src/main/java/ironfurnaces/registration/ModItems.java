package ironfurnaces.registration;

import dev.anvilcraft.lib.v2.registrum.Registrum;
import dev.anvilcraft.lib.v2.registrum.builders.ItemBuilder;
import dev.anvilcraft.lib.v2.registrum.util.entry.ItemEntry;
import dev.anvilcraft.lib.v2.registrum.util.nullness.NonNullFunction;
import ironfurnaces.items.*;
import ironfurnaces.items.augments.*;
import ironfurnaces.items.upgrades.furnace_upgrade.ItemUpgradeTool;
import ironfurnaces.items.upgrades.furnace_upgrade.recipe.PatternUpgradeRecipeBuilder;
import ironfurnaces.items.upgrades.furnace_upgrade.render.UpgradeToolSpecialRenderer;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.registration.util.ConditionRecipeUtil;
import ironfurnaces.registration.util.CriterionUtil;
import ironfurnaces.registration.util.IDUtil;
import ironfurnaces.tileentity.furnaces.pattern.render.refactor.FurnacePatternHolderSpecialRenderer;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.transfer.item.ItemUtil;


import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;
import static ironfurnaces.registration.ModItemTags.*;
import static ironfurnaces.registration.util.IDUtil.makeID;

public class ModItems {




    public static final ItemEntry<ItemHeater> ITEM_HEATER =
            registerItem("item_heater", p -> new ItemHeater(p.stacksTo(1))).recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get())
                                .pattern("#R#")
                                .pattern("RXR")
                                .pattern("#R#")
                                .define('#', bindForge("stone"))
                                .define('R', bindForge("dusts/redstone"))
                                .define('X', Items.COMPARATOR)
                                .unlockedBy("has_comparator", CriterionUtil.has(Items.COMPARATOR, provider))
                                .save(provider);
                    })
                    .register();

    public static final ItemEntry<ItemAugmentBlasting> BLASTING_AUGMENT =
            registerItem("augment_blasting", "Augment: Blasting", ItemAugmentBlasting::new)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get())
                                .pattern("#R#")
                                .pattern("PXP")
                                .pattern("#R#")
                                .define('#', bindForge("stone"))
                                .define('P', Items.PAPER)
                                .define('R', bindForge("dusts/redstone"))
                                .define('X', Items.BLAST_FURNACE)
                                .unlockedBy("has_blast_furnace", CriterionUtil.has(Items.BLAST_FURNACE, provider))
                                .save(provider, makeID("augments/" + ctx.getName()));

                    })
                    .register();

    public static final ItemEntry<ItemAugmentSmoking> SMOKING_AUGMENT =
            registerItem("augment_smoking", "Augment: Smoking", ItemAugmentSmoking::new)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get())
                                .pattern("#R#")
                                .pattern("PXP")
                                .pattern("#R#")
                                .define('#', bindForge("stone"))
                                .define('P', Items.PAPER)
                                .define('R', bindForge("dusts/redstone"))
                                .define('X', Items.SMOKER)
                                .unlockedBy("has_smoker", CriterionUtil.has(Items.SMOKER, provider))
                                .save(provider, makeID("augments/" + ctx.getName()));


                    }).register();

    public static final ItemEntry<ItemAugmentFactory> FACTORY_AUGMENT =
            registerItem("augment_factory", "Augment: Factory", ItemAugmentFactory::new)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get())
                                .pattern("#R#")
                                .pattern("PXP")
                                .pattern("#R#")
                                .define('#', bindForge("stone"))
                                .define('P', Items.PAPER)
                                .define('R', bindForge("dusts/redstone"))
                                .define('X', Items.PISTON)
                                .unlockedBy("has_piston", CriterionUtil.has(Items.PISTON, provider))
                                .save(provider, makeID("augments/" + ctx.getName()));

                    }).register();

    public static final ItemEntry<ItemAugmentGenerator> GENERATOR_AUGMENT =
            registerItem("augment_generator", "Augment: Generator", ItemAugmentGenerator::new)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get())
                                .pattern("#R#")
                                .pattern("PXP")
                                .pattern("#R#")
                                .define('#', bindForge("stone"))
                                .define('P', Items.PAPER)
                                .define('R', bindForge("dusts/redstone"))
                                .define('X', Items.REPEATER)
                                .unlockedBy("has_repeater", CriterionUtil.has(Items.REPEATER, provider))
                                .save(provider, makeID("augments/" + ctx.getName()));


                    }).register();

    public static final ItemEntry<ItemAugmentSpeed> SPEED_AUGMENT =
            registerItem("augment_speed", "Augment: Speed", ItemAugmentSpeed::new)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get())
                                .pattern("#R#")
                                .pattern("PXP")
                                .pattern("#R#")
                                .define('#', bindForge("stone"))
                                .define('P', Items.PAPER)
                                .define('R', bindForge("dusts/redstone"))
                                .define('X', Items.SUGAR)
                                .unlockedBy("has_sugar", CriterionUtil.has(Items.SUGAR, provider))
                                .save(provider, makeID("augments/" + ctx.getName()));

                    }).register();

    public static final ItemEntry<ItemAugmentFuel> FUEL_AUGMENT =
            registerItem("augment_fuel", "Augment: Fuel Efficiency", ItemAugmentFuel::new)
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get())
                                .pattern("#R#")
                                .pattern("PXP")
                                .pattern("#R#")
                                .define('#', bindForge("stone"))
                                .define('P', Items.PAPER)
                                .define('R', bindForge("dusts/redstone"))
                                .define('X', Items.COAL)
                                .unlockedBy("has_coal", CriterionUtil.has(Items.COAL, provider))
                                .save(provider, makeID("augments/" + ctx.getName()));

                    }).register();

    public static final ItemEntry<ItemSpooky> ITEM_SPOOKY =
            registerItem("item_spooky", ItemSpooky::new).recipe((ctx, provider) -> {
                        ShapelessRecipeBuilder.shapeless(provider.getItems(), RecipeCategory.MISC, ctx.get())
                                .requires(Items.CARVED_PUMPKIN)
                                .requires(ModItemTags.PLAYER_WORKSTATIONS_FURNACE)
                                .unlockedBy("has_furnace", CriterionUtil.has(ModItemTags.PLAYER_WORKSTATIONS_FURNACE, provider))
                                .save(provider);
                    })
                    .register();

    public static final ItemEntry<ItemXmas> ITEM_XMAS =
            registerItem("item_xmas", ItemXmas::new).recipe((ctx, provider) -> {
                        ShapelessRecipeBuilder.shapeless(provider.getItems(), RecipeCategory.MISC, ctx.get())
                                .requires(Items.YELLOW_DYE)
                                .requires(Items.RED_DYE)
                                .requires(Items.PAPER)
                                .requires(ModItemTags.PLAYER_WORKSTATIONS_FURNACE)
                                .unlockedBy("has_furnace", CriterionUtil.has(ModItemTags.PLAYER_WORKSTATIONS_FURNACE, provider))
                                .save(provider);
                    })
                    .register();



    public static final ItemEntry<ItemFurnaceCopyV2> ITEM_COPY_V2 =
            registerItem("item_copy", p -> new ItemFurnaceCopyV2(p.stacksTo(1)))
                    .recipe((ctx, provider) -> {
                        ShapedRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get())
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', Items.PAPER)
                                .define('X', ModItemTags.PLAYER_WORKSTATIONS_FURNACE)
                                .unlockedBy("has_furnace", CriterionUtil.has(ModItemTags.PLAYER_WORKSTATIONS_FURNACE, provider))
                                .save(provider);
                    })
                    .lang("Copy Tool")
                    .register();

    public static final ItemEntry<Item> RAINBOW_CORE =
            registerItem("rainbow_core", Item::new).recipe((ctx, provider) -> {
                        //? <1.21.11 {
                        /*ShapedRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get())
                                .pattern("ROY")
                                .pattern("FGF")
                                .pattern("BPM")
                                .define('R', Items.RED_STAINED_GLASS)
                                .define('O', Items.ORANGE_STAINED_GLASS)
                                .define('Y', Items.YELLOW_STAINED_GLASS)
                                .define('F', LegacyFurnaceBlocks.NETHERITE_FURNACE)
                                .define('G', Items.GREEN_STAINED_GLASS)
                                .define('B', Items.BLUE_STAINED_GLASS)
                                .define('P', Items.PURPLE_STAINED_GLASS)
                                .define('M', Items.MAGENTA_STAINED_GLASS)
                                .unlockedBy("has_netherite_furnace", CriterionUtil.has(LegacyFurnaceBlocks.NETHERITE_FURNACE.get(), provider))
                                .save(provider, IDUtil.makeID(ctx.getName()));
                        *///?}
                    })
                    .register();

    public static final ItemEntry<Item> RAINBOW_PLATING =
            registerItem("rainbow_plating", Item::new)
                    .recipe((ctx, provider) -> {
                        //? <1.21.11 {
                        /*ConditionRecipeUtil.whenHasTags(x -> ShapelessRecipeBuilder.shapeless(provider.getItems(), RecipeCategory.MISC, ctx.get(), 8)
                                .requires(LegacyFurnaceBlocks.IRON_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.GOLD_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.DIAMOND_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.EMERALD_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.OBSIDIAN_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.CRYSTAL_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.COPPER_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.SILVER_FURNACE.get())
                                .unlockedBy("has_iron_furnace", CriterionUtil.has(LegacyFurnaceBlocks.IRON_FURNACE.get(), provider))
                                .save(x, IDUtil.makeID(ctx.getName())), ctx, provider, "", ctx.getName(), ModItemTags.SILVER);
                        *///?}

                        //? forge {
                        /*ConditionalRecipe.builder()
                                .addCondition(new TagEmptyCondition(ModItemTags.SILVER.location()))
                                .addRecipe(x -> {
                                    ShapelessRecipeBuilder.shapeless(provider.getItems(), RecipeCategory.MISC, ctx.get(), 8)
                                            .requires(LegacyFurnaceBlocks.IRON_FURNACE.get())
                                            .requires(LegacyFurnaceBlocks.GOLD_FURNACE.get())
                                            .requires(LegacyFurnaceBlocks.DIAMOND_FURNACE.get())
                                            .requires(LegacyFurnaceBlocks.EMERALD_FURNACE.get())
                                            .requires(LegacyFurnaceBlocks.OBSIDIAN_FURNACE.get())
                                            .requires(LegacyFurnaceBlocks.CRYSTAL_FURNACE.get())
                                            .requires(LegacyFurnaceBlocks.COPPER_FURNACE.get())
                                            .unlockedBy("has_iron_furnace", CriterionUtil.has(LegacyFurnaceBlocks.IRON_FURNACE.get(), provider))
                                            .save(x, makeID(ctx.getName() + "_no_silver"));
                                }).build(provider, makeID(ctx.getName() + "_no_silver"));
                        *///? } else {
                        //? <1.21.11 {
                        /*ShapelessRecipeBuilder.shapeless(provider.getItems(), RecipeCategory.MISC, ctx.get(), 8)
                                .requires(LegacyFurnaceBlocks.IRON_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.GOLD_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.DIAMOND_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.EMERALD_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.OBSIDIAN_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.CRYSTAL_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.COPPER_FURNACE.get())
                                .unlockedBy("has_iron_furnace", CriterionUtil.has(LegacyFurnaceBlocks.IRON_FURNACE.get(), provider))
                                .save(new ConditionalRecipeOutput(provider, List.of(new TagEmptyCondition(ModItemTags.SILVER.location())).toArray(ICondition[]::new)), IDUtil.makeID(ctx.getName() + "_no_silver"));
                        *///?}

                        //?}

                    })
                    .register();

    public static final ItemEntry<ItemRainbowCoal> RAINBOW_COAL =
            registerItem("rainbow_coal", ItemRainbowCoal::new)
                    .recipe((ctx, provider) -> {
                        //? <1.21.11 {
                        /*ConditionRecipeUtil.whenHasTags(x -> ShapelessRecipeBuilder.shapeless(provider.getItems(), RecipeCategory.MISC, ctx.get())
                                .requires(LegacyFurnaceBlocks.IRON_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.GOLD_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.DIAMOND_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.EMERALD_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.OBSIDIAN_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.CRYSTAL_FURNACE.get())
                                .requires(LegacyFurnaceBlocks.COPPER_FURNACE.get())
                                .unlockedBy("has_iron_furnace", CriterionUtil.has(LegacyFurnaceBlocks.IRON_FURNACE.get(), provider))
                                .save(provider, IDUtil.makeID(ctx.getName())), ctx, provider, "", ctx.getName(), ModItemTags.SILVER);
                        *///?}

                    }).register();

    public static final ItemEntry<ItemUpgradeTool> UPGRADE_TOOL =
            registerItem("upgrade_tool", ItemUpgradeTool::new)
                    .model(() -> (ctx, prov) -> {

                        Identifier baseModel = IronFurnaces.id("item/upgrade_tool_base");

                        ModelTemplates.PARTICLE_ONLY.create(
                                baseModel,
                                TextureMapping.particle(TextureMapping.getBlockTexture(Blocks.STONE)),
                                prov.modelOutput
                        );

                        prov.itemModelOutput.accept(
                                ctx.get(),
                                ItemModelUtils.specialModel(
                                        baseModel,
                                        new UpgradeToolSpecialRenderer.Unbaked()
                                )
                        );
                    })
                    .recipe((ctx, provider) -> {
                        PatternUpgradeRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), IDUtil.makeID("upgrade_gold"))
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("#Y#")
                                .define('#', bindForge("ingots/gold"))
                                .define('X', bindForge("ingots/iron"))
                                .define('Y', bindForge("storage_blocks/gold"))
                                .unlockedBy("has_gold", CriterionUtil.has(bindForge("ingots/gold"), provider))
                                .save(provider, IDUtil.newUpgrade("upgrade_gold"));

                        PatternUpgradeRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), IDUtil.makeID("upgrade_iron"))
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', bindForge("ingots/iron"))
                                .define('X', bindVanilla("stone_tool_materials"))
                                .unlockedBy("has_iron", CriterionUtil.has(bindForge("ingots/iron"), provider))
                                .save(provider, IDUtil.newUpgrade("upgrade_iron"));

                        PatternUpgradeRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), IDUtil.makeID("upgrade_diamond"))
                                .pattern("###")
                                .pattern("GXG")
                                .pattern("###")
                                .define('#', bindForge("gems/diamond"))
                                .define('X', bindForge("ingots/gold"))
                                .define('G', bindForge("glass"))
                                .unlockedBy("has_diamond", CriterionUtil.has(bindForge("gems/diamond"), provider))
                                .save(provider, IDUtil.newUpgrade("upgrade_diamond"));

                        PatternUpgradeRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), IDUtil.makeID("upgrade_emerald"))
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', bindForge("gems/emerald"))
                                .define('X', bindForge("gems/diamond"))
                                .unlockedBy("has_emerald", CriterionUtil.has(bindForge("gems/emerald"), provider))
                                .save(provider, IDUtil.newUpgrade("upgrade_emerald"));

                        PatternUpgradeRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), IDUtil.makeID("upgrade_obsidian"))
                                .pattern("#Y#")
                                .pattern("YXY")
                                .pattern("#Y#")
                                .define('#', bindForge("obsidian"))
                                .define('X', bindForge("gems/emerald"))
                                .define('Y', bindForge("rods/blaze"))
                                .unlockedBy("has_obsidian", CriterionUtil.has(bindForge("obsidian"), provider))
                                .save(provider, IDUtil.newUpgrade("upgrade_obsidian"));

                        PatternUpgradeRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), IDUtil.makeID("upgrade_crystal"))
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', bindForge("glass"))
                                .define('X', bindForge("gems/diamond"))
                                .unlockedBy("has_glass", CriterionUtil.has(bindForge("glass"), provider))
                                .save(provider, IDUtil.newUpgrade("upgrade_crystal"));

                        PatternUpgradeRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), IDUtil.makeID("upgrade_netherite"))
                                .pattern("N#N")
                                .pattern("#X#")
                                .pattern("NSN")
                                .define('#', Items.MAGMA_CREAM)
                                .define('X', C_OBSIDIAN_NORMAL)
                                .define('S', bindVanilla("soul_fire_base_blocks"))
                                .define('N', Items.NETHERITE_INGOT)
                                .unlockedBy("has_netherite", CriterionUtil.has(Items.NETHERITE_INGOT, provider))
                                .save(provider, IDUtil.newUpgrade("upgrade_netherite"));

                        PatternUpgradeRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), IDUtil.makeID("upgrade_copper"))
                                .pattern("###")
                                .pattern("#X#")
                                .pattern("###")
                                .define('#', bindForge("ingots/copper"))
                                .define('X', ModItemTags.PLAYER_WORKSTATIONS_FURNACE)
                                .unlockedBy("has_copper", CriterionUtil.has(bindForge("ingots/copper"), provider))
                                .save(provider, IDUtil.newUpgrade("upgrade_copper"));

                        ConditionRecipeUtil.whenHasTags(x ->
                                        PatternUpgradeRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), IDUtil.makeID("upgrade_silver"))
                                                .pattern("###")
                                                .pattern("#S#")
                                                .pattern("#X#")
                                                .define('#', bindForge("ingots/silver"))
                                                .define('X', bindForge("ingots/copper"))
                                                .define('S', bindVanilla("stone_tool_materials"))
                                                .unlockedBy("has_silver", CriterionUtil.has(bindForge("ingots/silver"), provider))
                                                .save(x, IDUtil.newUpgrade("upgrade_silver")),
                                ctx, provider, "new_upgrades", "upgrade_silver", ModItemTags.SILVER
                        );

                        PatternUpgradeRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), IDUtil.makeID("upgrade_obsidian2"))
                                .pattern("#Y#")
                                .pattern("YXY")
                                .pattern("#Y#")
                                .define('#', bindForge("obsidian"))
                                .define('X', bindForge("glass"))
                                .define('Y', bindForge("rods/blaze"))
                                .unlockedBy("has_obsidian", CriterionUtil.has(bindForge("obsidian"), provider))
                                .save(provider, IDUtil.newUpgrade("upgrade_obsidian2"));

                        PatternUpgradeRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), IDUtil.makeID("upgrade_iron2"))
                                .pattern("###")
                                .pattern("GXG")
                                .pattern("###")
                                .define('#', bindForge("ingots/iron"))
                                .define('X', bindForge("ingots/copper"))
                                .define('G', bindForge("glass"))
                                .unlockedBy("has_diamond", CriterionUtil.has(bindForge("gems/diamond"), provider))
                                .save(provider, IDUtil.newUpgrade("upgrade_iron2"));

                        ConditionRecipeUtil.whenHasTags(x ->
                                        PatternUpgradeRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), IDUtil.makeID("upgrade_gold2"))
                                                .pattern("###")
                                                .pattern("#X#")
                                                .pattern("#Y#")
                                                .define('#', bindForge("ingots/gold"))
                                                .define('X', bindForge("ingots/silver"))
                                                .define('Y', bindForge("storage_blocks/gold"))
                                                .unlockedBy("has_gold", CriterionUtil.has(bindForge("ingots/gold"), provider))
                                                .save(x, IDUtil.newUpgrade("upgrade_gold2")),
                                ctx, provider, "new_upgrades", "upgrade_gold2", bindForge("ingots/silver")
                        );

                        ConditionRecipeUtil.whenHasTags(x ->
                                        PatternUpgradeRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), IDUtil.makeID("upgrade_silver2"))
                                                .pattern("#G#")
                                                .pattern("GXG")
                                                .pattern("#G#")
                                                .define('#', bindForge("ingots/silver"))
                                                .define('X', bindForge("ingots/iron"))
                                                .define('G', bindForge("glass"))
                                                .unlockedBy("has_silver", CriterionUtil.has(bindForge("ingots/silver"), provider))
                                                .save(x, IDUtil.newUpgrade("upgrade_silver2")),
                                ctx, provider, "new_upgrades", "upgrade_silver2", ModItemTags.SILVER
                        );

                        ConditionRecipeUtil.whenAllthemodium(x ->
                                        PatternUpgradeRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), IDUtil.makeID("upgrade_allthemodium"))
                                                .pattern("B#B")
                                                .pattern("#X#")
                                                .pattern("B#B")
                                                .define('#', bindForge("ingots/allthemodium"))
                                                .define('B', bindForge("storage_blocks/allthemodium"))
                                                .define('X', ModItemTags.NETHERITE_UPGRADE)
                                                .unlockedBy("has_allthemodium", CriterionUtil.has(bindForge("ingots/allthemodium"), provider))
                                                .save(x, IDUtil.newUpgrade("upgrade_allthemodium")),
                                ctx, "new_upgrades", "upgrade_allthemodium", provider
                        );

                        ConditionRecipeUtil.whenAllthemodium(x ->
                                        PatternUpgradeRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), IDUtil.makeID("upgrade_vibranium"))
                                                .pattern("B#B")
                                                .pattern("#X#")
                                                .pattern("B#B")
                                                .define('#', bindForge("ingots/vibranium"))
                                                .define('B', bindForge("storage_blocks/vibranium"))
                                                .define('X', bindForge("ingots/allthemodium"))
                                                .unlockedBy("has_vibranium", CriterionUtil.has(bindForge("ingots/vibranium"), provider))
                                                .save(x, IDUtil.newUpgrade("upgrade_vibranium")),
                                ctx, "new_upgrades", "upgrade_vibranium", provider
                        );

                        ConditionRecipeUtil.whenAllthemodium(x ->
                                        PatternUpgradeRecipeBuilder.shaped(provider.getItems(), RecipeCategory.MISC, ctx.get(), IDUtil.makeID("upgrade_unobtainium"))
                                                .pattern("B#B")
                                                .pattern("#X#")
                                                .pattern("B#B")
                                                .define('#', bindForge("ingots/unobtainium"))
                                                .define('B', bindForge("storage_blocks/unobtainium"))
                                                .define('X', bindForge("ingots/vibranium"))
                                                .unlockedBy("has_unobtainium", CriterionUtil.has(bindForge("ingots/unobtainium"), provider))
                                                .save(x, IDUtil.newUpgrade("upgrade_unobtainium")),
                                ctx, "new_upgrades", "upgrade_unobtainium", provider
                        );
                    })

                    .register();

    protected static <T extends Item> ItemBuilder<T, Registrum> registerItem(
            String name,
            String langName,
            NonNullFunction<Item.Properties, T> factory
    ) {
        return core(name, factory)
                .lang(langName);
    }

    protected static <T extends Item> ItemBuilder<T, Registrum> registerItem(
            String name,
            NonNullFunction<Item.Properties, T> factory
    ) {
        return core(name, factory);
    }

    protected static <T extends Item> ItemBuilder<T, Registrum> core(
            String name,
            NonNullFunction<Item.Properties, T> factory
    ) {
        return REGISTRATE
                .item(name, factory);
    }


    public static void register() {

    }

}
